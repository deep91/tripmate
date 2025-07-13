package com.tripmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for TripMate Backend
 * 
 * CORS (Cross-Origin Resource Sharing) is a security feature implemented by browsers
 * that restricts web pages from making requests to a different domain than the one
 * that served the original page.
 * 
 * This configuration allows the frontend (running on a different port/domain) to
 * communicate with the backend API.
 * 
 * Key Concepts:
 * - Origin: The domain, protocol, and port of the requesting page
 * - Preflight Request: Browser sends OPTIONS request before actual request
 * - Allowed Origins: Domains that can access the API
 * - Allowed Methods: HTTP methods that are permitted
 * - Allowed Headers: Request headers that are permitted
 * - Exposed Headers: Response headers that browsers can access
 * - Credentials: Whether cookies/authentication can be sent
 * 
 * Development vs Production:
 * - Development: Allow all origins (*) for easy testing
 * - Production: Restrict to specific domains for security
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    /**
     * Configure CORS for all endpoints
     * 
     * This method overrides the default CORS configuration and applies
     * our custom settings to all endpoints in the application.
     * 
     * @param registry CORS registry for configuration
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply to all endpoints
        .allowedOriginPatterns("http://localhost:*") // Allow any localhost port
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // Allowed HTTP methods
            .allowedHeaders("*") // Allow all headers
            .exposedHeaders("Authorization", "Content-Type", "X-Requested-With") // Expose specific headers
            .allowCredentials(false) // Allow cookies and authentication
            .maxAge(3600); // Cache preflight requests for 1 hour
    }
    
    /**
     * Alternative CORS configuration using CorsConfigurationSource
     * 
     * This bean provides more granular control over CORS settings.
     * It can be used in combination with or as an alternative to addCorsMappings.
     * 
     * @return CorsConfigurationSource with custom CORS settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow all origins for development
        // In production, specify exact origins:
        // configuration.setAllowedOrigins(Arrays.asList("https://yourdomain.com", "https://www.yourdomain.com"));
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        
        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Origin", "Content-Type", "Accept", "Authorization", 
            "X-Requested-With", "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Expose specific headers to the browser
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With"
        ));
        
        // Allow credentials (cookies, authentication headers)
        configuration.setAllowCredentials(true);
        
        // Cache preflight requests for 1 hour
        configuration.setMaxAge(3600L);
        
        // Apply configuration to all URLs
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * Production-ready CORS configuration
     * 
     * This method can be used in production to restrict CORS to specific domains.
     * Uncomment and modify the origins list for production deployment.
     * 
     * @return CorsConfigurationSource for production
     */
    /*
    @Bean
    @Profile("production") // Only active in production profile
    public CorsConfigurationSource productionCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Restrict to specific domains in production
        configuration.setAllowedOrigins(Arrays.asList(
            "https://tripmate.com",
            "https://www.tripmate.com",
            "https://app.tripmate.com"
        ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    */
} 