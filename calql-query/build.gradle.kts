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

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
    options.encoding = "UTF-8"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.bundles.junit5.implementation)

    testRuntimeOnly(libs.bundles.junit5.runtime)
}

tasks.javadoc {
    title = "CalQL Query v${project.version}"
    options {
        locale = "en_US"
        encoding = "UTF-8"
        (this as StandardJavadocDocletOptions).apply {
            links("https://docs.oracle.com/en/java/javase/11/docs/api/")
        }
    }
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
    if (project.hasProperty("signingKey") && project.hasProperty("signingPassword")) {
        logger.lifecycle("Signing with an in-memory key.")
        useInMemoryPgpKeys(project.property("signingKey").toString(), project.property("signingPassword").toString())
    }
    sign(publishing.publications["maven"])
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("started", "passed", "skipped", "failed", "standardOut", "standardError")
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
    toolVersion = libs.versions.checkstyle.get()
    configFile = file("${rootProject.projectDir}/config/checkstyle/checkstyle.xml")
    setConfigProperties(mapOf(
        "org.checkstyle.google.suppressionfilter.config" to "${rootProject.projectDir}/config/checkstyle/checkstyle-suppressions.xml"
    ))
    setIgnoreFailures(false)
    setMaxWarnings(0)
}
