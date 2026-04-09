package dev.j2k.runner

import java.nio.file.Path

data class ConversionResult(
    val javaFile: Path,
    val kotlinFile: Path,
    val success: Boolean,
    val error: String? = null,
)
