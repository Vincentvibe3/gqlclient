package io.github.vincentvibe3.gqlclient

data class SchemaIntrospection(val type:String):QueryElement("__schema"),Introspection {

    override fun toString(): String {
        return super.toString()
    }
}