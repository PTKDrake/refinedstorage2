# 7. Refined Architect

Date: 2023-03-07

## Status

Accepted

## Context

For Refined Storage, we want to build a large ecosystem of addon mods. However, all those addon mods, and even unrelated
mods managed by Refined Mods, have a lot of duplication in terms of build infrastructure, GitHub Actions workflows and
Gradle setup code.

This duplication makes it difficult to maintain the soon-to-be suite of mods as a whole, especially if we have to
upgrade Minecraft.

## Decision

We introduce [Refined Architect](https://github.com/refinedmods/refinedarchitect): a project that is used by all the
mods of Refined Mods.

It contains GitHub workflows, version management and Gradle helpers to help making (cross-platform) mods easier.

## Consequences

- Refined Storage adopts Refined Architect.
- Refined Architect must be kept up to date and maintained in order to upgrade Refined Storage.
