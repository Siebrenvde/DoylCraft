import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml
import java.time.Instant

plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.paperweight)
    alias(libs.plugins.resource.factory)
    alias(libs.plugins.indra.git)
    alias(libs.plugins.blossom)
    alias(libs.plugins.idea.ext)
    alias(libs.plugins.run.paper)
}

group = "dev.siebrenvde"
version = "1.8.2-SNAPSHOT"

val minecraftVersion = "1.21.6"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://maven.enginehub.org/repo/") {
        name = "sk89q-repo"
    }
    maven("https://nexus.scarsz.me/content/groups/public/") {
        name = "Scarsz-Nexus"
    }
    maven("https://repo.essentialsx.net/releases/") {
        name = "essentials"
    }
    maven("https://maven.maxhenkel.de/repository/public") {
        name = "maxhenkel"
    }
    maven("https://repo.bluecolored.de/releases") {
        name = "bluemap"
    }
    maven("https://repo.siebrenvde.dev/releases/") {
        name = "siebrenvde"
    }
}

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
    compileOnlyApi(libs.luckperms)
    compileOnlyApi(libs.worldguard)
    compileOnlyApi(libs.discordsrv)
    compileOnlyApi(libs.essentials) {
        exclude(module = "spigot-api")
    }
    compileOnlyApi(libs.voicechat)
    compileOnlyApi(libs.bluemap)
    compileOnlyApi(libs.configlib)
    compileOnlyApi(libs.j2html)
}

paperPluginYaml {
    main = "dev.siebrenvde.doylcraft.DoylCraft"
    loader = "dev.siebrenvde.doylcraft.paper.DoylCraftPluginLoader"
    author = "Siebrenvde"
    apiVersion = minecraftVersion
    website = "https://github.com/Siebrenvde/DoylCraft"
    dependencies {
        server("LuckPerms", PaperPluginYaml.Load.BEFORE)
        server("WorldGuard", PaperPluginYaml.Load.BEFORE)
        server("DiscordSRV", PaperPluginYaml.Load.BEFORE)
        server("Essentials", PaperPluginYaml.Load.BEFORE)
        server("voicechat", PaperPluginYaml.Load.BEFORE)
        server("BlueMap", PaperPluginYaml.Load.BEFORE)
    }
}

tasks.runServer {
    minecraftVersion(minecraftVersion)
    downloadPlugins {
        modrinth("luckperms", "v5.5.0-bukkit")
        modrinth("worldedit", "DYf6XJqU") // 7.3.15-beta-01
        modrinth("worldguard", "7.0.14")
        modrinth("discordsrv", "1.29.0")
        modrinth("essentialsx", "2.21.1")
        modrinth("simple-voice-chat", "bukkit-2.5.31")
        modrinth("bluemap", "5.9-paper")
    }
}

sourceSets.main {
    blossom.javaSources {
        property("version", project.version.toString())
        property("configLibVersion", libs.configlib.get().version)
        property("j2htmlVersion", libs.j2html.get().version)
        property("buildTime", Instant.now().toString())
        property("isCI", (System.getenv("CI") != null).toString())
        property("gitBranch", indraGit.branchName())
        val commit = indraGit.git()?.log()?.call()?.first()
        property("gitCommitHash", commit?.name())
        property("gitCommitMessage", commit?.shortMessage?.replace("\"", "\\\""))
        property("gitCommitAuthor", commit?.authorIdent?.name)
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
        repositories.maven {
            val repo = if (version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"
            url = uri("https://repo.siebrenvde.dev/${repo}/")
            name = "siebrenvde"
            credentials(PasswordCredentials::class)
        }
    }
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        @Suppress("UnstableApiUsage")
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}
