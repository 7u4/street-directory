package de.debuglevel.streetlister.overpass

import de.debuglevel.commons.wait.WaitUtils
import de.westnordost.osmapi.OsmConnection
import de.westnordost.osmapi.overpass.OverpassMapDataDao
import io.micronaut.context.annotation.Property
import mu.KotlinLogging
import java.time.Duration
import java.util.concurrent.Executors
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

@Singleton
class OverpassService(
    @Property(name = "app.street-lister.extractors.overpass.base-url") val baseUrl: String,
    @Property(name = "app.street-lister.extractors.overpass.user-agent") val userAgent: String,
    @Property(name = "app.street-lister.extractors.overpass.maximum-threads") val maximumThreads: Int,
    @Property(name = "app.street-lister.extractors.overpass.timeout.client") val clientTimeout: Duration,
    @Property(name = "app.street-lister.extractors.overpass.wait-between-requests") val waitBetweenRequests: Long,
) {
    private val logger = KotlinLogging.logger {}

    private val overpass: OverpassMapDataDao

    private val executor = Executors.newFixedThreadPool(maximumThreads)

    init {
        logger.debug { "Initializing with base URL $baseUrl..." }
        val millisecondTimeout = clientTimeout.seconds.toInt() * 1000
        val osmConnection = OsmConnection(baseUrl, userAgent, null, millisecondTimeout)
        overpass = OverpassMapDataDao(osmConnection)
    }

    /**
     * Execute a Overpass API
     * @param query Query to execute
     * @param overpassResultHandler Handler to parse the query results
     * @param serverTimeout The query timeout setting (or the assumed server default)
     */
    fun <T> execute(
        query: String,
        overpassResultHandler: OverpassResultHandler<T>,
        serverTimeout: Duration
    ): List<T> {
        logger.debug { "Enqueuing query..." }
        val results = executor.submit<List<T>> {
            WaitUtils.waitForNextRequestAllowed(this, waitBetweenRequests)
            WaitUtils.setLastRequestDateTime(this)

            logger.debug { "Executing Overpass query..." }
            logger.trace { "Query:\n$query" }

            val queryDurationMillis = measureTimeMillis {
                overpass.queryTable(query, overpassResultHandler)
            }
            val queryDuration = Duration.ofMillis(queryDurationMillis)
            logger.debug { "Query execution took a round trip time of about $queryDuration" } // includes overhead for transfer, parsing et cetera

            val results = try {
                overpassResultHandler.getResults()
            } catch (e: EmptyResultSetException) {
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
            return@submit results
        }.get()
        return results
    }

    data class TimeoutExceededException(val serverTimeout: Duration, val queryDuration: Duration) :
        Exception("Query ($queryDuration) exceeded server timeout ($serverTimeout)")
}