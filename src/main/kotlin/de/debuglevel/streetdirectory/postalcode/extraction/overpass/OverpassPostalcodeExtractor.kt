package de.debuglevel.streetdirectory.postalcode.extraction.overpass

import de.debuglevel.openstreetmap.overpass.OverpassQueryBuilder
import de.debuglevel.streetdirectory.overpass.OverpassService
import de.debuglevel.streetdirectory.postalcode.Postalcode
import de.debuglevel.streetdirectory.postalcode.extraction.OverpassPostalcodeExtractorSettings
import de.debuglevel.streetdirectory.postalcode.extraction.PostalcodeExtractor
import de.debuglevel.streetdirectory.postalcode.extraction.PostalcodeExtractorSettings
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import mu.KotlinLogging
import java.time.Duration

@Singleton
@Requires(property = "app.street-directory.postalcodes.extractors.overpass.enabled", value = "true")
class OverpassPostalcodeExtractor(
    @Property(name = "app.street-directory.postalcodes.extractors.overpass.timeout.server") val serverTimeout: Duration,
    private val overpassService: OverpassService,
) : PostalcodeExtractor {
    private val logger = KotlinLogging.logger {}

    override fun getPostalcodes(
        postalcodeExtractorSettings: PostalcodeExtractorSettings
    ): List<Postalcode> {
        require(postalcodeExtractorSettings is OverpassPostalcodeExtractorSettings) { "postalcodeExtractorSettings must be OverpassPostalcodeExtractorSettings" }
        logger.debug { "Getting postal codes for area ${postalcodeExtractorSettings.areaId}..." }

        val postalcodes = executeQuery(postalcodeExtractorSettings.areaId)
            .sortedBy { it.code }

        logger.debug { "Got ${postalcodes.count()} postal codes" }
        return postalcodes
    }

    private fun executeQuery(areaId: Long): List<Postalcode> {
        logger.trace { "Executing query for areaId=$areaId" }

        val postalcodeListHandler = PostalcodeListHandler()
        val query = buildQuery(areaId, serverTimeout)

        val postalcodes = overpassService.execute(query, postalcodeListHandler, serverTimeout)

        logger.trace { "Executed query for areaId=$areaId: ${postalcodes.size} postal codes" }
        return postalcodes
    }

    private fun buildQuery(areaId: Long, timeout: Duration? = null): String {
        logger.trace { "Building query for areaId=$areaId" }

        var query = ""
        query += OverpassQueryBuilder.timeout(timeout)
        query += OverpassQueryBuilder.csvOutput(
            listOf("::id", "::type", "::otype", "::lat", "::lon", "postal_code", "note"),
            true,
            "\t"
        )

        query += """        
            area(${areaId});
            relation["boundary"="postal_code"](area);
            out center;
               """
        query = query.trimIndent()

        logger.trace { "Built query for areaId=$areaId: $query" }
        return query
    }
}