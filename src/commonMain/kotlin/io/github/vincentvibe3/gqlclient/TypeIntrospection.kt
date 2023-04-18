package io.github.vincentvibe3.gqlclient

data class TypeIntrospection(val type:String):QueryElement("__type"),Introspection {

    init {
        components.add(Argument("name", "\"$type\""))
    }
    override fun toString(): String {
        return super.toString()
    }
}