
val taskGroup = "Puls Android build"

tasks.register("checkAll") {
    group = taskGroup
    description = "Run all tests and static analysis tools on '${project.path}' component"
    dependsOn(
        subprojects
            .filter { !it.isPlainDir() }
            .map { "${it.path}:check" }
    )
}

fun Project.isPlainDir(): Boolean {
    return !file("build.gradle").exists() && !file("build.gradle.kts").exists()
}
