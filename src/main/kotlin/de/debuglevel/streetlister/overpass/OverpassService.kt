package de.debuglevel.streetlister.overpass

import de.debuglevel.microservicecommons.wait.WaitUtils
import de.debuglevel.openstreetmap.overpass.OverpassClient
import de.debuglevel.openstreetmap.overpass.OverpassResultHandler
import mu.KotlinLogging
import java.time.Duration
import java.util.concurrent.Executors
import javax.inject.Singleton

@Singleton
class OverpassService(
    private val overpassProperties: OverpassProperties,
) {
    private val logger = KotlinLogging.logger {}

    private val executor = Executors.newFixedThreadPool(overpassProperties.maximumThreads)

    private val overpassClient =
        OverpassClient(overpassProperties.baseUrl, overpassProperties.timeout.client, overpassProperties.userAgent)

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
            WaitUtils.waitForNextRequestAllowed(this, overpassProperties.waitBetweenRequests)

            val results = overpassClient.execute(query, overpassResultHandler, serverTimeout)

            WaitUtils.setLastRequestDateTime(this)
            return@submit results
        }.get()
        return results
    }

    data class TimeoutExceededException(val serverTimeout: Duration, val queryDuration: Duration) :
        Exception("Query ($queryDuration) exceeded server timeout ($serverTimeout)")
}