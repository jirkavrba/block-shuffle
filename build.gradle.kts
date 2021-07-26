plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    kotlin("jvm") version "1.5.21"

}

group = "dev.vrba.minecraft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    implementation(kotlin("stdlib-common"))
    implementation(kotlin("stdlib"))
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
}