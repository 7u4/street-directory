package de.debuglevel.streetlister.street

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("app.street-lister.street")
class StreetProperties {
    /**
     * Number of maximum parallel requests to service
     */
    var maximumThreads = 5
}