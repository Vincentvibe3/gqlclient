package io.github.vincentvibe3.gqlclient.dsl

data class Argument(
    val name:String,
    val types:List<String>
): QueryComponent {
    constructor(name: String, type:String) : this(name, listOf(type))
    override fun toString(): String {
        return if (types.size==1){
            "$name:${types.first()}"
        } else if (types.isNotEmpty()){
            val typesJoin = types.joinToString(separator = ",")
            "$name:[$typesJoin]"
        } else {
            ""
        }
    }
}