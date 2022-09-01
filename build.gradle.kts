plugins {
    `java-library`
    `maven-publish`
    id("io.izzel.taboolib") version "1.42"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    install("common")
    install("common-5")
    install("module-kether")
    install("module-ui")
    install("module-nms")
    install("module-nms-util")
    install("module-chat")
    install("module-effect")
    install("module-configuration")
    install("platform-bukkit")
    version = "6.0.9-74"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v11200:11200")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}