import io.github.vincentvibe3.gqlclient.dsl.Field
import io.github.vincentvibe3.gqlclient.dsl.mutation
import io.github.vincentvibe3.gqlclient.http.DefaultGQLError
import io.github.vincentvibe3.gqlclient.http.GQLClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

suspend fun main(){
    val mutation = mutation("CreateReviewForEpisode"){
        variable("ep", "Episode!")
        variable("review", "ReviewInput!")
        field("createReview"){
            addArg("episode", "ep", Field.ArgumentType.VARIABLE)
            addArg("review", "review", Field.ArgumentType.VARIABLE)
            field("stars")
            field("commentary")
        }
    }
    val variables = buildJsonObject {
        put("ep", "JEDI")
        put("review", buildJsonObject {
            put("stars", 5)
            put("commentary", "This is a great movie!")
        })
    }
    val client = GQLClient()
    runBlocking {
        val response = client.sendMutation<JsonObject, DefaultGQLError>("127.0.0.1", mutation, variables)
        println(response.toString())
    }
}