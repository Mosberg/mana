# Mana System - Minecraft 1.21.10 Fabric Mod Configuration

[Mana System Repository](https://github.com/Mosberg/mana)

```properties
# ═══════════════════════════════════════════════════════════════════════════════
# Gradle JVM Configuration - Optimized for Fabric Development
# ═══════════════════════════════════════════════════════════════════════════════

# Allocate 4GB heap, use G1GC for large projects, enable parallel garbage collection
org.gradle.jvmargs=-Xmx4G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200

# Build performance optimizations
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configuration-cache=true

# ═══════════════════════════════════════════════════════════════════════════════
# Mod Metadata - Uniquely Identifies Your Mod
# ═══════════════════════════════════════════════════════════════════════════════

maven_group=dk.mosberg
archives_base_name=mana
mod_id=mana
mod_version=1.0.0
mod_name=Mana System
mod_description=Mana System for Minecraft
mod_author=Mosberg
mod_homepage=https://mosberg.github.io/mana
mod_sources=https://github.com/mosberg/mana
mod_issues=https://github.com/mosberg/mana/issues
mod_license=MIT

# ═══════════════════════════════════════════════════════════════════════════════
# Minecraft & Fabric Versions - Keep Updated via https://fabricmc.net/develop
# ═══════════════════════════════════════════════════════════════════════════════

minecraft_version=1.21.10
loader_version=0.18.4
yarn_mappings=1.21.10+build.3
loom_version=1.14.10
fabric_version=0.138.4+1.21.10
java_version=21

# ═══════════════════════════════════════════════════════════════════════════════
# Library Versions - Standard Dependencies
# ═══════════════════════════════════════════════════════════════════════════════

gson_version=2.13.2
slf4j_version=2.0.17
annotations_version=26.0.2

# ═══════════════════════════════════════════════════════════════════════════════
# Testing Framework
# ═══════════════════════════════════════════════════════════════════════════════

junit_version=6.0.1

# ═══════════════════════════════════════════════════════════════════════════════
# Optional Integrations - Recommended Mods for Development
# ═══════════════════════════════════════════════════════════════════════════════

cloth_config_version=19.0.147
modmenu_version=16.0.0-rc.1
```

```gradle
plugins {
    id 'fabric-loom' version "${loom_version}"
    id 'maven-publish'
    id 'java'
}

version = project.mod_version
group = project.maven_group

base {
    archivesName = project.archives_base_name
}

// ═════════════════════════════════════════════════════════════════════════════════
// Maven Repositories for Dependencies
// ═════════════════════════════════════════════════════════════════════════════════

repositories {
    mavenCentral()

    maven {
        name = "Fabric"
        url = "https://maven.fabricmc.net/"
    }

    maven {
        name = "Terraformers"
        url = "https://maven.terraformersmc.com/releases/"
    }

    maven {
        name = "Shedaniel"
        url = "https://maven.shedaniel.me/"
    }
}

// ═════════════════════════════════════════════════════════════════════════════════
// Fabric Loom Configuration - IDE Integration and Run Configurations
// ═════════════════════════════════════════════════════════════════════════════════

loom {
    splitEnvironmentSourceSets()

    mods {
        mana {
            sourceSet sourceSets.main
            sourceSet sourceSets.client
        }
    }

    runs {
        // Client run configuration (F5 in IDE)
        client {
            client()
            configName = "Minecraft Client"
            ideConfigGenerated = true
            runDir = "run"
        }

        // Server run configuration (F5 in IDE)
        server {
            server()
            configName = "Minecraft Server"
            ideConfigGenerated = true
            runDir = "run-server"
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════════
// Fabric API Data Generation Configuration
// ═════════════════════════════════════════════════════════════════════════════════

fabricApi {
    configureDataGeneration {
        client = false
    }
}

// ═════════════════════════════════════════════════════════════════════════════════
// Dependencies - All Required Libraries and Frameworks
// ═════════════════════════════════════════════════════════════════════════════════

dependencies {
    // Minecraft & Fabric Core
    minecraft "com.mojang:minecraft:${project.minecraft_version}"
    mappings "net.fabricmc:yarn:${project.yarn_mappings}:v2"
    modImplementation "net.fabricmc:fabric-loader:${project.loader_version}"
    modImplementation "net.fabricmc.fabric-api:fabric-api:${project.fabric_version}"

    // Optional Mods - Enhance Development Experience
    modImplementation "com.terraformersmc:modmenu:${project.modmenu_version}"
    modApi("me.shedaniel.cloth:cloth-config-fabric:${project.cloth_config_version}") {
        exclude group: "net.fabricmc.fabric-api"
    }

    // Libraries - Bundled with JAR
    include implementation("com.google.code.gson:gson:${project.gson_version}")

    // Compile-Only - Annotations for Better IDE Support
    compileOnly "org.jetbrains:annotations:${project.annotations_version}"

    // Testing Framework - JUnit 6 with BOM for Dependency Management
    testImplementation platform("org.junit:junit-bom:${project.junit_version}")
    testImplementation "org.junit.jupiter:junit-jupiter"
    testImplementation "org.junit.jupiter:junit-jupiter-params"
    testRuntimeOnly "org.junit.platform:junit-platform-launcher"
}

// ═════════════════════════════════════════════════════════════════════════════════
// Source Sets Configuration
// ═════════════════════════════════════════════════════════════════════════════════

sourceSets {
    main {
        resources {
            srcDirs += [
                "src/main/generated/resources"
            ]
            exclude ".cache"
        }
    }
}

// ═════════════════════════════════════════════════════════════════════════════════
// Resource Processing
// ═════════════════════════════════════════════════════════════════════════════════

processResources {
    // Capture properties as local variables to avoid Groovy closure issues
    def modId = project.mod_id
    def modVersion = project.mod_version
    def modName = project.mod_name
    def modDescription = project.mod_description
    def modAuthor = project.mod_author
    def modHomepage = project.mod_homepage
    def modSources = project.mod_sources
    def modIssues = project.mod_issues
    def modLicense = project.mod_license
    def fabricLoaderVer = project.loader_version
    def minecraftVer = project.minecraft_version
    def javaVer = project.java_version

    // Define inputs for build cache invalidation
    inputs.property "mod_id", modId
    inputs.property "mod_version", modVersion
    inputs.property "mod_name", modName
    inputs.property "mod_description", modDescription
    inputs.property "mod_author", modAuthor
    inputs.property "mod_homepage", modHomepage
    inputs.property "mod_sources", modSources
    inputs.property "mod_issues", modIssues
    inputs.property "mod_license", modLicense
    inputs.property "loader_version", fabricLoaderVer
    inputs.property "minecraft_version", minecraftVer
    inputs.property "java_version", javaVer

    // Process fabric.mod.json template with property values
    filesMatching("fabric.mod.json") {
        expand(
            "mod_id": modId,
            "mod_version": modVersion,
            "mod_name": modName,
            "mod_description": modDescription,
            "mod_author": modAuthor,
            "mod_homepage": modHomepage,
            "mod_sources": modSources,
            "mod_issues": modIssues,
            "mod_license": modLicense,
            "loader_version": fabricLoaderVer,
            "minecraft_version": minecraftVer,
            "java_version": javaVer
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════════
// Java Compilation Configuration
// ═════════════════════════════════════════════════════════════════════════════════

tasks.withType(JavaCompile).configureEach {
    it.options.encoding = "UTF-8"
    it.options.release = 21
}

java {
    withSourcesJar()
    withJavadocJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

// ═════════════════════════════════════════════════════════════════════════════════
// JAR Manifest Configuration - Build Metadata
// ═════════════════════════════════════════════════════════════════════════════════

jar {
    // Capture properties for manifest
    def archivesBaseName = project.archives_base_name
    def modName = project.mod_name
    def modAuthor = project.mod_author
    def modVersion = project.version

    from("LICENSE") {
        rename { "${it}_${archivesBaseName}" }
    }

    manifest {
        attributes(
            "Specification-Title": modName,
            "Specification-Vendor": modAuthor,
            "Specification-Version": "1",
            "Implementation-Title": modName,
            "Implementation-Version": modVersion,
            "Implementation-Vendor": modAuthor,
            "Implementation-Timestamp": new Date().format("yyyy-MM-dd'T'HH:mm:ssZ")
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════════
// Javadoc Configuration - Documentation Generation
// ═════════════════════════════════════════════════════════════════════════════════

javadoc {
    options.encoding = 'UTF-8'
    options.charSet = 'UTF-8'
    options.addStringOption('Xdoclint:none', '-quiet')
}

// ═════════════════════════════════════════════════════════════════════════════════
// Test Configuration - JUnit 5 with Comprehensive Logging
// ═════════════════════════════════════════════════════════════════════════════════

test {
    useJUnitPlatform()

    testLogging {
        events "passed", "skipped", "failed"
        exceptionFormat = "full"
        showStandardStreams = false
    }
}

// ═════════════════════════════════════════════════════════════════════════════════
// Maven Publication Configuration - JAR Distribution
// ═════════════════════════════════════════════════════════════════════════════════

publishing {
    publications {
        mavenJava(MavenPublication) {
            artifactId = project.archives_base_name
            from components.java

            pom {
                name = project.mod_name
                description = project.mod_description
                url = project.mod_homepage

                licenses {
                    license {
                        name = project.mod_license
                    }
                }

                developers {
                    developer {
                        name = project.mod_author
                    }
                }

                scm {
                    url = project.mod_sources
                    connection = "scm:git:${project.mod_sources}.git"
                }
            }
        }
    }

    repositories {
        // Uncomment to publish to local Maven repository
        // mavenLocal()

        // Template for custom Maven repository
        // maven {
        //     name = "MyMaven"
        //     url = "https://maven.example.com/releases"
        //     credentials {
        //         username = project.findProperty("maven.username") ?: System.getenv("MAVEN_USERNAME")
        //         password = project.findProperty("maven.password") ?: System.getenv("MAVEN_PASSWORD")
        //     }
        // }
    }
}

// ═════════════════════════════════════════════════════════════════════════════════
// Custom Tasks - Utility Commands
// ═════════════════════════════════════════════════════════════════════════════════

// Display build information - Configuration cache compliant
tasks.register("projectInfo") {
    // Capture properties at configuration time for configuration cache compatibility
    def modName = project.mod_name
    def modVersion = project.mod_version
    def minecraftVersion = project.minecraft_version
    def loaderVersion = project.loader_version
    def fabricVersion = project.fabric_version
    def javaVersion = project.java_version
    def gradleVersion = gradle.gradleVersion
    doLast {
        println """
            ======================================
            Mana Build Information
            ======================================
            Mod Name:       ${modName}
            Version:        ${modVersion}
            Minecraft:      ${minecraftVersion}
            Fabric Loader:  ${loaderVersion}
            Fabric API:     ${fabricVersion}
            Java Version:   ${javaVersion}
            Gradle Version: ${gradleVersion}
            ======================================
        """.stripIndent()
    }
}

// Clean generated resources
clean {
    delete "src/main/generated"
}
```

```gradle
pluginManagement {
 repositories {
  maven {
   name = 'Fabric'
   url = 'https://maven.fabricmc.net/'
  }
  mavenCentral()
  gradlePluginPortal()
 }
}

rootProject.name = "mana"
```

```json
{
  "schemaVersion": 1,
  "id": "${mod_id}",
  "version": "${mod_version}",
  "name": "${mod_name}",
  "description": "${mod_description}",
  "authors": ["${mod_author}"],
  "contact": {
    "homepage": "${mod_homepage}",
    "sources": "${mod_sources}",
    "issues": "${mod_issues}"
  },
  "license": "${mod_license}",
  "icon": "assets/mana/icon.png",
  "environment": "*",
  "entrypoints": {
    "main": ["dk.mosberg.Mana"],
    "client": ["dk.mosberg.client.ManaClient"]
  },
  "mixins": [],
  "depends": {
    "fabricloader": ">=${loader_version}",
    "fabric-api": "*",
    "minecraft": "~${minecraft_version}",
    "java": ">=${java_version}"
  },
  "recommends": {
    "modmenu": "*",
    "cloth-config": "*"
  },
  "custom": {
    "modmenu": {
      "update_checker": true
    }
  }
}
```

```plaintext
└── 📁src
    └── 📁client
        └── 📁java
            └── 📁dk
                └── 📁mosberg
                    └── 📁client
                        └── 📁overlay
                            ├── HealthBarHelper.java
                            ├── ManaHudOverlay.java
                            ├── StatusIconHelper.java
                        └── 📁renderer
                            ├── ColorHelper.java
                            ├── OverlayRenderer.java
                            ├── RenderHelper.java
                        ├── ManaClient.java
        └── 📁resources
    └── 📁main
        └── 📁java
            └── 📁dk
                └── 📁mosberg
                    └── 📁config
                        ├── ManaConfig.java
                    └── 📁mana
                        ├── ManaComponent.java
                        ├── ManaComponents.java
                        ├── ManaPool.java
                    ├── Mana.java
        └── 📁resources
            └── 📁assets
                └── 📁mana
                    ├── icon.png
            └── fabric.mod.json
```
