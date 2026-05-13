plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":events"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

