package io.github.vincentvibe3.gqlclient.dsl

data class Variable(
    private val name:String,
    private val type:String
): QueryComponent {
    override fun toString(): String {
        return "$name:$type"
    }
}