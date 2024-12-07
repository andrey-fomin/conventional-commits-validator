import net.ltgt.gradle.errorprone.errorprone

plugins {
    `java-library`
    antlr
    id("errorprone-conventions")
    id("java-conventions")
    id("spotless-java-conventions")
    id("publish-conventions")
}

dependencies {
    antlr("org.antlr:antlr4:+")
    implementation("org.yaml:snakeyaml:+")
    implementation("org.antlr:antlr4-runtime:+")
    implementation("org.eclipse.jgit:org.eclipse.jgit:+")
}

tasks.generateGrammarSource {
    arguments = arguments + listOf(
        "-package", "io.github.fomin.conventionalcommits.antlr"
    )
    outputDirectory =
        file("${project.layout.buildDirectory.get()}/generated-src/antlr/main/io/github/fomin/conventionalcommits/antlr")
}

tasks.compileJava {
    options.errorprone.excludedPaths.set(".*/antlr/.*\\.java")
}

tasks.sourcesJar {
    dependsOn(tasks.generateGrammarSource)
}

spotless {
    java {
        targetExclude("**/antlr/*.java")
    }
}

publishing {
    repositories {
        maven {
            name = "ossrh"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")

            credentials {
                username = project.findProperty("ossrhUsername").toString()
                password = project.findProperty("ossrhPassword").toString()
            }
        }
    }
}
