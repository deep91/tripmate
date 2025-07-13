package com.tripmate.controller;

import com.tripmate.dto.TourPackageDto;
import com.tripmate.model.TourPackage;
import com.tripmate.service.TourPackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * TourPackageController - REST API Controller for TourPackage
 * 
 * This controller provides REST endpoints for managing tour packages.
 * It handles HTTP requests and responses, input validation, and delegates
 * business logic to the service layer.
 * 
 * Key Concepts:
 * - @RestController: Marks this as a REST controller (combines @Controller + @ResponseBody)
 * - @RequestMapping: Base path for all endpoints in this controller
 * - @GetMapping, @PostMapping, etc.: HTTP method mappings
 * - @PathVariable: Extracts path variables from URL
 * - @RequestParam: Extracts query parameters from URL
 * - @RequestBody: Extracts JSON body from request
 * - @Valid: Triggers validation on DTOs
 * - ResponseEntity: Wraps response with status code and headers
 * 
 * HTTP Status Codes:
 * - 200 OK: Successful GET requests
 * - 201 Created: Successful POST requests
 * - 204 No Content: Successful DELETE requests
 * - 400 Bad Request: Invalid input data
 * - 404 Not Found: Resource not found
 * - 500 Internal Server Error: Server errors
 */
@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow all origins for development
public class TourPackageController {
    
    /**
     * Service layer for tour package business logic
     */
    private final TourPackageService tourPackageService;
    
    /**
     * GET /api/packages
     * Get all tour packages
     * 
     * @return List of all tour packages with 200 OK status
     */
    @GetMapping
    public ResponseEntity<List<TourPackage>> getAllTourPackages() {
        log.info("GET /api/packages - Fetching all tour packages");
        List<TourPackage> packages = tourPackageService.getAllTourPackages();
        return ResponseEntity.ok(packages);
    }
    
    /**
     * GET /api/packages/active
     * Get all active tour packages
     * 
     * @return List of active tour packages with 200 OK status
     */
    @GetMapping("/active")
    public ResponseEntity<List<TourPackage>> getActiveTourPackages() {
        log.info("GET /api/packages/active - Fetching active tour packages");
        List<TourPackage> packages = tourPackageService.getActiveTourPackages();
        return ResponseEntity.ok(packages);
    }
    
    /**
     * GET /api/packages/{id}
     * Get tour package by ID
     * 
     * @param id Tour package ID (path variable)
     * @return Tour package with 200 OK status, or 404 Not Found if not exists
     */
    @GetMapping("/{id}")
    public ResponseEntity<TourPackage> getTourPackageById(@PathVariable Long id) {
        log.info("GET /api/packages/{} - Fetching tour package by ID", id);
        
        Optional<TourPackage> tourPackage = tourPackageService.getTourPackageById(id);
        
        return tourPackage
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/packages/location/{location}
     * Get tour packages by location
     * 
     * @param location Location to search for (path variable)
     * @return List of tour packages in the location with 200 OK status
     */
    @GetMapping("/location/{location}")
    public ResponseEntity<List<TourPackage>> getTourPackagesByLocation(@PathVariable String location) {
        log.info("GET /api/packages/location/{} - Fetching tour packages by location", location);
        List<TourPackage> packages = tourPackageService.getTourPackagesByLocation(location);
        return ResponseEntity.ok(packages);
    }
    
    /**
     * GET /api/packages/search
     * Search tour packages by multiple criteria
     * 
     * @param location Optional location filter (query parameter)
     * @param maxPrice Optional maximum price filter (query parameter)
     * @param startDate Optional start date filter (query parameter)
     * @return List of tour packages matching criteria with 200 OK status
     */
    @GetMapping("/search")
    public ResponseEntity<List<TourPackage>> searchTourPackages(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) LocalDate startDate) {
        
        log.info("GET /api/packages/search - Searching packages with location: {}, maxPrice: {}, startDate: {}", 
                location, maxPrice, startDate);
        
        List<TourPackage> packages = tourPackageService.getTourPackagesByCriteria(location, maxPrice, startDate);
        return ResponseEntity.ok(packages);
    }
    
    /**
     * GET /api/packages/price-range
     * Get tour packages by price range
     * 
     * @param minPrice Minimum price (query parameter)
     * @param maxPrice Maximum price (query parameter)
     * @return List of tour packages in price range with 200 OK status
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<TourPackage>> getTourPackagesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        
        log.info("GET /api/packages/price-range - Fetching packages with price between {} and {}", minPrice, maxPrice);
        
        List<TourPackage> packages = tourPackageService.getTourPackagesByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(packages);
    }
    
    /**
     * GET /api/packages/available
     * Get available tour packages (with capacity)
     * 
     * @return List of available tour packages with 200 OK status
     */
    @GetMapping("/available")
    public ResponseEntity<List<TourPackage>> getAvailableTourPackages() {
        log.info("GET /api/packages/available - Fetching available tour packages");
        List<TourPackage> packages = tourPackageService.getAvailableTourPackages();
        return ResponseEntity.ok(packages);
    }
    
