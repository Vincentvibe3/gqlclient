import io.github.vincentvibe3.gqlclient.dsl.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DSLTests {

    @Test
    fun testField(){
        val query1Expected = "query {hero{name,appearsIn}}"
        val query = query {
            field("hero"){
                field("name")
                field("appearsIn")
            }
        }
        assertEquals(query1Expected, query.toString())
        val query2Expected = "query {human(id:\"1000\"){name,height}}"
        val query2 = query {
            field("human"){
                addArg("id", 1000, Field.ArgumentType.STRING_LITERAL)
                field("name")
                field("height")
            }
        }
        assertEquals(query2Expected, query2.toString())
        val query3Expected = "query {empireHero:hero(episode:EMPIRE){name},jediHero:hero(episode:JEDI){name}}"
        val query3 = query {
            field("hero"){
                addArg("episode", "EMPIRE", Field.ArgumentType.TYPE)
                alias = "empireHero"
                field("name")
            }
            field("hero"){
                addArg("episode", "JEDI", Field.ArgumentType.TYPE)
                alias = "jediHero"
                field("name")
            }
        }
        val query3copy = query {
            field("hero"){
                addArg("episode", "EMPIRE", Field.ArgumentType.TYPE)
                alias = "empireHero"
                field("name")
            }
            field("hero"){
                addArg("episode", "JEDI", Field.ArgumentType.TYPE)
                alias = "jediHero"
                field("name")
            }
        }
        assertEquals(query3Expected, query3.toString())
        assertEquals(query3, query3copy)
        assertTrue(query3!=query2)
    }

    @Test
    fun testFragment(){
        val query = query {
            val fragment = fragment("comparisonFields", "Character") {
                field("name")
                field("appearsIn")
                field("friends"){
                    field("name")
                }
            }
            field("hero"){
                addArg("episode", "EMPIRE", Field.ArgumentType.TYPE)
                alias = "leftComparison"
                useFragment(fragment)
            }
            field("hero"){
                addArg("episode", "JEDI", Field.ArgumentType.TYPE)
                alias = "rightComparison"
                useFragment(fragment)
            }
        }
        val expectedQuery = "query {leftComparison:hero(episode:EMPIRE){...comparisonFields},"+
                "rightComparison:hero(episode:JEDI){...comparisonFields}},"+
                "fragment comparisonFields on Character{name,appearsIn,friends{name}}"
        assertEquals(expectedQuery, query.toString())
    }


    @Test
    fun testExternalFragment(){
        val fragment = fragment("comparisonFields", "Character") {
            field("name")
            field("appearsIn")
            field("friends"){
                field("name")
            }
        }
        val query = query {
            registerFragment(fragment)
            field("hero"){
                addArg("episode", "EMPIRE", Field.ArgumentType.TYPE)
                alias = "leftComparison"
                useFragment(fragment)
            }
            field("hero"){
                addArg("episode", "JEDI", Field.ArgumentType.TYPE)
                alias = "rightComparison"
                useFragment(fragment)
            }
        }
        val expectedQuery = "query {leftComparison:hero(episode:EMPIRE){...comparisonFields},"+
                "rightComparison:hero(episode:JEDI){...comparisonFields}},"+
                "fragment comparisonFields on Character{name,appearsIn,friends{name}}"
        assertEquals(expectedQuery, query.toString())
    }

    @Test
    fun inlineFragments(){
        val expectedQuery = "query {hero(episode:JEDI){name,... on Droid{primaryFunction},... on Human{height}}}"
        val query = query {
            field("hero"){
                addArg("episode", "JEDI", Field.ArgumentType.TYPE)
                field("name")
                fragment("Droid"){
                    field("primaryFunction")
                }
                fragment("Human"){
                    field("height")
                }
            }
        }
        assertEquals(expectedQuery, query.toString())
    }

    @Test
    fun variableInFragment(){
        val query = query {
            val fragment = fragment("comparisonFields", "Character") {
                field("name")
                field("appearsIn")
                field("friends"){
                    field("name")
                    addArg("name", Variable("charName", "STRING"))
                }
            }
            field("hero"){
                addArg("episode", "EMPIRE", Field.ArgumentType.TYPE)
                alias = "leftComparison"
                useFragment(fragment)
            }
            field("hero"){
                addArg("episode", "JEDI", Field.ArgumentType.TYPE)
                alias = "rightComparison"
                useFragment(fragment)
            }
        }
        val expectedQuery = "query (\$charName:STRING){leftComparison:hero(episode:EMPIRE){...comparisonFields},"+
                "rightComparison:hero(episode:JEDI){...comparisonFields}},"+
                "fragment comparisonFields on Character{name,appearsIn,friends(name:\$charName){name}}"
        assertEquals(expectedQuery, query.toString())
    }

    @Test
    fun variablesInInlineFragments(){
        val expectedQuery = "query (\$droidName:STRING){hero(episode:JEDI){name,... on Droid{primaryFunction(name:\$droidName)},... on Human{height}}}"
        val query = query {
            field("hero"){
                addArg("episode", "JEDI", Field.ArgumentType.TYPE)
                field("name")
                fragment("Droid"){
                    field("primaryFunction"){
                        addArg("name", Variable("droidName", "STRING"))
                    }
                }
                fragment("Human"){
                    field("height")
                }
            }
        }
        assertEquals(expectedQuery, query.toString())
    }

    @Test
    fun variableInExternalFragment(){
        val fragment = fragment("comparisonFields", "Character") {
            field("name")
            field("appearsIn")
            field("friends"){
                field("name")
                addArg("name", Variable("charName", "STRING"))
            }
        }
        val query = query {
            registerFragment(fragment)
            field("hero"){
                addArg("episode", "EMPIRE", Field.ArgumentType.TYPE)
                alias = "leftComparison"
                useFragment(fragment)
            }
            field("hero"){
                addArg("episode", "JEDI", Field.ArgumentType.TYPE)
                alias = "rightComparison"
                useFragment(fragment)
            }
        }
        val expectedQuery = "query (\$charName:STRING){leftComparison:hero(episode:EMPIRE){...comparisonFields},"+
                "rightComparison:hero(episode:JEDI){...comparisonFields}},"+
                "fragment comparisonFields on Character{name,appearsIn,friends(name:\$charName){name}}"
        assertEquals(expectedQuery, query.toString())
    }

    @Test
    fun directives(){
        val includeExpectedQuery = "query Hero(\$episode:Episode,\$withFriends:Boolean!){"+
                "hero(episode:\$episode){name,friends@include(if:\$withFriends){name}}}"
        val includeQuery = query("Hero") {
            field("hero"){
                addArg("episode", Variable("episode", "Episode"))
                field("name")
                field("friends"){
                    include(Variable("withFriends", "Boolean!"))
                    field("name")
                }
            }
        }
        val skipExpectedQuery = "query Hero(\$withFriends:Boolean!,\$episode:Episode){"+
                "hero(episode:\$episode){name,friends@skip(if:\$withFriends){name}}}"
        val skipQuery = query("Hero") {
            variable("withFriends", "Boolean!")
            field("hero"){
                addArg("episode", Variable("episode", "Episode"))
                field("name")
                field("friends"){
                    skip(Variable("withFriends", "Boolean!"))
                    field("name")
                }
            }
        }
        assertEquals(skipExpectedQuery, skipQuery.toString())
        assertEquals(includeExpectedQuery, includeQuery.toString())
    }

    @Test
    fun mutation(){
        val expectedMutation = "mutation CreateReviewForEpisode(\$ep:Episode!,\$review:ReviewInput!){"+
                "createReview(episode:\$ep,review:\$review){stars,commentary}}"
        val mutation = mutation("CreateReviewForEpisode") {
            variable("ep", "Episode!")
            field("createReview"){
                addArg("episode", Variable("ep", "Episode!"))
                addArg("review", Variable("review", "ReviewInput!"))
                field("stars")
                field("commentary")
            }
        }
        assertEquals(expectedMutation, mutation.toString())
    }

    @Test
    fun introspection(){
        val typeExpected = "query {__type(name:\"Droid\"){name,kind}}"
        val type = query {
            type("Droid"){
                field("name")
                field("kind")
            }
        }
        val typeNameExpected = "query {search(text:\"an\"){__typename,... on Human{name},... on Droid{name},... on Starship{name}}}"
        val typename = query {
            field("search"){
                addArg("text","an", Field.ArgumentType.STRING_LITERAL)
                typename()
                fragment("Human"){
                    field("name")
                }
                fragment("Droid"){
                    field("name")
                }
                fragment("Starship"){
                    field("name")
                }
            }
        }
        assertEquals(typeNameExpected, typename.toString())
        assertEquals(typeExpected, type.toString())
    }

    @Test
    fun schemaIntrospection(){
        val expectedQuery = "query {__schema{types{name}}}"
        val query = query {
            schema {
                field("types"){
                    field("name")
                }
            }
        }
        assertEquals(expectedQuery, query.toString())
    }

    @Test
    fun nestedFragments(){
        val expectedQuery = "query {type2{...f2}},fragment f1 on type{id},fragment f2 on type2{...f1}"
        val fragment1 = fragment("f1", "type"){
            field("id")
        }
        val fragment2 = fragment("f2", "type2"){
            useFragment(fragment1)
        }
        val query = query {
            field("type2"){
                useFragment(fragment2)
            }
        }
        assertEquals(expectedQuery, query.toString())
    }

    @Test
    fun deepNestedFragments(){
        val expectedQuery = "query {type2{...f2}},fragment f1 on type{id},fragment f2 on type2{type{...f1}}"
        val fragment1 = fragment("f1", "type"){
            field("id")
        }
        val fragment2 = fragment("f2", "type2"){
            field("type"){
                useFragment(fragment1)
            }
        }
        val query = query {
            field("type2"){
                useFragment(fragment2)
            }
        }
        assertEquals(expectedQuery, query.toString())
    }

    @Test
    fun deepNestedFragmentsVariables(){
        val expectedQuery = "query (\$var1:type3){type2{...f2}},fragment f1 on type{id(arg:\$var1)},fragment f2 on type2{type{...f1}}"
        val fragment1 = fragment("f1", "type"){
            field("id"){
                addArg("arg", Variable("var1", "type3"))
            }
        }
        val fragment2 = fragment("f2", "type2"){
            field("type"){
                useFragment(fragment1)
            }
        }
        val query = query {
            registerFragment(fragment1)
            field("type2"){
                useFragment(fragment2)
            }
        }
        assertEquals(expectedQuery, query.toString())
    }
}