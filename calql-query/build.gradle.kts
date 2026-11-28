plugins {
    id("java")
    id("maven-publish")
    id("signing")
    id("checkstyle")
}

repositories {
    mavenCentral()
}

group = rootProject.group
version = rootProject.version
description = "Queries for calendars."

configurations {
    compileClasspath {
        resolutionStrategy.activateDependencyLocking()
    }
    runtimeClasspath {
        resolutionStrategy.activateDependencyLocking()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit.implementation)

    testRuntimeOnly(libs.bundles.junit.runtime)
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
    options.encoding = "UTF-8"
}

tasks.withType<JavaCompile>().matching { it.name.contains("test", ignoreCase = true) }.configureEach {
    javaCompiler.set(javaToolchains.compilerFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
}

tasks.javadoc {
    title = "CalQL Query v${project.version}"
    options {
        locale = "en_US"
        encoding = "UTF-8"
        (this as StandardJavadocDocletOptions).apply {
            links("https://docs.oracle.com/en/java/javase/17/docs/api/")
            addBooleanOption("Xdoclint:all", true)
            addBooleanOption("Xwerror", true)
        }
    }
}

tasks.check {
    dependsOn(tasks.javadoc)
}

tasks.jar {
    metaInf {
        from(rootProject.file("LICENSE"))
    }
}

tasks.named<Jar>("sourcesJar") {
    metaInf {
        from(rootProject.file("LICENSE"))
    }
}

tasks.named<Jar>("javadocJar") {
    metaInf {
        from(rootProject.file("LICENSE"))
    }
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
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
                        id.set("dmikurube")
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
                // See: https://central.sonatype.org/publish/publish-portal-snapshots/
                url = uri("https://central.sonatype.com/repository/maven-snapshots/")
            } else {
                // A release published here is not visible in the Central Portal until it is transferred
                // by "POST /manual/upload/defaultRepository/<namespace>" from the same IP address.
                // See: https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/
                url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
            }

            // A user token of the Central Portal. A token of the old OSSRH is rejected.
            credentials {
                username = project.findProperty("ossrhUsername")?.toString() ?: ""
                password = project.findProperty("ossrhPassword")?.toString() ?: ""
            }
        }
    }
}

signing {
    if (project.hasProperty("signingKey") && project.hasProperty("signingPassword")) {
        logger.lifecycle("Signing with an in-memory key.")
        useInMemoryPgpKeys(project.property("signingKey").toString(), project.property("signingPassword").toString())
    }

    // Skip signing when signingKey is not available. Note that Maven Central rejects an unsigned upload.
    isRequired = project.hasProperty("signingKey")

    sign(publishing.publications["maven"])
}

tasks.test {
    useJUnitPlatform()
    outputs.upToDateWhen { false }
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    testLogging {
        events("started", "passed", "skipped", "failed", "standardOut", "standardError")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showExceptions = true
        showStackTraces = true
        showStandardStreams = true
    }
}

tasks.withType<Checkstyle> {
    outputs.upToDateWhen { false }
    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(25))
    })
    ignoreFailures = false
    maxWarnings = 0
}

checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    configFile = file("${rootProject.projectDir}/config/checkstyle/checkstyle.xml")
    configProperties = mapOf(
        "org.checkstyle.google.suppressionfilter.config" to "${rootProject.projectDir}/config/checkstyle/checkstyle-suppressions.xml"
    )
}
