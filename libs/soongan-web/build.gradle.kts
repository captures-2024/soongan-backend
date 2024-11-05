configurations {
    all {
        exclude(group="commons-logging", module="commons-logging")
    }
}

dependencies {
    implementation(project(":soongan-support"))
    implementation(project(":soongan-persistence"))
    implementation(project(":soongan-redis"))

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    testImplementation("org.springframework.security:spring-security-test")
}