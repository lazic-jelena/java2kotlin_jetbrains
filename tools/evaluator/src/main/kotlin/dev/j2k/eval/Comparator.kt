package dev.j2k.eval

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.invariantSeparatorsPathString

object Comparator {

    fun evaluate(config: CliConfig): Pair<AggregateScore, List<FileScore>> {
        val javaFiles = Files.walk(config.javaRoot)
            .filter { Files.isRegularFile(it) && it.extension.equals("java", ignoreCase = true) }
            .sorted()
            .toList()

        val fileScores = javaFiles.map { javaFile ->
            val relative = config.javaRoot.relativize(javaFile).invariantSeparatorsPathString
            val generatedFile = config.generatedKotlinRoot.resolve(relative.removeSuffix(".java") + ".kt")
            val baselineFile = config.baselineKotlinRoot.resolve(relative.removeSuffix(".java") + ".kt")

            val javaText = Files.readString(javaFile)
            val generatedExists = Files.exists(generatedFile)
            val baselineExists = Files.exists(baselineFile)
            val generatedText = if (generatedExists) Files.readString(generatedFile) else ""
            val baselineText = if (baselineExists) Files.readString(baselineFile) else ""

            val javaDecls = StructuralExtractors.fromJava(javaText)
            val generatedDecls = if (generatedExists) StructuralExtractors.fromKotlin(generatedText) else Declarations(emptySet(), emptySet(), emptySet())
            val baselineDecls = if (baselineExists) StructuralExtractors.fromKotlin(baselineText) else Declarations(emptySet(), emptySet(), emptySet())

            val declarationRecall = overlapRatio(javaDecls.allNames(), generatedDecls.allNames())
            val baselineOverlap = if (baselineExists) overlapRatio(baselineDecls.allNames(), generatedDecls.allNames()) else 0.0
            val tokenJaccard = if (baselineExists) jaccard(StructuralExtractors.tokenize(generatedText), StructuralExtractors.tokenize(baselineText)) else 0.0

            FileScore(
                javaFile = relative,
                generatedFile = generatedFile.toString(),
                baselineFile = baselineFile.takeIf { baselineExists }?.toString(),
                converted = generatedExists,
                declarationRecallVsJava = declarationRecall,
                memberOverlapVsBaseline = baselineOverlap,
                tokenJaccardVsBaseline = tokenJaccard,
                bangBangCount = "!!".toRegex().findAll(generatedText).count(),
                todoCount = "TODO\\(".toRegex().findAll(generatedText).count(),
            )
        }

        val convertedCount = fileScores.count { it.converted }
        val aggregate = AggregateScore(
            javaFilesDiscovered = fileScores.size,
            filesSuccessfullyConverted = convertedCount,
            conversionSuccessRate = safeRatio(convertedCount, fileScores.size),
            meanDeclarationRecallVsJava = fileScores.map { it.declarationRecallVsJava }.averageOrZero(),
            meanMemberOverlapVsBaseline = fileScores.map { it.memberOverlapVsBaseline }.averageOrZero(),
            meanTokenJaccardVsBaseline = fileScores.map { it.tokenJaccardVsBaseline }.averageOrZero(),
            generatedFilesContainingBangBang = fileScores.count { it.bangBangCount > 0 },
            generatedFilesContainingTodo = fileScores.count { it.todoCount > 0 },
        )

        return aggregate to fileScores
    }

    fun evaluateEdgeCases(edgeCaseRoot: Path, generatedRoot: Path): List<EdgeCaseResult> {
        return Files.walk(edgeCaseRoot)
            .filter { Files.isRegularFile(it) && it.extension.equals("java", ignoreCase = true) }
            .sorted()
            .map { javaFile ->
                val relative = edgeCaseRoot.relativize(javaFile).invariantSeparatorsPathString
                val generatedFile = generatedRoot.resolve(relative.removeSuffix(".java") + ".kt")
                val generated = Files.exists(generatedFile)
                val text = if (generated) Files.readString(generatedFile) else ""

                EdgeCaseResult(
                    caseName = relative,
                    generated = generated,
                    bangBangCount = "!!".toRegex().findAll(text).count(),
                    todoCount = "TODO\\(".toRegex().findAll(text).count(),
                    nestedObjectCount = "object\\s*:".toRegex().findAll(text).count(),
                    notes = when {
                        !generated -> "conversion failed"
                        "!!" in text -> "contains explicit null-assertions"
                        "object :".toRegex().findAll(text).count() >= 2 -> "contains nested anonymous-object style output"
                        else -> "generated successfully; inspect for idiomatic quality"
                    },
                )
            }
            .toList()
    }

    private fun overlapRatio(expected: Set<String>, actual: Set<String>): Double {
        if (expected.isEmpty()) return 1.0
        return expected.intersect(actual).size.toDouble() / expected.size.toDouble()
    }

    private fun jaccard(a: Set<String>, b: Set<String>): Double {
        if (a.isEmpty() && b.isEmpty()) return 1.0
        val union = a union b
        if (union.isEmpty()) return 1.0
        return (a intersect b).size.toDouble() / union.size.toDouble()
    }

    private fun safeRatio(num: Int, den: Int): Double = if (den == 0) 0.0 else num.toDouble() / den.toDouble()
    private fun Iterable<Double>.averageOrZero(): Double = if (none()) 0.0 else average()
}
