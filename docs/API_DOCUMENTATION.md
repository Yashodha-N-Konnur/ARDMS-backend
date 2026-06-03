# ARDMS REST API Documentation

## Base URL
- **Local**: `http://localhost:8080/api`
- **Docker**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **OpenAPI Spec**: `http://localhost:8080/api/v3/api-docs`

## Authentication
All endpoints (except `/auth/**`) require a JWT Bearer token in the `Authorization` header:
```
Authorization: Bearer <access_token>
```

---

## Auth Endpoints

### POST /auth/login
Authenticate and receive JWT tokens.
```json
Request:  { "usernameOrEmail": "admin", "password": "Admin@1234" }
Response: { "success": true, "data": { "accessToken": "...", "refreshToken": "...", "roles": [...] } }
```

### POST /auth/register
Register a new user.
```json
Request:  { "username": "jdoe", "email": "jdoe@co.com", "password": "Pass@123", "firstName": "John", "lastName": "Doe" }
```

### POST /auth/refresh-token
```json
Request:  { "refreshToken": "uuid-token" }
```

### POST /auth/logout  _(requires auth)_
Revokes all refresh tokens for the current user.

---

## Release Endpoints

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /releases | ADMIN, RELEASE_MANAGER, DEVELOPER | Create release |
| GET | /releases | All authenticated | List all (paginated) |
| GET | /releases/{id} | All authenticated | Get by ID |
| GET | /releases/version/{ver} | All authenticated | Get by version |
| GET | /releases/status/{status} | All authenticated | Filter by status |
| GET | /releases/search?query= | All authenticated | Full-text search |
| PUT | /releases/{id} | ADMIN, RELEASE_MANAGER | Update release |
| PATCH | /releases/{id}/status | ADMIN, RELEASE_MANAGER | Change status |
| DELETE | /releases/{id} | ADMIN, RELEASE_MANAGER | Delete (DRAFT only) |

**Release Statuses**: `DRAFT, PLANNED, IN_PROGRESS, DEPLOYED, FAILED, ROLLED_BACK, CANCELLED`  
**Release Types**: `MAJOR, MINOR, PATCH, HOTFIX, EMERGENCY`

---

## Deployment Endpoints

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /deployments | ADMIN, RELEASE_MANAGER, DEVELOPER | Initiate deployment |
| GET | /deployments | All authenticated | List all |
| GET | /deployments/{id} | All authenticated | Get by ID |
| GET | /deployments/release/{id} | All authenticated | By release |
| GET | /deployments/environment/{id} | All authenticated | By environment |
| GET | /deployments/status/{status} | All authenticated | By status |
| PATCH | /deployments/{id}/status | ADMIN, RELEASE_MANAGER | Update status |

**Deployment Statuses**: `PENDING, IN_PROGRESS, SUCCESS, FAILED, ROLLED_BACK, CANCELLED`

---

## Rollback Endpoints

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /rollbacks | ADMIN, RELEASE_MANAGER | Initiate rollback |
| GET | /rollbacks | All authenticated | List all |
| GET | /rollbacks/{id} | All authenticated | Get by ID |
| GET | /rollbacks/deployment/{id} | All authenticated | By deployment |
| PATCH | /rollbacks/{id}/complete | ADMIN, RELEASE_MANAGER | Mark as complete |

---

## Environment Endpoints

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | /environments | ADMIN, RELEASE_MANAGER | Create environment |
| GET | /environments | All authenticated | List all |
| GET | /environments/active | All authenticated | Active only |
| GET | /environments/{id} | All authenticated | Get by ID |
| PATCH | /environments/{id}/toggle-status | ADMIN | Enable/Disable |

**Environment Types**: `DEVELOPMENT, QA, STAGING, PRODUCTION, DR`

---

## Dashboard

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /dashboard/stats | Aggregated statistics |

---

## Standard Response Format

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2025-01-01T10:00:00"
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Release not found with id: '99'",
  "error": { "code": "RESOURCE_NOT_FOUND" },
  "timestamp": "2025-01-01T10:00:00"
}
```

**Paginated Response:**
```json
{
  "success": true,
  "data": {
    "content": [...],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 42,
    "totalPages": 5,
    "isFirst": true,
    "isLast": false,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

---

## HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request / Validation Error |
| 401 | Unauthorized (invalid/missing token) |
| 403 | Forbidden (insufficient role) |
| 404 | Resource not found |
| 409 | Conflict (duplicate resource) |
| 500 | Internal Server Error |

---

## Default Users (Seeded)

| Username | Password | Role |
|----------|----------|------|
| admin | Admin@1234 | ADMIN |
| release_mgr | Admin@1234 | RELEASE_MANAGER |
| dev_user | Admin@1234 | DEVELOPER |
