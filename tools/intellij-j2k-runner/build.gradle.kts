plugins {
    kotlin("jvm") version "2.1.21"
    id("org.jetbrains.intellij") version "1.17.4"
}

repositories {
    mavenCentral()
}

intellij {
    version.set("2024.3")
    type.set("IC")
    plugins.set(listOf("org.jetbrains.kotlin"))
}

dependencies {
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(21)
}

tasks {
    patchPluginXml {
        sinceBuild.set("243")
        untilBuild.set("243.*")
    }

    runIde {
        jvmArgs("-Djava.awt.headless=true")
        systemProperty("idea.auto.reload.plugins", "false")
    }
}
