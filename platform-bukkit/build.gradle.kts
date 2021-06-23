plugins {
    java
    kotlin("jvm") version "1.5.10"
}

repositories {
    maven {
        isAllowInsecureProtocol = true
        url = uri("http://repo.ptms.ink/repository/maven-releases/")
    }
    maven { url = uri("https://repo.codemc.io/repository/nms/") }
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v11200:11200:all")
    compileOnly(project(":common"))
    compileOnly(project(":common-5"))
    compileOnly(project(":plugin"))
    compileOnly(project(":module-chat"))
    compileOnly(kotlin("stdlib"))
}