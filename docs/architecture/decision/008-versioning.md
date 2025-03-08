# 8. Versioning scheme

Date: 2023-01-11

## Status

Accepted

## Context

We must think about:

- What versioning scheme do we use?
- How do we store versioning information?
- When do we determine the next version number?
- How do we deal with Minecraft?

## Decision

### What versioning scheme do we use?

We choose [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

### How do we store versioning information?

The code doesn't contain version metadata: `build.gradle` specifies a version of `0.0.0` (
via [Refined Architect](https://github.com/refinedmods/refinedarchitect)).
The versioning information is entirely contained in Git by using tags.

### When do we determine the next version number?

Per [Semantic Versioning](https://semver.org/spec/v2.0.0.html), the version number being released depends on the changes
in that release. We usually can't predict those
changes at the start of a release cycle, so we can't bump the version at the start of a release cycle. That means that
the version number being released is determined at release time.

Because the version number is determined at release time, we can't store any versioning metadata in the
code (`build.gradle`). If we did, `build.gradle` would have the version number of the latest released version during the
release cycle of the new version, which isn't correct.

### How do we deal with Minecraft?

Whenever we port to a new Minecraft version, at least the minor version should be incremented.

This is needed so that we can still support older Minecraft versions without the version numbers conflicting.

## Consequences

- This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
- The code itself doesn't store versioning information.
- We choose the next version number upon release.
- Whenever we port to a new Minecraft version, at least the minor version should be incremented.