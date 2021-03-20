package de.debuglevel.streetlister.postalcode.extraction.overpass

import de.debuglevel.streetlister.postalcode.Postalcode
import de.debuglevel.streetlister.postalcode.extraction.PostalcodeExtractor
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

    private val areaId = 3600051477 // Germany
    //val areaId=3600017592 // Oberfranken

    init {
        logger.debug { "Initialize with base URL $baseUrl..." }
        val millisecondTimeout = timeout.seconds.toInt() * 1000
        val osmConnection = OsmConnection(baseUrl, "github.com/debuglevel/street-lister", null, millisecondTimeout)
        overpass = OverpassMapDataDao(osmConnection)
    }

    override fun getPostalcodes(): List<Postalcode> {
        logger.debug { "Getting postal codes..." }

        val postalcodeListHandler = PostalcodeListHandler()

        overpass.queryTable(
            """
            [out:csv(postal_code, note)];
            area($areaId);
            relation["boundary"="postal_code"](area);
            out;
        """.trimIndent(), postalcodeListHandler
        )

        val postalcodes = postalcodeListHandler.getPostalcodes().sortedBy { it.code }
        logger.debug { "Got ${postalcodes.count()} postal codes" }
        return postalcodes
    }
}