package de.debuglevel.streetlister.overpass

import mu.KotlinLogging
import java.time.Duration

/**
 * Utility functions to generate Overpass query parts
 */
object OverpassQueryBuilder {
    private val logger = KotlinLogging.logger {}

    /**
     * Set the query to output a CSV
     * @param columns List of columns to be extracted from the original query output
     * @param header Whether a header row with column names should be printed (should be true if OverpassQueryExecutor is used)
     * @param delimiter String to delimit the CSV columns (the default Tabulator is probably a safe choice for OpenStreetMaps data)
     */
    fun csvOutput(columns: List<String>, header: Boolean = true, delimiter: String = "\t"): String {
        logger.trace { "Building CSV output setting..." }

        val columnList = columns.joinToString(", ")
        val setting = "[out:csv($columnList; $header; \"$delimiter\")];"

        logger.trace { "Built CSV output setting: $setting" }
        return setting
    }

    /**
     * Set the server timeout
     * @param timeout Query timeout; null use server default
     */
    fun timeout(timeout: Duration?): String {
        // if timeout is set, it must be non-negative
        require(timeout == null || !timeout.isNegative) { "Timeout must be non-negative or null" }

        logger.trace { "Building timeout setting..." }
        val setting = if (timeout != null) "[timeout:${timeout.seconds}]" else ""
        logger.trace { "Built timeout setting: $setting" }
        return setting
    }
}