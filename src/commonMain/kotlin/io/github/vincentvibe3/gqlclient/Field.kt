package io.github.vincentvibe3.gqlclient


data class Field(
    val name:String,
): QueryElement(name){

    var args:List<Pair<String, String>>?=null
    var alias:String? = null
    private val fragments = ArrayList<Fragment>()
    private val usedFragments = ArrayList<String>()
    private var directives = ""

    fun fragment(
        type:String,
        init: Fragment.() -> Unit
    ): Fragment {
        val fragment = Fragment("...", type, true)
        fragments.add(fragment)
        fragment.init()
        return fragment
    }

    fun include(variable:String){
        directives+="@include(if:\$$variable)"
    }

    fun skip(variable: String){
        directives+="@skip(if:\$$variable)"
    }

    fun useFragment(name:String){
        usedFragments.add(name)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Field){
            other.hashCode()==this.hashCode()
        } else {
            super.equals(other)
        }
    }

    override fun toString(): String {
        var fieldString = ""
        alias?.let { fieldString+="$it:" }
        fieldString+=queryElementName
        val frozenArg = args
        frozenArg?.let { fieldString+="("+it.joinToString(separator = ",") { arg -> "${arg.first}:${arg.second}" }+")"}
        fieldString+=directives
        if (fields.isNotEmpty()||usedFragments.isNotEmpty()||fragments.isNotEmpty()){
            val sections = arrayListOf<String>()
            fieldString+="{"
            if (fields.isNotEmpty()){
                sections.add(fields.joinToString(separator=",") { it.toString()},)
            }
            if (fragments.isNotEmpty()){
                sections.add(fragments.joinToString(separator = ",") { it.toString() })
            }
            if (usedFragments.isNotEmpty()){
                sections.add(usedFragments.joinToString(separator = ","){ "...$it" })
            }
            fieldString+=sections.joinToString(separator = ",")
            fieldString+="}"
        }
        return fieldString
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (args?.hashCode() ?: 0)
        result = 31 * result + (alias?.hashCode() ?: 0)
        result = 31 * result + fragments.hashCode()
        result = 31 * result + usedFragments.hashCode()
        return result
    }

}