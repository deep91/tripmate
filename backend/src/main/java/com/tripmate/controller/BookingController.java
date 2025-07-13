package com.tripmate.controller;

import com.tripmate.dto.BookingDto;
import com.tripmate.model.Booking;
import com.tripmate.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * BookingController - REST API Controller for Booking
 * 
 * This controller provides REST endpoints for managing bookings.
 * It handles booking creation, cancellation, and retrieval operations.
 * 
 * Key Features:
 * - Booking creation with validation
 * - Booking cancellation with business rules
 * - User-specific booking retrieval
 * - Booking status management
 * - Statistics and analytics endpoints
 * 
 * Business Rules Enforced:
 * - Cannot book if tour package is not available
 * - Cannot book if user has already booked the same package
 * - Cannot cancel if booking is already cancelled
 * - Price validation and calculation
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow all origins for development
public class BookingController {
    
    /**
     * Service layer for booking business logic
     */
    private final BookingService bookingService;
    
    /**
     * GET /api/bookings
     * Get all bookings
     * 
     * @return List of all bookings with 200 OK status
     */
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        log.info("GET /api/bookings - Fetching all bookings");
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * GET /api/bookings/{id}
     * Get booking by ID
     * 
     * @param id Booking ID (path variable)
     * @return Booking with 200 OK status, or 404 Not Found if not exists
     */
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        log.info("GET /api/bookings/{} - Fetching booking by ID", id);
        
        Optional<Booking> booking = bookingService.getBookingById(id);
        
