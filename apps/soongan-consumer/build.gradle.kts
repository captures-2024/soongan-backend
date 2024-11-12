dependencies {
    implementation(project(":soongan-persistence"))
    implementation(project(":soongan-support"))
    implementation(project(":soongan-kafka"))

    implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.springframework.kafka:spring-kafka-test")
}