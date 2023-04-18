package io.github.vincentvibe3.gqlclient

data class FragmentUse(
    val name:String
):QueryElement("...$name"){
    override fun toString(): String {
        return super.toString()
    }
}