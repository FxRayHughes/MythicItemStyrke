plugins {
    java
    id("io.izzel.taboolib") version "1.30"
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
    version = "6.0.3-9"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v11600:11600:all")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}