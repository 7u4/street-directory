package de.debuglevel.streetlister.postalcode.extraction.overpass

import de.westnordost.osmapi.common.Handler

interface OverpassResultHandler<T> : Handler<Array<String>> {
    fun getResults(): List<T>
}