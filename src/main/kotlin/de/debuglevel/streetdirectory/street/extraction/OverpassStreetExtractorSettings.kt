package de.debuglevel.streetdirectory.street.extraction

import de.debuglevel.streetdirectory.postalcode.Postalcode

data class OverpassStreetExtractorSettings(
    val areaId: Long,
    val postalcode: Postalcode,
) : StreetExtractorSettings