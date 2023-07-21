import cats.Cats
import cats.CatsServiceDB
import cats.catRouter
import com.apurebase.kgraphql.GraphQL
import com.fasterxml.jackson.databind.SerializationFeature
import graphql.catGraphql
import io.ktor.application.*
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    val port = 8080

    val server = embeddedServer(Netty, port, module = Application::mainModule)

    server.start()
}

fun Application.mainModule() {
    DB.connect()

    transaction {
        SchemaUtils.create(Cats)
    }

    val catsService = CatsServiceDB()

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(GraphQL) {
        catGraphql(catsService)
    }
    routing {
        trace {
            application.log.debug(it.buildText())
        }
        get {
            context.respond(mapOf("Welcome" to "our Cat Hostel"))
        }
        catRouter(catsService)
    }
}
