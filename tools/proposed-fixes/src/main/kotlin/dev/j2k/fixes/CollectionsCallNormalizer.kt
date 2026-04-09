package dev.j2k.fixes

/**
 * Example post-processing fix proposal written in Kotlin.
 *
 * Motivation:
 * Static J2K output is often structurally correct but can remain Java-shaped.
 * One low-risk cleanup is normalizing obvious java.util.Collections factory calls
 * into idiomatic Kotlin collection factories.
 */
object CollectionsCallNormalizer {

    fun normalize(kotlinText: String): String {
        return kotlinText
            .replace(Regex("java\\.util\\.Collections\\.emptyList<([^>]+)>\\(\\)"), "emptyList<$1>()")
            .replace(Regex("java\\.util\\.Collections\\.emptySet<([^>]+)>\\(\\)"), "emptySet<$1>()")
            .replace(Regex("java\\.util\\.Collections\\.emptyMap<([^,]+),\\s*([^>]+)>\\(\\)"), "emptyMap<$1, $2>()")
    }
}
