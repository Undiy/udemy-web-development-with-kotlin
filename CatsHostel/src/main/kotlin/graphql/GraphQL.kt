package graphql

import cats.CatsServiceDB
import com.apurebase.kgraphql.GraphQL

fun GraphQL.Configuration.catGraphql(catsService: CatsServiceDB) {
    playground = true
    schema {
        mutation("createCat") {
            resolver { name: String, age: Int? ->
                val id = catsService.create(name, age)
                catsService.findById(id)
            }
        }
        mutation("deleteCat") {
            resolver {  id : Int?, name: String? ->
                when {
                    id != null -> catsService.findById(id)
                    name != null -> catsService.findByName(name)
                    else -> null
                }?.apply {
                    catsService.delete(this.id)
                }
            }
        }
        query("cats") {
            resolver { ->
                catsService.all()
            }
        }
        query("cat") {
            resolver { id : Int?, name: String? ->
                when {
                    id != null -> catsService.findById(id)
                    name != null -> catsService.findByName(name)
                    else -> null
                }

            }
        }
    }
}
