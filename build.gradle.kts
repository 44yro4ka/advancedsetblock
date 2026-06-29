plugins {
    kotlin("jvm") version "2.0.21"
    id("com.typewritermc.module-plugin") version "1.1.3"
}

group = "com.advancedblocks"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.typewritermc.com/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.opencollab.dev/main/")
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.typewritermc.module-plugin" && requested.name == "extension-processor") {
            useVersion("1.1.3")
        }
    }
    if (name == "compileClasspath") {
        exclude(group = "me.tofaa.entitylib", module = "spigot")
        exclude(group = "org.geysermc.geyser", module = "api")
        exclude(group = "org.geysermc.floodgate", module = "api")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("com.typewritermc:engine-paper:0.8.0")
}

typewriter {
    namespace = "advancedblocks"

    extension {
        name = "AdvancedBlocks"
        shortDescription = "Set sign blocks with facing, NBT data and BlockDisplay support."
        description = "Provides Advanced Set Block entry for Typewriter. Allows setting sign blocks with a custom facing direction, optional NBT data for sign text and colors, and optionally spawning a BlockDisplay entity with a custom item_model component for resource pack visuals."
        engineVersion = "0.8.0"
        channel = com.typewritermc.moduleplugin.ReleaseChannel.NONE
        paper()
    }
}