package com.tripmate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TourPackageDto - Data Transfer Object for TourPackage
 * 
 * DTOs are used to:
 * - Separate API contracts from internal entity structure
 * - Control what data is exposed to clients
 * - Add validation rules for incoming data
 * - Transform data between different layers
 * - Version API contracts independently
 * 
 * Key Annotations:
 * - @Data: Lombok generates getters, setters, toString, etc.
 * - @JsonInclude: Controls JSON serialization
 * - @Valid: Triggers validation
 * - @NotNull, @NotBlank, @Min, @Max: Validation constraints
 * - @JsonFormat: Controls date/time formatting
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON
public class TourPackageDto {
    
    /**
     * Tour package ID (null for new packages, populated for existing ones)
     */
    private Long id;
    
    /**
     * Title of the tour package
     * 
     * Validation Rules:
     * - @NotBlank: Cannot be null, empty, or whitespace only
     * - @Size: Must be between 3 and 255 characters
     * - @Pattern: Must contain only letters, numbers, spaces, and common punctuation
     */
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_,.!?()]+$", message = "Title contains invalid characters")
    private String title;
    
    /**
     * Detailed description of the tour package
     * 
     * Validation Rules:
     * - @NotBlank: Cannot be null, empty, or whitespace only
     * - @Size: Must be between 10 and 2000 characters
     */
    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    private String description;
    
    /**
     * Location/destination of the tour
     * 
     * Validation Rules:
     * - @NotBlank: Cannot be null, empty, or whitespace only
     * - @Size: Must be between 2 and 100 characters
     */
    @NotBlank(message = "Location is required")
    @Size(min = 2, max = 100, message = "Location must be between 2 and 100 characters")
    private String location;
    
    /**
     * Start date of the tour package
     * 
     * Validation Rules:
     * - @NotNull: Cannot be null
     * - @Future: Must be a future date
     * - @JsonFormat: Format as "yyyy-MM-dd" in JSON
     */
    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    /**
     * End date of the tour package
     * 
     * Validation Rules:
     * - @NotNull: Cannot be null
     * - @Future: Must be a future date
     * - Custom validation: Must be after start date (handled in service layer)
     * - @JsonFormat: Format as "yyyy-MM-dd" in JSON
     */
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    /**
     * Price of the tour package per person
     * 
     * Validation Rules:
     * - @NotNull: Cannot be null
     * - @DecimalMin: Must be at least 0.01
     * - @Digits: Must have up to 10 digits total, 2 decimal places
     */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be at least 0.01")
    @Digits(integer = 8, fraction = 2, message = "Price must have up to 8 digits and 2 decimal places")
    private BigDecimal price;
    
    /**
     * Optional: Image URL for the tour package
     * 
     * Validation Rules:
     * - @Pattern: Must be a valid URL format (if provided)
     * - @Size: Must be between 10 and 500 characters (if provided)
     */
    @Pattern(regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$", message = "Image URL must be a valid URL")
    @Size(max = 500, message = "Image URL must be less than 500 characters")
    private String imageUrl;
    
    /**
     * Optional: Maximum number of people allowed for this tour
     * 
     * Validation Rules:
     * - @Min: Must be at least 1 (if provided)
     * - @Max: Must be at most 1000 (if provided)
     */
    @Min(value = 1, message = "Maximum capacity must be at least 1")
    @Max(value = 1000, message = "Maximum capacity cannot exceed 1000")
    private Integer maxCapacity;
    
    /**
     * Optional: Whether the tour package is currently active
     * Default value is true for new packages
     */
    private Boolean isActive = true;
    
    /**
     * Constructor for creating a tour package with essential fields
     * This is useful for creating new tour packages
     */
    public TourPackageDto(String title, String description, String location, 
                         LocalDate startDate, LocalDate endDate, BigDecimal price) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.isActive = true;
    }
    
    /**
     * Business validation method: Check if end date is after start date
     * 
     * @return true if the dates are valid, false otherwise
     */
    public boolean isValidDateRange() {
        return startDate != null && endDate != null && endDate.isAfter(startDate);
    }
    
    /**
     * Business validation method: Check if the tour package is valid for creation
     * 
     * @return true if all required fields are present and valid
     */
    public boolean isValidForCreation() {
        return title != null && !title.trim().isEmpty() &&
               description != null && !description.trim().isEmpty() &&
               location != null && !location.trim().isEmpty() &&
               startDate != null && endDate != null &&
               price != null && price.compareTo(BigDecimal.ZERO) > 0 &&
               isValidDateRange();
    }
} 