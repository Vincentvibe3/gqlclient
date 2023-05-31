package io.github.vincentvibe3.gqlclient.dsl

/**
 * Represents an argument to a field in a query
 *
 * @property name Name of the argument
 * @property types Types or value of the argument
 *
 * @see Field.addArg
 *
 */
data class Argument(
    val name:String,
    val types:List<String>
): QueryComponent {
    constructor(name: String, type:String) : this(name, listOf(type))

    /**
     * Generate the string format of the arguments
     */
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