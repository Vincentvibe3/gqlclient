import io.github.vincentvibe3.gqlclient.dsl.Variable
import io.github.vincentvibe3.gqlclient.dsl.mutation
import io.github.vincentvibe3.gqlclient.dsl.query
import io.github.vincentvibe3.gqlclient.http.*
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import kotlin.test.Test
import kotlin.test.assertEquals

class HttpTests {

    @Test
    fun queryRequest(){
        val payload = "{\"data\":{\"hero\":{\"name\":\"R2-D2\",\"appearsIn\":[\"NEWHOPE\",\"EMPIRE\",\"JEDI\"]}}}"
        val expectedResponse = "{\"hero\":{\"name\":\"R2-D2\",\"appearsIn\":[\"NEWHOPE\",\"EMPIRE\",\"JEDI\"]}}"
        val query = query {
            field("hero"){
                field("name")
                field("appearsIn")
            }
        }
        val mockEngine = MockEngine{request ->
            val serializedData = Json.decodeFromString<QueryRequest>(request.body.toByteArray().decodeToString())
            if (serializedData.query==query.toString()){
                respond(
                    content = payload,
                    status=HttpStatusCode.OK,
                    headers= headersOf(HttpHeaders.ContentType, "application/json")
                )
            } else{
                respond(
                    content="failed",
                    status = HttpStatusCode.BadRequest
                )
            }
        }
        val client = GQLClient(HttpClient(mockEngine))
        runBlocking {
            val response = client.sendQuery<JsonObject, DefaultGQLError>("",query)
            assertEquals(expectedResponse, response.data.toString())
        }

    }

