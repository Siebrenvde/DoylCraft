import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    id("java")
    id("xyz.jpenilla.resource-factory-paper-convention") version "1.2.0"
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
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    compileOnly("com.discordsrv:discordsrv:1.28.0")
    compileOnly("net.essentialsx:EssentialsX:2.20.1") {
        exclude(module = "spigot-api")
    }
    compileOnly("de.maxhenkel.voicechat:voicechat-api:2.5.0")
    compileOnly("de.bluecolored:bluemap-api:2.7.3")
    compileOnly("dev.siebrenvde:ConfigLib:0.3.0-SNAPSHOT")
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
