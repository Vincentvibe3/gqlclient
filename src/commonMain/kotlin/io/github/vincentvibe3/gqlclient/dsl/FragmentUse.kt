package io.github.vincentvibe3.gqlclient.dsl

data class FragmentUse(
    val name:Fragment
): QueryElement("...${name.name}"){
    override fun toString(): String {
        return super.toString()
    }
}