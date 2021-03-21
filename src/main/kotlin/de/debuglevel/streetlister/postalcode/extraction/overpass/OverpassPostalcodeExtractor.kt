package de.debuglevel.streetlister.postalcode.extraction.overpass

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

@Singleton
@Requires(property = "app.street-lister.postalcodes.extractors.overpass.enabled", value = "true")
class OverpassPostalcodeExtractor(
    @Property(name = "app.street-lister.postalcodes.extractors.overpass.base-url") val baseUrl: String,
    @Property(name = "app.street-lister.postalcodes.extractors.overpass.timeout") val timeout: Duration,
) : PostalcodeExtractor {
    private val logger = KotlinLogging.logger {}

    private val overpass: OverpassMapDataDao

    init {
        logger.debug { "Initialize with base URL $baseUrl..." }
        val millisecondTimeout = timeout.seconds.toInt() * 1000
        val osmConnection = OsmConnection(baseUrl, "github.com/debuglevel/street-lister", null, millisecondTimeout)
        overpass = OverpassMapDataDao(osmConnection)
    }

    override fun getPostalcodes(
        postalcodeExtractorSettings: PostalcodeExtractorSettings
    ): List<Postalcode> {
        require(postalcodeExtractorSettings is OverpassPostalcodeExtractorSettings) { "postalcodeExtractorSettings must be OverpassPostalcodeExtractorSettings" }
        logger.debug { "Getting postal codes for area ${postalcodeExtractorSettings.areaId}..." }

        val postalcodeListHandler = PostalcodeListHandler()

        overpass.queryTable(buildQuery(postalcodeExtractorSettings.areaId), postalcodeListHandler)

        val postalcodes = postalcodeListHandler.getPostalcodes().sortedBy { it.code }
        logger.debug { "Got ${postalcodes.count()} postal codes" }
        return postalcodes
    }

    private fun buildQuery(areaId: Long): String {
        return """
            [out:csv(::id, ::type, ::otype, ::lat, ::lon, postal_code, note)];
            area(${areaId});
            relation["boundary"="postal_code"](area);
            out;
               """.trimIndent()
    }
}