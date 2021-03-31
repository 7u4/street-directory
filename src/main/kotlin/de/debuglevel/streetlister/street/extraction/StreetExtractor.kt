package de.debuglevel.streetlister.street.extraction

import de.debuglevel.streetlister.street.Street

interface StreetExtractor {
    fun getStreets(streetExtractorSettings: StreetExtractorSettings): List<Street>
}