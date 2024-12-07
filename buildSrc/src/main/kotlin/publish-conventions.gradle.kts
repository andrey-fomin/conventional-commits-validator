plugins {
    java
    `maven-publish`
    signing
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = project.name
            from(components["java"])
            versionMapping {
                allVariants {
                    fromResolutionResult()
                }
            }
            pom {
                name = "${project.group}:${project.name}"
                description = project.description
                url = "https://github.com/andrey-fomin/conventional-commits-validator"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        name = "Andrey Fomin"
                        email = "andrey.n.fomin@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/andrey-fomin/conventional-commits-validator.git"
                    developerConnection = "scm:git:ssh://github.com:andrey-fomin/conventional-commits-validator.git"
                    url = "https://github.com/andrey-fomin/conventional-commits-validator"
                }
            }
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

signing {
    sign(publishing.publications["maven"])
}
