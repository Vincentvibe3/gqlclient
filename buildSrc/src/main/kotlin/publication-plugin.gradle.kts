import java.util.*

plugins {
    `maven-publish`
    signing
}

ext["signing.keyId"] = null
ext["signing.password"] = null
ext["signing.key"] = null
ext["ossrhUsername"] = null
ext["ossrhPassword"] = null
ext["stagingRepoId"] = null

val secretPropsFile = project.rootProject.file("local.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        ext[name.toString()] = value
    }
} else {
    ext["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
    ext["signing.password"] = System.getenv("SIGNING_PASSWORD")
    ext["signing.key"] = System.getenv("SIGNING_SECRET_KEY")
    ext["ossrhUsername"] = System.getenv("OSSRH_USERNAME")
    ext["ossrhPassword"] = System.getenv("OSSRH_PASSWORD")
    ext["stagingRepoId"] = System.getenv("STAGING_REPO_ID")
}


fun getExtraString(name: String) = ext[name]?.toString()


publishing {
    // Configure maven central repository
    repositories {
        maven {
            name = "sonatype"
            val repoId = getExtraString("stagingRepoId")
            if (repoId==null){
                setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            } else {
                setUrl("https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/$repoId")
            }
            credentials {
                username = getExtraString("ossrhUsername")
                password = getExtraString("ossrhPassword")
            }
        }
    }

    // Configure all publications
    publications.withType<MavenPublication> {
        // Stub javadoc.jar artifact
        val javadocJar = tasks.register("${this.name}JavadocJar", Jar::class) {
            dependsOn("dokkaHtml")
            archiveClassifier.set("javadoc")
            from(buildDir.resolve("dokka/html"))
            // Each archive name should be distinct. Mirror the format for the sources Jar tasks.
            archiveBaseName.set("${archiveBaseName.get()}-${this.name}")
        }
        artifact(javadocJar)

        // Provide artifacts information requited by Maven Central
        pom {
            name.set("gqlclient")
            description.set("Simple GraphQL client for Kotlin Multiplatform")
            url.set("https://github.com/Vincentvibe3/gqlclient")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("vincentvibe3")
                    name.set("vincentvibe3")
                    email.set("vincentvibe3@gmail.com")
                }
            }
            scm {
                url.set("https://github.com/Vincentvibe3/gqlclient")
            }
        }
    }

}
// Signing artifacts. Signing.* extra properties values will be used
signing {
    useInMemoryPgpKeys(
        getExtraString("signing.keyId"),
        getExtraString("signing.key")?.chunked(64)?.joinToString("\n"),
        getExtraString("signing.password")
    )
    sign(publishing.publications)
}