    @Test
    fun queryRequestWithVariables(){
        val payload = "{\"data\":{\"hero\":{\"name\":\"R2-D2\",\"friends\":[{\"name\":\"LukeSkywalker\"},{\"name\":\"HanSolo\"},{\"name\":\"LeiaOrgana\"}]}}}"
        val expectedResponse = "{\"hero\":{\"name\":\"R2-D2\",\"friends\":[{\"name\":\"LukeSkywalker\"},{\"name\":\"HanSolo\"},{\"name\":\"LeiaOrgana\"}]}}"
        val query = query("HeroNameAndFriends") {
            variable("varName", "varType")
            field("hero"){
                addArg("episode",  Variable("episode", "Episode"))
                field("name")
                field("friends"){
                    field("name")
                }
            }
        }
        val mockEngine = MockEngine{request ->
            val serializedData = Json.decodeFromString<QueryRequest>(request.body.toByteArray().decodeToString())
            val receivedVariables = serializedData.variables
            if (receivedVariables!=null) {
                if (receivedVariables.getValue("episode").jsonPrimitive.content == "JEDI") {
                    return@MockEngine respond(
                        content = payload,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
            respond(
                content="failed",
                status = HttpStatusCode.BadRequest
            )
        }
        val client = GQLClient(HttpClient(mockEngine))
        val variables = buildJsonObject {
            put("episode", "JEDI")
        }
        runBlocking {
            val response = client.sendQuery<JsonObject, DefaultGQLError>("",query,variables)
            assertEquals(expectedResponse, response.data.toString())
        }
    }

    @Test
    fun queryRequestWithOperationName(){
        val payload = "{\"data\":{\"hero\":{\"name\":\"R2-D2\",\"friends\":[{\"name\":\"LukeSkywalker\"},{\"name\":\"HanSolo\"},{\"name\":\"LeiaOrgana\"}]}}}"
        val expectedResponse = "{\"hero\":{\"name\":\"R2-D2\",\"friends\":[{\"name\":\"LukeSkywalker\"},{\"name\":\"HanSolo\"},{\"name\":\"LeiaOrgana\"}]}}"
        val query = query("HeroNameAndFriends") {
            variable("varName", "varType")
            field("hero"){
                addArg("episode", Variable("episode", "Episode"))
                field("name")
                field("friends"){
                    field("name")
                }
            }
        }
        val mockEngine = MockEngine{request ->
            val serializedData = Json.decodeFromString<QueryRequest>(request.body.toByteArray().decodeToString())
            if (serializedData.operationName!=null) {
                if (serializedData.operationName == "queryHeroes") {
                    return@MockEngine respond(
                        content = payload,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
            }
            respond(
                content="failed",
                status = HttpStatusCode.BadRequest
            )
        }
        val client = GQLClient(HttpClient(mockEngine))
        val variables = buildJsonObject {
            put("episode", "JEDI")
        }
        runBlocking {
            val response = client.sendQuery<JsonObject, DefaultGQLError>("",query,variables, "queryHeroes")
            assertEquals(expectedResponse, response.data.toString())
        }
    }

    @Test
    fun testErrors(){
        val payload = "{\"errors\":[{\"message\":\"Cannot query field \\\"field\\\" on type \\\"Query\\\".\",\"status\":400,\"locations\":[{\"line\":16,\"column\":7}]}],\"data\":null}"
        val expectedError=CustomGQLError(
            "Cannot query field \"field\" on type \"Query\".",
            status = 400,
            locations = listOf(
                GQLError.ErrorLocations(
                16,7
            )),
        )
        val mockEngine = MockEngine{ _ ->
            respond(
                content=payload,
                status = HttpStatusCode.OK
            )
        }
        val client = GQLClient(HttpClient(mockEngine))
        runBlocking {
            val response = client.sendQuery<JsonObject, CustomGQLError>("", query {  })
            response.errors?.let { assertEquals(expectedError, it.first()) }
        }
    }

    @Test
    fun testHeaders(){
        val mockEngine = MockEngine{request ->
            if (request.headers.contains("X-Test-Header")){
                if (request.headers["X-Test-Header"] =="Test"){
                    return@MockEngine respond(
                        content="{\"data\":{\"status\":\"OK\"}}",
                        status=HttpStatusCode.OK
                    )
                }
            }
            respond(
                content="failed",
                status = HttpStatusCode.BadRequest
            )
        }
        val client = GQLClient(HttpClient(mockEngine))
        runBlocking {
            val response = client.sendMutation<JsonObject, CustomGQLError>("", mutation {  }, headers = listOf(
                HttpHeader("X-Test-Header", "Test")
            ))
            response.data?.get("status")?.jsonPrimitive?.let { assertEquals(it.content, "OK") }
        }
    }

    @Test
    fun testErrorsStringResponse(){
        val payload = "{\"errors\":[{\"message\":\"Cannot query field \\\"field\\\" on type \\\"Query\\\".\",\"status\":400,\"locations\":[{\"line\":16,\"column\":7}]}],\"data\":null}"
        val expectedError="[{\"message\":\"Cannot query field \\\"field\\\" on type \\\"Query\\\".\",\"status\":400,\"locations\":[{\"line\":16,\"column\":7}]}]"
        val mockEngine = MockEngine{ _ ->
            respond(
                content=payload,
                status = HttpStatusCode.OK
            )
        }
        val client = GQLClient(HttpClient(mockEngine))
        runBlocking {
            val response = client.stringSendQuery("", query {  })
            response.errors?.let { assertEquals(expectedError, it) }
        }
    }

    @Test
    fun testStringResponse(){
        val payload = "{\"data\":{\"hero\":{\"name\":\"R2-D2\",\"appearsIn\":[\"NEWHOPE\",\"EMPIRE\",\"JEDI\"]}}}"
        val expectedResponse = "{\"hero\":{\"name\":\"R2-D2\",\"appearsIn\":[\"NEWHOPE\",\"EMPIRE\",\"JEDI\"]}}"
        val mockEngine = MockEngine{ _ ->
            respond(
                content=payload,
                status = HttpStatusCode.OK
            )
        }
        val client = GQLClient(HttpClient(mockEngine))
        runBlocking {
            val response = client.stringSendQuery("", query {  })
            response.data?.let { assertEquals(expectedResponse, it) }
        }
    }
}