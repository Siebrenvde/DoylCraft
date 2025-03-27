import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("java")
    alias(libs.plugins.paperweight)
    alias(libs.plugins.resource.factory)
    alias(libs.plugins.indra.git)
    alias(libs.plugins.blossom)
    alias(libs.plugins.idea.ext)
}

group = "dev.siebrenvde"
version = "1.7.0"

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
    maven("https://repo.siebrenvde.dev/snapshots/") { // TODO: Change back to releases
        name = "siebrenvde"
    }
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)
    compileOnly(libs.luckperms)
    compileOnly(libs.worldguard)
    compileOnly(libs.discordsrv)
    compileOnly(libs.essentials) {
        exclude(module = "spigot-api")
    }
    compileOnly(libs.voicechat)
    compileOnly(libs.bluemap)
    compileOnly(libs.configlib)
}

paperPluginYaml {
    main = "dev.siebrenvde.doylcraft.DoylCraft"
    loader = "dev.siebrenvde.doylcraft.paper.DoylCraftPluginLoader"
    author = "Siebrenvde"
    apiVersion = "1.21"
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

sourceSets.main {
    blossom.javaSources {
        property("version", project.version.toString())
        property("gitCommit", indraGit.commit()?.name())
        property("gitBranch", indraGit.branchName())
        property("configLibVersion", libs.configlib.get().version)
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
