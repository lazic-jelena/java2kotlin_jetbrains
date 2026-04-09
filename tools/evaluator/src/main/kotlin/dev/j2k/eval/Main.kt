package dev.j2k.eval

import java.nio.file.Path
import kotlin.io.path.Path

fun main(args: Array<String>) {
    val config = parseArgs(args.toList())
    val (aggregate, fileScores) = Comparator.evaluate(config)
    val edgeCaseResults = Comparator.evaluateEdgeCases(config.edgeCaseRoot, config.edgeCaseGeneratedRoot)
    MarkdownReportWriter.write(config.outputDir, aggregate, fileScores, edgeCaseResults)
    println("Wrote reports to ${config.outputDir}")
}

private fun parseArgs(args: List<String>): CliConfig {
    val map = linkedMapOf<String, String>()
    var i = 0
    while (i < args.size) {
        val key = args[i]
        require(key.startsWith("--")) { "Expected --key but got '$key'" }
        require(i + 1 < args.size) { "Missing value for '$key'" }
        map[key.removePrefix("--")] = args[i + 1]
        i += 2
    }

    return CliConfig(
        javaRoot = Path(map.getValue("java-root")),
        generatedKotlinRoot = Path(map.getValue("generated-kotlin-root")),
        baselineKotlinRoot = Path(map.getValue("baseline-kotlin-root")),
        edgeCaseRoot = Path(map.getValue("edge-case-root")),
        edgeCaseGeneratedRoot = Path(map.getValue("edge-case-generated-root")),
        outputDir = Path(map.getValue("output-dir")),
    )
}
