---
description: "Apple Maps Java - Clean architecture with strict modern Java standards"
alwaysApply: true
---

# Apple Maps Java Agent Rules

## Document Organization [ORG]

- Purpose: keep every critical rule within the first 100 lines and referenceable by short hashes.
- Structure: Rule Summary first, then detailed sections keyed by 3–4 char hashes.
- Usage: cite hashes when giving guidance or checking compliance.

## Rule Summary [SUM]

- [GT1a-d] Git & Permissions (elevated-only git; no destructive commands)
- [FS1a-g] File Creation & Type Safety (typed records, no maps, no raw types)
- [ND1a-c] Naming Discipline (intent-revealing identifiers only)
- [AB1a-c] Abstraction Discipline (YAGNI; no anemic wrappers; earn reuse)
- [CS1a-f] Code Smells / Clean Code (DRY; primitive obsession; magic literals)
- [RC1a-e] Root Cause Resolution (no fallbacks; no error swallowing; let errors surface)
- [NO1a-e] Null/Optional Discipline (Optional for singletons; empty collections; no null returns)
- [AR1a-d] Architecture & DDD (domain records; ports/adapters; layer boundaries)
- [TS1a-d] Testing Standards (coverage mandatory; observable behavior; refactor-resilient)
- [VR1a-c] Verification Loops (build/test/lint steps)

## [GT1] Git & Permissions

- [GT1a] All git commands require elevated permissions; never run without escalation.
- [GT1b] Never remove `.git/index.lock` automatically—stop and ask the user.
- [GT1c] No destructive git commands (`git restore`, `git reset`, force checkout) unless explicitly ordered.
- [GT1d] Do not skip commit signing or hooks; no `--no-verify`.

## [FS1] File Creation & Type Safety

- [FS1a] Before any new file: search exhaustively for existing logic; reuse or extend if found.
- [FS1b] No `Map<String, Object>`, no raw types, no unchecked casts, no `@SuppressWarnings` in production.
- [FS1c] Every parameter/return type explicit; fix root causes with typed domain records/value objects.
- [FS1d] Single-responsibility methods; no dead code; no empty try/catch blocks.
- [FS1e] Domain has zero framework imports; dependencies point inward.
- [FS1f] No generic utilities: reject `*Utils/*Helper/*Common`; use domain-specific names.
- [FS1g] Domain value types: wrap identifiers, amounts, and values with invariants in records.

## [ND1] Naming Discipline

- [ND1a] No generic identifiers; names must be domain-specific and intent-revealing.
- [ND1b] Banned: `data`, `info`, `value`, `item`, `obj`, `result`, `temp`, `misc`, `foo`, `bar`.
- [ND1c] When legacy code uses generic names, rename them in the same edit.

## [AB1] Abstraction Discipline (YAGNI)

- [AB1a] Avoid anemic wrappers: do not add classes that only forward calls without adding value.
- [AB1b] New abstractions must earn reuse: extend existing code first; add new types only when removing real duplication.
- [AB1c] Delete unused code instead of keeping it "just in case".

## [CS1] Code Smells (DRY / Clean Code)

- [CS1a] Primitive obsession: wrap IDs/amounts/business values in domain types when they carry invariants.
- [CS1b] Data clumps: when 3+ parameters travel together, extract into a record.
- [CS1c] Long parameter lists: methods with >4 parameters must use a parameter object.
- [CS1d] Magic literals: no inline numbers (except 0, 1, -1) or strings; define named constants.
- [CS1e] Comment deodorant: if a comment explains _what_, refactor until self-documenting; comments explain _why_ only.
- [CS1f] DRY: define once, reference everywhere; duplicated logic is a defect.

## [RC1] Root Cause Resolution

- [RC1a] No fallback code that masks issues; no silent degradation (catch-and-log-empty, return-null/empty on failure).
- [RC1b] Investigate → understand → fix; no workarounds. Let errors surface.
- [RC1c] No empty catch blocks; no swallowed exceptions; propagate or handle explicitly.
- [RC1d] One definition only: do not add alternate implementations behind flags/toggles.
- [RC1e] **No compatibility layers, shims, or workarounds — NO EXCEPTIONS.** Fix at the source.

## [NO1] Null/Optional Discipline

- [NO1a] Public methods never return null; singletons use `Optional<T>`; collections return empty, never null.
- [NO1b] Domain models enforce invariants; avoid nullable fields unless business-optional and documented.
- [NO1c] Optional for return types that may be absent; never for parameters in public APIs.
- [NO1d] Use `Optional.map/flatMap/orElseThrow`; avoid `isPresent()/get()` chains.
- [NO1e] Null checks at boundaries only; interior code assumes non-null via constructor validation.

## [AR1] Architecture & DDD

- [AR1a] Layers: `domain/` (pure business logic), `application/` (use cases), `adapters/` (infrastructure), `boot/` (wiring).
- [AR1b] Domain has no framework imports; dependencies point inward (adapters → application → domain).
- [AR1c] Ports define interfaces in domain; adapters implement them in infrastructure.
- [AR1d] Domain records are immutable value objects; validation in constructors; no setters.

## [TS1] Testing Standards

- [TS1a] Test coverage is mandatory: new functionality requires corresponding tests before completion.
- [TS1b] Assert observable behavior, not implementation details; tests coupled to internals are defects.
- [TS1c] Refactor-resilient: if behavior is unchanged, tests must pass regardless of restructuring.
- [TS1d] IT ends with `IT`; unit tests end with `Test`.

## [VR1] Verification Loops

- [VR1a] Build: `./gradlew build`; expect success.
- [VR1b] Tests: `./gradlew test`; targeted runs use `--tests`.
- [VR1c] Lint: `./gradlew spotlessCheck` (if configured).
