plugins {
    id("java")
}

group = "com.test"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

// korean encoding
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Test>().configureEach {
    systemProperty("file.encoding", "UTF-8")
}


// JAR 설정 (리소스 포함)
tasks.jar {
    manifest {
        attributes["Main-Class"] = "app.Main"
    }

    from(sourceSets.main.get().output) // 클래스 파일 포함

    // src/main/resources 폴더 강제 포함
    from("src/main/resources") {
        include("**/*") // 모든 파일 포함
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

}
