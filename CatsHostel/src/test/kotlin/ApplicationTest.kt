import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.application.Application
import io.ktor.http.*
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApplicationTest {
    @Test
    fun emptyPath() {
        withTestApplication(Application::mainModule) {
            val call = handleRequest(HttpMethod.Get, "")

            assertEquals(HttpStatusCode.OK, call.response.status())
        }
    }
}

fun String.asJson() = ObjectMapper().readTree(this)
