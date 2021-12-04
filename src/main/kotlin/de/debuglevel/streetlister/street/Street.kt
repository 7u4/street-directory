package de.debuglevel.streetlister.street

import io.micronaut.data.annotation.DateCreated
import io.micronaut.data.annotation.DateUpdated
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

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
     * The postal code
     */
    var postalcode: String,
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
)