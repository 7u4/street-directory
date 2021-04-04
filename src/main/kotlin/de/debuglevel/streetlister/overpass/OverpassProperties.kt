package de.debuglevel.streetlister.overpass

import io.micronaut.context.annotation.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("app.street-lister.extractors.overpass")
class OverpassProperties {
    /**
     * Duration after which a geocode is assumed to be outdated and should be requested again.
     */
    var duration: Duration = Duration.ofDays(90)

    /**
     * Base URL of the service
     */
    var baseUrl = "https://overpass.kumi.systems/api/"

    /**
     * User-Agent to be set in HTTP headers as requested by e.g. overpass-api.de
     */
    var userAgent = "github.com/debuglevel/street-lister"

    /**
     * Number of maximum parallel requests to service
     */
    var maximumThreads = 1

    /**
     * How long should be waited between two requests (in nanoseconds).
     * This should be 0 if maximum-threads is greater than 1.
     */
    var waitBetweenRequests: Long = 1_000_000_000

    var timeout = Timeout()

    @ConfigurationProperties("timeout")
    class Timeout {
        /**
         * Timeout to abort the HTTP request by the client (should be slightly larger than server timeout)
         */
        var client: Duration = Duration.ofMinutes(6)
    }
}