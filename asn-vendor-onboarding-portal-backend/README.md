# ASN Vendor Onboarding Portal - Backend

A comprehensive Spring Boot application for managing vendor onboarding and ASN (Advance Shipping Notice) compliance.

## Overview

The ASN Vendor Onboarding Portal is designed to streamline the process of vendor registration, onboarding, and management for ASN compliance. It provides a robust backend API for managing OEMs, vendors, subscriptions, billing, and API integrations.

## Features

- **OEM Management**: Manage Original Equipment Manufacturers with configurable settings
- **Vendor Registration**: Complete vendor onboarding workflow with document management
- **Subscription Management**: Flexible subscription plans and billing
- **API Integration**: Secure API credentials and rate limiting
- **Audit Trail**: Comprehensive logging and compliance tracking
- **Multi-tenancy**: Support for multiple OEMs and their vendors
- **Security**: JWT-based authentication with role-based access control

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.6**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL**
- **Maven**
- **Lombok**
- **MapStruct**
- **QueryDSL**
- **OpenAPI/Swagger**

## Database Schema

The application uses a PostgreSQL database with the following main entities:

- `system_config` - System configuration parameters
- `oem_master` - OEM information and settings
- `subscription_plans` - Available subscription plans
- `vendors` - Vendor company information
- `vendor_gstin` - Vendor GSTIN details
- `vendor_oem_access` - Vendor-OEM access mapping
- `onboarding_process` - Onboarding workflow tracking
- `subscriptions` - Active subscriptions
- `payment_transactions` - Payment history
- `api_credentials` - API access credentials
- `audit_logs` - Audit trail

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 14 or higher

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd asn-vendor-onboarding-portal-backend
```

2. Set up the database:
```sql
CREATE DATABASE asn_vendor_portal;
```

3. Configure application properties:
```yaml
# Update src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/asn_vendor_portal
    username: your_username
    password: your_password
```

4. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api/v1`

### API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api/v1/api-docs`

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | Database URL | `jdbc:postgresql://localhost:5432/asn_vendor_portal` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `password` |
| `JWT_SECRET` | JWT signing secret | (default secret - change in production) |
| `JWT_EXPIRATION` | JWT token expiration (ms) | `86400000` (24 hours) |
| `ALLOWED_ORIGINS` | CORS allowed origins | `http://localhost:3000,http://localhost:4200` |

### Profiles

- `dev` - Development profile with debug logging
- `test` - Test profile with H2 in-memory database
- `prod` - Production profile with optimized settings

## Project Structure

```
src/main/java/com/asn/vendor/
├── AsnVendorOnboardingPortalApplication.java  # Main application class
├── config/                                    # Configuration classes
├── controllers/                               # REST API controllers
├── dto/                                       # Data Transfer Objects
├── entities/                                  # JPA entities
├── repositories/                              # Data access layer
├── services/                                  # Business logic layer
├── security/                                  # Security configuration
├── utils/                                     # Utility classes
└── exceptions/                                # Custom exceptions
```

## API Endpoints

### Authentication
- `POST /auth/login` - User login
- `POST /auth/refresh` - Refresh JWT token
- `POST /auth/logout` - User logout

### OEM Management
- `GET /oems` - List all OEMs
- `POST /oems` - Create new OEM
- `GET /oems/{id}` - Get OEM details
- `PUT /oems/{id}` - Update OEM
- `DELETE /oems/{id}` - Delete OEM

### Vendor Management
- `GET /vendors` - List vendors
- `POST /vendors` - Register new vendor
- `GET /vendors/{id}` - Get vendor details
- `PUT /vendors/{id}` - Update vendor
- `DELETE /vendors/{id}` - Delete vendor

### Onboarding
- `POST /onboarding/start` - Start onboarding process
- `GET /onboarding/{id}` - Get onboarding status
- `PUT /onboarding/{id}/step` - Update onboarding step
- `POST /onboarding/{id}/complete` - Complete onboarding

### Subscriptions
- `GET /subscriptions` - List subscriptions
- `POST /subscriptions` - Create subscription
- `GET /subscriptions/{id}` - Get subscription details
- `PUT /subscriptions/{id}` - Update subscription

## Testing

Run tests with:
```bash
mvn test
```

Run with coverage:
```bash
mvn clean test jacoco:report
```

## Building

Build the application:
```bash
mvn clean package
```

This creates a JAR file in the `target/` directory.

## Docker Support

Build Docker image:
```bash
docker build -t asn-vendor-portal-backend .
```

Run with Docker:
```bash
docker run -p 8080:8080 asn-vendor-portal-backend
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please contact:
- Email: support@asnportal.com
- Phone: +1-800-ASN-HELP
