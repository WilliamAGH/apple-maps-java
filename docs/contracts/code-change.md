---
title: "Code change policy contract"
usage: "Use whenever creating/modifying files: where to put code, when to create new types, and how to stay SRP/DDD compliant"
description: "Evergreen contract for change decisions (new file vs edit), repository structure/naming, and domain model hierarchy; references rule IDs in `AGENTS.md`"
---

# Code Change Policy Contract

See `AGENTS.md` ([LOC1a-e], [MO1a-g], [FS1a-h], [ND1a-c], [CC1a-d], [TS1a-d], [VR1a-c]).

## Non-negotiables (applies to every change)

- **SRP/DDD only**: each new type/method has one reason to change ([MO1d], [CC1a]).
- **New feature → new file**; do not grow monoliths ([MO1b], [FS1b]).
- **No edits to >350 LOC files**; first split/retrofit ([LOC1c]).
- **Domain is framework-free**; dependencies point inward ([CC1d], [FS1e]).
- **No DTOs**; domain records/interfaces are the API response types.
- **No map payloads** (`Map<String,Object>`, stringly helpers, map-based mappers) ([ZA1c], [FS1b]).

## Decision matrix: create new file vs edit existing

Use this as a hard rule, not a suggestion.

| Situation | MUST do | MUST NOT do |
|----------|---------|-------------|
| New user-facing behavior (new endpoint, new domain capability) | Add a new, narrowly scoped type in the correct layer/package ([MO1b]) | “Just add a method” to an unrelated class ([MO1a], [MO1d]) |
| Bug fix (existing behavior wrong) | Edit the smallest correct owner; add/adjust tests to lock behavior ([MO1f], [TS1a]) | Create a parallel/shadow implementation ([RC1d]) |
| Logic change in stable code | Extract/replace via composition; keep stable code stable ([MO1g]) | Add flags, shims, or “compat” paths to hide uncertainty ([RC1e]) |
| Touching a large/overloaded file | Extract at least one seam (new type + typed contract) ([MO1b], [MO1e]) | Grow the file further ([MO1a]) |
| Reuse needed across features | Add a domain value object / explicit port / explicit service with intent-revealing name ([CS1a], [AR1c], [ND1a]) | Add `*Utils/*Helper/*Common/*Base*` grab bags ([FS1f]) |

### When adding a method is allowed

Adding to an existing type is allowed only when all are true:

- It is the **same responsibility** as the type’s existing purpose ([MO1d]).
- The method’s inputs belong together (avoid data clumps/long parameter lists; extract a parameter record when needed) ([CS1b], [CS1c]).
- The method does not pull in a new dependency direction (dependencies still point inward) ([CC1d]).

If any bullet fails, create a new type and inject it explicitly.

## Create-new-type checklist (before you write code)

1. **Search/reuse first**: confirm a type/pattern doesn’t already exist ([FS1a], [ZA1a]).
2. **Pick the correct layer** (web → use case → domain → adapters/out) ([AR1a]).
3. **Pick the correct feature package** (feature-first, lowercase, singular nouns).
4. **Name by role** (ban generic names; suffix declares meaning) ([ND1a-b]).
5. **Keep the file small** (stay comfortably under 350 LOC; split by concept early) ([LOC1a], [MO1d]).
6. **Add/adjust tests** using existing patterns/utilities ([TS1a], [TS1b]).
7. **Verify** with repo-standard commands (`./gradlew build`, `./gradlew spotlessCheck`) ([VR1a], [VR1c]).

## Repository structure and naming (placement is part of the contract)

### Canonical roots (Java)

Only these root packages are allowed ([AR1a]):

- `com.williamcallahan.applemaps.boot`
- `com.williamcallahan.applemaps.adapters`
- `com.williamcallahan.applemaps.cli`
- `com.williamcallahan.applemaps.domain`

### Feature-first package rule

All layers organize by **feature first**, then by role.

Examples:
- `domain/model/place/...`
- `domain/port/place/...`
- `adapters/mapsserver/...`

### No mixed packages

A package contains either:
- Direct classes only, or
- Subpackages only (plus optional `package-info.java`).

If you need both, insert one more nesting level.

## Domain model hierarchy

- Domain records are immutable value objects.
- Validation happens in constructors.
- Domain → API mapping happens once at the boundary.

## Layer responsibility contract

### Controllers / CLI

Allowed:
- Bind/validate inputs.
- Delegate to use cases/domain services.
- Return domain records.

Prohibited:
- Business logic.

### Domain

Allowed:
- Pure business logic.
- Invariants.

Prohibited:
- Framework imports.
- Persistence details.

## Verification gates (do not skip)

- LOC enforcement: manual check / script ([LOC1c]).
- Build/test/lint: `./gradlew build` ([VR1a]).
