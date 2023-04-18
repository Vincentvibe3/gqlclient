package io.github.vincentvibe3.gqlclient

data class Fragment(
    val name:String,
    private val type:String,
    val inline:Boolean
): QueryElement("$name on $type") {

    override fun toString(): String {
        return if (inline){
            super.toString()
        } else {
            "fragment ${super.toString()}"
        }
    }

}