dependencies {
    implementation(project(":soongan-persistence"))
    implementation(project(":soongan-support"))
    implementation(project(":soongan-web"))
    implementation(project(":soongan-redis"))

    implementation("com.google.auth:google-auth-library-oauth2-http:1.27.0")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("com.nimbusds:nimbus-jose-jwt:9.40")
    implementation("com.google.api-client:google-api-client:2.2.0")
    implementation("com.google.cloud:spring-cloud-gcp-starter-storage:5.4.3")
    implementation("com.google.cloud:google-cloud-logging-logback:0.131.11-alpha")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
}