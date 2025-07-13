package com.tripmate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI Configuration for TripMate Backend
 * 
 * This configuration sets up Swagger/OpenAPI documentation for the REST API.
 * It provides interactive API documentation that can be accessed via web browser.
 * 
 * Key Concepts:
 * - OpenAPI: Standard specification for REST API documentation
 * - Swagger UI: Interactive web interface for API documentation
 * - API Info: Metadata about the API (title, description, version, etc.)
 * - Servers: Different environments where the API can be accessed
 * - Contact: Information about API maintainers
 * - License: Legal information about API usage
 * 
 * Access Points:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 * - OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml
 * 
 * Benefits:
 * - Interactive API testing directly from browser
 * - Auto-generated documentation from code
 * - Easy API exploration for frontend developers
 * - Professional API documentation for stakeholders
 * - Version control for API documentation
 */
@Configuration
public class OpenApiConfig {
    
    /**
     * Configure OpenAPI documentation
     * 
     * This bean defines the overall API documentation including:
     * - API information (title, description, version)
     * - Contact information
     * - License information
     * - Server configurations
     * 
     * @return OpenAPI configuration object
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(apiInfo())
            .servers(servers());
    }
    
    /**
     * Define API information
     * 
     * This method provides metadata about the API including:
     * - Title and description
     * - Version information
     * - Contact details
     * - License information
     * 
     * @return Info object with API metadata
     */
    private Info apiInfo() {
        return new Info()
            .title("TripMate API")
            .description("""
                # TripMate Tourist Package Booking API
                
                This API provides endpoints for managing tourist packages and bookings.
                
                ## Features
                - **Tour Packages**: Create, read, update, and delete tour packages
                - **Bookings**: Manage tour bookings with validation and business rules
                - **Search & Filter**: Advanced search and filtering capabilities
                - **Statistics**: Analytics and reporting endpoints
                
                ## Authentication
                Currently, this API does not require authentication for development purposes.
                In production, JWT-based authentication will be implemented.
                
                ## Rate Limiting
                API requests are limited to 1000 requests per hour per IP address.
                
                ## Error Handling
                All endpoints return consistent error responses with appropriate HTTP status codes.
                Error responses include:
                - HTTP status code
                - Error message
                - Timestamp
                - Request path
                - Field-specific validation errors (when applicable)
                
                ## Data Formats
                - **Dates**: ISO 8601 format (YYYY-MM-DD)
                - **Times**: ISO 8601 format (YYYY-MM-DDTHH:mm:ss)
                - **Prices**: Decimal with 2 decimal places
                - **JSON**: All request/response bodies use JSON format
                
                ## Examples
                
                ### Create a Tour Package
                ```json
                {
                  "title": "Paris Adventure",
                  "description": "Explore the beautiful city of Paris",
                  "location": "Paris, France",
                  "startDate": "2024-06-01",
                  "endDate": "2024-06-05",
                  "price": 1299.99,
                  "maxCapacity": 20
                }
                ```
                
                ### Create a Booking
                ```json
                {
                  "userName": "John Doe",
                  "email": "john@example.com",
                  "numberOfPeople": 2,
                  "totalPrice": 2599.98,
                  "tourPackageId": 1
                }
                ```
                """)
            .version("1.0.0")
            .contact(contactInfo())
            .license(licenseInfo());
    }
    
    /**
     * Define contact information
     * 
     * @return Contact object with maintainer information
     */
    private Contact contactInfo() {
        return new Contact()
            .name("TripMate Development Team")
            .email("dev@tripmate.com")
            .url("https://tripmate.com");
    }
    
    /**
     * Define license information
     * 
     * @return License object with legal information
     */
    private License licenseInfo() {
        return new License()
            .name("MIT License")
            .url("https://opensource.org/licenses/MIT");
    }
    
    /**
     * Define server configurations
     * 
     * This method defines different environments where the API can be accessed.
     * Multiple servers can be configured for different environments.
     * 
     * @return List of server configurations
     */
    private List<Server> servers() {
        return List.of(
            // Development server
            new Server()
                .url("http://localhost:8080")
                .description("Development Server"),
            
            // Production server (example)
            new Server()
                .url("https://api.tripmate.com")
                .description("Production Server"),
            
            // Staging server (example)
            new Server()
                .url("https://staging-api.tripmate.com")
                .description("Staging Server")
        );
    }
} 