# Cloud-Native Inventory Management System

Microservice-based inventory tracking platform built with Java, Spring Boot, Docker, AWS EC2, and Git.

## Architecture

- `inventory-service` (port `8081`): manages stock levels and reservation/restock workflows.
- `order-service` (port `8082`): creates orders and calls inventory service to reserve stock.
- Docker Compose: runs both services as independent containers.

## Features

- Real-time inventory reservation flow via REST APIs.
- Persistent storage with PostgreSQL for both microservices.
- JWT-based authentication with role-based access control (`USER`, `ADMIN`).
- Microservice communication using HTTP between services.
- Containerized runtime for cloud-friendly deployment.
- CI/CD pipeline with GitHub Actions.

## Run Locally (without Docker)

```bash
mvn -pl inventory-service spring-boot:run
mvn -pl order-service spring-boot:run
```

## Run with Docker

```bash
docker compose up --build -d
```

## Default Credentials (for login)

- `appuser / appuser123` (role: `USER`)
- `admin / admin123` (role: `ADMIN`)

## API Examples

### Get JWT token

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"appuser","password":"appuser123"}'
```

Use the returned token as:

```bash
export TOKEN=<jwt_token_here>
```

### Check Inventory (requires USER or ADMIN)

```bash
curl http://localhost:8081/api/inventory/SKU-100 \
  -H "Authorization: Bearer $TOKEN"
```

### Reserve Stock (requires USER or ADMIN)

```bash
curl -X POST http://localhost:8081/api/inventory/reserve \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sku":"SKU-100","quantity":2}'
```

### Restock (requires ADMIN)

```bash
curl -X POST http://localhost:8081/api/inventory/restock \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sku":"SKU-100","quantity":20}'
```

### Create Order (requires USER or ADMIN)

```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"orderId":"ORD-001","sku":"SKU-100","quantity":2}'
```

## Deploy on AWS EC2

1. Launch an EC2 instance (Ubuntu 22.04 recommended).
2. Open inbound security group ports: `22`, `8081`, `8082`.
3. Install Docker and Compose plugin:
   ```bash
   sudo apt update
   sudo apt install -y docker.io docker-compose-plugin git
   sudo usermod -aG docker $USER
   ```
4. Clone repository and start services:
   ```bash
   git clone <your-repo-url>
   cd inventory-management-system
   docker compose up --build -d
   ```
5. Access APIs from:
   - `http://<ec2-public-ip>:8081`
   - `http://<ec2-public-ip>:8082`

## CI/CD

- GitHub Actions workflow: `.github/workflows/ci-cd.yml`
- On push/PR to `main`, it:
  - builds and verifies all modules with Maven
  - performs a Docker build smoke test

## EC2 Automated Deploy Script

- Script path: `deploy/ec2-deploy.sh`
- Usage:
  ```bash
  chmod +x deploy/ec2-deploy.sh
  REPO_URL=<your-repo-url> ./deploy/ec2-deploy.sh
  ```

## Agile + Git Workflow Suggestion

- Use weekly sprint goals for API features, observability, and scaling tasks.
- Track work in feature branches and open PRs for peer review.
- Tag releases by sprint (`sprint-1`, `sprint-2`) for deployment traceability.
