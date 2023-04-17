package io.github.vincentvibe3.gqlclient

open class QueryElement(val queryElementName: String) {

    protected val fields = ArrayList<Field>()

    fun field(
        name:String,
        init: Field.() -> Unit = {}
    ): Field {
        val field = Field(name)
        field.init()
        fields.add(field)
        return field
    }

    override fun equals(other: Any?): Boolean {
        return if (other is QueryElement){
            other.hashCode()==this.hashCode()
        } else {
            super.equals(other)
        }
    }

    override fun toString(): String {
        var queryString = "$queryElementName{"
        queryString+=fields.joinToString(separator=",") {
            it.toString()
        }
        queryString+="}"
        return queryString
    }

    override fun hashCode(): Int {
        return fields.hashCode()
    }

}