    /**
     * GET /api/packages/search/title
     * Search tour packages by title keyword
     * 
     * @param keyword Search keyword (query parameter)
     * @return List of tour packages matching keyword with 200 OK status
     */
    @GetMapping("/search/title")
    public ResponseEntity<List<TourPackage>> searchTourPackagesByTitle(@RequestParam String keyword) {
        log.info("GET /api/packages/search/title - Searching packages with keyword: {}", keyword);
        List<TourPackage> packages = tourPackageService.searchTourPackagesByTitle(keyword);
        return ResponseEntity.ok(packages);
    }
    
    /**
     * POST /api/packages
     * Create a new tour package
     * 
     * @param tourPackageDto Tour package data (request body)
     * @return Created tour package with 201 Created status, or 400 Bad Request if validation fails
     */
    @PostMapping
    public ResponseEntity<TourPackage> createTourPackage(@Valid @RequestBody TourPackageDto tourPackageDto) {
        log.info("POST /api/packages - Creating new tour package: {}", tourPackageDto.getTitle());
        
        try {
            TourPackage createdPackage = tourPackageService.createTourPackage(tourPackageDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPackage);
        } catch (IllegalArgumentException e) {
            log.error("Error creating tour package: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * PUT /api/packages/{id}
     * Update an existing tour package
     * 
     * @param id Tour package ID (path variable)
     * @param tourPackageDto Updated tour package data (request body)
     * @return Updated tour package with 200 OK status, or 404 Not Found if not exists, or 400 Bad Request if validation fails
     */
    @PutMapping("/{id}")
    public ResponseEntity<TourPackage> updateTourPackage(
            @PathVariable Long id,
            @Valid @RequestBody TourPackageDto tourPackageDto) {
        
        log.info("PUT /api/packages/{} - Updating tour package", id);
        
        try {
            TourPackage updatedPackage = tourPackageService.updateTourPackage(id, tourPackageDto);
            return ResponseEntity.ok(updatedPackage);
        } catch (IllegalArgumentException e) {
            log.error("Error updating tour package: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * DELETE /api/packages/{id}
     * Delete a tour package
     * 
     * @param id Tour package ID (path variable)
     * @return 204 No Content if successful, or 404 Not Found if not exists
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTourPackage(@PathVariable Long id) {
        log.info("DELETE /api/packages/{} - Deleting tour package", id);
        
        try {
            tourPackageService.deleteTourPackage(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting tour package: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * PATCH /api/packages/{id}/deactivate
     * Deactivate a tour package (soft delete)
     * 
     * @param id Tour package ID (path variable)
     * @return Deactivated tour package with 200 OK status, or 404 Not Found if not exists
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<TourPackage> deactivateTourPackage(@PathVariable Long id) {
        log.info("PATCH /api/packages/{}/deactivate - Deactivating tour package", id);
        
        try {
            TourPackage deactivatedPackage = tourPackageService.deactivateTourPackage(id);
            return ResponseEntity.ok(deactivatedPackage);
        } catch (IllegalArgumentException e) {
            log.error("Error deactivating tour package: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * PATCH /api/packages/{id}/activate
     * Activate a tour package
     * 
     * @param id Tour package ID (path variable)
     * @return Activated tour package with 200 OK status, or 404 Not Found if not exists
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<TourPackage> activateTourPackage(@PathVariable Long id) {
        log.info("PATCH /api/packages/{}/activate - Activating tour package", id);
        
        try {
            TourPackage activatedPackage = tourPackageService.activateTourPackage(id);
            return ResponseEntity.ok(activatedPackage);
        } catch (IllegalArgumentException e) {
            log.error("Error activating tour package: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * GET /api/packages/statistics
     * Get tour package statistics
     * 
     * @return Tour package statistics with 200 OK status
     */
    @GetMapping("/statistics")
    public ResponseEntity<TourPackageService.TourPackageStatistics> getTourPackageStatistics() {
        log.info("GET /api/packages/statistics - Fetching tour package statistics");
        TourPackageService.TourPackageStatistics statistics = tourPackageService.getTourPackageStatistics();
        return ResponseEntity.ok(statistics);
    }
} 