import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val port = 8080

    val server = embeddedServer(Netty, port, module = Application::mainModule)

    server.start()
}

fun Application.mainModule() {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    routing {
        trace {
            application.log.debug(it.buildText())
        }
        get {
            context.respond(mapOf("Welcome" to "our Cat Hostel"))
        }
        get("/{name}") {
            val name = context.parameters["name"]
            context.respond(mapOf("Cat name" to name))
        }
    }
}