package de.debuglevel.streetdirectory.postalcode

import de.debuglevel.streetdirectory.street.Street
import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
data class Postalcode(
    /**
     * @implNote: Needs @GeneratedValue(generator = "uuid2"), @GenericGenerator and @Column to work with MariaDB/MySQL. See https://github.com/micronaut-projects/micronaut-data/issues/1210
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID?,
    /**
     * The actual value of the postal code
     */
    var code: String,
    /**
     * The [Street]s associated with this [Postalcode].
     */
    @OneToMany(mappedBy = "postalcode")
    var streets: List<Street>,
    /**
     * Latitude of the center of the postal code area
     */
    var centerLatitude: Double?,
    /**
     * Longitude of the center of the postal code area
     */
    var centerLongitude: Double?,
    /**
     * A note which might be available from some providers
     */
    var note: String? = null,
    /**
     * DateTime when the last attempt to street extraction was made; null if not yet attempted.
     */
    var lastStreetExtractionOn: LocalDateTime? = null,
    /**
     * When created in the database
     */
    @DateCreated
    var createdOn: LocalDateTime = LocalDateTime.now(),
    /**
     * When last modified in the database
     */
    @DateUpdated
    var lastModifiedOn: LocalDateTime = LocalDateTime.now(),
) {
    override fun toString(): String {
        return "Postalcode(id=$id, code='$code')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Postalcode

        if (code != other.code) return false

        return true
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}