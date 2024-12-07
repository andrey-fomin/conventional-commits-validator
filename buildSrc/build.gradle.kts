plugins {
    `kotlin-dsl`
}

apply("src/main/kotlin/dependency-lock-conventions.gradle.kts")

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("net.ltgt.gradle:gradle-errorprone-plugin:+")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:+")
    implementation("net.researchgate:gradle-release:+")
    implementation("com.gradle.publish:plugin-publish-plugin:+")
}
