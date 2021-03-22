package de.debuglevel.streetlister.overpass

import de.debuglevel.streetlister.postalcode.extraction.overpass.OverpassResultHandler
import de.debuglevel.streetlister.postalcode.extraction.overpass.PostalcodeListHandler
import de.westnordost.osmapi.OsmConnection
import de.westnordost.osmapi.overpass.OverpassMapDataDao
import mu.KotlinLogging
import java.time.Duration
import kotlin.system.measureTimeMillis

class OverpassQueryExecutor<T>(baseUrl: String, clientTimeout: Duration) {
    private val logger = KotlinLogging.logger {}

    private val overpass: OverpassMapDataDao
    private val userAgent = "github.com/debuglevel/street-lister"

    init {
        logger.debug { "Initialize with base URL $baseUrl..." }
        val millisecondTimeout = clientTimeout.seconds.toInt() * 1000
        val osmConnection = OsmConnection(baseUrl, userAgent, null, millisecondTimeout)
        overpass = OverpassMapDataDao(osmConnection)
    }

    fun execute(
        query: String,
        overpassResultHandler: OverpassResultHandler<T>,
        serverTimeout: Duration
    ): List<T> {
        logger.debug { "Executing Overpass query..." }
        logger.trace { "Query:\n$query" }

        val queryDurationMillis = measureTimeMillis {
            overpass.queryTable(query, overpassResultHandler)
        }
        val queryDuration = Duration.ofMillis(queryDurationMillis)
        logger.debug { "Query execution took a round trip time of about $queryDuration" } // includes overhead for transfer, parsing et cetera

        val results = try {
            overpassResultHandler.getResults()
        } catch (e: PostalcodeListHandler.EmptyResultSetException) {
            // if query duration took longer than the server timeout,
            // there is good chance the server timeout was hit
            if (queryDuration >= serverTimeout) {
                throw TimeoutExceededException(serverTimeout, queryDuration)
            } else {
                throw e
            }
        }

        // TODO: possible failures:
        //        - quota reached (what do then?)
        //        - invalid resultset (don't know if and when happens)

        logger.debug { "Executed Overpass query: ${results.count()} results." }
        return results
    }

    data class TimeoutExceededException(val serverTimeout: Duration, val queryDuration: Duration) :
        Exception("Query ($queryDuration) exceeded server timeout ($serverTimeout)")
}