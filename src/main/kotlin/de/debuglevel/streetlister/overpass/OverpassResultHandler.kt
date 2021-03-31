package de.debuglevel.streetlister.overpass

import de.westnordost.osmapi.common.Handler

interface OverpassResultHandler<T> : Handler<Array<String>> {
    fun getResults(): List<T>
}