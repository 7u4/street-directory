package de.debuglevel.streetlister.postalcode

import de.debuglevel.streetlister.postalcode.extraction.OverpassPostalcodeExtractorSettings
import de.debuglevel.streetlister.postalcode.extraction.PostalcodeExtractor
import io.micronaut.data.exceptions.EmptyResultException
import mu.KotlinLogging
import java.util.*
import javax.inject.Singleton

@Singleton
class PostalcodeService(
    private val postalcodeRepository: PostalcodeRepository,
    private val postalcodeExtractor: PostalcodeExtractor
) {
    private val logger = KotlinLogging.logger {}

    fun get(id: UUID): Postalcode {
        logger.debug { "Getting postalcode with ID '$id'..." }

        val postalcode: Postalcode = postalcodeRepository.findById(id).orElseThrow {
            logger.debug { "Getting postalcode with ID '$id' failed" }
            ItemNotFoundException(id)
        }

        logger.debug { "Got postalcode with ID '$id': $postalcode" }
        return postalcode
    }

    fun add(postalcode: Postalcode): Postalcode {
        logger.debug { "Adding postalcode '$postalcode'..." }

        val savedPostalcode = postalcodeRepository.save(postalcode)

        logger.debug { "Added postalcode: $savedPostalcode" }
        return savedPostalcode
    }

    fun update(id: UUID, postalcode: Postalcode): Postalcode {
        logger.debug { "Updating postalcode '$postalcode' with ID '$id'..." }

        // an object must be known to Hibernate (i.e. retrieved first) to get updated;
        // it would be a "detached entity" otherwise.
        val updatePostalcode = this.get(id).apply {
            code = postalcode.code
            centerLatitude = postalcode.centerLatitude
            centerLongitude = postalcode.centerLongitude
            note = postalcode.note
            lastStreetExtractionOn = postalcode.lastStreetExtractionOn
        }

        val updatedPostalcode = postalcodeRepository.update(updatePostalcode)

        logger.debug { "Updated postalcode: $updatedPostalcode with ID '$id'" }
        return updatedPostalcode
    }

    fun list(): Set<Postalcode> {
        logger.debug { "Getting all postalcodes ..." }

        val postalcodes = postalcodeRepository.findAll().toSet()

        logger.debug { "Got all postalcodes" }
        return postalcodes
    }

    fun delete(id: UUID) {
        logger.debug { "Deleting postalcode with ID '$id'..." }

        if (postalcodeRepository.existsById(id)) {
            postalcodeRepository.deleteById(id)
        } else {
            throw ItemNotFoundException(id)
        }

        logger.debug { "Deleted postalcode with ID '$id'" }
    }

    fun deleteAll() {
        logger.debug { "Deleting all postalcodes..." }

        val countBefore = postalcodeRepository.count()
        postalcodeRepository.deleteAll() // CAVEAT: does not delete dependent entities; use this instead: postalcodeRepository.findAll().forEach { postalcodeRepository.delete(it) }
        val countAfter = postalcodeRepository.count()
        val countDeleted = countBefore - countAfter

        logger.debug { "Deleted $countDeleted of $countBefore postalcodes, $countAfter remaining" }
    }

    fun populate(areaId: Long) {
        logger.debug { "Populating postal codes for area '$areaId'..." }
        val postalcodes = postalcodeExtractor.getPostalcodes(OverpassPostalcodeExtractorSettings(areaId))

        postalcodes.forEach { addOrUpdate(it) }

        logger.debug { "Populated ${postalcodes.count()} postal codes for area '$areaId'" }
    }

    private fun exists(id: UUID): Boolean {
        logger.debug { "Checking if postalcode $id exists..." }
        val isExisting = postalcodeRepository.existsById(id)
        logger.debug { "Checked if postalcode $id exists: $isExisting" }
        return isExisting
    }

    private fun exists(code: String): Boolean {
        logger.debug { "Checking if postalcode $code exists..." }
        val isExisting = postalcodeRepository.existsByCode(code)
        logger.debug { "Checked if postalcode $code exists: $isExisting" }
        return isExisting
    }

    private fun addOrUpdate(postalcode: Postalcode) {
        logger.debug { "Adding or updating $postalcode..." }

        try {
            val existingPostalcode = postalcodeRepository.find(postalcode.code)
            this.update(existingPostalcode.id!!, postalcode)
        } catch (e: EmptyResultException) {
            this.add(postalcode)
        }
    }

    class ItemNotFoundException(criteria: Any) : Exception("Item '$criteria' does not exist.")
}