package de.debuglevel.streetlister.overpass

import mu.KotlinLogging
import java.time.Duration

object OverpassQueryBuilder {
    private val logger = KotlinLogging.logger {}

    fun csvOutput(columns: List<String>, header: Boolean = true, delimiter: String = "\t"): String {
        logger.trace { "Building CSV output setting..." }

        val columnList = columns.joinToString(", ")
        val setting = "[out:csv($columnList; $header; \"$delimiter\")];"

        logger.trace { "Built CSV output setting: $setting" }
        return setting
    }

    fun timeout(timeout: Duration?): String {
        // if timeout is set, it must be non-negative
        require(if (timeout == null) true else (!timeout.isNegative))

        logger.trace { "Building timeout setting..." }
        val setting = if (timeout != null) "[timeout:${timeout.seconds}]" else ""
        logger.trace { "Built timeout setting: $setting" }
        return setting
    }
}