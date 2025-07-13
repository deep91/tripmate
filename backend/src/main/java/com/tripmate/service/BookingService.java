package com.tripmate.service;

import com.tripmate.dto.BookingDto;
import com.tripmate.model.Booking;
import com.tripmate.model.TourPackage;
import com.tripmate.repository.BookingRepository;
import com.tripmate.repository.TourPackageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * BookingService - Business Logic Layer for Booking
 * 
 * This service class contains all business logic related to bookings.
 * It manages the relationship between bookings and tour packages.
 * 
 * Key Features:
 * - Booking creation with validation
 * - Booking cancellation with business rules
 * - Tour package capacity management
 * - Price calculations
 * - Transaction management
 * - Error handling and logging
 * 
 * Business Rules:
 * - Cannot book if tour package is not available
 * - Cannot book if capacity is exceeded
 * - Cannot cancel if booking is already cancelled
 * - Total price = package price * number of people
 * - Booking date is automatically set
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {
    
    /**
     * Repository for booking data access
     */
    private final BookingRepository bookingRepository;
    
    /**
     * Repository for tour package data access
     */
    private final TourPackageRepository tourPackageRepository;
    
    /**
     * Get all bookings
     * 
     * @return List of all bookings
     */
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll();
    }
    
    /**
     * Get booking by ID
     * 
     * @param id Booking ID
     * @return Optional containing the booking if found
     */
    @Transactional(readOnly = true)
    public Optional<Booking> getBookingById(Long id) {
        log.info("Fetching booking with ID: {}", id);
        return bookingRepository.findById(id);
    }
    
    /**
     * Get bookings by user email
     * 
     * @param email User email
     * @return List of bookings for the user
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByEmail(String email) {
        log.info("Fetching bookings for email: {}", email);
        return bookingRepository.findByEmail(email);
    }
    
    /**
     * Get active bookings by user email
     * 
     * @param email User email
     * @return List of active bookings for the user
     */
    @Transactional(readOnly = true)
    public List<Booking> getActiveBookingsByEmail(String email) {
        log.info("Fetching active bookings for email: {}", email);
        return bookingRepository.findActiveBookingsByEmail(email);
    }
    
    /**
     * Get bookings by status
     * 
     * @param status Booking status (CONFIRMED, CANCELLED, PENDING)
     * @return List of bookings with the specified status
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByStatus(String status) {
        log.info("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatusOrderByBookingDateDesc(status);
    }
    
    /**
     * Get bookings by tour package ID
     * 
     * @param tourPackageId Tour package ID
     * @return List of bookings for the tour package
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookingsByTourPackageId(Long tourPackageId) {
        log.info("Fetching bookings for tour package ID: {}", tourPackageId);
        return bookingRepository.findByTourPackageId(tourPackageId);
    }
    
    /**
     * Get recent bookings (last 30 days)
     * 
     * @return List of recent bookings
     */
    @Transactional(readOnly = true)
    public List<Booking> getRecentBookings() {
        log.info("Fetching recent bookings");
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return bookingRepository.findRecentBookings(thirtyDaysAgo);
    }
    
    /**
     * Create a new booking
     * 
     * @param bookingDto DTO containing booking data
     * @return Created booking
     * @throws IllegalArgumentException if validation fails or tour package not available
     */
    public Booking createBooking(BookingDto bookingDto) {
        log.info("Creating new booking for tour package ID: {}", bookingDto.getTourPackageId());
        
        // Validate DTO
        if (!bookingDto.isValidForCreation()) {
            throw new IllegalArgumentException("Invalid booking data");
        }
        
        // Find tour package
        TourPackage tourPackage = tourPackageRepository.findById(bookingDto.getTourPackageId())
            .orElseThrow(() -> new IllegalArgumentException("Tour package with ID " + bookingDto.getTourPackageId() + " not found"));
        
        // Check if tour package is active
        if (!tourPackage.getIsActive()) {
            throw new IllegalArgumentException("Tour package is not active");
        }
        
        // Check if tour package is available for booking
        if (!tourPackage.isAvailableForBooking()) {
            throw new IllegalArgumentException("Tour package is not available for booking");
        }
        
        // Check if user has already booked this tour package
        if (bookingRepository.existsByEmailAndTourPackageId(bookingDto.getEmail(), bookingDto.getTourPackageId())) {
            throw new IllegalArgumentException("User has already booked this tour package");
        }
        
        // Calculate total price
        BigDecimal totalPrice = tourPackage.getPrice().multiply(BigDecimal.valueOf(bookingDto.getNumberOfPeople()));
        
        // Validate total price matches DTO
        if (totalPrice.compareTo(bookingDto.getTotalPrice()) != 0) {
            throw new IllegalArgumentException("Total price calculation mismatch");
        }
        
        // Create booking entity
        Booking booking = new Booking(
            bookingDto.getUserName(),
            bookingDto.getEmail(),
            bookingDto.getNumberOfPeople(),
            totalPrice,
            tourPackage
        );
        
        // Set optional fields
        booking.setPhoneNumber(bookingDto.getPhoneNumber());
        booking.setSpecialRequests(bookingDto.getSpecialRequests());
        
        // Save booking
        Booking savedBooking = bookingRepository.save(booking);
        
        // Update tour package booking count
        tourPackage.incrementBookings();
        tourPackageRepository.save(tourPackage);
        
        log.info("Successfully created booking with ID: {} for tour package: {}", 
                savedBooking.getId(), tourPackage.getTitle());
        
        return savedBooking;
    }
    
    /**
     * Cancel a booking
     * 
     * @param id Booking ID
     * @return Cancelled booking
     * @throws IllegalArgumentException if booking not found or cannot be cancelled
     */
    public Booking cancelBooking(Long id) {
        log.info("Cancelling booking with ID: {}", id);
        
        // Find booking
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Booking with ID " + id + " not found"));
        
        // Check if booking can be cancelled
        if (booking.isCancelled()) {
            throw new IllegalArgumentException("Booking is already cancelled");
        }
        
        if (!booking.isConfirmed()) {
            throw new IllegalArgumentException("Only confirmed bookings can be cancelled");
        }
        
        // Cancel booking
        booking.cancel();
        Booking cancelledBooking = bookingRepository.save(booking);
        
        // Update tour package booking count
        TourPackage tourPackage = booking.getTourPackage();
        tourPackage.decrementBookings();
        tourPackageRepository.save(tourPackage);
        
        log.info("Successfully cancelled booking with ID: {}", id);
        
        return cancelledBooking;
    }
    
    /**
     * Update booking status
     * 
     * @param id Booking ID
     * @param status New status
     * @return Updated booking
     * @throws IllegalArgumentException if booking not found or invalid status
     */
    public Booking updateBookingStatus(Long id, String status) {
        log.info("Updating booking status to {} for booking ID: {}", status, id);
        
        // Validate status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid booking status: " + status);
        }
        
        // Find booking
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Booking with ID " + id + " not found"));
        
        // Update status
        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);
        
        log.info("Successfully updated booking status to {} for booking ID: {}", status, id);
        
        return updatedBooking;
    }
    
    /**
     * Get booking statistics
     * 
     * @return Booking statistics
     */
    @Transactional(readOnly = true)
    public BookingStatistics getBookingStatistics() {
        log.info("Fetching booking statistics");
        
        long totalBookings = bookingRepository.count();
        long confirmedBookings = bookingRepository.countByStatus("CONFIRMED");
        long cancelledBookings = bookingRepository.countByStatus("CANCELLED");
        long pendingBookings = bookingRepository.countByStatus("PENDING");
        
        return new BookingStatistics(totalBookings, confirmedBookings, cancelledBookings, pendingBookings);
    }
    
    /**
     * Check if user can book a tour package
     * 
     * @param email User email
     * @param tourPackageId Tour package ID
     * @return true if user can book, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean canUserBookTourPackage(String email, Long tourPackageId) {
        // Check if user has already booked this tour package
        if (bookingRepository.existsByEmailAndTourPackageId(email, tourPackageId)) {
            return false;
        }
        
        // Check if tour package is available
        TourPackage tourPackage = tourPackageRepository.findById(tourPackageId).orElse(null);
        return tourPackage != null && tourPackage.isAvailableForBooking();
    }
    
    /**
     * Calculate total revenue from bookings
     * 
     * @return Total revenue
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalRevenue() {
        log.info("Calculating total revenue from confirmed bookings");
        
        List<Booking> confirmedBookings = bookingRepository.findByStatus("CONFIRMED");
        return confirmedBookings.stream()
            .map(Booking::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Validate booking status
     * 
     * @param status Status to validate
     * @return true if status is valid, false otherwise
     */
    private boolean isValidStatus(String status) {
        return "CONFIRMED".equals(status) || "CANCELLED".equals(status) || "PENDING".equals(status);
    }
    
    /**
     * Inner class for booking statistics
     */
    public static class BookingStatistics {
        private final long totalBookings;
        private final long confirmedBookings;
        private final long cancelledBookings;
        private final long pendingBookings;
        
        public BookingStatistics(long totalBookings, long confirmedBookings, 
                               long cancelledBookings, long pendingBookings) {
            this.totalBookings = totalBookings;
            this.confirmedBookings = confirmedBookings;
            this.cancelledBookings = cancelledBookings;
            this.pendingBookings = pendingBookings;
        }
        
        public long getTotalBookings() { return totalBookings; }
        public long getConfirmedBookings() { return confirmedBookings; }
        public long getCancelledBookings() { return cancelledBookings; }
        public long getPendingBookings() { return pendingBookings; }
    }
} 