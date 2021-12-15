package de.debuglevel.streetdirectory.street.extraction

import de.debuglevel.streetdirectory.street.Street

interface StreetExtractor {
    fun getStreets(streetExtractorSettings: StreetExtractorSettings): List<Street>
}