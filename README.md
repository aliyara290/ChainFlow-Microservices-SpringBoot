# ChainFlow - Microservices Architecture

## 📋 Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Microservices](#microservices)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Security](#security)
- [Business Rules](#business-rules)
- [Service Communication](#service-communication)
- [Contributing](#contributing)

## 🎯 Overview

**ChainFlow** is a comprehensive supply chain management system built with a microservices architecture. The platform provides end-to-end supply chain management from raw material procurement to finished product delivery, featuring seamless integration between procurement, production, and customer delivery modules.

### Key Features
- **Procurement Management**: Supplier management, raw materials tracking, and supply order processing
- **Production Management**: Product manufacturing, bill of materials (BOM), and production order scheduling
- **Customer & Delivery Management**: Customer orders, delivery tracking, and logistics coordination
- **Centralized Authentication**: Secure role-based access control across all services
- **Service Discovery**: Dynamic service registration and discovery with Eureka
- **API Gateway**: Single entry point for all client requests with routing and load balancing

## 🏗️ Architecture

The system follows a microservices architecture with synchronous communication using Feign Client, service discovery via Eureka, and a centralized API Gateway.

### Service Components
- **Eureka Server** (Port: 8761) - Service registry and discovery
- **API Gateway** (Port: 8888) - Single entry point for client requests
- **Auth Service** (Port: 8081) - Authentication and authorization
- **Supply Service** (Port: 8082) - Procurement management
- **Production Service** (Port: 8083) - Manufacturing operations
- **Customer Service** (Port: 8084) - Customer orders and delivery

## 🎯 Microservices

### 1. **Eureka Server** (Port: 8761)
Service registry that enables service discovery and registration for all microservices.

**Responsibilities:**
- Service registration and health monitoring
- Dynamic service discovery
- Load balancing support

### 2. **API Gateway** (Port: 8888)
Single entry point for all client requests with intelligent routing.

**Responsibilities:**
- Request routing to appropriate microservices
- Load balancing
- Cross-cutting concerns (logging, monitoring)
- Rate limiting and throttling

### 3. **Auth Service** (Port: 8081)
Centralized authentication and authorization service.

**Responsibilities:**
- User account management
- Role-based access control (RBAC)
- Authentication via email/password in HTTP headers
- User role assignment and modification

**Supported Roles:**
- `ADMIN` - Full system access
- **Procurement Module:**
    - `GESTIONNAIRE_APPROVISIONNEMENT`
    - `RESPONSABLE_ACHATS`
    - `SUPERVISEUR_LOGISTIQUE`
- **Production Module:**
    - `CHEF_PRODUCTION`
    - `PLANIFICATEUR`
    - `SUPERVISEUR_PRODUCTION`
- **Delivery Module:**
    - `GESTIONNAIRE_COMMERCIAL`
    - `RESPONSABLE_LOGISTIQUE`
    - `SUPERVISEUR_LIVRAISONS`

### 4. **Supply Service** (Port: 8082)
Manages procurement operations including suppliers, raw materials, and supply orders.

**Core Entities:**
- **Supplier**: Vendor management with ratings and lead times
- **RawMaterial**: Inventory tracking with critical stock alerts
- **SupplyOrder**: Purchase order management with status tracking

**Key Features:**
- Supplier CRUD operations with active order validation
- Raw material inventory management
- Critical stock level monitoring
- Supply order lifecycle management (EN_ATTENTE, EN_COURS, RECUE)
- Supplier search by name or code
- Pagination support for all listing endpoints

### 5. **Production Service** (Port: 8083)
Handles manufacturing operations, product management, and production planning.

**Core Entities:**
- **Product**: Finished goods with production time and cost tracking
- **BillOfMaterial (BOM)**: Material requirements for each product
- **ProductionOrder**: Manufacturing order management

**Key Features:**
- Product lifecycle management
- BOM definition and material consumption tracking
- Production order scheduling and prioritization
- Material availability verification before production
- Production time estimation
- Order status tracking (EN_ATTENTE, EN_PRODUCTION, TERMINE, BLOQUE)
- Priority order handling

### 6. **Customer Service** (Port: 8084)
Manages customer relationships, orders, and delivery logistics.

**Core Entities:**
- **Customer**: Client information and order history
- **Order**: Customer purchase orders
- **Delivery**: Shipment tracking and logistics

**Key Features:**
- Customer account management
- Customer order processing
- Delivery scheduling and tracking
- Vehicle and driver assignment
- Delivery cost calculation
- Order status management (EN_PREPARATION, EN_ROUTE, LIVREE)
- Customer search capabilities

## 💻 Technology Stack

### Backend Framework
- **Spring Boot 3.x** - Application framework
- **Spring Cloud** - Microservices infrastructure
    - Spring Cloud Netflix Eureka - Service discovery
    - Spring Cloud Gateway - API gateway
    - Spring Cloud OpenFeign - Synchronous service communication
- **Spring Data JPA** - Data persistence
- **Spring Security** - Authentication and authorization
- **Hibernate** - ORM framework

### Database
- **MySQL / PostgreSQL** - Relational database (each service has its own database)

### API & Documentation
- **REST API** - RESTful web services
- **Swagger** - API documentation and testing interface
- **MapStruct** - DTO mapping

### Security
- **Spring Security** - Authentication and authorization
- **Bean Validation** - Input validation

### DevOps & CI/CD
- **Jenkins** - Continuous integration and deployment
- **SonarQube** - Code quality and security analysis
- **JaCoCo** - Code coverage reporting
- **Docker** - Containerization and deployment

### Testing
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework for unit tests
- **AssertJ** - Fluent assertion library
- **Spring Boot Test** - Integration testing support
- **MockMvc** - REST API testing
- **@WebMvcTest** - Controller layer testing
- **@DataJpaTest** - Repository layer testing
- **@SpringBootTest** - Full integration testing

## 📋 Prerequisites

- **Java 17+**
- **Maven 3.8+**
- **PostgreSQL 15+**
- **Docker**

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/your-org/ChainFlow-pro.git
cd ChainFlow-pro
```

### 2. Configure Application Properties
Update `application.yml` in each service with your database credentials.

### 3. Start docker compose


### 4. Verify Service Registration
Visit Eureka Dashboard: http://localhost:8761
All services should be registered and showing as UP.

### 5. Access API Gateway
All API requests should go through: http://localhost:8888

## 📚 API Documentation

Once services are running, access Swagger UI documentation:

- **Auth Service**: http://localhost:8081/swagger-ui.html
- **Supply Service**: http://localhost:8082/swagger-ui.html
- **Production Service**: http://localhost:8083/swagger-ui.html
- **Customer Service**: http://localhost:8084/swagger-ui.html

**Via API Gateway**: http://localhost:8888/{service-name}/swagger-ui.html

Swagger provides interactive API documentation where you can:
- Explore all available endpoints
- View request/response schemas
- Test APIs directly from the browser
- Understand authentication requirements

## 🔒 Security

### Authentication & Authorization
The system uses **Spring Security** for comprehensive security implementation:

- **JWT-based authentication**
- **Role-based access control (RBAC)** across all services
- Secure password encryption
- Protected REST endpoints with method-level security

### Security Headers
API requests include authentication credentials:

```http
GET /api/suppliers
Headers:
  Authorization: Bearer <token>
```

### Authorization Rules
Each endpoint is protected based on user roles. Access is validated by the Auth Service and enforced across all microservices using Spring Security filters and interceptors.

## 📋 Business Rules

### Procurement Module
- A raw material can have multiple suppliers
- A supply order is associated with a single supplier
- Suppliers cannot be deleted if they have active orders
- Critical stock levels trigger alerts (optional: email notifications via SMTP scheduler)

### Production Module
- Each production order consumes materials according to the BOM
- Products can only be manufactured if all required materials are available
- Products cannot be deleted if they have associated production orders
- Priority orders are processed before standard orders

### Delivery Module
- Customers can have multiple orders
- Each customer order is linked to a single delivery
- Deliveries are only possible if the product is available in stock
- Customers cannot be deleted if they have active orders

## 🔄 Service Communication

### Synchronous Communication with Feign Client

Services communicate synchronously using **Spring Cloud OpenFeign**:

**Example: Production Service calling Supply Service**
```java
@FeignClient(name = "supply-service")
public interface SupplyServiceClient {
    @GetMapping("/api/materials/{id}")
    MaterialAvailabilityDTO checkAvailability(@PathVariable Long id);
}
```

**Common Integration Patterns:**
- **Production → Supply**: Check material availability before creating production orders
- **Customer → Production**: Verify product stock before accepting customer orders
- **All Services → Auth**: Validate user permissions and roles

### Service Discovery
Services register with Eureka on startup and discover other services dynamically. Feign clients use service names (not hard-coded URLs) for inter-service communication.

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Code Coverage with JaCoCo
Generate code coverage reports:
```bash
mvn clean test jacoco:report
```
View coverage report at: `target/site/jacoco/index.html`

### Code Quality Analysis
```bash
# Run SonarQube analysis
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=your-token
```

## 🚀 CI/CD Pipeline

### Jenkins Pipeline
The project includes a comprehensive CI/CD pipeline:

**Pipeline Stages:**
1. **Checkout** - Pull latest code from repository
2. **Build** - Compile all microservices
3. **Unit Tests** - Execute JUnit tests
4. **Code Quality** - SonarQube analysis
5. **Docker Build** - Build Docker images for each service
6. **Docker Push** - Push images to Docker registry

### Quality Gates
- **Minimum Code Coverage**: 80%
- **SonarQube Quality Gate**: Must pass
- **Zero Critical/Blocker Issues**
- **All Tests Pass**


### Run with Docker Compose
```bash
docker-compose up -d
```

### Available Docker Images
- `aliyara29/eureka-server:latest`
- `aliyara29/api-gateway:latest`
- `aliyara29/auth-service:latest`
- `aliyara29/supply-service:latest`
- `aliyara29/production-service:latest`
- `aliyara29/customer-service:latest`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.

## 📧 Contact

For questions or support, please contact: support@ChainFlow-pro.com

---

**ChainFlow** - Powering Modern Supply Chain Management 🚀
