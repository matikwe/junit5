plugins {
	alias(libs.plugins.nohttp)
	alias(libs.plugins.nexusPublish)
	id("junitbuild.base-conventions")
	id("junitbuild.build-metadata")
	id("junitbuild.dependency-update-check")
	id("junitbuild.jacoco-aggregation-conventions")
	id("junitbuild.temp-maven-repo")
	id("org.sonarqube") version "3.4.0.2513"
}

description = "JUnit 5"

val license by extra(License(
	name = "Eclipse Public License v2.0",
	url = uri("https://www.eclipse.org/legal/epl-v20.html"),
	headerFile = layout.projectDirectory.file("gradle/config/spotless/eclipse-public-license-2.0.java")
))

val platformProjects by extra(listOf(
		projects.junitPlatformCommons,
		projects.junitPlatformConsole,
		projects.junitPlatformConsoleStandalone,
		projects.junitPlatformEngine,
		projects.junitPlatformJfr,
		projects.junitPlatformLauncher,
		projects.junitPlatformReporting,
		projects.junitPlatformRunner,
		projects.junitPlatformSuite,
		projects.junitPlatformSuiteApi,
		projects.junitPlatformSuiteCommons,
		projects.junitPlatformSuiteEngine,
		projects.junitPlatformTestkit
).map { it.dependencyProject })

val jupiterProjects by extra(listOf(
		projects.junitJupiter,
		projects.junitJupiterApi,
		projects.junitJupiterEngine,
		projects.junitJupiterMigrationsupport,
		projects.junitJupiterParams
).map { it.dependencyProject })

val vintageProjects by extra(listOf(
		projects.junitVintageEngine.dependencyProject
))

val mavenizedProjects by extra(platformProjects + jupiterProjects + vintageProjects)
val modularProjects by extra(mavenizedProjects - listOf(projects.junitPlatformConsoleStandalone.dependencyProject))

dependencies {
	(modularProjects + listOf(projects.platformTests.dependencyProject)).forEach {
		jacocoAggregation(project(it.path))
	}
}

nexusPublishing {
	packageGroup.set("org.junit")
	repositories {
	}
}

nohttp {
	source.exclude("**/.gradle/**", "gradle/plugins/**/build/**", "buildSrc/build/**")
}

tasks.checkstyleNohttp {
	notCompatibleWithConfigurationCache("https://github.com/spring-io/nohttp/issues/61")
}

sonarqube {
	properties {
		property("sonar.projectKey", "junit5_25-2")
	}
}