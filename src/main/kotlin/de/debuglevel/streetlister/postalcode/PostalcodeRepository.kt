package de.debuglevel.streetlister.postalcode

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface PostalcodeRepository : CrudRepository<Postalcode, UUID> {
    fun find(code: String): Postalcode
}