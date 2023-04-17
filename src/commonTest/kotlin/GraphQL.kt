import io.github.vincentvibe3.gqlclient.mutation
import io.github.vincentvibe3.gqlclient.query
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphQL {

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
                args = listOf(Pair("id", "\"1000\""))
                field("name")
                field("height")
            }
        }
        assertEquals(query2Expected, query2.toString())
        val query3Expected = "query {empireHero:hero(episode:EMPIRE){name},jediHero:hero(episode:JEDI){name}}"
        val query3 = query {
            field("hero"){
                args = listOf(Pair("episode", "EMPIRE"))
                alias = "empireHero"
                field("name")
            }
            field("hero"){
                args = listOf(Pair("episode", "JEDI"))
                alias = "jediHero"
                field("name")
            }
        }
        val query3copy = query {
            field("hero"){
                args = listOf(Pair("episode", "EMPIRE"))
                alias = "empireHero"
                field("name")
            }
            field("hero"){
                args = listOf(Pair("episode", "JEDI"))
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
            field("hero"){
                args = listOf(Pair("episode", "EMPIRE"))
                alias = "leftComparison"
                useFragment("comparisonFields")
            }
            field("hero"){
                args = listOf(Pair("episode", "JEDI"))
                alias = "rightComparison"
                useFragment("comparisonFields")
            }
            fragment("comparisonFields", "Character") {
                field("name")
                field("appearsIn")
                field("friends"){
                    field("name")
                }
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
                args=listOf(Pair("episode", "JEDI"))
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
    fun directives(){
        val expectedQuery = "query Hero(\$episode:Episode,\$withFriends:Boolean!){"+
                "hero(episode:\$episode){name,friends@include(if:\$withFriends){name}}}"
        val query = query("Hero") {
            variable("episode", "Episode")
            variable("withFriends", "Boolean!")
            field("hero"){
                args = listOf(Pair("episode", "\$episode"))
                field("name")
                field("friends"){
                    include("withFriends")
                    field("name")
                }
            }
        }
        assertEquals(expectedQuery, query.toString())
    }

    @Test
    fun mutation(){
        val expectedMutation = "mutation CreateReviewForEpisode(\$ep:Episode!,\$review:ReviewInput!){"+
                "createReview(episode:\$ep,review:\$review){stars,commentary}}"
        val mutation = mutation("CreateReviewForEpisode") {
            variable("ep", "Episode!")
            variable("review", "ReviewInput!")
            field("createReview"){
                args= listOf(Pair("episode", "\$ep"),Pair("review", "\$review"))
                field("stars")
                field("commentary")
            }
        }
        assertEquals(expectedMutation, mutation.toString())
    }

}