package dev.j2k.eval

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

object MarkdownReportWriter {

    fun write(outputDir: Path, aggregate: AggregateScore, files: List<FileScore>, edgeCases: List<EdgeCaseResult>) {
        outputDir.createDirectories()
        outputDir.resolve("summary.generated.md").writeText(summaryMarkdown(aggregate))
        outputDir.resolve("edgecases.generated.md").writeText(edgeCasesMarkdown(edgeCases))
        outputDir.resolve("report.json").writeText(reportJson(aggregate, files, edgeCases))
    }

    private fun summaryMarkdown(aggregate: AggregateScore): String = buildString {
        appendLine("# Generated J2K Summary")
        appendLine()
        appendLine("| Metric | Value |")
        appendLine("|---|---:|")
        appendLine("| Java files discovered | ${aggregate.javaFilesDiscovered} |")
        appendLine("| Files successfully converted | ${aggregate.filesSuccessfullyConverted} |")
        appendLine("| Conversion success rate | ${format(aggregate.conversionSuccessRate)} |")
        appendLine("| Mean declaration recall vs Java | ${format(aggregate.meanDeclarationRecallVsJava)} |")
        appendLine("| Mean member overlap vs Kotlin baseline | ${format(aggregate.meanMemberOverlapVsBaseline)} |")
        appendLine("| Mean token Jaccard vs Kotlin baseline | ${format(aggregate.meanTokenJaccardVsBaseline)} |")
        appendLine("| Generated files containing !! | ${aggregate.generatedFilesContainingBangBang} |")
        appendLine("| Generated files containing TODO() | ${aggregate.generatedFilesContainingTodo} |")
    }

    private fun edgeCasesMarkdown(results: List<EdgeCaseResult>): String = buildString {
        appendLine("# Generated Edge-Case Report")
        appendLine()
        appendLine("| Case | Generated? | `!!` count | `TODO()` count | `object :` count | Notes |")
        appendLine("|---|---|---:|---:|---:|---|")
        results.forEach { result ->
            appendLine("| ${result.caseName} | ${result.generated} | ${result.bangBangCount} | ${result.todoCount} | ${result.nestedObjectCount} | ${result.notes} |")
        }
    }

    private fun reportJson(aggregate: AggregateScore, files: List<FileScore>, edgeCases: List<EdgeCaseResult>): String = buildString {
        appendLine("{")
        appendLine("  \"aggregate\": {")
        appendLine("    \"javaFilesDiscovered\": ${aggregate.javaFilesDiscovered},")
        appendLine("    \"filesSuccessfullyConverted\": ${aggregate.filesSuccessfullyConverted},")
        appendLine("    \"conversionSuccessRate\": ${aggregate.conversionSuccessRate},")
        appendLine("    \"meanDeclarationRecallVsJava\": ${aggregate.meanDeclarationRecallVsJava},")
        appendLine("    \"meanMemberOverlapVsBaseline\": ${aggregate.meanMemberOverlapVsBaseline},")
        appendLine("    \"meanTokenJaccardVsBaseline\": ${aggregate.meanTokenJaccardVsBaseline},")
        appendLine("    \"generatedFilesContainingBangBang\": ${aggregate.generatedFilesContainingBangBang},")
        appendLine("    \"generatedFilesContainingTodo\": ${aggregate.generatedFilesContainingTodo}")
        appendLine("  },")
        appendLine("  \"files\": [")
        files.forEachIndexed { index, score ->
            appendLine("    {")
            appendLine("      \"javaFile\": \"${escape(score.javaFile)}\",")
            appendLine("      \"generatedFile\": \"${escape(score.generatedFile)}\",")
            appendLine("      \"baselineFile\": ${score.baselineFile?.let { "\"${escape(it)}\"" } ?: "null"},")
            appendLine("      \"converted\": ${score.converted},")
            appendLine("      \"declarationRecallVsJava\": ${score.declarationRecallVsJava},")
            appendLine("      \"memberOverlapVsBaseline\": ${score.memberOverlapVsBaseline},")
            appendLine("      \"tokenJaccardVsBaseline\": ${score.tokenJaccardVsBaseline},")
            appendLine("      \"bangBangCount\": ${score.bangBangCount},")
            appendLine("      \"todoCount\": ${score.todoCount}")
            append("    }")
            if (index != files.lastIndex) append(',')
            appendLine()
        }
        appendLine("  ],")
        appendLine("  \"edgeCases\": [")
        edgeCases.forEachIndexed { index, result ->
            appendLine("    {")
            appendLine("      \"caseName\": \"${escape(result.caseName)}\",")
            appendLine("      \"generated\": ${result.generated},")
            appendLine("      \"bangBangCount\": ${result.bangBangCount},")
            appendLine("      \"todoCount\": ${result.todoCount},")
            appendLine("      \"nestedObjectCount\": ${result.nestedObjectCount},")
            appendLine("      \"notes\": \"${escape(result.notes)}\"")
            append("    }")
            if (index != edgeCases.lastIndex) append(',')
            appendLine()
        }
        appendLine("  ]")
        appendLine("}")
    }

    private fun format(value: Double): String = "%.4f".format(value)
    private fun escape(value: String): String = value.replace("\\", "\\\\").replace("\"", "\\\"")
}
