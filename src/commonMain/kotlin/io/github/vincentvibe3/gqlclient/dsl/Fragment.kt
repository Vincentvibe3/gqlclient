package io.github.vincentvibe3.gqlclient.dsl

fun fragment(
    name:String,
    type:String,
    init: Fragment.() -> Unit
): Fragment {
    val fragment = Fragment(name, type, false)
    fragment.init()
    return fragment
}

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