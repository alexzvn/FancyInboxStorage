import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

java.sourceCompatibility = JavaVersion.VERSION_16

group = "dev.alexzvn.inboxstorage"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.xenondevs.xyz/releases")
    maven("https://repo.loohpjames.com/repository")
}

dependencies {
    testImplementation(kotlin("test"))

    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.loohp:InteractiveChatDiscordSrvAddon:4.2.11.0")
    compileOnly("com.loohp:InteractiveChat:4.2.11.0") {
        exclude("net.kyori")
    }

    implementation("org.apache.httpcomponents:fluent-hc:4.5.13")
    implementation("org.apache.httpcomponents:httpmime:4.3.1")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.github.hamza-cskn.obliviate-invs:core:4.3.0")
    implementation("com.github.hamza-cskn.obliviate-invs:pagination:4.3.0")
    implementation(files("libs/drink-1.0.5.jar"))

    shadow("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
        kotlinOptions.jvmTarget = "1.8"
    }

    processResources {
        expand(project.properties)
    }

    shadowJar {
        archiveBaseName.set("FancyInboxStorage")
        archiveClassifier.set("")
        archiveVersion.set(project.version.toString())

        minimize()

        manifest {
            attributes(mapOf("Main-Class" to "$group/Main"))
        }
    }
}

tasks.build {
    dependsOn("shadowJar")
}