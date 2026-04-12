plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":biology"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
