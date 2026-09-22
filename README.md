# Cloud-Native Real-Time Chat Platform

A scalable real-time messaging platform built to explore **backend engineering, distributed systems, cloud architecture, and reliable real-time communication**.

The project provides private and group messaging, real-time communication through WebSockets, authentication, message persistence, online presence, file sharing, monitoring, automated deployment, and cloud infrastructure.

> **Project goal:** Design and deploy a production-oriented messaging system while exploring the engineering challenges behind real-time and distributed applications.

---

## Features

### Authentication & Users

* User registration and login
* Secure password hashing
* JWT-based authentication
* User profiles
* Authorization and protected resources
* Online/offline presence

### Messaging

* Private conversations
* Group conversations
* Real-time messaging
* Message history
* Message timestamps
* Delivered/read status
* Typing indicators
* Message pagination
* File and image sharing

### Real-Time Communication

* WebSocket-based communication
* Persistent connections
* Real-time presence updates
* Real-time typing indicators
* Real-time message delivery
* Support for multiple backend instances

### Cloud & Infrastructure

* Containerized services with Docker
* PostgreSQL database
* Redis for caching and real-time coordination
* AWS cloud deployment
* Load balancing
* Object storage with Amazon S3
* Infrastructure as Code with Terraform
* Automated CI/CD

### Observability

* Application logging
* Metrics collection
* Infrastructure monitoring
* Prometheus
* Grafana
* Health checks
* Error tracking

### Security

* Password hashing
* JWT authentication
* Input validation
* Authorization
* HTTPS
* CORS configuration
* Rate limiting
* Secure secrets management
* Private database infrastructure
* AWS IAM permissions

---

# Architecture

The application is designed as a cloud-native distributed system.

```text
                           ┌───────────────┐
                           │     Users     │
                           │ Web / Mobile  │
                           └───────┬───────┘
                                   │
                              HTTPS / WSS
                                   │
                                   ▼
                         ┌──────────────────┐
                         │  Load Balancer   │
                         └────────┬─────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │                           │
                    ▼                           ▼
             ┌──────────────┐           ┌──────────────┐
             │ Spring Boot  │           │ Spring Boot  │
             │   Instance   │           │   Instance   │
             │      #1      │           │      #2      │
             └──────┬───────┘           └──────┬───────┘
                    │                          │
                    └────────────┬─────────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
                    ▼                         ▼
             ┌──────────────┐         ┌──────────────┐
             │    Redis     │         │  PostgreSQL  │
             │              │         │              │
             │ Cache        │         │ Users        │
             │ Pub/Sub      │         │ Messages     │
             │ Presence     │         │ Conversations│
             └──────────────┘         └──────────────┘
                                             │
                                             ▼
                                      ┌──────────────┐
                                      │   Amazon S3  │
                                      │ Files/Images │
                                      └──────────────┘
```

---

# Technology Stack

## Frontend

* React
* TypeScript
* Vite
* WebSocket client
* REST API
* HTML/CSS

## Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* WebSocket / STOMP
* Bean Validation

## Data

* PostgreSQL
* Redis

## Infrastructure

* Docker
* Docker Compose
* AWS
* Amazon ECS/Fargate
* Amazon RDS
* Amazon ElastiCache
* Amazon S3
* Application Load Balancer
* Amazon CloudWatch

## DevOps

* GitHub Actions
* Terraform
* Docker
* Automated testing
* Security scanning

## Monitoring

* Prometheus
* Grafana
* Spring Boot Actuator

---

# Project Structure

```text
cloud-chat/
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── hooks/
│   │   ├── services/
│   │   ├── context/
│   │   ├── types/
│   │   └── utils/
│   └── package.json
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/
│   │   │       └── com/
│   │   │           └── cloudchat/
│   │   │               ├── controller/
│   │   │               ├── service/
│   │   │               ├── repository/
│   │   │               ├── model/
│   │   │               ├── dto/
│   │   │               ├── security/
│   │   │               └── websocket/
│   │   └── test/
│   └── pom.xml
│
├── infrastructure/
│   └── terraform/
│       ├── main.tf
│       ├── variables.tf
│       ├── outputs.tf
│       ├── vpc.tf
│       ├── ecs.tf
│       ├── rds.tf
│       ├── redis.tf
│       └── s3.tf
│
├── docs/
│   ├── architecture.md
│   ├── database-design.md
│   ├── websocket-design.md
│   ├── scalability.md
│   ├── security.md
│   ├── reliability.md
│   └── architecture-decisions.md
│
├── .github/
│   └── workflows/
│       ├── backend.yml
│       └── frontend.yml
│
├── docker-compose.yml
├── README.md
└── .gitignore
```

---

# Database Design

The initial database model contains the following entities:

```text
User
 │
 ├──────────────┐
 │              │
 ▼              ▼
ConversationMember
 │
 ▼
Conversation
 │
 ▼
Message
```

### Users

```text
users
----------------
id
username
email
password_hash
created_at
updated_at
```

### Conversations

```text
conversations
----------------
id
type
created_at
updated_at
```

### Conversation Members

```text
conversation_members
----------------
conversation_id
user_id
joined_at
```

### Messages

```text
messages
----------------
id
conversation_id
sender_id
content
status
created_at
updated_at
```

The schema is designed to support both private and group conversations.

---

# Real-Time Messaging

Messages are delivered using WebSockets.

Instead of repeatedly polling the API:

```text
Client
  │
  ├── GET messages
  ├── GET messages
  ├── GET messages
  └── GET messages
```

the client maintains a persistent connection:

```text
Client
   │
   │ WebSocket
   ▼
Spring Boot
   │
   │ event
   ▼
Recipient
```

