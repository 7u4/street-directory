package de.debuglevel.streetlister.postalcode.extraction.overpass

import de.debuglevel.openstreetmap.overpass.OverpassQueryBuilder
import de.debuglevel.streetlister.overpass.OverpassService
import de.debuglevel.streetlister.postalcode.Postalcode
import de.debuglevel.streetlister.postalcode.extraction.OverpassPostalcodeExtractorSettings
import de.debuglevel.streetlister.postalcode.extraction.PostalcodeExtractor
import de.debuglevel.streetlister.postalcode.extraction.PostalcodeExtractorSettings
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import mu.KotlinLogging
import java.time.Duration
import javax.inject.Singleton

@Singleton
@Requires(property = "app.street-lister.postalcodes.extractors.overpass.enabled", value = "true")
class OverpassPostalcodeExtractor(
    @Property(name = "app.street-lister.postalcodes.extractors.overpass.timeout.server") val serverTimeout: Duration,
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
        val postalcodeListHandler = PostalcodeListHandler()
        val query = buildQuery(areaId, serverTimeout)

        val postalcodes = overpassService.execute(query, postalcodeListHandler, serverTimeout)

        return postalcodes
    }

    private fun buildQuery(areaId: Long, timeout: Duration? = null): String {
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
        return query.trimIndent()
    }
}