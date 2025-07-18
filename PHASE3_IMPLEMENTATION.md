# Phase 3: Advanced Features Implementation

This document describes the implementation of Phase 3 advanced features for the Property Management System (PMS), including rate management, offer system, and department permissions.

## Overview

The Phase 3 implementation introduces three major microservices:

1. **Rate Management Service** (`rate-management`) - Port 8083
2. **Offer System Service** (`offer-system`) - Port 8084  
3. **Enhanced User Management Service** (`usermanagement`) - Port 8080 (existing, enhanced with department permissions)

## 1. Rate Management Service

### Features Implemented

- **Rate Plan Management**: Create, update, and manage hotel rate plans
- **Seasonal Rate Adjustments**: Configure seasonal rate modifications
- **Rate Restrictions**: Set booking restrictions by date
- **Rate Calculation Engine**: Calculate rates with seasonal adjustments and restrictions
- **Best Available Rate (BAR)**: Support for dynamic rate selection

### Key Entities

- `RatePlan`: Core rate plan configuration
- `SeasonalRate`: Seasonal rate adjustments (percentage or fixed amount)
- `RateRestriction`: Date-specific booking restrictions

### API Endpoints

```
POST   /api/v1/rate-management/rate-plans           # Create rate plan
PUT    /api/v1/rate-management/rate-plans/{id}      # Update rate plan
GET    /api/v1/rate-management/rate-plans/property/{propertyId}  # Get rate plans by property
POST   /api/v1/rate-management/rates/calculate      # Calculate rates
GET    /api/v1/rate-management/health               # Health check
```

### Rate Calculation Features

- Seasonal rate adjustments (percentage or fixed amount)
- Stay duration validation (minimum/maximum stay)
- Rate restriction checking
- Daily rate breakdown
- Average nightly rate calculation

## 2. Offer System Service

### Features Implemented

- **Offer Definition**: Create and manage promotional offers
- **Department Authorization**: Control which departments can use specific offers
- **Approval Workflows**: Require approval for certain offers based on department permissions
- **Eligibility Checking**: Validate offer eligibility based on booking criteria
- **Offer Application**: Apply offers to reservations with discount calculation
- **Usage Tracking**: Track offer usage and enforce limits

### Key Entities

- `Offer`: Core offer configuration with discount rules
- `OfferApproval`: Approval workflow tracking
- `OfferApplication`: Record of applied offers

### Offer Types

- `ROOM_DISCOUNT`: Direct room rate discounts
- `PACKAGE_DEAL`: Bundled offers
- `SPECIAL_RATE`: Special promotional rates
- `LOYALTY_REWARD`: Loyalty program benefits
- `PROMOTIONAL`: General promotional offers

### Discount Types

- `PERCENTAGE`: Percentage-based discounts
- `FIXED_AMOUNT`: Fixed dollar amount discounts
- `NIGHTS_FREE`: Free night promotions

### API Endpoints

```
POST   /api/v1/offer-system/offers                 # Create offer
PUT    /api/v1/offer-system/offers/{id}            # Update offer
GET    /api/v1/offer-system/offers/property/{propertyId}  # Get offers by property
GET    /api/v1/offer-system/offers/property/{propertyId}/department/{department}  # Get offers by department
POST   /api/v1/offer-system/offers/eligibility     # Check offer eligibility
POST   /api/v1/offer-system/approvals              # Request offer approval
PUT    /api/v1/offer-system/approvals/{id}/approve # Approve offer
PUT    /api/v1/offer-system/approvals/{id}/reject  # Reject offer
POST   /api/v1/offer-system/offers/{code}/apply    # Apply offer to reservation
GET    /api/v1/offer-system/approvals/department/{department}/pending  # Get pending approvals
GET    /api/v1/offer-system/health                 # Health check
```

### Eligibility Rules

- Room type compatibility
- Rate plan compatibility  
- Stay duration requirements
- Advance booking requirements
- Department authorization
- Usage limits
- Date validity

## 3. Department Permissions (Enhanced User Management)

### Features Implemented

- **Department Management**: Create and manage organizational departments
- **Role-Based Access Control**: Enhanced RBAC with department-level permissions
- **Permission Validation**: Check user permissions for specific actions
- **Offer Permission Control**: Control offer application permissions with discount limits
- **Approval Thresholds**: Configure approval requirements based on amount thresholds

### Key Entities

- `Department`: Organizational departments
- `User`: Enhanced with department association
- `Role`: User roles with permissions
- `Permission`: Action-based permissions
- `DepartmentPermission`: Department-specific permission configurations

### Permission Features

