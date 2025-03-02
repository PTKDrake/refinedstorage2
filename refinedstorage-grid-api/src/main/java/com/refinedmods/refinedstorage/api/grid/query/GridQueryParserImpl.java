package com.refinedmods.refinedstorage.api.grid.query;

import com.refinedmods.refinedstorage.api.grid.view.GridResource;
import com.refinedmods.refinedstorage.api.grid.view.GridResourceAttributeKey;
import com.refinedmods.refinedstorage.api.grid.view.GridView;
import com.refinedmods.refinedstorage.query.lexer.Lexer;
import com.refinedmods.refinedstorage.query.lexer.LexerException;
import com.refinedmods.refinedstorage.query.lexer.LexerTokenMappings;
import com.refinedmods.refinedstorage.query.lexer.Source;
import com.refinedmods.refinedstorage.query.lexer.Token;
import com.refinedmods.refinedstorage.query.lexer.TokenType;
import com.refinedmods.refinedstorage.query.parser.Parser;
import com.refinedmods.refinedstorage.query.parser.ParserException;
import com.refinedmods.refinedstorage.query.parser.ParserOperatorMappings;
import com.refinedmods.refinedstorage.query.parser.node.BinOpNode;
import com.refinedmods.refinedstorage.query.parser.node.LiteralNode;
import com.refinedmods.refinedstorage.query.parser.node.Node;
import com.refinedmods.refinedstorage.query.parser.node.ParenNode;
import com.refinedmods.refinedstorage.query.parser.node.UnaryOpNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.1.0")
public class GridQueryParserImpl<T extends GridResource> implements GridQueryParser<T> {
    private final LexerTokenMappings tokenMappings;
    private final ParserOperatorMappings operatorMappings;
    private final Map<String, Set<GridResourceAttributeKey>> unaryOperatorToAttributeKeyMapping;

    public GridQueryParserImpl(final LexerTokenMappings tokenMappings,
                               final ParserOperatorMappings operatorMappings,
                               final Map<String, Set<GridResourceAttributeKey>> unaryOperatorToAttributeKeyMapping) {
        this.tokenMappings = tokenMappings;
        this.operatorMappings = operatorMappings;
        this.unaryOperatorToAttributeKeyMapping = unaryOperatorToAttributeKeyMapping;
    }

    @Override
    public BiPredicate<GridView<T>, T> parse(final String query) throws GridQueryParserException {
        if (query.trim().isEmpty()) {
            return (view, resource) -> true;
        }
        final List<Token> tokens = getTokens(query);
        final List<Node> nodes = getNodes(tokens);
        return implicitAnd(nodes);
    }

    private List<Token> getTokens(final String query) throws GridQueryParserException {
        try {
            final Lexer lexer = new Lexer(new Source("Grid query input", query), tokenMappings);
            lexer.scan();
            return lexer.getTokens();
        } catch (LexerException e) {
            throw new GridQueryParserException(e.getMessage(), e);
        }
    }

    private List<Node> getNodes(final List<Token> tokens) throws GridQueryParserException {
        try {
            final Parser parser = new Parser(tokens, operatorMappings);
            parser.parse();
            return parser.getNodes();
        } catch (ParserException e) {
            throw new GridQueryParserException(e.getMessage(), e);
        }
    }

    private BiPredicate<GridView<T>, T> implicitAnd(final List<Node> nodes) throws GridQueryParserException {
        final List<BiPredicate<GridView<T>, T>> conditions = new ArrayList<>();
        for (final Node node : nodes) {
            conditions.add(parseNode(node));
        }
        return and(conditions);
    }

    private BiPredicate<GridView<T>, T> parseNode(final Node node) throws GridQueryParserException {
        return switch (node) {
            case LiteralNode literalNode -> parseLiteral(literalNode);
            case UnaryOpNode unaryOpNode -> parseUnaryOp(unaryOpNode);
            case BinOpNode binOpNode -> parseBinOp(binOpNode);
            case ParenNode parenNode -> implicitAnd(parenNode.nodes());
            default -> throw new GridQueryParserException("Unsupported node", null);
        };
    }