        return booking
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * GET /api/bookings/user/{email}
     * Get bookings by user email
     * 
     * @param email User email (path variable)
     * @return List of bookings for the user with 200 OK status
     */
    @GetMapping("/user/{email}")
    public ResponseEntity<List<Booking>> getBookingsByEmail(@PathVariable String email) {
        log.info("GET /api/bookings/user/{} - Fetching bookings by email", email);
        List<Booking> bookings = bookingService.getBookingsByEmail(email);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * GET /api/bookings/user/{email}/active
     * Get active bookings by user email
     * 
     * @param email User email (path variable)
     * @return List of active bookings for the user with 200 OK status
     */
    @GetMapping("/user/{email}/active")
    public ResponseEntity<List<Booking>> getActiveBookingsByEmail(@PathVariable String email) {
        log.info("GET /api/bookings/user/{}/active - Fetching active bookings by email", email);
        List<Booking> bookings = bookingService.getActiveBookingsByEmail(email);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * GET /api/bookings/status/{status}
     * Get bookings by status
     * 
     * @param status Booking status (CONFIRMED, CANCELLED, PENDING) (path variable)
     * @return List of bookings with the specified status with 200 OK status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Booking>> getBookingsByStatus(@PathVariable String status) {
        log.info("GET /api/bookings/status/{} - Fetching bookings by status", status);
        List<Booking> bookings = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * GET /api/bookings/package/{tourPackageId}
     * Get bookings by tour package ID
     * 
     * @param tourPackageId Tour package ID (path variable)
     * @return List of bookings for the tour package with 200 OK status
     */
    @GetMapping("/package/{tourPackageId}")
    public ResponseEntity<List<Booking>> getBookingsByTourPackageId(@PathVariable Long tourPackageId) {
        log.info("GET /api/bookings/package/{} - Fetching bookings by tour package ID", tourPackageId);
        List<Booking> bookings = bookingService.getBookingsByTourPackageId(tourPackageId);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * GET /api/bookings/recent
     * Get recent bookings (last 30 days)
     * 
     * @return List of recent bookings with 200 OK status
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Booking>> getRecentBookings() {
        log.info("GET /api/bookings/recent - Fetching recent bookings");
        List<Booking> bookings = bookingService.getRecentBookings();
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * POST /api/bookings
     * Create a new booking
     * 
     * @param bookingDto Booking data (request body)
     * @return Created booking with 201 Created status, or 400 Bad Request if validation fails
     */
    @PostMapping
    public ResponseEntity<Booking> createBooking(@Valid @RequestBody BookingDto bookingDto) {
        log.info("POST /api/bookings - Creating new booking for tour package ID: {}", bookingDto.getTourPackageId());
        
        try {
            Booking createdBooking = bookingService.createBooking(bookingDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBooking);
        } catch (IllegalArgumentException e) {
            log.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * DELETE /api/bookings/{id}
     * Cancel a booking
     * 
     * @param id Booking ID (path variable)
     * @return Cancelled booking with 200 OK status, or 404 Not Found if not exists, or 400 Bad Request if cannot be cancelled
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
        log.info("DELETE /api/bookings/{} - Cancelling booking", id);
        
        try {
            Booking cancelledBooking = bookingService.cancelBooking(id);
            return ResponseEntity.ok(cancelledBooking);
        } catch (IllegalArgumentException e) {
            log.error("Error cancelling booking: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * PATCH /api/bookings/{id}/status
     * Update booking status
     * 
     * @param id Booking ID (path variable)
     * @param status New status (request body as string)
     * @return Updated booking with 200 OK status, or 404 Not Found if not exists, or 400 Bad Request if invalid status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody String status) {
        
        log.info("PATCH /api/bookings/{}/status - Updating booking status to: {}", id, status);
        
        try {
            Booking updatedBooking = bookingService.updateBookingStatus(id, status);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            log.error("Error updating booking status: {}", e.getMessage());
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * GET /api/bookings/statistics
     * Get booking statistics
     * 
     * @return Booking statistics with 200 OK status
     */
    @GetMapping("/statistics")
    public ResponseEntity<BookingService.BookingStatistics> getBookingStatistics() {
        log.info("GET /api/bookings/statistics - Fetching booking statistics");
        BookingService.BookingStatistics statistics = bookingService.getBookingStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * GET /api/bookings/revenue
     * Get total revenue from confirmed bookings
     * 
     * @return Total revenue with 200 OK status
     */
    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        log.info("GET /api/bookings/revenue - Calculating total revenue");
        BigDecimal totalRevenue = bookingService.calculateTotalRevenue();
        return ResponseEntity.ok(totalRevenue);
    }
    
    /**
     * GET /api/bookings/check-availability
     * Check if user can book a tour package
     * 
     * @param email User email (query parameter)
     * @param tourPackageId Tour package ID (query parameter)
     * @return Boolean indicating if user can book with 200 OK status
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkBookingAvailability(
            @RequestParam String email,
            @RequestParam Long tourPackageId) {
        
        log.info("GET /api/bookings/check-availability - Checking availability for email: {}, tourPackageId: {}", 
                email, tourPackageId);
        
        boolean canBook = bookingService.canUserBookTourPackage(email, tourPackageId);
        return ResponseEntity.ok(canBook);
    }
    
    /**
     * GET /api/bookings/user/{email}/summary
     * Get booking summary for a user
     * 
     * @param email User email (path variable)
     * @return Booking summary with 200 OK status
     */
    @GetMapping("/user/{email}/summary")
    public ResponseEntity<BookingSummary> getBookingSummary(@PathVariable String email) {
        log.info("GET /api/bookings/user/{}/summary - Fetching booking summary", email);
        
        List<Booking> allBookings = bookingService.getBookingsByEmail(email);
        List<Booking> activeBookings = bookingService.getActiveBookingsByEmail(email);
        
        long totalBookings = allBookings.size();
        long activeBookingsCount = activeBookings.size();
        long cancelledBookings = allBookings.stream()
            .filter(Booking::isCancelled)
            .count();
        
        BigDecimal totalSpent = allBookings.stream()
            .filter(booking -> "CONFIRMED".equals(booking.getStatus()))
            .map(Booking::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BookingSummary summary = new BookingSummary(
            email, totalBookings, activeBookingsCount, cancelledBookings, totalSpent
        );
        
        return ResponseEntity.ok(summary);
    }
    
    /**
     * Inner class for booking summary
     */
    public static class BookingSummary {
        private final String email;
        private final long totalBookings;
        private final long activeBookings;
        private final long cancelledBookings;
        private final BigDecimal totalSpent;
        
        public BookingSummary(String email, long totalBookings, long activeBookings, 
                            long cancelledBookings, BigDecimal totalSpent) {
            this.email = email;
            this.totalBookings = totalBookings;
            this.activeBookings = activeBookings;
            this.cancelledBookings = cancelledBookings;
            this.totalSpent = totalSpent;
        }
        
        // Getters
        public String getEmail() { return email; }
        public long getTotalBookings() { return totalBookings; }
        public long getActiveBookings() { return activeBookings; }
        public long getCancelledBookings() { return cancelledBookings; }
        public BigDecimal getTotalSpent() { return totalSpent; }
    }
} 