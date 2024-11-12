dependencies {
    implementation(project(":soongan-persistence"))
    implementation(project(":soongan-support"))
    implementation(project(":soongan-kafka"))
    implementation(project(":soongan-web"))

    implementation("com.google.auth:google-auth-library-oauth2-http:1.27.0")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.springframework.kafka:spring-kafka-test")
}