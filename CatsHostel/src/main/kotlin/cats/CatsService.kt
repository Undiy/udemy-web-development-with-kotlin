package cats

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

interface CatsService {
    suspend fun create(name: String, age: Int?): Int

    suspend fun all(): List<Cat>

    suspend fun findById(id: Int): Cat?

    suspend fun delete(id: Int): Boolean

    suspend fun update(id: Int, name: String, age: Int?): Boolean
}

class CatsServiceDB : CatsService {
    override suspend fun create(name: String, age: Int?): Int {
        val id = transaction {
            Cats.insertAndGetId {cat ->
                cat[Cats.name] = name
                if (age != null) {
                    cat[Cats.age] = age
                }
            }
        }
        return id.value
    }

    override suspend fun all(): List<Cat> {
        return transaction {
            Cats.selectAll().map { resultRow ->
                resultRow.asCat()
            }
        }
    }

    override suspend fun findById(id: Int): Cat? {
        val row = transaction {
            addLogger(StdOutSqlLogger)
            Cats.select {
                Cats.id eq id
            }.firstOrNull()
        }
        return row?.asCat()
    }

    override suspend fun delete(id: Int): Boolean {
        return transaction {
            addLogger(StdOutSqlLogger)
            Cats.deleteWhere {
                Cats.id eq id
            }> 0
        }
    }

    override suspend fun update(id: Int, name: String, age: Int?): Boolean {
        return transaction {
            Cats.update({ Cats.id eq id }) {cat ->
                cat[Cats.name] = name
                if (age != null) {
                    cat[Cats.age] = age
                }
            } > 0
        }
    }
}

data class Cat(val id: Int, val name: String, val age: Int)

private fun ResultRow.asCat() = Cat(
    this[Cats.id].value,
    this[Cats.name],
    this[Cats.age]
)