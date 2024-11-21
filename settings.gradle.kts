rootProject.name = "soongan-backend"

// apps
listOf(
    "soongan-api",
    "soongan-consumer"
).forEach {
    include(it)
    project(":$it").projectDir = File("$rootDir/apps/$it")
}

// libs
listOf(
    "soongan-kafka",
    "soongan-persistence",
    "soongan-support",
    "soongan-redis",
    "soongan-web"
).forEach {
    include(it)
    project(":$it").projectDir = File("$rootDir/libs/$it")
}

//include("apps:soongan-consumer")
//findProject(":apps:soongan-consumer")?.name = "soongan-consumer"
//include("apps:soongan-api")
//findProject(":apps:soongan-api")?.name = "soongan-api"
