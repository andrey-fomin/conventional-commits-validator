plugins {
    `java-gradle-plugin`
    id("java-conventions")
    id("spotless-java-conventions")
    id("com.gradle.plugin-publish")
}

gradlePlugin {
    plugins {
        create("conventionalCommitsPlugin") {
            id = "io.github.fomin.conventional-commits"
            implementationClass = "io.github.fomin.conventionalcommits.ConventionalCommitsGradlePlugin"
            displayName = "Gradle Conventional Commits Plugin"
            description = "Gradle plugin for conventional commits validation"
            website = "https://github.com/andrey-fomin/conventional-commits-validator"
            vcsUrl = "https://github.com/andrey-fomin/conventional-commits-validator"
            tags = listOf("conventional-commits", "git", "commit")
        }
    }
}

repositories {
    mavenLocal()
}

dependencies {
    compileOnly(project(":validator-jre"))
}

tasks.test {
    dependsOn(":validator-jre:publishToMavenLocal")
}

val versionSourcesDir = layout.buildDirectory.dir("generated-sources/version/java")

val generateVersionJavaFile by tasks.registering {
    inputs.property("version", rootProject.version)
    outputs.dir(versionSourcesDir)
    doLast {
        var fileDir = versionSourcesDir.get().file("io/github/fomin/conventionalcommits").asFile
        fileDir.mkdirs()
        val file = fileDir.resolve("Version.java")
        file.writeText(
            """
            package io.github.fomin.conventionalcommits;

            public final class Version {
              private Version() {}
            
              public static final String VERSION = "${rootProject.version}";
            }
            
            """.trimIndent()
        )
    }
}

tasks.compileJava {
    dependsOn(generateVersionJavaFile)
}

tasks.spotlessJava {
    dependsOn(generateVersionJavaFile)
}

tasks.withType<Jar> {
    dependsOn(generateVersionJavaFile)
}

sourceSets {
    main {
        java {
            srcDir(versionSourcesDir)
        }
    }
}
