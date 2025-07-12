package com.tripmate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TourPackage Entity
 * 
 * This class represents a tourist package in the database.
 * It uses JPA (Java Persistence API) annotations to map Java objects to database tables.
 * 
 * Key JPA Annotations:
 * - @Entity: Marks this class as a JPA entity (will be mapped to a database table)
 * - @Table: Specifies the database table name (optional, defaults to class name)
 * - @Id: Marks a field as the primary key
 * - @GeneratedValue: Specifies how the primary key is generated
 * - @Column: Specifies column properties (optional, defaults to field name)
 * 
 * Lombok Annotations:
 * - @Data: Generates getters, setters, toString, equals, and hashCode methods
 * - @NoArgsConstructor: Generates a no-args constructor
 * - @AllArgsConstructor: Generates a constructor with all fields
 */
@Entity
@Table(name = "tour_packages") // Explicitly name the table
@Data // Lombok: generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: generates constructor with no parameters
@AllArgsConstructor // Lombok: generates constructor with all parameters
public class TourPackage {
    
    /**
     * Primary key for the tour package.
     * 
     * @Id: Marks this field as the primary key
     * @GeneratedValue: Automatically generates values for this field
     *   - strategy = GenerationType.IDENTITY: Uses database auto-increment
     *   - This is the most common strategy for MySQL
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Title of the tour package (e.g., "Paris Adventure", "Tokyo Explorer")
     * 
     * @Column: Specifies column properties
     *   - nullable = false: This field cannot be null in the database
     *   - length = 255: Maximum length for VARCHAR column
     */
    @Column(nullable = false, length = 255)
    private String title;
    
    /**
     * Detailed description of the tour package
     * 
     * @Column: 
     *   - columnDefinition = "TEXT": Uses MySQL TEXT type for longer content
     *   - nullable = false: This field cannot be null
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    /**
     * Location/destination of the tour (e.g., "Paris, France", "Tokyo, Japan")
     * 
     * @Column:
     *   - nullable = false: This field cannot be null
     *   - length = 100: Maximum length for VARCHAR column
     */
    @Column(nullable = false, length = 100)
    private String location;
    
    /**
     * Start date of the tour package
     * 
     * @Column:
     *   - nullable = false: This field cannot be null
     *   - LocalDate maps to DATE in MySQL
     */
    @Column(nullable = false)
    private LocalDate startDate;
    
    /**
     * End date of the tour package
     * 
     * @Column:
     *   - nullable = false: This field cannot be null
     *   - LocalDate maps to DATE in MySQL
     */
    @Column(nullable = false)
    private LocalDate endDate;
    
    /**
     * Price of the tour package
     * 
     * @Column:
     *   - nullable = false: This field cannot be null
     *   - precision = 10, scale = 2: Allows up to 10 digits total, 2 decimal places
     *   - BigDecimal is used for precise monetary calculations (avoid floating-point errors)
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    /**
     * Optional: Image URL for the tour package
     * 
     * @Column:
     *   - nullable = true: This field can be null (optional)
     *   - length = 500: Maximum length for URL
     */
    @Column(nullable = true, length = 500)
    private String imageUrl;
    
    /**
     * Optional: Maximum number of people allowed for this tour
     * 
     * @Column:
     *   - nullable = true: This field can be null (optional)
     */
    @Column(nullable = true)
    private Integer maxCapacity;
    
    /**
     * Optional: Current number of people booked for this tour
     * 
     * @Column:
     *   - nullable = true: This field can be null (optional)
     *   - Default value will be 0 when not specified
     */
    @Column(nullable = true)
    private Integer currentBookings = 0;
    
    /**
     * Optional: Whether the tour package is currently active/available
     * 
     * @Column:
     *   - nullable = false: This field cannot be null
     *   - Default value is true (active by default)
     */
    @Column(nullable = false)
    private Boolean isActive = true;
    
    /**
     * Custom constructor for creating a tour package with essential fields
     * This is useful when you don't need all fields immediately
     */
    public TourPackage(String title, String description, String location, 
                      LocalDate startDate, LocalDate endDate, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.isActive = true;
        this.currentBookings = 0;
    }
    
    /**
     * Business method: Check if the tour package is available for booking
     * 
     * @return true if the tour is active and has available capacity
     */
    public boolean isAvailableForBooking() {
        return isActive && (maxCapacity == null || currentBookings < maxCapacity);
    }
    
    /**
     * Business method: Get the number of available spots
     * 
     * @return number of available spots, or null if no capacity limit
     */
    public Integer getAvailableSpots() {
        if (maxCapacity == null) {
            return null; // No limit
        }
        return Math.max(0, maxCapacity - currentBookings);
    }
    
    /**
     * Business method: Increment the booking count
     * This method should be called when a new booking is made
     */
    public void incrementBookings() {
        if (this.currentBookings == null) {
            this.currentBookings = 0;
        }
        this.currentBookings++;
    }
    
    /**
     * Business method: Decrement the booking count
     * This method should be called when a booking is cancelled
     */
    public void decrementBookings() {
        if (this.currentBookings != null && this.currentBookings > 0) {
            this.currentBookings--;
        }
    }
} 