package com.tripmate.repository;

import com.tripmate.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * BookingRepository - Data Access Layer for Booking Entity
 * 
 * This interface extends JpaRepository to provide CRUD operations and custom queries
 * for booking management.
 * 
 * Key Features:
 * - Basic CRUD operations inherited from JpaRepository
 * - Custom query methods for booking-specific operations
 * - Relationship queries with TourPackage
 * - Date-based queries for booking analytics
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    /**
     * Find all bookings by user email
     * 
     * Generated SQL: SELECT * FROM bookings WHERE email = ?
     */
    List<Booking> findByEmail(String email);
    
    /**
     * Find all bookings by user name
     * 
     * Generated SQL: SELECT * FROM bookings WHERE user_name = ?
     */
    List<Booking> findByUserName(String userName);
    
    /**
     * Find all confirmed bookings
     * 
     * Generated SQL: SELECT * FROM bookings WHERE status = 'CONFIRMED'
     */
    List<Booking> findByStatus(String status);
    
    /**
     * Find all confirmed bookings
     * 
     * Generated SQL: SELECT * FROM bookings WHERE status = 'CONFIRMED'
     */
    List<Booking> findByStatusOrderByBookingDateDesc(String status);
    
    /**
     * Find bookings by tour package ID
     * 
     * Generated SQL: SELECT * FROM bookings WHERE package_id = ?
     */
    List<Booking> findByTourPackageId(Long tourPackageId);
    
    /**
     * Find bookings by tour package ID and status
     * 
     * Generated SQL: SELECT * FROM bookings WHERE package_id = ? AND status = ?
     */
    List<Booking> findByTourPackageIdAndStatus(Long tourPackageId, String status);
    
    /**
     * Find bookings made after a specific date
     * 
     * Generated SQL: SELECT * FROM bookings WHERE booking_date > ?
     */
    List<Booking> findByBookingDateAfter(LocalDateTime date);
    
    /**
     * Find bookings made between two dates
     * 
     * Generated SQL: SELECT * FROM bookings WHERE booking_date BETWEEN ? AND ?
     */
    List<Booking> findByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find bookings by email and status
     * 
     * Generated SQL: SELECT * FROM bookings WHERE email = ? AND status = ?
     */
    List<Booking> findByEmailAndStatus(String email, String status);
    
    /**
     * Find bookings with total price greater than specified amount
     * 
     * Generated SQL: SELECT * FROM bookings WHERE total_price > ?
     */
    List<Booking> findByTotalPriceGreaterThan(java.math.BigDecimal minPrice);
    
    /**
     * Find bookings by number of people
     * 
     * Generated SQL: SELECT * FROM bookings WHERE number_of_people = ?
     */
    List<Booking> findByNumberOfPeople(Integer numberOfPeople);
    
    /**
     * Custom query to find bookings with tour package details
     * 
     * This query joins the bookings table with tour_packages table
     * to get booking information along with tour package details
     * 
     * Generated SQL: 
     * SELECT b.*, tp.* FROM bookings b 
     * JOIN tour_packages tp ON b.package_id = tp.id 
     * WHERE b.email = ?
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.tourPackage WHERE b.email = :email")
    List<Booking> findBookingsWithTourPackageByEmail(@Param("email") String email);
    
    /**
     * Custom query to find bookings by location
     * 
     * This query finds bookings for tour packages in a specific location
     * 
     * Generated SQL:
     * SELECT b.* FROM bookings b 
     * JOIN tour_packages tp ON b.package_id = tp.id 
     * WHERE tp.location = ?
     */
    @Query("SELECT b FROM Booking b JOIN FETCH b.tourPackage tp WHERE tp.location = :location")
    List<Booking> findBookingsByLocation(@Param("location") String location);
    
    /**
     * Custom query to find recent bookings (last 30 days)
     * 
     * This query finds bookings made in the last 30 days
     * 
     * Generated SQL: SELECT * FROM bookings WHERE booking_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)
     */
    @Query("SELECT b FROM Booking b WHERE b.bookingDate >= :startDate ORDER BY b.bookingDate DESC")
    List<Booking> findRecentBookings(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Custom query to find bookings by price range
     * 
     * This query finds bookings with total price in a specific range
     * 
     * Generated SQL: SELECT * FROM bookings WHERE total_price BETWEEN ? AND ?
     */
    @Query("SELECT b FROM Booking b WHERE b.totalPrice BETWEEN :minPrice AND :maxPrice")
    List<Booking> findBookingsByPriceRange(
        @Param("minPrice") java.math.BigDecimal minPrice,
        @Param("maxPrice") java.math.BigDecimal maxPrice
    );
    
    /**
     * Custom query to find active bookings for a user
     * 
     * This query finds confirmed bookings for a specific user
     * 
     * Generated SQL: SELECT * FROM bookings WHERE email = ? AND status = 'CONFIRMED'
     */
    @Query("SELECT b FROM Booking b WHERE b.email = :email AND b.status = 'CONFIRMED' ORDER BY b.bookingDate DESC")
    List<Booking> findActiveBookingsByEmail(@Param("email") String email);
    
    /**
     * Custom query to find booking statistics by tour package
     * 
     * This query counts bookings and calculates total revenue for each tour package
     * 
     * Generated SQL:
     * SELECT tp.id, tp.title, COUNT(b.id), SUM(b.total_price) 
     * FROM tour_packages tp 
     * LEFT JOIN bookings b ON tp.id = b.package_id 
     * WHERE b.status = 'CONFIRMED' 
     * GROUP BY tp.id, tp.title
     */
    @Query("SELECT tp.id, tp.title, COUNT(b.id), SUM(b.totalPrice) " +
           "FROM TourPackage tp LEFT JOIN Booking b ON b.tourPackage = tp " +
           "WHERE b.status = 'CONFIRMED' " +
           "GROUP BY tp.id, tp.title")
    List<Object[]> findBookingStatisticsByPackage();
    
    /**
     * Count bookings by status
     * 
     * Generated SQL: SELECT COUNT(*) FROM bookings WHERE status = ?
     */
    long countByStatus(String status);
    
    /**
     * Count bookings by email
     * 
     * Generated SQL: SELECT COUNT(*) FROM bookings WHERE email = ?
     */
    long countByEmail(String email);
    
    /**
     * Check if a booking exists for a specific email and tour package
     * 
     * Generated SQL: SELECT COUNT(*) > 0 FROM bookings WHERE email = ? AND package_id = ?
     */
    boolean existsByEmailAndTourPackageId(String email, Long tourPackageId);
    
    /**
     * Find the most recent booking for a user
     * 
     * Generated SQL: SELECT * FROM bookings WHERE email = ? ORDER BY booking_date DESC LIMIT 1
     */
    @Query("SELECT b FROM Booking b WHERE b.email = :email ORDER BY b.bookingDate DESC")
    List<Booking> findTopByEmailOrderByBookingDateDesc(@Param("email") String email);
} 