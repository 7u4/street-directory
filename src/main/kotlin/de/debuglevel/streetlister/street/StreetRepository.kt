package de.debuglevel.streetlister.street

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface StreetRepository : CrudRepository<Street, UUID> {
    fun find(postalcode: String): List<Street>
    fun find(postalcode: String, streetname: String): List<Street>
    fun existsByPostalcodeAndStreetname(postalcode: String, streetname: String): Boolean
}