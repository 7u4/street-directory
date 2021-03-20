package de.debuglevel.streetlister.person

import java.util.*

data class AddPersonResponse(
    val id: UUID,
    val name: String,
) {
    constructor(person: Person) : this(
        person.id!!,
        person.name,
    )
}