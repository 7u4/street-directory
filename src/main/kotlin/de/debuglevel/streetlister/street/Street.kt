package de.debuglevel.streetlister.street

import de.debuglevel.streetlister.postalcode.Postalcode
import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
data class Street(
    /**
     * @implNote: Needs @GeneratedValue(generator = "uuid2"), @GenericGenerator and @Column to work with MariaDB/MySQL. See https://github.com/micronaut-projects/micronaut-data/issues/1210
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    var id: UUID?,
    /**
     * The [Postalcode] of this [Street].
     */
    @ManyToOne()
    var postalcode: Postalcode,
    /**
     * The name of the street
     */
    var streetname: String,
    /**
     * Latitude of the center of the postal code area
     */
    var centerLatitude: Double?,
    /**
     * Longitude of the center of the postal code area
     */
    var centerLongitude: Double?,
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
        return "Street(id=$id, streetname='$streetname')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Street

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}