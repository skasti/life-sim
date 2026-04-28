plugins {
    kotlin("jvm")
    application
}

val gdxVersion = "1.14.0"

dependencies {
    implementation(project(":biology"))

    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")
    runtimeOnly("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    runtimeOnly("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")

    testImplementation(kotlin("test"))
}

application {
    mainClass.set("life.sim.simulator.DesktopLauncherKt")
}

tasks.named<JavaExec>("run") {
    if (System.getProperty("os.name").contains("Mac", ignoreCase = true)) {
        jvmArgs("-XstartOnFirstThread")
    }
}

tasks.test {
    useJUnitPlatform()
}
