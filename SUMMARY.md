# J2K Evaluation Summary

## What the pipeline does

This fork adds a CI pipeline that runs JetBrains static Java-to-Kotlin conversion against the Java Spring PetClinic codebase and evaluates the output using a Kotlin-only analyzer.

Pipeline stages:

1. Build the IntelliJ-based J2K runner.
2. Convert the repository's Java sources under `src/main/java` into Kotlin under `build/j2k/generated`.
3. Clone the public Kotlin PetClinic repository as a comparison baseline.
4. Run the Kotlin evaluator to compute structural similarity metrics.
5. Upload reports as CI artifacts.

## Evaluation methodology

The evaluator intentionally avoids claiming semantic equivalence. Instead, it measures reproducible structural indicators:

- file conversion success rate,
- generated file coverage,
- declared type-name preservation,
- member-name recall from Java to generated Kotlin,
- member overlap with the public Kotlin baseline,
- token-level Jaccard similarity between generated and baseline Kotlin,
- smell counters such as `TODO()` and `!!`.

This is aligned with the assignment's request for comparative analysis using an official Kotlin version or structural heuristics.

## Real-world evaluation results

> Replace this section after the first successful GitHub Actions run using `build/j2k/report/summary.generated.md`.

Suggested table to paste populated values into:

| Metric | Value |
|---|---:|
| Java files discovered | TODO |
| Files successfully converted | TODO |
| Conversion success rate | TODO |
| Mean declaration recall vs Java | TODO |
| Mean member overlap vs Kotlin baseline | TODO |
| Mean token Jaccard vs Kotlin baseline | TODO |
| Generated files containing `!!` | TODO |
| Generated files containing `TODO()` | TODO |

## Initial interpretation template

Use the generated report to write a short interpretation such as:

- J2K handled simple domain entities well.
- Structural recall dropped on framework-heavy classes.
- Nullability markers and Java collection idioms remained the most common cleanup points.
- The official Kotlin baseline was consistently more idiomatic than raw J2K output.

## Known limitations

- Structural heuristics are not a proof of runtime equivalence.
- The public Kotlin PetClinic is not guaranteed to be a line-for-line mechanical translation of the current Java PetClinic revision.
- J2K quality depends on how well the IntelliJ project model resolves symbols.

