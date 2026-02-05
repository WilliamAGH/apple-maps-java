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

- [ZA1a-d] Zero Tolerance Policy (zero assumptions, validation, forbidden practices, dependency verification)
- [GT1a-h] Git & Permissions (elevated-only git; no destructive commands)
- [CC1a-d] Clean Code & DDD (Mandatory)
- [ID1a-d] Idiomatic Patterns & Defaults
- [DS1a-e] Dependency Source Verification
- [FS1a-h] File Creation & Type Safety (typed records, no maps, no raw types)
- [LOC1a-e] Line Count Ceiling (350 lines max; SRP enforcer; zero tolerance)
- [MO1a-g] No Monoliths (Strict SRP; Decision Logic; Extension/OCP)
- [ND1a-c] Naming Discipline (intent-revealing identifiers only)
- [AB1a-c] Abstraction Discipline (YAGNI; no anemic wrappers; earn reuse)
- [CS1a-f] Code Smells / Clean Code (DRY; primitive obsession; magic literals)
- [RC1a-e] Root Cause Resolution (no fallbacks; no error swallowing; let errors surface)
- [NO1a-e] Null/Optional Discipline (Optional for singletons; empty collections; no null returns)
- [AR1a-d] Architecture & DDD (domain records; ports/adapters; layer boundaries)
- [TS1a-d] Testing Standards (coverage mandatory; observable behavior; refactor-resilient)
- [VR1a-c] Verification Loops (build/test/lint steps)

## [ZA1] Zero Tolerance Policy

- [ZA1a] **Zero Assumptions**: Do not assume behavior, APIs, or versions. Verify in the codebase/docs first.
- [ZA1b] **Source Verification**: For dependency code questions, inspect `~/.m2` JARs or `~/.gradle/caches/` first; fallback to upstream GitHub; never answer without referencing code.
- [ZA1c] **Forbidden Practices**:
  - No `Map<String, Object>`, raw types, unchecked casts, `@SuppressWarnings`, or `eslint-disable` in production.
  - No trusting memory—verify every import/API/config against current docs.
- [ZA1d] **Mandatory Research**: You MUST research dependency questions and correct usage. Never use legacy or `@deprecated` usage from dependencies. Ensure correct usage by reviewing related code directly in `node_modules` or Gradle caches and using online tool calls.

## [GT1] Git & Permissions

- [GT1a] All git commands require elevated permissions; never run without escalation.
- [GT1b] Never remove `.git/index.lock` automatically—stop and ask the user or seek explicit approval.
- [GT1c] Read-only git commands (e.g., `git status`, `git diff`, `git log`, `git show`) never require permission. Any git command that writes to the working tree, index, or history requires explicit permission.
- [GT1d] Do not skip commit signing or hooks; no `--no-verify`. No `Co-authored-by` or AI attribution.
- [GT1e] Destructive git commands are prohibited unless explicitly ordered by the user (e.g., `git restore`, `git reset`, force checkout).
- [GT1f] Treat existing staged/unstaged changes as intentional unless the user says otherwise; never “clean up” someone else’s work unprompted.
- [GT1g] Examples of write operations that require permission: `git add`, `git commit`, `git checkout`, `git merge`, `git rebase`, `git reset`, `git restore`, `git clean`, `git cherry-pick`.
- [GT1h] When in doubt whether a git command writes, treat it as write and request explicit approval.

## [CC1] Clean Code & DDD (Mandatory)

- [CC1a] **Mandatory Principles**: Clean Code principles (Robert C. Martin) and Domain-Driven Design (DDD) are **mandatory** and required in this repository.
- [CC1b] **DRY (Don't Repeat Yourself)**: Avoid redundant code. Reuse code where appropriate and consistent with clean code principles.
- [CC1c] **YAGNI (You Aren't Gonna Need It)**: Do not build features or abstractions "just in case". Implement only what is required for the current task.
- [CC1d] **Clean Architecture**: Dependencies point inward. Domain logic has zero framework imports.

## [ID1] Idiomatic Patterns & Defaults

- [ID1a] **Defaults First**: Always prefer the idiomatic, expected, and default patterns provided by the framework, library, or SDK (Java 21+, etc.).
- [ID1b] **Custom Justification**: Custom implementations require a compelling reason. If you can't justify it, use the standard way.
- [ID1c] **No Reinventing**: Do not build custom utilities for things the platform already does.
- [ID1d] **Dependencies**: Make careful use of dependencies. Do not make assumptions—use the correct idiomatic behavior to avoid boilerplate.

## [DS1] Dependency Source Verification

- [DS1a] **Locate**: Find source JARs in Gradle cache: `find ~/.gradle/caches/modules-2/files-2.1 -name "*-sources.jar" | grep <artifact>`.
- [DS1b] **List**: View JAR contents without extraction: `unzip -l <jar_path> | grep <ClassName>`.
- [DS1c] **Read**: Pipe specific file content to stdout: `unzip -p <jar_path> <internal/path/to/Class.java>`.
- [DS1d] **Search**: To use `ast-grep` on dependencies, pipe content directly: `unzip -p <jar> <file> | ast-grep run --pattern '...' --lang java --stdin`. No temp files required.
- [DS1e] **Efficiency**: Do not extract full JARs. Use CLI piping for instant access.

## [FS1] File Creation & Type Safety

- [FS1a] Before any new file: search exhaustively for existing logic; reuse or extend if found.
- [FS1b] No `Map<String, Object>`, no raw types, no unchecked casts, no `@SuppressWarnings` in production.
- [FS1c] Every parameter/return type explicit; fix root causes with typed domain records/value objects.
- [FS1d] Single-responsibility methods; no dead code; no empty try/catch blocks.
- [FS1e] Domain has zero framework imports; dependencies point inward.
- [FS1f] No generic utilities: reject `*Utils/*Helper/*Common`; use domain-specific names.
- [FS1g] Domain value types: wrap identifiers, amounts, and values with invariants in records.
- [FS1h] File size discipline: see [LOC1a] and [MO1a].

## [LOC1] Line Count Ceiling (Repo-Wide)

- [LOC1a] All written, non-generated source files in this repository MUST be <= 350 lines (`wc -l`), including `AGENTS.md`
- [LOC1b] SRP Enforcer: This 350-line "stick" forces modularity (DDD/SRP); > 350 lines = too many responsibilities (see [MO1d])
- [LOC1c] Zero Tolerance: No edits allowed to files > 350 LOC (even legacy); you MUST split/retrofit before applying your change
- [LOC1d] Enforcement: run line count checks and treat failures as merge blockers
- [LOC1e] Exempt files: generated content, lockfiles, and large example/data dumps

## [MO1] No Monoliths

- [MO1a] No monoliths: avoid multi-concern files and catch-all modules
- [MO1b] New work starts in new files; when touching a monolith, extract at least one seam
- [MO1c] If safe extraction impossible, halt and ask
- [MO1d] Strict SRP: each unit serves one actor; separate logic that changes for different reasons
- [MO1e] Boundary rule: cross-module interaction happens only through explicit, typed contracts with dependencies pointing inward; don’t reach into other modules’ internals or mix web/use-case/domain/persistence concerns in one unit
- [MO1f] Decision Logic: New feature → New file; Bug fix → Edit existing; Logic change → Extract/Replace
- [MO1g] Extension (OCP): Add functionality via new classes/composition; do not modify stable code to add features
-   Contract: `docs/contracts/code-change.md`

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
