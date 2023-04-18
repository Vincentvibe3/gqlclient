package io.github.vincentvibe3.gqlclient

data class Fragment(
    private val name:String,
    private val type:String,
    private val inline:Boolean
): QueryElement("fragment") {

    override fun toString(): String {
        var fragmentString = if(inline){
            ""
        } else {
            "$queryElementName "
        }
        fragmentString = "$fragmentString$name on $type{"
        fragmentString+=fields.joinToString(separator=",") {
            it.toString()
        }
        fragmentString+="}"
        return fragmentString
    }

}