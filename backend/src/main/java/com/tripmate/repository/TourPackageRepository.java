package com.tripmate.repository;

import com.tripmate.model.TourPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * TourPackageRepository - Data Access Layer for TourPackage Entity
 * 
 * This interface extends JpaRepository to provide CRUD operations and custom queries.
 * Spring Data JPA automatically implements this interface at runtime.
 * 
 * Key Concepts:
 * - JpaRepository: Provides basic CRUD operations (save, findById, findAll, delete, etc.)
 * - @Repository: Marks this as a Spring repository component
 * - Query Methods: Spring Data JPA can create queries from method names
 * - @Query: Custom JPQL or native SQL queries
 * - @Param: Binds method parameters to query parameters
 */
@Repository
public interface TourPackageRepository extends JpaRepository<TourPackage, Long> {
    
    /**
     * Find all active tour packages
     * 
     * Spring Data JPA automatically creates a query from the method name:
     * - findBy: indicates a query
     * - IsActive: matches the field name
     * - True: matches the value
     * 
     * Generated SQL: SELECT * FROM tour_packages WHERE is_active = true
     */
    List<TourPackage> findByIsActiveTrue();
    
    /**
     * Find tour packages by location
     * 
     * Generated SQL: SELECT * FROM tour_packages WHERE location = ?
     */
    List<TourPackage> findByLocation(String location);
    
    /**
     * Find tour packages by location and active status
     * 
     * Generated SQL: SELECT * FROM tour_packages WHERE location = ? AND is_active = ?
     */
    List<TourPackage> findByLocationAndIsActive(String location, Boolean isActive);
    
    /**
     * Find tour packages with price less than or equal to specified amount
     * 
     * Generated SQL: SELECT * FROM tour_packages WHERE price <= ?
     */
    List<TourPackage> findByPriceLessThanEqual(BigDecimal maxPrice);
    
    /**
     * Find tour packages with price between min and max
     * 
     * Generated SQL: SELECT * FROM tour_packages WHERE price BETWEEN ? AND ?
     */
    List<TourPackage> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find tour packages starting after a specific date
     * 
     * Generated SQL: SELECT * FROM tour_packages WHERE start_date > ?
     */
    List<TourPackage> findByStartDateAfter(LocalDate date);
    
    /**
     * Find tour packages by location and price range
     * 
     * Generated SQL: SELECT * FROM tour_packages WHERE location = ? AND price BETWEEN ? AND ?
     */
    List<TourPackage> findByLocationAndPriceBetween(String location, BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Custom query using JPQL (Java Persistence Query Language)
     * 
     * @Query: Defines a custom query
     * - JPQL uses entity names and field names, not table/column names
     * - :location is a parameter placeholder
     * - :minPrice and :maxPrice are parameter placeholders
     * 
     * Generated SQL: SELECT * FROM tour_packages WHERE location = ? AND price BETWEEN ? AND ? AND is_active = true
     */
    @Query("SELECT tp FROM TourPackage tp WHERE tp.location = :location AND tp.price BETWEEN :minPrice AND :maxPrice AND tp.isActive = true")
    List<TourPackage> findActivePackagesByLocationAndPriceRange(
        @Param("location") String location,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice
    );
    
    /**
     * Custom query to find packages with available capacity
     * 
     * This query finds packages where either:
     * - maxCapacity is null (no limit), OR
     * - currentBookings is less than maxCapacity
     * 
     * Generated SQL: SELECT * FROM tour_packages WHERE (max_capacity IS NULL OR current_bookings < max_capacity) AND is_active = true
     */
    @Query("SELECT tp FROM TourPackage tp WHERE (tp.maxCapacity IS NULL OR tp.currentBookings < tp.maxCapacity) AND tp.isActive = true")
    List<TourPackage> findAvailablePackages();
    
    /**
     * Custom query to find packages by title containing a keyword (case-insensitive)
     * 
     * LOWER() function makes the search case-insensitive
     * %:keyword% creates a LIKE query with wildcards
     * 
     * Generated SQL: SELECT * FROM tour_packages WHERE LOWER(title) LIKE LOWER('%?%')
     */
    @Query("SELECT tp FROM TourPackage tp WHERE LOWER(tp.title) LIKE LOWER(CONCAT('%', :keyword, '%')) AND tp.isActive = true")
    List<TourPackage> searchPackagesByTitle(@Param("keyword") String keyword);
    
    /**
     * Custom query to find packages by multiple criteria
     * 
     * This demonstrates a complex query with multiple optional conditions
     * 
     * Generated SQL: SELECT * FROM tour_packages WHERE 
     *   (location = ? OR ? IS NULL) AND 
     *   (price <= ? OR ? IS NULL) AND 
     *   (start_date >= ? OR ? IS NULL) AND 
     *   is_active = true
     */
    @Query("SELECT tp FROM TourPackage tp WHERE " +
           "(:location IS NULL OR tp.location = :location) AND " +
           "(:maxPrice IS NULL OR tp.price <= :maxPrice) AND " +
           "(:startDate IS NULL OR tp.startDate >= :startDate) AND " +
           "tp.isActive = true")
    List<TourPackage> findPackagesByCriteria(
        @Param("location") String location,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("startDate") LocalDate startDate
    );
    
    /**
     * Count packages by location
     * 
     * Generated SQL: SELECT COUNT(*) FROM tour_packages WHERE location = ?
     */
    long countByLocation(String location);
    
    /**
     * Check if any package exists with the given title
     * 
     * Generated SQL: SELECT COUNT(*) > 0 FROM tour_packages WHERE title = ?
     */
    boolean existsByTitle(String title);
} 