plugins {
    id("java")
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

    implementation("net.dv8tion:JDA:5.0.0-beta.24")
    implementation("dev.arbjerg:lavaplayer:2.2.2")
    implementation("com.github.topi314.lavasrc:lavasrc:4.8.1")
    implementation("org.slf4j:slf4j-simple:2.0.12")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.test {
    useJUnitPlatform()
}
