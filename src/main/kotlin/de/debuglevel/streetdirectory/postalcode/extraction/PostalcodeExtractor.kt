package de.debuglevel.streetdirectory.postalcode.extraction

import de.debuglevel.streetdirectory.postalcode.Postalcode

interface PostalcodeExtractor {
    fun getPostalcodes(postalcodeExtractorSettings: PostalcodeExtractorSettings): List<Postalcode>
}