extra["springCloudGcpVersion"] = "5.4.3"
extra["springCloudVersion"] = "2023.0.2"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")


    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.5")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

    implementation("com.google.cloud:spring-cloud-gcp-starter-storage:5.4.3")
}

dependencyManagement {
    imports {
        mavenBom("com.google.cloud:spring-cloud-gcp-dependencies:${property("springCloudGcpVersion")}")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}