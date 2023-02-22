# spigot-plugin-base
This is a basic codebase for a Spigot plugin written in Kotlin and built using Gradle.

## Requirements
- JDK 8+ (Kotlin builds to this target)
- Gradle 5+

## Initial Usage
Clone this repo, and then change the following files to match your new package name:
- `src/main/java/me/duncanleo/spigot_plugin_base/` (rename the directory)
- `src/main/java/me/duncanleo/spigot_plugin_base/App.kt` (change the package name in line 1)
- `src/resources/plugin.yml` (There is a `main` field)

### Additional Notes
- The Spigot version is specified in `build.gradle.kts`
- Follow the conventions of the Gradle `java` plugin
  - Keep all your source code under `src/main/java/`
  - Keep all other assets (e.g. `plugin.yml`) under `src/main/resources`

## Usage
To build a JAR file for use with Spigot, run `gradle shadowJar`. The completed file will be in `build/libs`.
