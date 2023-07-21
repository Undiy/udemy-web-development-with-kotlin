package graphql

import asJson
import cats.Cats
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import mainModule
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CatsGraphQLTest {
    @BeforeEach
    fun setup() {
        DB.connect()
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Cats)
            Cats.deleteAll()
            Cats.insert {
                it[name] = "Shmuzy"
                it[age] = 3
            }
            Cats.insert {
                it[name] = "Fluffy"
                it[age] = 2
            }
        }
    }

    @Test
    fun `GraphQL returns cats`() {
        withTestApplication(Application::mainModule) {
            val graphqlResponse = handleRequest(HttpMethod.Post, "/graphql") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("""
                        {
                            cats {
                                name
                                age
                            }
                        }
                    """.asGraphQLQuery())
            }

            assertEquals("""{"data":{"cats":[{"name":"Shmuzy","age":3},{"name":"Fluffy","age":2}]}}""".asJson(),
                graphqlResponse.response.content?.asJson())
        }
    }

    @Test
    fun `GraphQL returns a cat`() {
        withTestApplication(Application::mainModule) {
            val dbCat = transaction {
                Cats.selectAll().first()
            }
            val graphqlResponse = handleRequest(HttpMethod.Post, "/graphql") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("""
                        {
                            cat(id: ${dbCat[Cats.id]}) {
                                name
                            }
                        }
                    """.asGraphQLQuery())
            }

            assertEquals("""{"data":{"cat":{"name":"Shmuzy"}}}""".asJson(),
                graphqlResponse.response.content?.asJson())
        }
    }

    @Test
    fun `GraphQL returns a cat by name`() {
        withTestApplication(Application::mainModule) {
            val graphqlResponse = handleRequest(HttpMethod.Post, "/graphql") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("""
                        {
                            cat(name: \"Shmuzy\") {
                                name
                            }
                        }
                    """.asGraphQLQuery())
            }

            assertEquals("""{"data":{"cat":{"name":"Shmuzy"}}}""".asJson(),
                graphqlResponse.response.content?.asJson())
        }
    }

    @Test
    fun `GraphQL creates a cat`() {
        withTestApplication(Application::mainModule) {
            val graphqlResponse = handleRequest(HttpMethod.Post, "/graphql") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("""
                        mutation {
                            createCat(name: \"Fibi\", age: 4) {
                                name
                                age
                            }
                        }
                    """.asGraphQLQuery())
            }

            assertEquals("""{"data":{"createCat":{"name":"Fibi", "age": 4}}}""".asJson(),
                graphqlResponse.response.content?.asJson())
        }
    }

    @Test
    fun `GraphQL deletes a cat`() {
        withTestApplication(Application::mainModule) {
            val graphqlResponse = handleRequest(HttpMethod.Post, "/graphql") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody("""
                        mutation {
                            deleteCat(name: \"Shmuzy\") {
                                name
                            }
                        }
                    """.asGraphQLQuery())
            }

            assertEquals("""{"data":{"deleteCat":{"name":"Shmuzy"}}}""".asJson(),
                graphqlResponse.response.content?.asJson())
        }
    }
}

private fun String.asGraphQLQuery() = """{"query": "$this"}""".replace("\n", "")