This allows messages, typing indicators, presence updates, and delivery events to be propagated in real time.

---

# Message Flow

When a user sends a message:

```text
1. User writes message
        ↓
2. Frontend sends WebSocket event
        ↓
3. Spring Boot validates request
        ↓
4. Authorization is checked
        ↓
5. Message is persisted in PostgreSQL
        ↓
6. Event is published
        ↓
7. Recipient receives the message
        ↓
8. Delivery status is updated
```

---

# Why Redis?

Redis is used for data and state that needs fast access or coordination between backend instances.

Examples include:

* User presence
* Typing indicators
* Caching
* Pub/Sub
* Distributed WebSocket event propagation

This becomes important when multiple backend instances are running.

```text
                    Load Balancer
                   /             \
                  /               \
                 ▼                 ▼
          Spring Boot #1     Spring Boot #2
                 │                 │
                 └───────┬─────────┘
                         ▼
                       Redis
```

Without shared coordination, an event received by one instance may not reach a user connected to another instance.

---

# AWS Architecture

The production deployment uses AWS services.

```text
                         Internet
                            │
                            ▼
                    Application Load
                       Balancer
                            │
                ┌───────────┴───────────┐
                ▼                       ▼
          ECS/Fargate #1          ECS/Fargate #2
                │                       │
                └───────────┬───────────┘
                            │
              ┌─────────────┼─────────────┐
              ▼             ▼             ▼
            RDS        ElastiCache       S3
         PostgreSQL       Redis         Files
```

### AWS Services

| Service     | Purpose                |
| ----------- | ---------------------- |
| ECS/Fargate | Run backend containers |
| RDS         | Managed PostgreSQL     |
| ElastiCache | Managed Redis          |
| S3          | File and image storage |
| ALB         | Load balancing         |
| CloudWatch  | Logs and monitoring    |
| IAM         | Access control         |
| VPC         | Network isolation      |

---

# Scalability

The system is designed to support horizontal scaling.

Instead of increasing the resources of one server:

```text
Server
  ↓
More CPU/RAM
```

the architecture can add more instances:

```text
              Load Balancer
             /      |      \
            ▼       ▼       ▼
          API 1   API 2   API 3
```

This allows the application to handle increased traffic while keeping the backend stateless where possible.

---

# Load Testing

The system will be tested under increasing traffic levels.

Example scenarios:

```text
100 concurrent users
        ↓
1,000 concurrent users
        ↓
10,000 concurrent users
```

The following metrics will be measured:

* Request latency
* Throughput
* Error rate
* CPU utilization
* Memory utilization
* Database connections
* Active WebSocket connections

The goal is not to claim a specific capacity without measurement, but to identify system bottlenecks and document the results.

---

# Security

Security is considered throughout the application lifecycle.

Implemented or planned controls include:

* Secure password hashing
* JWT authentication
* Role-based authorization
* Input validation
* Protection against unauthorized resource access
* Rate limiting
* CORS configuration
* HTTPS
* Secure environment variables
* AWS IAM least-privilege policies
* Private database access
* Encrypted storage
* Dependency vulnerability scanning

Secrets are never committed to the repository.

---

# Testing

The project uses multiple levels of testing.

### Backend

* Unit tests
* Service tests
* Repository tests
* Integration tests
* API tests

### Frontend

* Component tests
* Integration tests

### Infrastructure

* Docker health checks
* Application health checks
* Infrastructure validation

---

# CI/CD

Every change goes through an automated pipeline.

```text
Developer
    │
    ▼
Git Push
    │
    ▼
GitHub Actions
    │
    ├── Run tests
    ├── Build application
    ├── Build Docker image
    ├── Security scan
    └── Deploy
             │
             ▼
           AWS
```

The objective is to make deployments reproducible and reduce manual intervention.

---

# Monitoring

The application exposes metrics using Spring Boot Actuator and Prometheus.

Grafana dashboards will monitor:

* API requests
* Response latency
* HTTP errors
* JVM metrics
* CPU
* Memory
* Database connections
* WebSocket connections

Example architecture:

```text
Spring Boot
     │
     ▼
Actuator
     │
     ▼
Prometheus
     │
     ▼
Grafana
```

---

# Infrastructure as Code

AWS infrastructure is managed using Terraform.

Example:

```bash
terraform init
terraform plan
terraform apply
```

Infrastructure components include:

* VPC
* Subnets
* Security groups
* ECS
* RDS
* ElastiCache
* S3
* Load Balancer
* IAM

The objective is to make the infrastructure reproducible rather than manually configured.

---

# Running Locally

## Requirements

Install:

* Java 21+
* Node.js
* Docker
* Docker Compose
* Git

Clone the repository:

```bash
git clone https://github.com/YOUR_USERNAME/cloud-chat.git

cd cloud-chat
```

Start PostgreSQL and Redis:

```bash
docker compose up -d postgres redis
```

Start the backend:

```bash
cd backend

./mvnw spring-boot:run
```

Start the frontend:

```bash
cd frontend

npm install
npm run dev
```

The application will then be available locally.

---

# Running Everything with Docker

Build and start the complete local environment:

```bash
docker compose up --build
```

Stop the environment:

```bash
docker compose down
```

Remove containers and volumes:

```bash
docker compose down -v
```

---

# Roadmap

## Phase 1 — Core Backend

* [ ] Spring Boot project
* [ ] PostgreSQL integration
* [ ] User entity
* [ ] Authentication
* [ ] JWT
* [ ] Conversation API
* [ ] Message API

## Phase 2 — Frontend

* [ ] React application
* [ ] Login page
* [ ] Registration page
* [ ] Chat interface
* [ ] Conversation list
* [ ] Message history

## Phase
