package com.tripmate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Booking Entity
 * 
 * This class represents a booking made by a user for a tour package.
 * It uses JPA annotations to map to a database table and establish relationships.
 * 
 * Key JPA Annotations:
 * - @Entity: Marks this class as a JPA entity
 * - @Table: Specifies the database table name
 * - @Id: Marks a field as the primary key
 * - @GeneratedValue: Specifies how the primary key is generated
 * - @ManyToOne: Establishes a many-to-one relationship with TourPackage
 * - @JoinColumn: Specifies the foreign key column
 * - @Column: Specifies column properties
 * 
 * Lombok Annotations:
 * - @Data: Generates getters, setters, toString, equals, and hashCode
 * - @NoArgsConstructor: Generates a no-args constructor
 * - @AllArgsConstructor: Generates a constructor with all fields
 */
@Entity
@Table(name = "bookings") // Explicitly name the table
@Data // Lombok: generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: generates constructor with no parameters
@AllArgsConstructor // Lombok: generates constructor with all parameters
public class Booking {
    
    /**
     * Primary key for the booking.
     * 
     * @Id: Marks this field as the primary key
     * @GeneratedValue: Automatically generates values for this field
     *   - strategy = GenerationType.IDENTITY: Uses database auto-increment
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Name of the person making the booking
     * 
     * @Column:
     *   - nullable = false: This field cannot be null
     *   - length = 100: Maximum length for VARCHAR column
     */
    @Column(nullable = false, length = 100)
    private String userName;
    
    /**
     * Email address of the person making the booking
     * 
     * @Column:
     *   - nullable = false: This field cannot be null
     *   - length = 255: Maximum length for email addresses
     *   - unique = true: Each email can only have one booking (optional constraint)
     */
    @Column(nullable = false, length = 255)
    private String email;
    
    /**
     * Phone number for contact purposes
     * 
     * @Column:
     *   - nullable = true: This field can be null (optional)
     *   - length = 20: Maximum length for phone numbers
     */
    @Column(nullable = true, length = 20)
    private String phoneNumber;
    
    /**
     * Number of people for this booking
     * 
     * @Column:
     *   - nullable = false: This field cannot be null
     *   - Default value is 1 (single person booking)
     */
    @Column(nullable = false)
    private Integer numberOfPeople = 1;
    
    /**
     * Total price for this booking (package price * number of people)
     * 
     * @Column:
     *   - nullable = false: This field cannot be null
     *   - precision = 10, scale = 2: Allows up to 10 digits total, 2 decimal places
     *   - BigDecimal for precise monetary calculations
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private java.math.BigDecimal totalPrice;
    
    /**
     * Date and time when the booking was made
     * 
     * @Column:
     *   - nullable = false: This field cannot be null
     *   - LocalDateTime maps to DATETIME in MySQL
     *   - Default value is set to current timestamp
     */
    @Column(nullable = false)
    private LocalDateTime bookingDate = LocalDateTime.now();
    
    /**
     * Status of the booking (e.g., "CONFIRMED", "CANCELLED", "PENDING")
     * 
     * @Column:
     *   - nullable = false: This field cannot be null
     *   - length = 20: Maximum length for status
     *   - Default value is "CONFIRMED"
     */
    @Column(nullable = false, length = 20)
    private String status = "CONFIRMED";
    
    /**
     * Relationship to TourPackage entity
     * 
     * @ManyToOne: Many bookings can be made for one tour package
     *   - fetch = FetchType.EAGER: Load the tour package immediately when booking is loaded
     *   - cascade = CascadeType.ALL: Propagate operations to the related tour package
     * 
     * @JoinColumn: Specifies the foreign key column
     *   - name = "package_id": Name of the foreign key column in the bookings table
     *   - nullable = false: Every booking must have a tour package
     *   - referencedColumnName = "id": References the id field in tour_packages table
     */
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "package_id", nullable = false, referencedColumnName = "id")
    private TourPackage tourPackage;
    
    /**
     * Optional: Special requests or notes for the booking
     * 
     * @Column:
     *   - nullable = true: This field can be null (optional)
     *   - columnDefinition = "TEXT": Uses MySQL TEXT type for longer content
     */
    @Column(nullable = true, columnDefinition = "TEXT")
    private String specialRequests;
    
    /**
     * Custom constructor for creating a booking with essential fields
     * This is useful when you don't need all fields immediately
     */
    public Booking(String userName, String email, Integer numberOfPeople, 
                  java.math.BigDecimal totalPrice, TourPackage tourPackage) {
        this.userName = userName;
        this.email = email;
        this.numberOfPeople = numberOfPeople;
        this.totalPrice = totalPrice;
        this.tourPackage = tourPackage;
        this.bookingDate = LocalDateTime.now();
        this.status = "CONFIRMED";
    }
    
    /**
     * Business method: Check if the booking is confirmed
     * 
     * @return true if the booking status is "CONFIRMED"
     */
    public boolean isConfirmed() {
        return "CONFIRMED".equals(status);
    }
    
    /**
     * Business method: Check if the booking is cancelled
     * 
     * @return true if the booking status is "CANCELLED"
     */
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }
    
    /**
     * Business method: Cancel the booking
     * This method changes the status to "CANCELLED"
     */
    public void cancel() {
        this.status = "CANCELLED";
    }
    
    /**
     * Business method: Get the tour package title
     * 
     * @return the title of the associated tour package, or null if not set
     */
    public String getTourPackageTitle() {
        return tourPackage != null ? tourPackage.getTitle() : null;
    }
    
    /**
     * Business method: Get the tour package location
     * 
     * @return the location of the associated tour package, or null if not set
     */
    public String getTourPackageLocation() {
        return tourPackage != null ? tourPackage.getLocation() : null;
    }
    
    /**
     * Business method: Calculate the price per person
     * 
     * @return the price per person, or null if numberOfPeople is 0 or null
     */
    public java.math.BigDecimal getPricePerPerson() {
        if (numberOfPeople == null || numberOfPeople == 0) {
            return null;
        }
        return totalPrice.divide(java.math.BigDecimal.valueOf(numberOfPeople), 2, java.math.RoundingMode.HALF_UP);
    }
} 