package dev.j2k.eval

object StructuralExtractors {

    private val javaTypeRegex = Regex("\\b(class|interface|enum|record)\\s+([A-Za-z_][A-Za-z0-9_]*)")
    private val javaMethodRegex = Regex("(?m)^\\s*(?:public|protected|private|static|final|abstract|synchronized|native|default|strictfp|\\s)+[A-Za-z0-9_<>,\\[\\]?@\\s]+\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\(")
    private val javaFieldRegex = Regex("(?m)^\\s*(?:public|protected|private|static|final|transient|volatile|\\s)+[A-Za-z0-9_<>,\\[\\]?@\\s]+\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*(?:=|;)")

    private val kotlinTypeRegex = Regex("\\b(?:data\\s+class|sealed\\s+class|sealed\\s+interface|enum\\s+class|class|interface|object)\\s+([A-Za-z_][A-Za-z0-9_]*)")
    private val kotlinFunRegex = Regex("\\bfun\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*\\(")
    private val kotlinPropertyRegex = Regex("\\b(?:val|var)\\s+([A-Za-z_][A-Za-z0-9_]*)")

    fun fromJava(text: String): Declarations = Declarations(
        types = javaTypeRegex.findAll(text).map { it.groupValues[2] }.toSet(),
        callables = javaMethodRegex.findAll(text).map { it.groupValues[1] }.filterNot(::isCommonKeyword).toSet(),
        properties = javaFieldRegex.findAll(text).map { it.groupValues[1] }.filterNot(::isCommonKeyword).toSet(),
    )

    fun fromKotlin(text: String): Declarations = Declarations(
        types = kotlinTypeRegex.findAll(text).map { it.groupValues[1] }.toSet(),
        callables = kotlinFunRegex.findAll(text).map { it.groupValues[1] }.filterNot(::isCommonKeyword).toSet(),
        properties = kotlinPropertyRegex.findAll(text).map { it.groupValues[1] }.filterNot(::isCommonKeyword).toSet(),
    )

    fun tokenize(text: String): Set<String> =
        Regex("[A-Za-z_][A-Za-z0-9_]*").findAll(text).map { it.value }.toSet()

    private fun isCommonKeyword(name: String): Boolean = name in setOf(
        "if", "for", "while", "when", "class", "object", "interface", "return", "throw", "catch", "switch"
    )
}
