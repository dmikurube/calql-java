plugins {
    id("java")
    id("maven-publish")
    id("signing")
    id("checkstyle")
}

repositories {
    mavenCentral()
}

configurations {
    compileClasspath {
        resolutionStrategy.activateDependencyLocking()
    }
    runtimeClasspath {
        resolutionStrategy.activateDependencyLocking()
    }
}

group = rootProject.group
version = rootProject.version
description = "Queries for calendars."

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
    options.encoding = "UTF-8"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
}

tasks.javadoc {
    title = "CalQL Query v${project.version}"
    options {
        locale = "en_US"
        encoding = "UTF-8"
        overview = "src/main/html/overview.html"
        (this as StandardJavadocDocletOptions).apply {
            links("https://docs.oracle.com/javase/8/docs/api/")
        }
    }
}

tasks.jar {
    metaInf {
        from(rootProject.file("LICENSE"))
        from(rootProject.file("NOTICE"))
    }
}

tasks.named<Jar>("sourcesJar") {
    metaInf {
        from(rootProject.file("LICENSE"))
        from(rootProject.file("NOTICE"))
    }
}

tasks.named<Jar>("javadocJar") {
    metaInf {
        from(rootProject.file("LICENSE"))
        from(rootProject.file("NOTICE"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name

            from(components["java"])
            // javadocJar and sourcesJar are added by java.withJavadocJar() and java.withSourcesJar() above.
            // See: https://docs.gradle.org/current/javadoc/org/gradle/api/plugins/JavaPluginExtension.html

            pom {  // https://central.sonatype.org/pages/requirements.html
                packaging = "jar"
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/dmikurube/calql-java")

                licenses {
                    license {
                        // http://central.sonatype.org/pages/requirements.html#license-information
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        name.set("Dai MIKURUBE")
                        email.set("dmikurube@acm.org")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/dmikurube/calql-java.git")
                    developerConnection.set("scm:git:git@github.com:dmikurube/calql-java.git")
                    url.set("https://github.com/dmikurube/calql-java")
                }
            }
        }
    }

    repositories {
        maven {  // publishMavenPublicationToMavenCentralRepository
            name = "mavenCentral"
            if (project.version.toString().endsWith("-SNAPSHOT")) {
                url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            } else {
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            }

            credentials {
                username = project.findProperty("ossrhUsername")?.toString() ?: ""
                password = project.findProperty("ossrhPassword")?.toString() ?: ""
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showExceptions = true
        showStackTraces = true
        showStandardStreams = true
        outputs.upToDateWhen { false }
    }
}

tasks.withType<Checkstyle> {
    reports {
        outputs.upToDateWhen { false }
    }
}

checkstyle {
    toolVersion = "8.42"
    configFile = file("${rootProject.projectDir}/config/checkstyle/checkstyle.xml")
    setConfigProperties(mapOf(
        "checkstyle.config.path" to file("${rootProject.projectDir}/config/checkstyle")
    ))
    setIgnoreFailures(false)
    maxWarnings = 0
}
