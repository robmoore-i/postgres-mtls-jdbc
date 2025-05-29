plugins {
    java
    application
    id("com.gradleup.shadow") version "8.3.6"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.postgresql)
}

application {
    mainClass.set("com.rob.pg.Main")
}
