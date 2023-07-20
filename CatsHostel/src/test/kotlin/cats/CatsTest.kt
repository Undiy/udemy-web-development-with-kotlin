package cats

import asJson
import com.sun.nio.sctp.HandlerResult
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import kotlin.test.assertEquals
import io.ktor.application.Application
import mainModule
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


class CatsTest {
    @Test
    fun `Create cat`() {
        withTestApplication(Application::mainModule) {
            val call = createCat("Fuzzy", 3)

            assertEquals(HttpStatusCode.Created, call.response.status())
        }
    }

    @Test
    fun `All cats`() {
        withTestApplication(Application::mainModule) {
            val beforeCreate = handleRequest(HttpMethod.Get, "/cats")
            assertEquals("[]".asJson(), beforeCreate.response.content?.asJson())

            createCat("Shmuzy", 2)

            val afterCreate = handleRequest(HttpMethod.Get, "/cats")
            assertEquals("""[{"id":1,"name":"Shmuzy","age":2}]""".asJson(), afterCreate.response.content?.asJson())
        }
    }

    @Test
    fun `Cat by ID`() {
        withTestApplication(Application::mainModule) {
            val createCall = createCat("Apollo", 12)
            val id = createCall.response.content

            val afterCreate = handleRequest(HttpMethod.Get, "/cats/$id")
            assertEquals("""{"id":1,"name":"Apollo","age":12}""".asJson(), afterCreate.response.content?.asJson())
        }
    }

    @BeforeEach
    fun cleanup() {
        DB.connect()
        transaction {
            SchemaUtils.drop(Cats)
        }
    }
}

fun TestApplicationEngine.createCat(name: String, age: Int): TestApplicationCall {
    return handleRequest(HttpMethod.Post, "/cats") {
        addHeader(HttpHeaders.ContentType, ContentType.Application.FormUrlEncoded.toString())
        setBody(listOf("name" to name, "age" to age.toString()).formUrlEncode())
    }
}