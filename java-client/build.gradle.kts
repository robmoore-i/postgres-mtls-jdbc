plugins {
    java
    application
    distribution
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

tasks.named<JavaExec>("run") {
    systemProperty("certs.dir", rootProject.layout.projectDirectory.dir("../certs"))
    systemProperty("certs.provider", "user.home")
}

val dockerDistribution by tasks.registering(Tar::class) {
    from(tasks.named<Sync>("installDist"))
    into("/opt/app")
    destinationDirectory.set(layout.buildDirectory.dir("distDockerTar"))
    archiveFileName.set("distDockerTar.tar")
}

val dockerBuildDirectory by tasks.registering(Sync::class) {
    from(layout.projectDirectory.dir("src/docker"))
    from(dockerDistribution)
    into(layout.buildDirectory.dir("dockerBuild"))
}

val dockerBuildDirectoryCerts by tasks.registering(Sync::class) {
    from(rootProject.layout.projectDirectory.dir("../certs"))
    into(layout.buildDirectory.dir("dockerBuild/certs"))
}

tasks.register<Exec>("dockerBuild") {
    dependsOn(dockerBuildDirectory, dockerBuildDirectoryCerts)
    commandLine("docker", "build", "-t", "postgres-mtls-jdbc", "build/dockerBuild")
}
