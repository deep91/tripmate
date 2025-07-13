package com.tripmate.service;

import com.tripmate.dto.TourPackageDto;
import com.tripmate.model.TourPackage;
import com.tripmate.repository.TourPackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * TourPackageService - Business Logic Layer for TourPackage
 * 
 * This service class contains all business logic related to tour packages.
 * It acts as an intermediary between controllers and repositories.
 * 
 * Key Concepts:
 * - @Service: Marks this as a Spring service component
 * - @Transactional: Manages database transactions
 * - @RequiredArgsConstructor: Lombok generates constructor for final fields
 * - @Slf4j: Lombok provides logging capabilities
 * - Business Logic: Contains complex business rules and validations
 * - Data Transformation: Converts between DTOs and entities
 * 
 * Responsibilities:
 * - CRUD operations for tour packages
 * - Business rule validation
 * - Data transformation (DTO ↔ Entity)
 * - Transaction management
 * - Error handling and logging
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TourPackageService {
    
    /**
     * Repository for tour package data access
     * @RequiredArgsConstructor automatically creates a constructor for this field
     */
    private final TourPackageRepository tourPackageRepository;
    
    /**
     * Get all tour packages
     * 
     * @return List of all tour packages
     */
    @Transactional(readOnly = true)
    public List<TourPackage> getAllTourPackages() {
        log.info("Fetching all tour packages");
        return tourPackageRepository.findAll();
    }
    
    /**
     * Get all active tour packages
     * 
     * @return List of active tour packages
     */
    @Transactional(readOnly = true)
    public List<TourPackage> getActiveTourPackages() {
        log.info("Fetching all active tour packages");
        return tourPackageRepository.findByIsActiveTrue();
    }
    
    /**
     * Get tour package by ID
     * 
     * @param id Tour package ID
     * @return Optional containing the tour package if found
     */
    @Transactional(readOnly = true)
    public Optional<TourPackage> getTourPackageById(Long id) {
        log.info("Fetching tour package with ID: {}", id);
        return tourPackageRepository.findById(id);
    }
    
    /**
     * Get tour packages by location
     * 
     * @param location Location to search for
     * @return List of tour packages in the specified location
     */
    @Transactional(readOnly = true)
    public List<TourPackage> getTourPackagesByLocation(String location) {
        log.info("Fetching tour packages for location: {}", location);
        return tourPackageRepository.findByLocation(location);
    }
    
    /**
     * Get tour packages by price range
     * 
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of tour packages within the price range
     */
    @Transactional(readOnly = true)
    public List<TourPackage> getTourPackagesByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Fetching tour packages with price between {} and {}", minPrice, maxPrice);
        return tourPackageRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    /**
     * Get available tour packages (with capacity)
     * 
     * @return List of tour packages with available capacity
     */
    @Transactional(readOnly = true)
    public List<TourPackage> getAvailableTourPackages() {
        log.info("Fetching available tour packages");
        return tourPackageRepository.findAvailablePackages();
    }
    
    /**
     * Search tour packages by title keyword
     * 
     * @param keyword Search keyword
     * @return List of tour packages matching the keyword
     */
    @Transactional(readOnly = true)
    public List<TourPackage> searchTourPackagesByTitle(String keyword) {
        log.info("Searching tour packages with keyword: {}", keyword);
        return tourPackageRepository.searchPackagesByTitle(keyword);
    }
    
    /**
     * Get tour packages by multiple criteria
     * 
     * @param location Optional location filter
     * @param maxPrice Optional maximum price filter
     * @param startDate Optional start date filter
     * @return List of tour packages matching the criteria
     */
    @Transactional(readOnly = true)
    public List<TourPackage> getTourPackagesByCriteria(String location, BigDecimal maxPrice, LocalDate startDate) {
        log.info("Fetching tour packages with criteria - location: {}, maxPrice: {}, startDate: {}", 
                location, maxPrice, startDate);
        return tourPackageRepository.findPackagesByCriteria(location, maxPrice, startDate);
    }
    
    /**
     * Create a new tour package
     * 
     * @param tourPackageDto DTO containing tour package data
     * @return Created tour package
     * @throws IllegalArgumentException if validation fails
     */
    public TourPackage createTourPackage(TourPackageDto tourPackageDto) {
        log.info("Creating new tour package: {}", tourPackageDto.getTitle());
        
        // Validate DTO
        if (!tourPackageDto.isValidForCreation()) {
            throw new IllegalArgumentException("Invalid tour package data");
        }
        
        // Check if tour package with same title already exists
        if (tourPackageRepository.existsByTitle(tourPackageDto.getTitle())) {
            throw new IllegalArgumentException("Tour package with title '" + tourPackageDto.getTitle() + "' already exists");
        }
        
        // Validate date range
        if (!tourPackageDto.isValidDateRange()) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        // Convert DTO to entity
        TourPackage tourPackage = new TourPackage(
            tourPackageDto.getTitle(),
            tourPackageDto.getDescription(),
            tourPackageDto.getLocation(),
            tourPackageDto.getStartDate(),
            tourPackageDto.getEndDate(),
            tourPackageDto.getPrice()
        );
        
        // Set optional fields
        tourPackage.setImageUrl(tourPackageDto.getImageUrl());
        tourPackage.setMaxCapacity(tourPackageDto.getMaxCapacity());
        tourPackage.setIsActive(tourPackageDto.getIsActive());
        
        // Save and return
        TourPackage savedPackage = tourPackageRepository.save(tourPackage);
        log.info("Successfully created tour package with ID: {}", savedPackage.getId());
        return savedPackage;
    }
    
    /**
     * Update an existing tour package
     * 
     * @param id Tour package ID
     * @param tourPackageDto DTO containing updated data
     * @return Updated tour package
     * @throws IllegalArgumentException if tour package not found or validation fails
     */
    public TourPackage updateTourPackage(Long id, TourPackageDto tourPackageDto) {
        log.info("Updating tour package with ID: {}", id);
        
        // Find existing tour package
        TourPackage existingPackage = tourPackageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tour package with ID " + id + " not found"));
        
        // Validate DTO
        if (!tourPackageDto.isValidForCreation()) {
            throw new IllegalArgumentException("Invalid tour package data");
        }
        
        // Validate date range
        if (!tourPackageDto.isValidDateRange()) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        
        // Check if title is being changed and if new title already exists
        if (!existingPackage.getTitle().equals(tourPackageDto.getTitle()) &&
            tourPackageRepository.existsByTitle(tourPackageDto.getTitle())) {
            throw new IllegalArgumentException("Tour package with title '" + tourPackageDto.getTitle() + "' already exists");
        }
        
        // Update fields
        existingPackage.setTitle(tourPackageDto.getTitle());
        existingPackage.setDescription(tourPackageDto.getDescription());
        existingPackage.setLocation(tourPackageDto.getLocation());
        existingPackage.setStartDate(tourPackageDto.getStartDate());
        existingPackage.setEndDate(tourPackageDto.getEndDate());
        existingPackage.setPrice(tourPackageDto.getPrice());
        existingPackage.setImageUrl(tourPackageDto.getImageUrl());
        existingPackage.setMaxCapacity(tourPackageDto.getMaxCapacity());
        existingPackage.setIsActive(tourPackageDto.getIsActive());
        
        // Save and return
        TourPackage updatedPackage = tourPackageRepository.save(existingPackage);
        log.info("Successfully updated tour package with ID: {}", updatedPackage.getId());
        return updatedPackage;
    }
    
    /**
     * Delete a tour package
     * 
     * @param id Tour package ID
     * @throws IllegalArgumentException if tour package not found
     */
    public void deleteTourPackage(Long id) {
        log.info("Deleting tour package with ID: {}", id);
        
        // Check if tour package exists
        if (!tourPackageRepository.existsById(id)) {
            throw new IllegalArgumentException("Tour package with ID " + id + " not found");
        }
        
        // Delete the tour package
        tourPackageRepository.deleteById(id);
        log.info("Successfully deleted tour package with ID: {}", id);
    }
    
    /**
     * Deactivate a tour package (soft delete)
     * 
     * @param id Tour package ID
     * @return Deactivated tour package
     * @throws IllegalArgumentException if tour package not found
     */
    public TourPackage deactivateTourPackage(Long id) {
        log.info("Deactivating tour package with ID: {}", id);
        
        TourPackage tourPackage = tourPackageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tour package with ID " + id + " not found"));
        
        tourPackage.setIsActive(false);
        TourPackage deactivatedPackage = tourPackageRepository.save(tourPackage);
        log.info("Successfully deactivated tour package with ID: {}", id);
        return deactivatedPackage;
    }
    
    /**
     * Activate a tour package
     * 
     * @param id Tour package ID
     * @return Activated tour package
     * @throws IllegalArgumentException if tour package not found
     */
    public TourPackage activateTourPackage(Long id) {
        log.info("Activating tour package with ID: {}", id);
        
        TourPackage tourPackage = tourPackageRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tour package with ID " + id + " not found"));
        
        tourPackage.setIsActive(true);
        TourPackage activatedPackage = tourPackageRepository.save(tourPackage);
        log.info("Successfully activated tour package with ID: {}", id);
        return activatedPackage;
    }
    
    /**
     * Get tour package statistics
     * 
     * @return Number of total and active tour packages
     */
    @Transactional(readOnly = true)
    public TourPackageStatistics getTourPackageStatistics() {
        log.info("Fetching tour package statistics");
        
        long totalPackages = tourPackageRepository.count();
        long activePackages = tourPackageRepository.countByLocation(""); // This will be updated with proper query
        
        return new TourPackageStatistics(totalPackages, activePackages);
    }
    
    /**
     * Inner class for tour package statistics
     */
    public static class TourPackageStatistics {
        private final long totalPackages;
        private final long activePackages;
        
        public TourPackageStatistics(long totalPackages, long activePackages) {
            this.totalPackages = totalPackages;
            this.activePackages = activePackages;
        }
        
        public long getTotalPackages() { return totalPackages; }
        public long getActivePackages() { return activePackages; }
    }
} 