# gqlclient

A simple GraphQL client for Kotlin Multiplatform

gql uses a DSL to form GraphQL queries and sends them using HTTP POST requests.

## Getting Started

To use gqlclient you will need gqlclient and an engine for [Ktor](https://ktor.io/)

To add an engine see https://ktor.io/docs/http-client-engines.html

### Gradle

```kotlin
dependencies {
    implementation("io.github.vincentvibe3:gqlclient:<version here>")
}
```

## QuickStart

```kotlin
val query = query{
    field("hello"){ 
        field("world")
    }
}
val client = GQLClient()
val response = client.sendQuery<JsonObject, DefaultGQLError>("127.0.0.1", query)
println(response.data.toString())
```

## Examples

Find examples [here](https://github.com/Vincentvibe3/gqlclient/tree/main/src/examples)

## License

gqlclient is licensed under an [MIT License](https://github.com/Vincentvibe3/gqlclient/blob/main/LICENSE)