- **Resource-Action Based**: Permissions defined by resource and action (e.g., "OFFER", "APPLY")
- **Discount Limits**: Maximum discount amounts per department
- **Approval Thresholds**: Automatic approval requirement based on amounts
- **Hierarchical Departments**: Support for parent-child department relationships

### API Endpoints

```
GET    /api/v1/user-management/departments/property/{propertyId}  # Get departments by property
GET    /api/v1/user-management/departments/{departmentCode}/permissions  # Get department permissions
POST   /api/v1/user-management/departments         # Create department
POST   /api/v1/user-management/permissions/check   # Check user permission
POST   /api/v1/user-management/permissions/offer-check  # Check offer permission
POST   /api/v1/user-management/permissions/approval-check  # Check if approval required
POST   /api/v1/user-management/departments/{id}/permissions  # Grant permission
DELETE /api/v1/user-management/departments/{id}/permissions  # Revoke permission
GET    /api/v1/user-management/health              # Health check
```

## Integration Architecture

### Service Communication

The services are designed to work together through:

1. **REST API Integration**: Services communicate via HTTP APIs
2. **Eureka Service Discovery**: All services register with Eureka for discovery
3. **API Gateway**: Gateway routes requests to appropriate services
4. **Event-Driven Architecture**: Kafka integration for asynchronous communication

### Data Flow Examples

#### Offer Application Flow

1. Frontend requests offer eligibility check from Offer System
2. Offer System validates offers against booking criteria
3. If offer requires approval, Offer System creates approval request
4. User Management Service validates department permissions
5. Manager approves/rejects through Offer System
6. Approved offers can be applied to reservations

#### Rate Calculation Flow

1. Booking system requests rate calculation from Rate Management
2. Rate Management finds applicable rate plans
3. Seasonal adjustments are applied
4. Restrictions are checked
5. Final rate breakdown is returned

## Database Design

### Rate Management Database (PostgreSQL)

- `rate_plans`: Core rate plan data
- `seasonal_rates`: Seasonal rate adjustments
- `rate_restrictions`: Date-specific restrictions

### Offer System Database (PostgreSQL)

- `offers`: Offer definitions
- `offer_approvals`: Approval workflow tracking
- `offer_applications`: Applied offer records

### User Management Database (PostgreSQL)

- `departments`: Department definitions
- `users`: User accounts with department associations
- `roles`: User roles
- `permissions`: Role-based permissions
- `department_permissions`: Department-specific permissions
- `user_roles`: User-role mappings

## Testing

### Unit Tests Implemented

1. **RateManagementServiceTest**: Tests rate plan creation, calculation logic
2. **OfferServiceTest**: Tests offer creation, eligibility checking, discount calculation
3. **DepartmentPermissionServiceTest**: Tests permission validation, department management

### Test Coverage

- Service layer business logic
- Permission validation
- Edge cases and error conditions
- Mock-based testing with Mockito

## Configuration

### Service Ports

- Discovery Service: 8761
- Gateway Service: 8080
- Configuration Service: 8888
- User Management: 8080
- Rate Management: 8083
- Offer System: 8084

### Database Configuration

Each service is configured to use PostgreSQL with separate databases:

- User Management: `userdb`
- Rate Management: `ratedb`
- Offer System: `offerdb`

## Deployment

### Prerequisites

- JDK 17+
- Maven 3.8+
- PostgreSQL 12+
- Kafka (for event-driven communication)

### Build and Run

```bash
# Build all services
mvn clean compile

# Run tests
mvn test

# Run individual services
cd usermanagement && mvn spring-boot:run
cd rate-management && mvn spring-boot:run
cd offer-system && mvn spring-boot:run
```

## Security Considerations

1. **Authentication**: JWT-based authentication (framework ready)
2. **Authorization**: Role and department-based access control
3. **Data Validation**: Input validation on all API endpoints
4. **Database Security**: Prepared statements prevent SQL injection
5. **Audit Trails**: All entities include audit fields (created_by, updated_by, timestamps)

## Future Enhancements

1. **Real-time Notifications**: Implement real-time approval notifications
2. **Advanced Reporting**: Analytics on rate performance and offer usage
3. **Integration APIs**: External system integration (Channel Managers, OTAs)
4. **Mobile Support**: REST APIs are ready for mobile application integration
5. **Caching**: Implement Redis caching for rate calculations and offers
6. **Multi-tenancy**: Property-level isolation is already implemented

## Monitoring and Observability

- **Health Checks**: All services expose `/health` endpoints
- **Actuator Integration**: Spring Boot Actuator for monitoring
- **Distributed Tracing**: Zipkin integration ready
- **Metrics**: Micrometer integration for application metrics

This implementation provides a solid foundation for the Phase 3 advanced features while maintaining scalability, security, and maintainability principles.