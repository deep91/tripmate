package com.tripmate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * BookingDto - Data Transfer Object for Booking
 * 
 * This DTO is used for:
 * - Creating new bookings
 * - Updating existing bookings
 * - API responses with booking information
 * - Input validation for booking requests
 * 
 * Key Features:
 * - Comprehensive validation rules
 * - JSON formatting for dates
 * - Business logic validation methods
 * - Flexible field inclusion/exclusion
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON
public class BookingDto {
    
    /**
     * Booking ID (null for new bookings, populated for existing ones)
     */
    private Long id;
    
    /**
     * Name of the person making the booking
     * 
     * Validation Rules:
     * - @NotBlank: Cannot be null, empty, or whitespace only
     * - @Size: Must be between 2 and 100 characters
     * - @Pattern: Must contain only letters, spaces, and common punctuation
     */
    @NotBlank(message = "User name is required")
    @Size(min = 2, max = 100, message = "User name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s\\-']+$", message = "User name can only contain letters, spaces, hyphens, and apostrophes")
    private String userName;
    
    /**
     * Email address of the person making the booking
     * 
     * Validation Rules:
     * - @NotBlank: Cannot be null, empty, or whitespace only
     * - @Email: Must be a valid email format
     * - @Size: Must be between 5 and 255 characters
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(min = 5, max = 255, message = "Email must be between 5 and 255 characters")
    private String email;
    
    /**
     * Phone number for contact purposes (optional)
     * 
     * Validation Rules:
     * - @Pattern: Must be a valid phone number format (if provided)
     * - @Size: Must be between 10 and 20 characters (if provided)
     */
    @Pattern(regexp = "^[+]?[0-9\\s\\-\\(\\)]+$", message = "Phone number must contain only digits, spaces, hyphens, parentheses, and optional plus sign")
    @Size(min = 10, max = 20, message = "Phone number must be between 10 and 20 characters")
    private String phoneNumber;
    
    /**
     * Number of people for this booking
     * 
     * Validation Rules:
     * - @NotNull: Cannot be null
     * - @Min: Must be at least 1
     * - @Max: Must be at most 50 (reasonable limit for a tour)
     */
    @NotNull(message = "Number of people is required")
    @Min(value = 1, message = "Number of people must be at least 1")
    @Max(value = 50, message = "Number of people cannot exceed 50")
    private Integer numberOfPeople;
    
    /**
     * Total price for this booking (calculated as package price * number of people)
     * 
     * Validation Rules:
     * - @NotNull: Cannot be null
     * - @DecimalMin: Must be at least 0.01
     * - @Digits: Must have up to 10 digits total, 2 decimal places
     */
    @NotNull(message = "Total price is required")
    @DecimalMin(value = "0.01", message = "Total price must be at least 0.01")
    @Digits(integer = 8, fraction = 2, message = "Total price must have up to 8 digits and 2 decimal places")
    private BigDecimal totalPrice;
    
    /**
     * Date and time when the booking was made
     * 
     * This field is automatically set when creating a booking
     * - @JsonFormat: Format as "yyyy-MM-dd'T'HH:mm:ss" in JSON
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingDate;
    
    /**
     * Status of the booking (e.g., "CONFIRMED", "CANCELLED", "PENDING")
     * 
     * Validation Rules:
     * - @NotBlank: Cannot be null, empty, or whitespace only
     * - @Pattern: Must be one of the valid status values
     */
    @NotBlank(message = "Booking status is required")
    @Pattern(regexp = "^(CONFIRMED|CANCELLED|PENDING)$", message = "Status must be CONFIRMED, CANCELLED, or PENDING")
    private String status;
    
    /**
     * ID of the tour package being booked
     * 
     * Validation Rules:
     * - @NotNull: Cannot be null
     * - @Min: Must be a positive number
     */
    @NotNull(message = "Tour package ID is required")
    @Min(value = 1, message = "Tour package ID must be a positive number")
    private Long tourPackageId;
    
    /**
     * Optional: Special requests or notes for the booking
     * 
     * Validation Rules:
     * - @Size: Must be less than 1000 characters (if provided)
     */
    @Size(max = 1000, message = "Special requests must be less than 1000 characters")
    private String specialRequests;
    
    /**
     * Constructor for creating a new booking
     * This is useful for booking creation requests
     */
    public BookingDto(String userName, String email, Integer numberOfPeople, 
                     BigDecimal totalPrice, Long tourPackageId) {
        this.userName = userName;
        this.email = email;
        this.numberOfPeople = numberOfPeople;
        this.totalPrice = totalPrice;
        this.tourPackageId = tourPackageId;
        this.bookingDate = LocalDateTime.now();
        this.status = "CONFIRMED";
    }
    

    
    /**
     * Business validation method: Check if the booking is valid for creation
     * 
     * @return true if all required fields are present and valid
     */
    public boolean isValidForCreation() {
        return userName != null && !userName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               numberOfPeople != null && numberOfPeople > 0 &&
               totalPrice != null && totalPrice.compareTo(BigDecimal.ZERO) > 0 &&
               tourPackageId != null && tourPackageId > 0;
    }
    
    /**
     * Business validation method: Check if the booking can be cancelled
     * 
     * @return true if the booking is confirmed and can be cancelled
     */
    public boolean canBeCancelled() {
        return "CONFIRMED".equals(status);
    }
    
    /**
     * Business validation method: Calculate price per person
     * 
     * @return price per person, or null if calculation is not possible
     */
    public BigDecimal getPricePerPerson() {
        if (numberOfPeople != null && numberOfPeople > 0 && totalPrice != null) {
            return totalPrice.divide(BigDecimal.valueOf(numberOfPeople), 2, java.math.RoundingMode.HALF_UP);
        }
        return null;
    }
    
    /**
     * Business validation method: Check if email format is valid
     * 
     * @return true if email format is valid
     */
    public boolean isValidEmail() {
        if (email == null) return false;
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }
} 
