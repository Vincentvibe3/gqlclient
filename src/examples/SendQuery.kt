import io.github.vincentvibe3.gqlclient.dsl.query
import io.github.vincentvibe3.gqlclient.http.DefaultGQLError
import io.github.vincentvibe3.gqlclient.http.GQLClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject

suspend fun main(){
    val query = query{
        field("hello"){
            field("world")
        }
    }
    val client = GQLClient()
    runBlocking {
        val response = client.sendQuery<JsonObject, DefaultGQLError>("127.0.0.1",query)
        println(response.toString())
    }
}