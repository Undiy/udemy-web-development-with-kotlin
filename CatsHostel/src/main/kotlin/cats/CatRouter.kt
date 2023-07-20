package cats

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.post
import io.ktor.routing.get

fun Route.catRouter(catsService: CatsService) {
    route("/cats") {
        post {
            with(call) {
                val parameters = receiveParameters()
                val name = requireNotNull(parameters["name"])
                val age = parameters["age"]?.toInt()

                val id = catsService.create(name, age)
                call.response.status(HttpStatusCode.Created)
                respond(id)
            }
        }

        get("/{id}") {
            with(call) {
                val id = requireNotNull(parameters["id"]).toInt()
                val cat = catsService.findById(id)

                if (cat == null) {
                    respond(HttpStatusCode.NotFound)
                } else {
                    respond(cat)
                }
            }
        }

        get {
            call.respond(catsService.all())
        }


    }
}