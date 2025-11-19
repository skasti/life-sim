plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":genome"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
