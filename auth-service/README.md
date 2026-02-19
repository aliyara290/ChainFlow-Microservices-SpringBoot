# Auth Service - Basic Authentication Implementation

## Overview

This is a Spring Boot authentication service that implements session-based basic authentication. The service manages users, roles, and authorities with a secure login/logout mechanism.

## Architecture

### Technology Stack
- **Spring Boot 3.5.7**
- **Spring Security** - For authentication and authorization
- **Spring Data JPA** - For database operations
- **PostgreSQL** - Database
- **Eureka Client** - Service discovery
- **Lombok** - Reduce boilerplate code
- **MapStruct** - Object mapping

### Database Schema

The service uses a hierarchical permission model:

```
User → Role → Authority
```

- **Users**: Application users with credentials
- **Roles**: Groups of permissions (e.g., ADMIN, USER)
- **Authorities**: Specific permissions (e.g., READ_USER, WRITE_USER)

## Authentication Flow

### 1. User Registration
Users are created through the User API with roles assigned:

```http
POST /api/v1/users
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "roles": [
    {
      "name": "ADMIN"
    }
  ]
}
```

### 2. Login Process

**Endpoint:** `POST /api/v1/auth/login`

**Request:**
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**Response:**
```json
{
  "status": true,
  "message": "Login successful",
  "data": {
    "id": "uuid-here",
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "roles": [
      {
        "id": "role-uuid",
        "name": "ADMIN",
        "authorities": [...]
      }
    ]
  }
}
```

**How it works:**
1. Client sends username and password
2. `AuthenticationManager` validates credentials against database
3. Password is verified using BCrypt
4. If valid, user details are loaded with roles and authorities
5. Spring Security creates a session (JSESSIONID cookie)
6. Session cookie is returned to client for subsequent requests

### 3. Logout Process

**Endpoint:** `POST /api/v1/auth/logout`

**Response:**
```json
{
  "status": true,
  "message": "Logout successful",
  "data": null
}
```

**How it works:**
1. Clears the security context
2. Invalidates the session
3. Client should discard the session cookie

## Security Implementation

### Password Encryption
- Passwords are hashed using **BCrypt** before storing in database
- BCrypt includes salt automatically for added security
- Configured in `SecurityConfig.passwordEncoder()`

### Session Management
- **Session-based authentication** (not stateless)
- Maximum 1 concurrent session per user
- Session cookie name: `JSESSIONID`
- Sessions are stored server-side

### Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()  // Public endpoints
                .anyRequest().authenticated()                     // Protected endpoints
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
            );
        return http.build();
    }
}
```

### CustomUserDetailsService
Loads user from database with all roles and authorities:

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
}
```

### Authority Mapping
- Roles are prefixed with `ROLE_` (e.g., `ROLE_ADMIN`)
- Authorities are added as-is (e.g., `READ_USER`, `WRITE_USER`)
- Both are loaded into Spring Security's `GrantedAuthority` collection

## API Endpoints

### Public Endpoints (No Authentication Required)
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/logout` - User logout

### Protected Endpoints (Authentication Required)

#### User Management
- `POST /api/v1/users` - Create user
- `GET /api/v1/users` - Get all users
- `GET /api/v1/users/{id}` - Get user by ID
- `PUT /api/v1/users/{id}` - Update user
- `DELETE /api/v1/users/{id}` - Delete user

#### Role Management
- `POST /api/v1/roles` - Create role
- `GET /api/v1/roles` - Get all roles
- `GET /api/v1/roles/{id}` - Get role by ID
- `PUT /api/v1/roles/{id}` - Update role
- `DELETE /api/v1/roles/{id}` - Delete role

#### Authority Management
- `POST /api/v1/authorities` - Create authority
- `GET /api/v1/authorities` - Get all authorities
- `GET /api/v1/authorities/{id}` - Get authority by ID
- `PUT /api/v1/authorities/{id}` - Update authority
- `DELETE /api/v1/authorities/{id}` - Delete authority

## Testing with cURL

### 1. Create an Authority
```bash
curl -X POST http://localhost:8084/api/v1/authorities \
  -H "Content-Type: application/json" \
  -d '{
    "name": "READ_USER"
  }'
```

### 2. Create a Role with Authorities
```bash
curl -X POST http://localhost:8084/api/v1/roles \
  -H "Content-Type: application/json" \
  -d '{
    "name": "ADMIN",
    "authorities": [
      {"name": "READ_USER"}
    ]
  }'
```

### 3. Create a User
```bash
curl -X POST http://localhost:8084/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123",
    "roles": [
      {"name": "ADMIN"}
    ]
  }'
```

### 4. Login
```bash
curl -X POST http://localhost:8084/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

### 5. Access Protected Endpoint
```bash
curl -X GET http://localhost:8084/api/v1/users \
  -b cookies.txt
```

### 6. Logout
```bash
curl -X POST http://localhost:8084/api/v1/auth/logout \
  -b cookies.txt
```

## Testing with Postman

1. **Login:**
    - Send POST request to `http://localhost:8084/api/v1/auth/login`
    - Postman will automatically save the session cookie

2. **Access Protected Endpoints:**
    - Session cookie will be sent automatically with subsequent requests
    - No need to manually add headers

3. **Logout:**
    - Send POST request to `http://localhost:8084/api/v1/auth/logout`

## Configuration

### application.yml
```yaml
server:
  port: 8084

spring:
  application:
    name: auth-service
  
  datasource:
    url: jdbc:postgresql://localhost:5435/auth_db
    username: aliyara29
    password: Yara2001
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
```

## Error Handling

### Common Errors

**Invalid Credentials (401 Unauthorized):**
```json
{
  "status": false,
  "message": "Invalid username or password"
}
```

**User Already Exists (409 Conflict):**
```json
{
  "status": false,
  "message": "User with username 'johndoe' already exists"
}
```

**Resource Not Found (404 Not Found):**
```json
{
  "status": false,
  "message": "User with ID: xyz not found!"
}
```

## Key Classes

### Security Layer
- `SecurityConfig` - Spring Security configuration
- `CustomUserDetails` - Implements UserDetails interface
- `CustomUserDetailsService` - Loads user from database

### Service Layer
- `AuthService` - Authentication business logic
- `UserService` - User management
- `RoleService` - Role management
- `AuthorityService` - Authority management

### Controller Layer
- `AuthController` - Login/Logout endpoints
- `UserController` - User CRUD operations
- `RoleController` - Role CRUD operations
- `AuthorityController` - Authority CRUD operations

## Running the Service

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL
- Eureka Server (optional)

### Steps
1. Start PostgreSQL database
2. Update database credentials in `application.yml`
3. Run the application:
```bash
./mvnw spring-boot:run
```

4. Service will start on port 8084

## Docker Support

Build and run with Docker:

```bash
# Build
docker build -t auth-service .

# Run
docker run -p 8084:8084 auth-service
```

## Future Enhancements

Potential improvements for production use:

1. **JWT Token Authentication** - For stateless microservices
2. **Refresh Tokens** - For long-lived sessions
3. **OAuth2 Integration** - Social login support
4. **Rate Limiting** - Prevent brute force attacks
5. **Account Lockout** - After failed login attempts
6. **Email Verification** - Verify user email on registration
7. **Password Reset** - Forgot password functionality
8. **Audit Logging** - Track authentication events
9. **HTTPS Enforcement** - Secure communication
10. **CORS Configuration** - For frontend integration

## License

This project is licensed under the MIT License.

## Contact

For questions or support, please contact the development team.