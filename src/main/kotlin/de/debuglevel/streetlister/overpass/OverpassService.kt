package de.debuglevel.streetlister.overpass

import de.debuglevel.microservicecommons.wait.WaitUtils
import de.debuglevel.openstreetmap.overpass.OverpassClient
import de.debuglevel.openstreetmap.overpass.OverpassResultHandler
import mu.KotlinLogging
import java.time.Duration
import java.util.concurrent.Executors
import jakarta.inject.Singleton

@Singleton
class OverpassService(
    private val overpassProperties: OverpassProperties,
) {
    private val logger = KotlinLogging.logger {}

    private val executor = Executors.newFixedThreadPool(overpassProperties.maximumThreads)

    private val overpassClient = OverpassClient(
        baseUrl = overpassProperties.baseUrl,
        clientTimeout = overpassProperties.timeout.client,
        userAgent = overpassProperties.userAgent
    )

    /**
     * Execute [query] and pass result to [overpassResultHandler] to parse it.
     * [serverTimeout] is an assumption on the server-side timeout setting to throw the correct Exception if a query might have taken too long.
     */
    fun <T> execute(
        query: String,
        overpassResultHandler: OverpassResultHandler<T>,
        serverTimeout: Duration
    ): List<T> {
        logger.debug { "Executing query..." }

        val results = executor.submit<List<T>> {
            WaitUtils.waitForNextRequestAllowed(this, overpassProperties.waitBetweenRequests)

            val results = overpassClient.execute(query, overpassResultHandler, serverTimeout)

            WaitUtils.setLastRequestDateTime(this)
            return@submit results
        }.get()

        logger.debug { "Executed query: ${results.size} items" }
        return results
    }
}