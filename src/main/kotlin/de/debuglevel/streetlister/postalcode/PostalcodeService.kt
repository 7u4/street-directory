package de.debuglevel.streetlister.postalcode

import de.debuglevel.streetlister.postalcode.extraction.PostalcodeExtractor
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
            EntityNotFoundException(id)
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
            throw EntityNotFoundException(id)
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

    fun test(): List<Postalcode> {
        return postalcodeExtractor.getPostalcodes()
    }

    class EntityNotFoundException(criteria: Any) : Exception("Entity '$criteria' does not exist.")
}