plugins {
    kotlin("jvm") version "2.1.21"
    application
}

dependencies {
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("dev.j2k.eval.MainKt")
}
