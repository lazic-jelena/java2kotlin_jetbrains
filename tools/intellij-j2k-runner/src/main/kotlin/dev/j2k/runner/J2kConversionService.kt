package dev.j2k.runner

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiManager
import org.jetbrains.kotlin.j2k.ConverterSettings
import org.jetbrains.kotlin.j2k.JavaToKotlinConverter
import org.jetbrains.kotlin.j2k.J2kPostProcessor
import org.jetbrains.kotlin.j2k.idea.IdeaReferenceSearcher
import org.jetbrains.kotlin.j2k.idea.IdeaResolverForConverter
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.extension
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.nameWithoutExtension

class J2kConversionService {

    fun convertTree(projectRoot: Path, sourceRoot: Path, outputRoot: Path, project: com.intellij.openapi.project.Project): List<ConversionResult> {
        if (!Files.exists(sourceRoot)) {
            error("Source root does not exist: $sourceRoot")
        }

        outputRoot.createDirectories()

        val converter = JavaToKotlinConverter(
            project,
            ConverterSettings.defaultSettings,
            IdeaReferenceSearcher,
            IdeaResolverForConverter,
        )

        val results = mutableListOf<ConversionResult>()

        Files.walk(sourceRoot)
            .filter { Files.isRegularFile(it) && it.extension.equals("java", ignoreCase = true) }
            .sorted()
            .forEach { javaFile ->
                val relative = sourceRoot.relativize(javaFile)
                val target = outputRoot.resolve(relative.invariantSeparatorsPathString.removeSuffix(".java") + ".kt")
                target.parent?.createDirectories()

                val conversion = try {
                    val kotlinText = convertSingle(project, javaFile, converter)
                    Files.writeString(target, kotlinText)
                    ConversionResult(javaFile = javaFile, kotlinFile = target, success = true)
                } catch (t: Throwable) {
                    ConversionResult(
                        javaFile = javaFile,
                        kotlinFile = target,
                        success = false,
                        error = t.stackTraceToString(),
                    )
                }

                results += conversion
            }

        return results
    }

    private fun convertSingle(
        project: com.intellij.openapi.project.Project,
        javaFile: Path,
        converter: JavaToKotlinConverter,
    ): String {
        val vFile = LocalFileSystem.getInstance().refreshAndFindFileByNioFile(javaFile)
            ?: error("Unable to resolve VFS file for $javaFile")

        val psiJavaFile = ApplicationManager.getApplication().runReadAction<PsiJavaFile> {
            val psi = PsiManager.getInstance(project).findFile(vFile)
                ?: error("No PSI file for $javaFile")
            psi as? PsiJavaFile ?: error("Not a PsiJavaFile: $javaFile")
        }

        val postProcessor = J2kPostProcessor(formatCode = true)
        val converted = converter.filesToKotlin(listOf(psiJavaFile), postProcessor)
        return converted.results.singleOrNull()
            ?: error("Expected single J2K result for ${javaFile.nameWithoutExtension}")
    }
}
