package de.debuglevel.streetlister.street

import de.debuglevel.streetlister.postalcode.PostalcodeService
import de.debuglevel.streetlister.street.extraction.OverpassStreetExtractorSettings
import de.debuglevel.streetlister.street.extraction.StreetExtractor
import io.micronaut.data.exceptions.EmptyResultException
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*
import javax.inject.Singleton

@Singleton
class StreetService(
    private val streetRepository: StreetRepository,
    private val streetExtractor: StreetExtractor,
    private val postalcodeService: PostalcodeService,
) {
    private val logger = KotlinLogging.logger {}

    fun get(id: UUID): Street {
        logger.debug { "Getting street with ID '$id'..." }

        val street: Street = streetRepository.findById(id).orElseThrow {
            logger.debug { "Getting street with ID '$id' failed" }
            EntityNotFoundException(id)
        }

        logger.debug { "Got street with ID '$id': $street" }
        return street
    }

    fun getAll(postalcode: String, streetname: String?): List<Street> {
        logger.debug { "Getting streets with postalcode=$postalcode, streetname=$streetname..." }

        val streets = if (streetname != null) {
            streetRepository.find(postalcode, streetname)
        } else {
            streetRepository.find(postalcode)
        }

        logger.debug { "Got ${streets.count()} streets with postalcode=$postalcode, streetname=$streetname" }
        return streets
    }

    fun get(postalcode: String, streetname: String): Street {
        logger.debug { "Getting street with postalcode=$postalcode, streetname=$streetname..." }

        val street = try {
            getAll(postalcode, streetname).single()
        } catch (e: NoSuchElementException) {
            logger.debug { "Getting street with postalcode=$postalcode, streetname=$streetname failed" }
            throw EntityNotFoundException("postalcode=$postalcode, streetname=$streetname")
        } catch (e: IllegalArgumentException) {
            logger.debug { "Getting street with postalcode=$postalcode, streetname=$streetname failed" }
            throw MultipleEntitiesFoundException("postalcode=$postalcode, streetname=$streetname")
        }

        logger.debug { "Got street with postalcode=$postalcode, streetname=$streetname: $street" }
        return street
    }

    fun add(street: Street): Street {
        logger.debug { "Adding street '$street'..." }

        val savedStreet = streetRepository.save(street)

        logger.debug { "Added street: $savedStreet" }
        return savedStreet
    }

    fun update(id: UUID, street: Street): Street {
        logger.debug { "Updating street '$street' with ID '$id'..." }

        // an object must be known to Hibernate (i.e. retrieved first) to get updated;
        // it would be a "detached entity" otherwise.
        val updateStreet = this.get(id).apply {
            postalcode = street.postalcode
            streetname = street.streetname
            centerLatitude = street.centerLatitude
            centerLongitude = street.centerLongitude
        }

        val updatedStreet = streetRepository.update(updateStreet)

        logger.debug { "Updated street: $updatedStreet with ID '$id'" }
        return updatedStreet
    }

    fun list(): Set<Street> {
        logger.debug { "Getting all streets ..." }

        val streets = streetRepository.findAll().toSet()

        logger.debug { "Got all streets" }
        return streets
    }

    fun delete(id: UUID) {
        logger.debug { "Deleting street with ID '$id'..." }

        if (streetRepository.existsById(id)) {
            streetRepository.deleteById(id)
        } else {
            throw EntityNotFoundException(id)
        }

        logger.debug { "Deleted street with ID '$id'" }
    }

    fun deleteAll() {
        logger.debug { "Deleting all streets..." }

        val countBefore = streetRepository.count()
        streetRepository.deleteAll() // CAVEAT: does not delete dependent entities; use this instead: streetRepository.findAll().forEach { streetRepository.delete(it) }
        val countAfter = streetRepository.count()
        val countDeleted = countBefore - countAfter

        logger.debug { "Deleted $countDeleted of $countBefore streets, $countAfter remaining" }
    }

    fun populate() {
        logger.debug { "Populating streets..." }
        val areaIds = mapOf(
            "Germany" to 3600051477,
            "Bavaria" to 3602145268,
            "Upper Frankonia" to 3600017592,
        )
        val areaId = areaIds["Germany"]!!

        postalcodeService.list().forEach { postalcode ->
            val streets = streetExtractor.getStreets(OverpassStreetExtractorSettings(areaId, postalcode.code))
            streets.forEach { street -> addOrUpdate(street) }

            postalcode.lastStreetExtractionOn = LocalDateTime.now()
            postalcodeService.update(postalcode.id!!, postalcode)
        }

        logger.debug { "Populated streets" }
    }

    private fun exists(id: UUID): Boolean {
        logger.debug { "Checking if street $id exists..." }
        val isExisting = streetRepository.existsById(id)
        logger.debug { "Checked if street $id exists: $isExisting" }
        return isExisting
    }

    private fun exists(postalcode: String, streetname: String): Boolean {
        logger.debug { "Checking if street '$streetname' in $postalcode exists..." }
        val isExisting = streetRepository.existsByPostalcodeAndStreetname(postalcode, streetname)
        logger.debug { "Checked if street '$streetname' in $postalcode exists: $isExisting" }
        return isExisting
    }

    private fun addOrUpdate(street: Street) {
        logger.debug { "Adding or updating $street..." }

        try {
            val existingStreet = get(street.postalcode, street.streetname)
            this.update(existingStreet.id!!, street)
        } catch (e: EmptyResultException) {
            this.add(street)
        }
    }

    class EntityNotFoundException(criteria: Any) : Exception("Entity '$criteria' does not exist.")
    class MultipleEntitiesFoundException(criteria: Any) : Exception("More than one item meets '$criteria'.")
}