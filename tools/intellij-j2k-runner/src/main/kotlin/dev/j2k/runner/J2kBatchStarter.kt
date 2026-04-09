package dev.j2k.runner

import com.intellij.ide.impl.ProjectUtil
import com.intellij.ide.impl.OpenProjectTask
import com.intellij.openapi.application.ApplicationStarter
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

class J2kBatchStarter : ApplicationStarter {

    override fun getCommandName(): String = "j2k-batch"

    override fun isHeadless(): Boolean = true

    override fun main(args: List<String>) {
        val parsed = parseArgs(args.drop(1))

        val projectRoot = Path(parsed.getValue("project-root"))
        val sourceRoot = Path(parsed.getValue("source-root"))
        val outputRoot = Path(parsed.getValue("output-root"))

        val openedProject = ProjectUtil.openOrImport(projectRoot, OpenProjectTask())
            ?: error("Unable to open IntelliJ project at $projectRoot")

        val results = J2kConversionService().convertTree(
            projectRoot = projectRoot,
            sourceRoot = sourceRoot,
            outputRoot = outputRoot,
            project = openedProject,
        )

        val manifest = buildString {
            appendLine("java_file,kotlin_file,success,error")
            results.forEach { result ->
                appendLine(
                    listOf(
                        result.javaFile.toString(),
                        result.kotlinFile.toString(),
                        result.success.toString(),
                        csvEscape(result.error.orEmpty()),
                    ).joinToString(",")
                )
            }
        }

        outputRoot.createDirectories()
        outputRoot.resolve("conversion-manifest.csv").writeText(manifest)

        val failures = results.count { !it.success }
        println("J2K completed. Converted=${results.size - failures}, failed=$failures, total=${results.size}")

        if (failures > 0) {
            error("J2K failed for $failures file(s). See conversion-manifest.csv")
        }
    }

    private fun parseArgs(args: List<String>): Map<String, String> {
        val map = linkedMapOf<String, String>()
        var i = 0
        while (i < args.size) {
            val key = args[i]
            require(key.startsWith("--")) { "Expected --key argument but got '$key'" }
            require(i + 1 < args.size) { "Missing value for argument '$key'" }
            map[key.removePrefix("--")] = args[i + 1]
            i += 2
        }
        return map
    }

    private fun csvEscape(value: String): String =
        if (value.contains(',') || value.contains('"') || value.contains('\n')) {
            '"' + value.replace("\"", "\"\"") + '"'
        } else {
            value
        }
}
