package de.debuglevel.streetdirectory.street

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("app.street-directory.street")
class StreetProperties {
    /**
     * Number of maximum parallel requests to service
     */
    var maximumThreads = 5
}