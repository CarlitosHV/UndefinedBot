plugins {
    id("java")
    id("com.gradleup.shadow") version "9.2.0"
}

group = "com.undefined"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://maven.topi.wtf/releases")
    maven("https://maven.lavalink.dev/releases")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("net.dv8tion:JDA:6.2.0")
    implementation("dev.arbjerg:lavaplayer:2.2.6")
    implementation("com.github.topi314.lavasrc:lavasrc:4.8.1")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("dev.lavalink.youtube:v2:1.16.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    shadowJar {
        manifest {
            attributes["Main-Class"] = "com.undefined.Startup"
        }
        archiveBaseName.set("undefined-bot")
        archiveClassifier.set("")
        archiveVersion.set("1.0")
    }
}

tasks.test {
    useJUnitPlatform()
}
