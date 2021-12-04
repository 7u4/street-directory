package de.debuglevel.streetlister.street

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.util.*

@Repository
interface StreetRepository : CrudRepository<Street, UUID>