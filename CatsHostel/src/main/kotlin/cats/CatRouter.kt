package cats

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.Route
import io.ktor.routing.route
import io.ktor.routing.post
import io.ktor.routing.get
import io.ktor.routing.put
import io.ktor.routing.delete

fun Route.catRouter(catsService: CatsService) {
    route("/cats") {
        post {
            with(call) {
                val parameters = receiveParameters()
                val name = requireNotNull(parameters["name"])
                val age = parameters["age"]?.toInt()

                val id = catsService.create(name, age)
                response.status(HttpStatusCode.Created)
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

        put("/{id}") {
            with(call) {
                val id = requireNotNull(parameters["id"]).toInt()
                val parameters = receiveParameters()
                val name = requireNotNull(parameters["name"])
                val age = parameters["age"]?.toInt() ?: 0

                if (catsService.update(Cat(id, name, age))) {
                    respond(HttpStatusCode.NoContent)
                } else {
                    respond(HttpStatusCode.NotFound)
                }
            }
        }

        delete("/{id}") {
            with(call) {
                val id = requireNotNull(parameters["id"]).toInt()
                if (catsService.delete(id)) {
                    respond(HttpStatusCode.NoContent)
                } else {
                    respond(HttpStatusCode.NotFound)
                }
            }
        }

        get {
            call.respond(catsService.all())
        }


    }
}