# 9. Package by feature

Date: 2023-11-01

## Status

Accepted

## Context

Refined Storage adds a lot of content. For ease of maintenance, we need to think about how we are going to segment our
packages.

## Decision

We [package by feature, not by layer](https://wayback-api.archive.org/web/20240000000000*/http://www.javapractices.com/topic/TopicAction.do?Id=205).

Implementation provided in [[1]](#1).

## Consequences

- Code is able to use package-private scope a lot more.

## References

- <a id="1">[1]</a>
  See [implementation](https://github.com/refinedmods/refinedstorage2/commit/d109b09be863c6ea71138091b6ce66c2a573546e)
