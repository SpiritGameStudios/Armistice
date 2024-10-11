plugins {
    `java-library`
    alias(libs.plugins.moddevgradle)
}

class ModInfo {
    val id = property("mod.id").toString()
    val group = property("mod.group").toString()
    val version = property("mod.version").toString()
    val name = property("mod.name").toString()
    val description = property("mod.description").toString()
    val authors = property("mod.authors").toString()
    val license = property("mod.license").toString()
}

val mod = ModInfo()

val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version" to libs.versions.minecraft.asProvider().get(),
        "minecraft_version_range" to libs.versions.minecraft.range.get(),
        "neo_version" to libs.versions.neoforge.asProvider().get(),
        "neo_version_range" to libs.versions.neoforge.range.get(),
        "loader_version_range" to libs.versions.loader.range.get(),
        "mod_id" to mod.id,
        "mod_name" to mod.name,
        "mod_license" to mod.license,
        "mod_version" to mod.version,
        "mod_authors" to mod.authors,
        "mod_description" to mod.description
    )

    inputs.properties(replaceProperties)
    expand(replaceProperties)

    from("src/main/templates")
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}


sourceSets.main.get().resources.srcDir(generateModMetadata)

version = mod.version
group = mod.group
base.archivesName.set(mod.id)

repositories {
    mavenLocal()
}


java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    version = libs.versions.neoforge.asProvider().get()

    parchment {
        mappingsVersion = libs.versions.parchment.get()
        minecraftVersion = libs.versions.minecraft.asProvider().get()
    }

    runs {
        register("client") {
            client()

            systemProperty("neoforge.enabledGameTestNamespaces", mod.id)
        }

        register("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", mod.id)
        }

        register("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", mod.id)
        }

        register("data") {
            data()

            programArguments.addAll(
                "--mod",
                mod.id,
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(mod.id) {
            sourceSet(sourceSets.main.get())
        }
    }

    ideSyncTask(generateModMetadata)
}

sourceSets.main.get().resources.srcDir("src/generated/resources")

dependencies {

}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.named("neoForgeIdeSync") {
    dependsOn(generateModMetadata)
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
