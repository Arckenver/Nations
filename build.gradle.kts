import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.0.0"
    id("io.papermc.paperweight.userdev") version "1.7.7"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "com.arckenver"
version = "3.0-SNAPSHOT"

paperweight.reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("com.cronutils:cron-utils:9.2.0")

    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.ElgarL:groupmanager:3.2")
    compileOnly("xyz.jpenilla:squaremap-api:1.3.3")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
    implementation("net.kyori:adventure-text-serializer-gson:4.17.0")

    testImplementation(kotlin("test"))
    testImplementation("se.lovef:kotlin-assert-utils:0.10.1")
}

bukkit {
    main = "com.arckenver.nations.bukkit.NationsPlugin"
    apiVersion = "1.21"
    authors = listOf("Arckenver")
    website = "https://github.com/Arckenver/Nations"
    depend = listOf("Vault")
    commands {
        register("nation") {
            description = ""
            aliases = listOf("n")
            permission = "nations.command.nation"
            usage = ""
        }
        register("zone") {
            description = ""
            aliases = listOf("z")
            permission = "nations.command.zone"
            usage = ""
        }
        register("nationadmin") {
            description = ""
            aliases = listOf("na")
            permission = "nations.command.nationadmin"
            usage = ""
        }
    }
    permissions {
        register("nations.command.nation") {
            description = "Allows using all nation commands"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("nations.command.zone") {
            description = "Allows using all zone commands"
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("nations.command.nationadmin") {
            description = "Allows using all nationadmin commands"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("nations.admin.bypass.build") {
            description = "Allows bypassing build protection on all territories"
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("nations.admin.bypass.interact") {
            description = "Allows bypassing interaction protection on all territories"
            default = BukkitPluginDescription.Permission.Default.OP
        }
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
