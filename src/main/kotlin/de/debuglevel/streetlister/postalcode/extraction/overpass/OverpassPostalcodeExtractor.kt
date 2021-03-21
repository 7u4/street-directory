package de.debuglevel.streetlister.postalcode.extraction.overpass

import de.debuglevel.streetlister.overpass.OverpassQueryBuilder
import de.debuglevel.streetlister.postalcode.Postalcode
import de.debuglevel.streetlister.postalcode.extraction.OverpassPostalcodeExtractorSettings
import de.debuglevel.streetlister.postalcode.extraction.PostalcodeExtractor
import de.debuglevel.streetlister.postalcode.extraction.PostalcodeExtractorSettings
import de.westnordost.osmapi.OsmConnection
import de.westnordost.osmapi.overpass.OverpassMapDataDao
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import mu.KotlinLogging
import java.time.Duration
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

@Singleton
@Requires(property = "app.street-lister.postalcodes.extractors.overpass.enabled", value = "true")
class OverpassPostalcodeExtractor(
    @Property(name = "app.street-lister.postalcodes.extractors.overpass.base-url") val baseUrl: String,
    @Property(name = "app.street-lister.postalcodes.extractors.overpass.timeout.server") val clientTimeout: Duration,
    @Property(name = "app.street-lister.postalcodes.extractors.overpass.timeout.client") val serverTimeout: Duration,
) : PostalcodeExtractor {
    private val logger = KotlinLogging.logger {}

    private val overpass: OverpassMapDataDao

    init {
        logger.debug { "Initialize with base URL $baseUrl..." }
        val millisecondTimeout = clientTimeout.seconds.toInt() * 1000
        val osmConnection = OsmConnection(baseUrl, "github.com/debuglevel/street-lister", null, millisecondTimeout)
        overpass = OverpassMapDataDao(osmConnection)
    }

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
        val queryDuration = measureTimeMillis {
            overpass.queryTable(buildQuery(areaId, serverTimeout), postalcodeListHandler)
        }
        logger.debug { "Query took ${queryDuration}ms" } // includes overhead for parsing et cetera
        val postalcodes = postalcodeListHandler.getPostalcodes()

        // TODO: possible failures:
        //        - quota reached (what do then?)
        //        - invalid resultset (don't know if and when happens)
        //        - empty resultset (might occur if timeout was hit)

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