    private BiPredicate<GridView<T>, T> parseBinOp(final BinOpNode node) throws GridQueryParserException {
        final String operator = node.binOp().content();
        if ("&&".equals(operator)) {
            return parseAndBinOpNode(node);
        } else if ("||".equals(operator)) {
            return parseOrBinOpNode(node);
        } else {
            throw new GridQueryParserException("Unsupported operator: " + operator, null);
        }
    }

    private BiPredicate<GridView<T>, T> parseAndBinOpNode(final BinOpNode node)
        throws GridQueryParserException {
        return and(Arrays.asList(
            parseNode(node.left()),
            parseNode(node.right())
        ));
    }

    private BiPredicate<GridView<T>, T> parseOrBinOpNode(final BinOpNode node)
        throws GridQueryParserException {
        return or(Arrays.asList(
            parseNode(node.left()),
            parseNode(node.right())
        ));
    }

    private BiPredicate<GridView<T>, T> parseUnaryOp(final UnaryOpNode node) throws GridQueryParserException {
        final String operator = node.operator().content();
        final Node content = node.node();
        final BiPredicate<GridView<T>, T> predicate;

        if ("!".equals(operator)) {
            predicate = not(parseNode(content));
        } else if (unaryOperatorToAttributeKeyMapping.containsKey(operator)) {
            final Set<GridResourceAttributeKey> keys = unaryOperatorToAttributeKeyMapping.get(operator);
            if (content instanceof LiteralNode(Token token)) {
                predicate = attributeMatch(keys, token.content());
            } else {
                throw new GridQueryParserException("Expected a literal", null);
            }
        } else if (">".equals(operator)) {
            predicate = count(content, (actualCount, wantedCount) -> actualCount > wantedCount);
        } else if (">=".equals(operator)) {
            predicate = count(content, (actualCount, wantedCount) -> actualCount >= wantedCount);
        } else if ("<".equals(operator)) {
            predicate = count(content, (actualCount, wantedCount) -> actualCount < wantedCount);
        } else if ("<=".equals(operator)) {
            predicate = count(content, (actualCount, wantedCount) -> actualCount <= wantedCount);
        } else if ("=".equals(operator)) {
            predicate = count(content, Long::equals);
        } else {
            throw new GridQueryParserException("Unsupported unary operator", null);
        }
        return predicate;
    }

    private static <T extends GridResource> BiPredicate<GridView<T>, T> count(final Node node,
                                                                              final BiPredicate<Long, Long> predicate)
        throws GridQueryParserException {
        if (!(node instanceof LiteralNode)) {
            throw new GridQueryParserException("Count filtering expects a literal", null);
        }

        if (((LiteralNode) node).token().type() != TokenType.INTEGER_NUMBER) {
            throw new GridQueryParserException("Count filtering expects an integer number", null);
        }

        final long wantedCount = Long.parseLong(((LiteralNode) node).token().content());

        return (view, resource) -> predicate.test(resource.getAmount(view), wantedCount);
    }

    private static <T extends GridResource> BiPredicate<GridView<T>, T> attributeMatch(
        final Set<GridResourceAttributeKey> keys,
        final String query
    ) {
        return (view, resource) -> keys
            .stream()
            .map(resource::getAttribute)
            .flatMap(Collection::stream)
            .anyMatch(value -> normalize(value).contains(normalize(query)));
    }

    private static String normalize(final String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private static <T extends GridResource> BiPredicate<GridView<T>, T> parseLiteral(final LiteralNode node) {
        return (view, resource) -> normalize(resource.getName()).contains(normalize(node.token().content()));
    }

    private static <T extends GridResource> BiPredicate<GridView<T>, T> and(
        final List<BiPredicate<GridView<T>, T>> chain) {
        return (view, resource) -> {
            for (final BiPredicate<GridView<T>, T> predicate : chain) {
                if (!predicate.test(view, resource)) {
                    return false;
                }
            }
            return true;
        };
    }

    private static <T extends GridResource> BiPredicate<GridView<T>, T> or(
        final List<BiPredicate<GridView<T>, T>> chain) {
        return (view, resource) -> {
            for (final BiPredicate<GridView<T>, T> predicate : chain) {
                if (predicate.test(view, resource)) {
                    return true;
                }
            }
            return false;
        };
    }

    private static <T extends GridResource> BiPredicate<GridView<T>, T> not(
        final BiPredicate<GridView<T>, T> predicate) {
        return (view, resource) -> !predicate.test(view, resource);
    }
}
