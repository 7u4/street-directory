package de.debuglevel.streetlister.postalcode.extraction

import de.debuglevel.streetlister.postalcode.Postalcode

interface PostalcodeExtractor {
    fun getPostalcodes(): List<Postalcode>
}