package de.debuglevel.streetlister.street.extraction.overpass

import de.debuglevel.openstreetmap.overpass.OverpassQueryBuilder
import de.debuglevel.streetlister.overpass.OverpassService
import de.debuglevel.streetlister.street.Street
import de.debuglevel.streetlister.street.extraction.OverpassStreetExtractorSettings
import de.debuglevel.streetlister.street.extraction.StreetExtractor
import de.debuglevel.streetlister.street.extraction.StreetExtractorSettings
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import mu.KotlinLogging
import java.time.Duration
import javax.inject.Singleton

@Singleton
@Requires(property = "app.street-lister.streets.extractors.overpass.enabled", value = "true")
class OverpassStreetExtractor(
    @Property(name = "app.street-lister.streets.extractors.overpass.timeout.server") val serverTimeout: Duration,
    private val overpassService: OverpassService,
) : StreetExtractor {
    private val logger = KotlinLogging.logger {}

    override fun getStreets(
        streetExtractorSettings: StreetExtractorSettings
    ): List<Street> {
        require(streetExtractorSettings is OverpassStreetExtractorSettings) { "streetExtractorSettings must be OverpassStreetExtractorSettings" }
        logger.debug { "Getting streets for postal code ${streetExtractorSettings.postalcode}..." }

        val streets = executeQuery(streetExtractorSettings.areaId, streetExtractorSettings.postalcode)
            .sortedBy { it.postalcode }

        logger.debug { "Got ${streets.count()} postal codes" }
        return streets
    }

    private fun executeQuery(areaId: Long, postalcode: String): List<Street> {
        val streetListHandler = StreetListHandler(postalcode)
        val query = buildQuery(areaId, postalcode, serverTimeout)

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