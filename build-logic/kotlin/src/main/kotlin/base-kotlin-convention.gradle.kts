import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinLanguageVersion = "1.7"

/**
 * Exists because `compile` task ambiguous in projects with jvm and android modules combined
 */
val compileAllTask: TaskProvider<Task> = tasks.register("compileAll") {
    description = "Compiles all available modules in all variants"

    dependsOn(tasks.withType<KotlinCompile>())
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {

        jvmTarget = JavaVersion.VERSION_11.toString()

        languageVersion = kotlinLanguageVersion
        apiVersion = kotlinLanguageVersion
    }
}
