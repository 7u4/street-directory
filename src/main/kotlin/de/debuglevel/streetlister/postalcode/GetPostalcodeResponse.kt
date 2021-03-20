package de.debuglevel.streetlister.postalcode

import java.util.*

data class GetPostalcodeResponse(
    val id: UUID,
    val code: String,
) {
    constructor(postalcode: Postalcode) : this(
        postalcode.id!!,
        postalcode.code,
    )
}