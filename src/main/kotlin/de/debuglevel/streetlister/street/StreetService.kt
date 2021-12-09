package de.debuglevel.streetlister.street

import de.debuglevel.streetlister.postalcode.Postalcode
import de.debuglevel.streetlister.postalcode.PostalcodeService
import de.debuglevel.streetlister.street.extraction.OverpassStreetExtractorSettings
import de.debuglevel.streetlister.street.extraction.StreetExtractor
import mu.KotlinLogging
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future
import jakarta.inject.Singleton
import javax.transaction.Transactional

@Singleton
open class StreetService(
    private val streetRepository: StreetRepository,
    private val streetExtractor: StreetExtractor,
    private val postalcodeService: PostalcodeService,
    private val streetProperties: StreetProperties,
) {
    private val logger = KotlinLogging.logger {}

    private val queueMonitor = mutableMapOf<Postalcode, Future<*>>()
    private val executor = Executors.newFixedThreadPool(streetProperties.maximumThreads)

    fun get(id: UUID): Street {
        logger.debug { "Getting street with ID '$id'..." }

        val street: Street = streetRepository.findById(id).orElseThrow {
            logger.debug { "Getting street with ID '$id' failed" }
            PostalcodeService.ItemNotFoundException(id)
        }

        logger.debug { "Got street with ID '$id': $street" }
        return street
    }

    /**
     * Get the [Street] named [streetname] in [postalcode].
     */
    fun get(postalcodeId: UUID, streetname: String): Street {
        logger.debug { "Getting street with postalcodeId=$postalcodeId, streetname=$streetname..." }

        val postalcode = postalcodeService.get(postalcodeId)

        val street = try {
            postalcode.streets.single { it.streetname == streetname }
        } catch (e: NoSuchElementException) {
            logger.debug { "Getting street with postalcode=$postalcode, streetname=$streetname failed; not found" }
            throw ItemNotFoundException("postalcode=$postalcode, streetname=$streetname")
        } catch (e: IllegalArgumentException) {
            logger.debug { "Getting street with postalcode=$postalcode, streetname=$streetname failed; multiple found" }
            throw MultipleItemsFoundException("postalcode=$postalcode, streetname=$streetname")
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
            throw e
        }

        logger.debug { "Got street with postalcode=$postalcode, streetname=$streetname: $street" }
        return street
    }

    fun add(street: Street): Street {
        logger.debug { "Adding street '$street'..." }

        // Get postalcode again to avoid detached entity
        val postalcode = postalcodeService.get(street.postalcode.id!!)
        val savingStreet = street.copy(postalcode = postalcode)

        val savedStreet = streetRepository.save(savingStreet)

        logger.debug { "Added street: $savedStreet" }
        return savedStreet
    }

    fun update(id: UUID, street: Street): Street {
        logger.debug { "Updating street '$street' with ID '$id'..." }

        // Get postalcode again to avoid detached entity
        val postalcodeObj = postalcodeService.get(street.postalcode.id!!)

        // an object must be known to Hibernate (i.e. retrieved first) to get updated;
        // it would be a "detached entity" otherwise.
        val updateStreet = this.get(id).apply {
            postalcode = postalcodeObj
            streetname = street.streetname
            centerLatitude = street.centerLatitude
            centerLongitude = street.centerLongitude
        }

        val updatedStreet = streetRepository.update(updateStreet)

        logger.debug { "Updated street: $updatedStreet with ID '$id'" }
        return updatedStreet
    }

    fun getAll(): Set<Street> {
        logger.debug { "Getting all streets ..." }

        val streets = streetRepository.findAll().toSet()

        logger.debug { "Got ${streets.size} streets" }
        return streets
    }

    fun delete(id: UUID) {
        logger.debug { "Deleting street with ID '$id'..." }

        if (streetRepository.existsById(id)) {
            streetRepository.deleteById(id)
        } else {
            throw ItemNotFoundException(id)
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

    fun populate(areaId: Long) {
        logger.debug { "Populating streets for area '$areaId'..." }

        postalcodeService.getAll().forEach { postalcode ->
            logger.debug { "Enqueuing street population for postal code $postalcode..." }
            if (!queueMonitor.contains(postalcode)) {
                val future = executor.submit {
                    logger.debug { "Starting enqueued street population for postal code $postalcode..." }
                    populate(postalcode, areaId)
                    logger.debug { "Ended enqueued street population for postal code $postalcode" }
                }
                queueMonitor[postalcode] = future
                logger.debug { "Enqueued street population for postal code $postalcode" }
            } else {
                logger.debug { "Not enqueued street population for postal code $postalcode as it was already in the queue" }
            }
        }

        logger.debug { "Populated streets for area '$areaId'" }
    }

    @Transactional
    open fun populate(postalcode: Postalcode, areaId: Long) {
        logger.debug { "Starting enqueued street population for postal code $postalcode..." }

        try {
            // Get streets from Overpass API
            val streets = streetExtractor.getStreets(OverpassStreetExtractorSettings(areaId, postalcode))
            // Add (or update) each street
            streets.forEach { street -> updateOrAdd(street) }

            // Update the last extraction datetime on the Postalcode
            postalcode.lastStreetExtractionOn = LocalDateTime.now()
            postalcodeService.update(postalcode.id!!, postalcode)
        } catch (e: Exception) {
            logger.error(e) { "Unhandled exception" }
        }

        queueMonitor.remove(postalcode)
        logger.debug { "Enqueued street population for postal code $postalcode is done" }
    }

    private fun exists(id: UUID): Boolean {
        logger.debug { "Checking if street $id exists..." }
        val isExisting = streetRepository.existsById(id)
        logger.debug { "Checked if street $id exists: $isExisting" }
        return isExisting
    }

    private fun updateOrAdd(street: Street): Street {
        logger.debug { "Updating or adding $street..." }

        return try {
            val existingStreet = get(street.postalcode.id!!, street.streetname)
            this.update(existingStreet.id!!, street)
        } catch (e: ItemNotFoundException) {
            this.add(street)
        }
    }

    class ItemNotFoundException(criteria: Any) : Exception("Item '$criteria' does not exist.")
    class MultipleItemsFoundException(criteria: Any) : Exception("More than one item meets '$criteria'.")
}