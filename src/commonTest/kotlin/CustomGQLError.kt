import io.github.vincentvibe3.gqlclient.http.GQLError
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class CustomGQLError(
    override val message: String,
    val status:Long,
    override val locations: List<GQLError.ErrorLocations>?,
    override val path: List<String>? = null,
    override val extensions: JsonObject? = null
) : GQLError