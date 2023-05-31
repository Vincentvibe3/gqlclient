package io.github.vincentvibe3.gqlclient.dsl

/**
 * Represents a directive for use in a field
 *
 * @property type The type of directive to add.
 * @property condition The condition to be checked
 *
 * @see Field.skip
 * @see Field.include
 *
 */
data class Directive(
    private val type: DirectiveType,
    private val condition:String
): QueryComponent {

    /**
     * Types of supported directives
     */
    enum class DirectiveType{
        INCLUDE, SKIP
    }

    /**
     * Generate the directive to be added
     */
    override fun toString(): String {
        return when (type){
            DirectiveType.INCLUDE -> "@include(if:\$$condition)"
            DirectiveType.SKIP -> "@skip(if:\$$condition)"
        }
    }

}