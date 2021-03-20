package de.debuglevel.streetlister.postalcode

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Postalcode(
    @Id
    @GeneratedValue
    var id: UUID?,
    var code: String
)