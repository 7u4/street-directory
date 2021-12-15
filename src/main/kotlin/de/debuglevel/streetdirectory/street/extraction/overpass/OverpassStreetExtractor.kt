package de.debuglevel.streetdirectory.street.extraction.overpass

import de.debuglevel.openstreetmap.overpass.OverpassQueryBuilder
import de.debuglevel.streetdirectory.overpass.OverpassService
import de.debuglevel.streetdirectory.postalcode.Postalcode
import de.debuglevel.streetdirectory.street.Street
import de.debuglevel.streetdirectory.street.extraction.OverpassStreetExtractorSettings
import de.debuglevel.streetdirectory.street.extraction.StreetExtractor
import de.debuglevel.streetdirectory.street.extraction.StreetExtractorSettings
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import jakarta.inject.Singleton
import mu.KotlinLogging
import java.time.Duration

@Singleton
@Requires(property = "app.street-directory.streets.extractors.overpass.enabled", value = "true")
class OverpassStreetExtractor(
    @Property(name = "app.street-directory.streets.extractors.overpass.timeout.server") val serverTimeout: Duration,
    private val overpassService: OverpassService,
) : StreetExtractor {
    private val logger = KotlinLogging.logger {}

    override fun getStreets(
        streetExtractorSettings: StreetExtractorSettings
    ): List<Street> {
        require(streetExtractorSettings is OverpassStreetExtractorSettings) { "streetExtractorSettings must be OverpassStreetExtractorSettings" }
        logger.debug { "Getting streets for postal code ${streetExtractorSettings.postalcode}..." }

        val streets = executeQuery(streetExtractorSettings.areaId, streetExtractorSettings.postalcode)
            .sortedBy { it.postalcode.code }

        logger.debug { "Got ${streets.count()} postal codes" }
        return streets
    }

    private fun executeQuery(areaId: Long, postalcode: Postalcode): List<Street> {
        val streetListHandler = StreetListHandler(postalcode)
        val query = buildQuery(areaId, postalcode.code, serverTimeout)

        val streets = overpassService.execute(query, streetListHandler, serverTimeout)

        return streets
    }

    private fun buildQuery(areaId: Long, postalcode: String, timeout: Duration? = null): String {
        var query = ""
        query += OverpassQueryBuilder.timeout(timeout)
        query += OverpassQueryBuilder.csvOutput(
            listOf("::id", "::type", "::otype", "::lat", "::lon", "name"),
            true,
            "\t"
        )

        query += """        
            area(${areaId});
            rel["boundary"="postal_code"]["postal_code"="${postalcode}"](area);
            map_to_area;
            way(area)[highway][name];
            out center;
               """
        return query.trimIndent()
    }
}