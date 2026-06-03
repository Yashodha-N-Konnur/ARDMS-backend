# ARDMS — Automated Release & Deployment Management System

> Enterprise-grade Spring Boot application for managing software releases, tracking deployments across environments, and automating rollback procedures with full CI/CD integration.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2, Java 17 |
| Security | Spring Security, JWT (jjwt) |
| Database | MySQL 8.0, Flyway migrations |
| ORM | Spring Data JPA / Hibernate |
| Mapping | MapStruct |
| Logging | Log4j2 (structured, rolling) |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven 3.9 |
| Containerization | Docker, Docker Compose |
| Infrastructure | Terraform (AWS VPC, EC2, RDS) |
| Configuration Mgmt | Ansible |
| CI/CD | Jenkins Pipeline |

---

## Project Structure

```
ardms/
├── backend/                        # Spring Boot application
│   ├── src/main/java/com/ardms/
│   │   ├── config/                 # Security, OpenAPI, Audit configs
│   │   ├── controller/             # REST controllers
│   │   ├── dto/
│   │   │   ├── request/            # Request DTOs with validation
│   │   │   └── response/           # Response DTOs
│   │   ├── entity/                 # JPA entities + enums
│   │   ├── exception/              # Custom exceptions + GlobalExceptionHandler
│   │   ├── mapper/                 # MapStruct mappers
│   │   ├── repository/             # Spring Data JPA repositories
│   │   ├── security/
│   │   │   ├── jwt/                # JWT provider + filter
│   │   │   └── service/            # UserDetailsService
│   │   ├── service/                # Service interfaces
│   │   │   └── impl/               # Service implementations
│   │   └── util/                   # Utility classes
│   ├── src/main/resources/
│   │   ├── db/migration/           # Flyway SQL migrations
│   │   ├── application.yml         # Main config
│   │   ├── application-dev.yml     # Dev profile
│   │   ├── application-prod.yml    # Prod profile
│   │   ├── application-docker.yml  # Docker profile
│   │   └── log4j2.xml              # Log4j2 configuration
│   ├── Dockerfile
│   └── pom.xml
├── docker/
│   ├── docker-compose.yml          # Multi-container setup
│   └── .env.example                # Environment variables template
├── terraform/                      # AWS infrastructure
│   ├── main.tf / variables.tf / outputs.tf
│   └── modules/
│       ├── vpc/                    # VPC, subnets, IGW
│       ├── security_groups/        # App and DB security groups
│       ├── rds/                    # MySQL RDS instance
│       └── ec2/                    # Application server
├── ansible/                        # Configuration management
│   ├── site.yml                    # Full setup playbook
│   ├── deploy.yml                  # Deploy-only playbook
│   ├── rollback.yml                # Rollback playbook
│   ├── inventory/                  # Host inventory
│   ├── group_vars/                 # Group variables
│   └── roles/
│       ├── common/                 # Base OS setup
│       ├── java/                   # Java 17 installation
│       ├── docker/                 # Docker + Compose
│       └── app/                    # Application deployment
├── jenkins/
│   └── Jenkinsfile                 # Full CI/CD pipeline
├── scripts/
│   ├── setup-local.sh              # Local dev setup
│   ├── deploy-docker.sh            # Docker deployment
│   ├── rollback.sh                 # Docker rollback
│   └── db-migrate.sh               # Run Flyway migrations
├── postman/
│   └── ARDMS_API_Collection.json   # Postman collection
└── docs/
    └── API_DOCUMENTATION.md        # API reference
```

---

## Quick Start (Local Development)

### Prerequisites
- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- MySQL 8.0 (or use the Docker setup below)

### Option 1 — Docker (Recommended)
```bash
# Clone and navigate
git clone https://github.com/your-org/ardms.git && cd ardms

# Start everything (MySQL + Backend)
./scripts/deploy-docker.sh

# Access
open http://localhost:8080/api/swagger-ui.html
```

### Option 2 — Manual Local Setup
```bash
# 1. Start MySQL only
cd docker && cp .env.example .env
docker-compose up -d mysql

# 2. Build and run backend
cd ../backend
mvn clean package -DskipTests
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# 3. Open Swagger
open http://localhost:8080/api/swagger-ui.html
```

---

## API Authentication

**Step 1 — Get Token:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail":"admin","password":"Admin@1234"}'
```

**Step 2 — Use Token:**
```bash
TOKEN="<paste access_token here>"
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/releases
```

---

## Default Credentials

| Username | Password | Role |
|----------|----------|------|
| `admin` | `Admin@1234` | ADMIN |
| `release_mgr` | `Admin@1234` | RELEASE_MANAGER |
| `dev_user` | `Admin@1234` | DEVELOPER |

---

## Infrastructure Deployment

### Terraform (AWS)
```bash
cd terraform
cp terraform.tfvars.example terraform.tfvars
# Edit terraform.tfvars with your values

terraform init
terraform plan -out=plan.tfplan
terraform apply plan.tfplan
```

### Ansible (Server Setup)
```bash
cd ansible
# Edit inventory/hosts.ini with your server IPs

# Full setup (first time)
ansible-playbook site.yml -i inventory/hosts.ini --ask-vault-pass

# Deploy update
ansible-playbook deploy.yml -i inventory/hosts.ini \
  -e "app_image_tag=1.2.0" --ask-vault-pass

# Rollback
ansible-playbook rollback.yml -i inventory/hosts.ini \
  -e "rollback_version=1.1.0" --ask-vault-pass
```

### Jenkins CI/CD
1. Create a new Jenkins Pipeline job
2. Point it to the `Jenkinsfile` in this repo
3. Configure credentials:
   - `docker-registry-credentials` — Docker Hub / registry login
   - `ardms-db-credentials` — DB username/password
   - `ardms-jwt-secret` — JWT secret
   - `ansible-vault-password` — Ansible vault password file
4. Trigger a build with your target environment

---

## Environment Variables Reference

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile |
| `DB_URL` | localhost MySQL | JDBC connection URL |
| `DB_USERNAME` | `ardms_user` | Database user |
| `DB_PASSWORD` | `ardms_pass` | Database password |
| `JWT_SECRET` | (required) | 256-bit+ secret key |
| `JWT_EXPIRATION` | `86400000` | Access token TTL (ms) |
| `JWT_REFRESH_EXPIRATION` | `604800000` | Refresh token TTL (ms) |
| `SERVER_PORT` | `8080` | HTTP port |
| `LOG_PATH` | `./logs` | Log file directory |
| `CORS_ORIGINS` | localhost | Allowed CORS origins |

---

## API Endpoints Summary

| Resource | Base Path | Key Operations |
|----------|-----------|----------------|
| Auth | `/api/auth` | login, register, refresh, logout |
| Releases | `/api/releases` | CRUD, status management, search |
| Deployments | `/api/deployments` | Create, track, status updates |
| Rollbacks | `/api/rollbacks` | Initiate, track, complete |
| Environments | `/api/environments` | CRUD, toggle active status |
| Users | `/api/users` | List, search, toggle status |
| Dashboard | `/api/dashboard` | Aggregated statistics |
| Health | `/api/actuator/health` | Health check (public) |

---

## Running Tests
```bash
cd backend
mvn test
mvn verify   # also runs integration tests
```

---

## Logs
| Log File | Content |
|----------|---------|
| `logs/ardms-app.log` | All application events |
| `logs/ardms-error.log` | Errors and exceptions only |
| `logs/ardms-deployment.log` | Deployment lifecycle events |
| `logs/ardms-security.log` | Auth and security events |

---

## License
MIT License — see [LICENSE](LICENSE)
