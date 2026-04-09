# Hypotheses Tested Against Static J2K

This file documents explicit hypotheses that the evaluation pipeline is designed to test.

## H1 — Nested anonymous classes will degrade readability and structure

**Reasoning:** J2K often has to map anonymous Java classes into nested Kotlin `object : ...` expressions. Deep nesting is mechanically valid but tends to produce hard-to-read output.

**How tested:** Custom edge-case file `NestedAnonymousClasses.java` and structural smell inspection in generated output.

## H2 — Raw types and wildcard-heavy generics will weaken type quality

**Reasoning:** Java raw types and wildcard bounds are common places where the converter cannot infer ideal Kotlin variance or nullability.

**How tested:** `RawTypesAndWildcards.java`; evaluator counts broad or weak generic signatures and manual inspection.

## H3 — Spring-style annotation-heavy classes will convert structurally but not idiomatically

**Reasoning:** Framework annotations are usually preserved, but constructor injection, nullability, and bean wiring style often remain Java-shaped after conversion.

**How tested:** Real-world Spring PetClinic controllers/services plus `SpringAnnotationsAndInjection.java`.

## H4 — Checked-exception-oriented APIs will stay awkward after conversion

**Reasoning:** Kotlin has no checked exceptions, but converted APIs may still carry Java ceremony or lambda patterns that remain non-idiomatic.

**How tested:** `CheckedExceptionsInCallbacks.java`.

## H5 — J2K will preserve names better than behavior-specific idioms

**Reasoning:** Static conversion should preserve class/member names relatively well, but Kotlin idioms (properties, extension functions, collection idioms, null-safety) will lag behind.

**How tested:** Aggregate declaration recall, baseline overlap, and token similarity metrics.

