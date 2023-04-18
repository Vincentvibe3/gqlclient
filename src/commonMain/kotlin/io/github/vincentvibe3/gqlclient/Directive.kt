package io.github.vincentvibe3.gqlclient

data class Directive(
    private val type:DirectiveType,
    private val condition:String
):QueryComponent {

    enum class DirectiveType{
        INCLUDE, SKIP
    }

    override fun toString(): String {
        return when (type){
            DirectiveType.INCLUDE -> "@include(if:\$$condition)"
            DirectiveType.SKIP -> "@skip(if:\$$condition)"
        }
    }

}