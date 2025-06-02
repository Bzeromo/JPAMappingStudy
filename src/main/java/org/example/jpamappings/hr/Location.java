package org.example.jpamappings.hr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="LOCATIONS")
public class Location {

    @Id
    @Column(name="LOCATION_ID")
    private Integer locationId;

    @Column(name="CITY")
    private String city;

    @Column(name="COUNTRY_ID", columnDefinition = "CHAR(2)")
    private String countryId;

    @Column(name="STREET_ADDRESS")
    private String streetAddress;

    @Column(name="POSTAL_CODE")
    private String postalCode;

    @Column(name="STATE_PROVINCE")
    private String stateProvince;

}
