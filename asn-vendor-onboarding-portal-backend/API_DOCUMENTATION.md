# ASN Vendor Onboarding Portal API Documentation

## Overview

The ASN Vendor Onboarding Portal API provides comprehensive endpoints for vendor onboarding, OEM integration, and portal management. This API follows RESTful principles and uses JWT-based authentication with multi-tenancy support.

## Base URL

- **Development**: `http://localhost:8082/api/v1`
- **Production**: `https://api.asnvendorportal.com/api/v1`

## Authentication

All protected endpoints require JWT authentication using the Bearer token scheme:

```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

> **Note**: Authentication is handled by a separate microservice. This service only handles JWT token validation and business operations.

### User Management APIs

#### 1. Get User Profile
- **Endpoint**: `GET /user/profile`
- **Description**: Retrieves current user's profile
- **Headers**: `Authorization: Bearer <token>`
- **Response**: User profile information

#### 2. Update User Profile
- **Endpoint**: `PUT /user/profile`
- **Description**: Updates user profile
- **Headers**: `Authorization: Bearer <token>`
- **Body**: Updated user profile data
- **Response**: Updated user profile

#### 3. Get Profile Image
- **Endpoint**: `GET /user/profile/image`
- **Description**: Gets user profile image URL
- **Headers**: `Authorization: Bearer <token>`
- **Response**: Profile image URL

#### 4. Get Session Context
- **Endpoint**: `GET /user/session-context`
- **Description**: Gets user session information
- **Headers**: `Authorization: Bearer <token>`
- **Response**: Session context data

#### 5. Select OEM
- **Endpoint**: `POST /user/select-oem`
- **Description**: Sets selected OEM for session
- **Headers**: `Authorization: Bearer <token>`
- **Body**: OEM selection data
- **Response**: Selection confirmation

#### 6. Refresh OEM Access
- **Endpoint**: `POST /user/refresh-oem-access`
- **Description**: Refreshes OEM access permissions
- **Headers**: `Authorization: Bearer <token>`
- **Response**: Updated access information

### OEM Portal APIs

#### 1. Get Available OEMs
- **Endpoint**: `GET /oems/available`
- **Description**: Gets list of available OEMs for vendor
- **Headers**: `Authorization: Bearer <token>`
- **Response**: List of OEMs with access information

#### 2. Get OEM Details
- **Endpoint**: `GET /oems/{oemId}`
- **Description**: Gets detailed OEM information
- **Headers**: `Authorization: Bearer <token>`
- **Path Parameters**: `oemId` - OEM identifier
- **Response**: Detailed OEM information

#### 3. Get OEM Configuration
- **Endpoint**: `GET /oems/{oemId}/config`
- **Description**: Gets OEM-specific configuration
- **Headers**: `Authorization: Bearer <token>`
- **Path Parameters**: `oemId` - OEM identifier
- **Response**: OEM configuration settings

#### 4. Request OEM Access
- **Endpoint**: `POST /oems/{oemId}/request-access`
- **Description**: Requests access to specific OEM
- **Headers**: `Authorization: Bearer <token>`
- **Path Parameters**: `oemId` - OEM identifier
- **Body**: Access request data
- **Response**: Access request confirmation

## Response Format

All API responses follow a consistent format:

### Success Response
```json
{
  "responseCode": 200,
  "message": "Operation successful",
  "ok": true,
  "body": {
    // Response data
  }
}
```

### Error Response
```json
{
  "responseCode": 400,
  "message": "Error description",
  "ok": false,
  "body": null
}
```

## Data Models

### User Profile
```json
{
  "id": "string",
  "email": "string",
  "name": "string",
  "companyName": "string",
  "panNumber": "string",
  "contactPerson": "string",
  "phone": "string",
  "vendorCode": "string",
  "currentPlan": "string",
  "gstinDetails": [
    {
      "id": "string",
      "gstinNumber": "string",
      "legalName": "string",
      "tradeName": "string",
      "state": "string",
      "stateCode": "string",
      "address": "string",
      "pincode": "string",
      "status": "string",
      "isPrimary": boolean
    }
  ],
  "status": "string",
  "createdAt": "string"
}
```

### OEM Information
```json
{
  "id": "string",
  "shortName": "string",
  "fullName": "string",
  "logoBackground": "string",
  "features": ["string"],
  "status": "string",
  "hasAccess": boolean,
  "accessLevel": "string",
  "isComingSoon": boolean,
  "noAccess": boolean,
  "noAccessReason": "string",
  "description": "string",
  "website": "string",
  "supportEmail": "string",
  "supportPhone": "string"
}
```

## Error Codes

- **200**: Success
- **400**: Bad Request
- **401**: Unauthorized
- **403**: Forbidden
- **404**: Not Found
- **500**: Internal Server Error

## Rate Limiting

API calls are rate-limited based on subscription plan:
- **Basic Plan**: 1000 calls/day
- **Premium Plan**: 10000 calls/day
- **Enterprise Plan**: Unlimited

## Multi-Tenancy

The API supports multi-tenancy through company codes extracted from JWT tokens. Each request is automatically scoped to the appropriate tenant context.

## Security Features

- JWT-based authentication
- 2FA support
- Token validation
- Multi-tenant data isolation
- Audit logging
- Rate limiting
- CORS protection

## Support

For API support and documentation:
- **Email**: support@taxgenie.in
- **Documentation**: https://docs.asnvendorportal.com
- **Status Page**: https://status.asnvendorportal.com
