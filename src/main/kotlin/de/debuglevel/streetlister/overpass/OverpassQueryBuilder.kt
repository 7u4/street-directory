package de.debuglevel.streetlister.overpass

import mu.KotlinLogging

object OverpassQueryBuilder {
    private val logger = KotlinLogging.logger {}

    fun csvOutput(columns: List<String>, header: Boolean = true, delimiter: String = "\t"): String {
        logger.trace { "Building CSV output setting..." }

        val columnList = columns.joinToString(", ")
        val setting = "[out:csv($columnList; $header; \"$delimiter\")];"

        logger.trace { "Built CSV output setting: $setting" }
        return setting
    }
}