import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.0"
    `maven-publish`
}

group = "world.estaria"
version = "1.0.1"

repositories {
    mavenCentral()

    // minecraft repositories
    maven("https://repo.papermc.io/repository/maven-public/")

    // estaria dependencies
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/estaria-world/kube-configmap-kit")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    // avionik dependencies
    compileOnly("world.avionik:config-kit:1.0.2")

    // estaria dependencies
    compileOnly("world.estaria:kube-configmap-kit:1.0.1")

    // velocity dependencies
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")

    // kubernetes dependencies
    api("io.fabric8:kubernetes-client:6.12.1")
}

tasks.named("shadowJar", ShadowJar::class) {
    mergeServiceFiles()
}