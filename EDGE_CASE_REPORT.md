# Edge-Case Dataset Report

## Purpose

This report documents a supplementary Java dataset designed to stress-test static J2K beyond the main repository benchmark.

## Dataset files

| File | Focus area | Hypothesis |
|---|---|---|
| `NestedAnonymousClasses.java` | anonymous classes, callbacks | H1 |
| `RawTypesAndWildcards.java` | generic erasure and wildcards | H2 |
| `SpringAnnotationsAndInjection.java` | framework annotations and injection style | H3 |
| `CheckedExceptionsInCallbacks.java` | checked exceptions in callback code | H4 |

## How to update this report after CI

After the workflow runs, inspect `build/j2k/report/edgecases.generated.md` from the workflow artifact and paste concrete findings here.

## Result template

| Case | Passed? | Observed output issue | Notes |
|---|---|---|---|
| Nested anonymous classes | TODO | TODO | TODO |
| Raw types and wildcards | TODO | TODO | TODO |
| Spring annotations and injection | TODO | TODO | TODO |
| Checked exceptions in callbacks | TODO | TODO | TODO |

## Expected failure themes

These are the most likely failure patterns the dataset is designed to reveal:

- deep `object : Interface { ... }` nesting,
- weak generic typing or noisy wildcard translation,
- non-idiomatic constructor/property initialization in Spring classes,
- callback code that remains Java-shaped even after successful conversion.

## Why this dataset matters

The main benchmark tells us whether J2K can convert a real application. The edge-case dataset tells us **where it breaks** in a controlled and explainable way.

