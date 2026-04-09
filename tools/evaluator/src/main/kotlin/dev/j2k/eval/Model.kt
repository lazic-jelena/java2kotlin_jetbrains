package dev.j2k.eval

import java.nio.file.Path

data class CliConfig(
    val javaRoot: Path,
    val generatedKotlinRoot: Path,
    val baselineKotlinRoot: Path,
    val edgeCaseRoot: Path,
    val edgeCaseGeneratedRoot: Path,
    val outputDir: Path,
)

data class Declarations(
    val types: Set<String>,
    val callables: Set<String>,
    val properties: Set<String>,
) {
    fun allNames(): Set<String> = types + callables + properties
}

data class FileScore(
    val javaFile: String,
    val generatedFile: String,
    val baselineFile: String?,
    val converted: Boolean,
    val declarationRecallVsJava: Double,
    val memberOverlapVsBaseline: Double,
    val tokenJaccardVsBaseline: Double,
    val bangBangCount: Int,
    val todoCount: Int,
)

data class AggregateScore(
    val javaFilesDiscovered: Int,
    val filesSuccessfullyConverted: Int,
    val conversionSuccessRate: Double,
    val meanDeclarationRecallVsJava: Double,
    val meanMemberOverlapVsBaseline: Double,
    val meanTokenJaccardVsBaseline: Double,
    val generatedFilesContainingBangBang: Int,
    val generatedFilesContainingTodo: Int,
)

data class EdgeCaseResult(
    val caseName: String,
    val generated: Boolean,
    val bangBangCount: Int,
    val todoCount: Int,
    val nestedObjectCount: Int,
    val notes: String,
)
