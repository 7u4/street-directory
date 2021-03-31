package de.debuglevel.streetlister.street.extraction

data class OverpassStreetExtractorSettings(
    val areaId: Long,
    val postalcode: String,
) : StreetExtractorSettings