# ⚡ VoltEdge — Electronics E-Commerce Platform

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen)
![Keycloak](https://img.shields.io/badge/Keycloak-26.6-blueviolet)
![Angular](https://img.shields.io/badge/Frontend-Angular-c3002f)
![AI](https://img.shields.io/badge/AI-Groq%2C%20Ollama%2C%20Qdrant-orange)
![Database](https://img.shields.io/badge/Database-PostgreSQL%2C%20Elasticsearch-4169E1)
![Status](https://img.shields.io/badge/Status-Completed-brightgreen)

> A full-featured **electronics e-commerce platform** built with **Spring Boot 4.1.0, Java 21, Keycloak, Stripe, Elasticsearch**, **Angular**, and **AI-powered product recommendations** using **Groq LLM, Ollama embeddings, and Qdrant vector store** — following clean architecture, role-based security, and modern API & UI design principles.

---

## 📌 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [AI Recommendation System](#-ai-recommendation-system)
- [System Architecture](#-system-architecture)
- [Project Structure](#-project-structure)
- [Module Responsibilities](#-module-responsibilities)
- [Technology Stack](#-technology-stack)
- [Design Patterns & Principles](#-design-patterns--principles)
- [Authentication & Authorization Flow](#-authentication--authorization-flow)
- [Concurrency & Transaction Management](#-concurrency--transaction-management)
- [Frontend (Angular)](#-frontend-angular)
- [Run the Application](#-run-the-application)
- [API Documentation](#-api-documentation)
- [Testing & Code Coverage](#-testing--code-coverage)
- [CI/CD Pipeline](#-cicd-pipeline)
- [Demo Video](#-demo-video)
- [Team Members](#-team-members)

---

## 📖 Overview

**VoltEdge** is a comprehensive electronics e-commerce platform built as a **REST API** with **Spring Boot 4.1.0**. The platform supports three user roles (**Customer, Merchant, Admin**) and provides the full e-commerce lifecycle: product browsing/search, shopping cart, wishlist, checkout with **Stripe** payment processing, order management, reviews/ratings, merchant dashboards, and an **AI-powered product recommendation engine**.

The system leverages **modern technologies** including:
- **Java 21** with **Spring Boot 4.1.0** for robust backend development
- **Keycloak 26.6** for OAuth2 / OpenID Connect authentication and role-based authorization
- **Stripe** for secure payment processing with idempotency guarantees
- **Elasticsearch 9.4.2** for full-text product search with autocomplete and faceted filters
- **Spring AI 2.0** with **Groq (Llama 3.3 70B)**, **Ollama (nomic-embed-text)**, and **Qdrant** for AI-powered product recommendations
- **PostgreSQL** for reliable relational data persistence
- **Cloudinary** for image and file uploads

This architecture enables the platform to handle high traffic, ensure data consistency, deliver intelligent product recommendations, and provide a seamless API experience for frontend applications.

---

## ✨ Features

### 🛍️ User Features

#### **Authentication & Authorization**
* JWT-based OAuth2 authentication via **Keycloak 26.6**
* Role-based access control (**Customer, Merchant, Admin**)
* Registration with automatic role assignment (custom Keycloak SPI)
* Login with client-role enforcement (custom Keycloak authenticator)
* Remember-me, forgot password flow
* Custom branded VoltEdge login theme (FreeMaker templates)

#### **Product Discovery & Search**
* Browse all available products with pagination
* Advanced filtering (by category, price range, keyword)
* **Elasticsearch** full-text search with multi-match queries
* Autocomplete suggestions
* Sort by price, rating, newest, best-selling
* Featured product recommendations
* Category-based navigation
* Detailed product information pages

#### **Shopping Experience**
* Add/remove products from shopping cart
* Real-time stock validation with pessimistic locking
* Wishlist creation and management
* Persistent cart storage per customer

#### **Ordering & Checkout**
* **Stripe PaymentIntent** checkout flow
* Idempotent payment processing (no double charges)
* Order history with detailed tracking
* Order status lifecycle (Pending → Paid → Shipped → Delivered → Cancelled)
* Invoice generation and payment records

#### **Reviews & Ratings**
* Submit product reviews with ratings (1-5 stars)
* View customer reviews with aggregate summaries
* Review management (edit/delete)

#### **User Account Management**
* View and edit profile information
* Profile picture upload via **Cloudinary**
* Manage multiple saved addresses
* Track order history with detailed information
* View wishlist

#### **Guest Features**
* Browse all products without login requirement
* View product details and merchant information
* View existing reviews and community ratings
* Search and filter products by category, price, rating

### 🛡️ Merchant Features

#### **Dashboard & Analytics**
* Sales performance dashboard
* Product statistics and insights
* Order management dashboard

#### **Product Management**
* Add new products with images
* Edit product details
* Manage product inventory/stock
* View own product catalog

#### **Order Management**
* View incoming orders
* Track order status updates

### 👑 Admin Features

#### **Dashboard & Analytics**
* Full order management (all users)
* Payment oversight and records
* Category management

#### **User Management**
* View all merchants and customers
* Order history for any user account

---

## 🤖 AI Recommendation System

The platform integrates an **AI-powered recommendation engine** that delivers personalised product suggestions based on customer natural-language preferences.

### Pipeline Architecture

```
User Preferences (natural language)
        │
        ▼
┌──────────────────────────┐
│   Groq                   │  ←  LLM ranks & recommends products
│   via OpenAI-compatible  │     with natural-language reasoning
│   API                    │
└──────────┬───────────────┘
           │ ranked product IDs + reasons
           ▼
┌──────────────────────────┐
│   Ollama                 │  ←  Generates product embeddings
│   nomic-embed-text       │     for semantic understanding
└──────────┬───────────────┘
           │ embeddings
           ▼
┌──────────────────────────┐
│   Qdrant Vector Store    │  ←  Vector similarity search
│   (products collection)  │     across product catalogue
└──────────┬───────────────┘
           │ similar products
           ▼
┌──────────────────────────┐
│   Validation & Fallback  │  ←  Never invents product IDs
│                          │     Fills gaps with best-sellers
└──────────┬───────────────┘
           │
           ▼
   Personalised Recommendations
```

### How It Works

1. **User submits preferences** — a natural-language message like *"I'm looking for a gaming laptop with good graphics, 16GB RAM, under $1500"*
2. **Candidate selection** — top 20 best-selling in-stock products are fetched as candidates
3. **LLM ranking** — Groq's Llama 3.3 70B ranks candidates against preferences, returning product IDs with natural-language reasons
4. **Embedding & similarity** — Ollama generates embeddings; Qdrant performs vector similarity search
5. **Validation** — AI output is validated against actual products (never invents IDs)
6. **Fallback** — remaining slots are filled with popular products if AI returns fewer than requested

### Endpoint

```http
POST /recommendations
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "preferences": "I need a wireless gaming mouse with RGB lighting",
  "limit": 5
}
```

```json
[
  {
    "id": "prod-xyz",
    "name": "Logitech G Pro X Superlight",
    "price": 129.99,
    "imageUrl": "https://...",
    "rating": 4.8,
    "reason": "Top-rated wireless gaming mouse with ultra-lightweight design, hero sensor, and RGB compatibility — matches your gaming preferences perfectly.",
    "aiGenerated": true
  }
]
```

---

## 🧱 System Architecture

The application follows a **Layered Client-Server Architecture** with **OAuth2-secured REST APIs**:

```
┌──────────────┐     ┌───────────────────────────────────────────────────────────────┐
│  Angular UI  │     │                Spring Boot REST API (:8080)                   │
│ (:3001-3003) │────▶│                                                               │
└──────────────┘     │  ┌───────────┐ ┌──────────┐ ┌────────────┐ ┌──────────────┐   │
                     │  │Controllers│ │ Services │ │Repositories│ │  Document    │   │
                     │  └─────┬─────┘ └────┬─────┘ └──────┬─────┘ │  (Elastic)   │   │
                     │        │            │              │       └──────────────┘   │
                     │  ┌─────▼────────────▼──────────────▼────────────────────────┐ │
                     │  │               RecommendationService                      │ │
                     │  │  ┌─────────┐  ┌───────────┐  ┌────────────────────┐      │ │
                     │  │  │  Groq   │  │  Ollama   │  │  Qdrant Vector DB  │      │ │
                     │  │  │ (LLM)   │  │ (embeds)  │  │                    │      │ │
                     │  │  └─────────┘  └───────────┘  └────────────────────┘      │ │
                     │  └──────────────────────────────────────────────────────────┘ │
                     └──────────────────────────┬────────────────────────────────────┘
                                                │
          ┌─────────────────────────────────────┼─────────────────────────────────────┐
          │                                     │                                     │
  ┌───────▼────────┐                  ┌─────────▼──────────┐              ┌───────────▼──────────┐
  │   PostgreSQL   │                  │  Elasticsearch 9.4 │              │     Keycloak 26.6    │
  │  (app + KC DB) │                  │  (product search)  │              │  (OAuth2 / OIDC IAM) │
  └────────────────┘                  └────────────────────┘              └──────────────────────┘
```

**Key Architectural Principles:**
- **Separation of Concerns**: Each layer has distinct responsibilities
- **Stateless API**: JWT-based stateless authentication (no HTTP sessions)
- **Event-Driven Indexing**: Elasticsearch indexing via `@TransactionalEventListener` (AFTER_COMMIT)
- **Fail-Fast Validation**: Bean Validation on all DTOs

---

## 📂 Project Structure

```
spring-electronics-ecommerce/
│
├── docker-compose.yml                    # Infrastructure services (ES, PostgreSQL, Keycloak)
├── .env                                  # Environment variables (Keycloak admin creds)
├── pom.xml                               # Maven parent POM (Spring Boot 4.1.0, Java 21)
│
├── keycloak/                             # Keycloak customisation
│   ├── Dockerfile                        # Multi-stage Keycloak 26.6 custom build
│   ├── spi/                              # Custom Keycloak SPI extensions
│   │   ├── pom.xml
│   │   └── src/main/java/com/voltedge/keycloak/
│   │       ├── listener/                 # RegisterClientRoleAssignerProvider
│   │       └── authenticator/            # LoginClientRoleEnforcerAuthenticator
│   ├── realm-config/import/
│   │   └── electronics-realm.json        # Realm export (roles, clients, IDP)
│   └── themes/electronics/login/         # Custom VoltEdge login theme
│       ├── theme.properties
│       ├── template.ftl
│       ├── login.ftl / register.ftl / error.ftl
│       ├── login-reset-password.ftl / login-update-password.ftl / terms.ftl
│       └── resources/css/styles.css, img/logo.png
│
└── src/
    ├── main/java/com/electronics/
    │   ├── ElectronicsEcommerceApplication.java    # Main entry point
    │   │
    │   ├── config/
    │   │   ├── SecurityConfig.java                 # Spring Security (JWT, roles, CORS)
    │   │   ├── JwtAuthConfig.java                  # JWT role extraction (realm_access)
    │   │   ├── CorsConfig.java                     # CORS for Angular clients
    │   │   ├── CurrentUserProvisioningFilter.java  # Auto-create user on first JWT login
    │   │   ├── StripeConfig.java                   # Stripe API initialisation
    │   │   ├── RestTemplateConfig.java
    │   │   └── CloudinaryConfig.java               # Cloudinary upload config
    │   │
    │   ├── controller/               # 15 REST controllers
    │   │
    │   ├── service/                  # 21 service classes
    │   │
    │   ├── repository/               # 14 JPA repositories
    │   │
    │   ├── entity/                   # 16 JPA entities
    │   │
    │   ├── dto/                      # 30+ DTOs
    │   │
    │   ├── document/
    │   │   └── ProductDocument.java               # Elasticsearch document
    │   ├── exception/                # GlobalExceptionHandler + 15+ custom exceptions
    │   └── util/                     # Utility classes
    │
    ├── main/resources/
    │   ├── application.properties
    │   └── db/schema_postgres.sql
    │
    └── test/java/com/electronics/
        └── ElectronicsEcommernceApplicationTests.java

```

---

## 📦 Module Responsibilities

| Module | Responsibility | Technology |
|--------|---------------|------------|
| **Keycloak SPI** | Custom authentication & registration logic | Java 21, Keycloak 26.6 SPI |
| **Keycloak Theme** | Branded VoltEdge login pages | FreeMaker, CSS, HTML |
| **Spring Boot App** | REST API, business logic, persistence, AI | Spring Boot 4.1.0 |
| **Controllers** | HTTP request handling, input validation, response formatting | Spring Web MVC |
| **Services** | Business logic, Stripe integration, AI orchestration | Spring, Spring AI |
| **Repositories** | Data access layer (JPA specifications, pessimistic locking) | Spring Data JPA |
| **Elasticsearch** | Full-text product search & indexing | Spring Data Elasticsearch |
| **AI Engine** | Groq LLM + Ollama embeddings + Qdrant vector search | Spring AI 2.0 |
| **Auth** | OAuth2 / JWT token validation, role extraction | Spring Security, Keycloak |

---

## 🛠 Technology Stack

### Backend

| Category | Technology | Version | Purpose |
|----------|-----------|---------|---------|
| **Language** | Java | 21 | Core backend development |
| **Framework** | Spring Boot | 4.1.0 | Application framework |
| **ORM** | Hibernate / Spring Data JPA | - | Object-Relational Mapping |
| **Security** | Spring Security + OAuth2 Resource Server | - | JWT auth, role-based access |
| **IAM** | Keycloak | 26.6 | OAuth2 / OIDC identity provider |
| **Database Driver** | PostgreSQL JDBC | - | Database connectivity |
| **Payment** | Stripe Java SDK | 33.0.0 | Payment processing |
| **Media** | Cloudinary HTTP44 | 1.39.0 | Image upload & hosting |
| **AI / LLM** | Spring AI (Groq, Ollama, Qdrant) | 2.0.0 | AI-powered recommendations |
| **Search** | Elasticsearch | 9.4.2 | Full-text product search |
| **API Docs** | SpringDoc OpenAPI | 3.0.2 | Swagger UI / OpenAPI spec |
| **Code Generation** | Lombok | - | Boilerplate reduction |
| **Logging** | Spring Boot Logging (Logback) | - | Application logging |
| **Testing** | Spring Boot Test, JUnit 5, Mockito | - | Unit & integration testing |

### Infrastructure

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| **Containerisation** | Docker & Docker Compose | - | Service orchestration |
| **Database** | PostgreSQL | 16 (Alpine) | Relational data storage |
| **Vector Database** | Qdrant | Latest | Product embedding storage & similarity search |
| **Embedding Model** | Ollama | Latest | nomic-embed-text model |
| **LLM Provider** | Groq | - | Llama 3.3 70B via OpenAI-compatible API |
| **Search Engine** | Elasticsearch | 9.4.2 | Product search indexing |
| **Web Server** | Tomcat (embedded) | - | Servlet container |

### Build & Deployment

| Tool | Purpose |
|------|---------|
| **Build Tool** | Maven (with wrapper) |
| **Package Format** | JAR (Spring Boot executable) |
| **Formatting** | Spotless Maven Plugin (Eclipse style) |
| **CI** | GitHub Actions (build + format check) |
| **Source Control** | Git |

---

## 🧠 Design Patterns & Principles

| Pattern | Usage |
|---------|-------|
| **SOLID Principles** | Throughout all layers |
| **Layered Architecture** | Controller → Service → Repository |
| **DTO Pattern** | Request/Response objects separate from entities |
| **Repository Pattern** | Spring Data JPA repositories for data access |
| **Specification Pattern** | `ProductSpecifications` for dynamic JPA Criteria queries |
| **Event-Driven Architecture** | `ProductIndexEvent` / `@TransactionalEventListener` for async ES indexing |
| **Strategy Pattern** | Role-based authentication strategies |
| **Builder Pattern** | Entity/DTO construction |
| **Factory Pattern** | Service instantiation |
| **Singleton Pattern** | Spring beans (default scope) |
| **Template Method Pattern** | Spring's `JpaRepository` and `Querydsl` support |

---

## 🔐 Authentication & Authorization Flow

```
                  ┌──────────┐
                  │  Angular │
                  │   Client │
                  └─────┬────┘
                        │ 1. Login request
                        ▼
              ┌──────────────────┐
              │    Keycloak      │
              │  26.6 (OAuth2)   │
              │                  │
              │ - Validates creds│
              │  2. Checks role  │ ← Custom Authenticator enforces client-role
              │ - Issues JWT     │
              └────────┬─────────┘
                       │ 3. JWT access token
                       ▼
              ┌──────────────────┐
              │   Spring Boot    │
              │  Resource Server │
              │                  │
              │ 4. Validates JWT │ ← JWK Set from Keycloak
              │ 5. Extracts roles│ ← JwtAuthConfig (realm_access.roles)
              │ 6. Provisions    │ ← Creates local User entity if new
              │    local user    │
              │ 7. Authorizes    │ ← @PreAuthorize / SecurityConfig
              │    endpoint      │
              └──────────────────┘
```

1. User logs in via Keycloak login page (custom VoltEdge theme)
2. Keycloak authenticates credentials and enforces client-role mapping
3. JWT access token is issued with realm roles embedded
4. Spring Boot validates the JWT against Keycloak's JWK Set
5. `JwtAuthConfig` extracts `realm_access.roles` → Spring Security `ROLE_` authorities
6. `CurrentUserProvisioningFilter` auto-creates a local User record on first login
7. `SecurityConfig` enforces role-based access on endpoints

### Keycloak Realm Configuration

| Setting | Value |
|---------|-------|
| **Realm** | `electronics` |
| **Roles** | `CUSTOMER`, `MERCHANT`, `ADMIN` |
| **Clients** | `voltedge-customer` (:3002), `voltedge-merchant` (:3001), `voltedge-admin` (:3003) |
| **Reset Password** | Enabled |
| **Remember Me** | Enabled |
| **Google IDP** | Configured |
| **Access Token TTL** | 300 seconds (5 min) |
| **SSO Session Max** | 36,000 seconds (10 hrs) |

---

## 🔒 Concurrency & Transaction Management

### Pessimistic Locking on Stock

During checkout, product stock is protected with **pessimistic write locks** to prevent overselling:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM Product p WHERE p.id IN :ids")
List<Product> findAllWithLock(@Param("ids") Set<Long> ids);
```

### Transactional Event-Driven Indexing

Elasticsearch indexing is decoupled from the main transaction using `@TransactionalEventListener`:

```java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleProductIndexEvent(ProductIndexEvent event) { ... }
```

This ensures:
- ES indexing only happens after the DB transaction commits successfully
- If the transaction rolls back, the indexing event is discarded
- No index inconsistency between DB and ES

### Stripe Idempotency

Payment processing includes:
- Cart total re-validation before Stripe charge
- Payment status tracking (`PENDING → SUCCEEDED / FAILED`)
- Prevention of duplicate charges via payment intent ID tracking

---

## 🎨 Frontend (Angular)

The frontend of **VoltEdge** is developed as **three independent Angular applications**, each tailored to a specific user role. This separation keeps each application focused on its own business requirements, permissions, and user experience while sharing the same backend REST API.

### Applications

| Application            | Description                                                                                                                                    | Primary Users  |
| ---------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- | -------------- |
| **Customer UI**        | Complete shopping experience including product browsing, AI recommendations, cart, wishlist, checkout, profile management, and order tracking. | Customers      |
| **Merchant Dashboard** | Product management, inventory control, sales analytics, order management, and merchant profile.                                                | Merchants      |
| **Admin Dashboard**    | Platform administration, category management, user management, order oversight, and payment monitoring.                                        | Administrators |

### Technology Stack

| Technology                 | Purpose                        |
| -------------------------- | ------------------------------ |
| **Angular**                | Frontend framework             |
| **TypeScript**             | Application development        |
| **Angular Router**         | Client-side routing            |
| **Angular HttpClient**     | REST API communication         |
| **Angular Signals / RxJS** | State management               |
| **Tailwind CSS**           | Responsive UI styling          |
| **Keycloak Angular**       | Authentication & authorization |
| **Angular Forms**          | Reactive forms & validation    |

### Features

#### Customer Application

* User registration & login using Keycloak
* Browse products
* Product search & filtering
* AI-powered product recommendations
* Shopping cart management
* Wishlist management
* Product reviews & ratings
* Stripe checkout
* Order history
* Profile & address management

#### Merchant Dashboard

* Merchant authentication
* Product CRUD
* Inventory management
* Image uploads
* Sales dashboard
* Incoming order management
* Merchant profile management

#### Admin Dashboard

* Category management
* Product administration
* User management
* Order management
* Payment monitoring
* Platform analytics

### Repository

The frontend applications are maintained in a **separate GitHub repository**.

> **Frontend Repository:**
> **https://github.com/abdoemad552/voltedge.git**

Clone the frontend repository and follow its README to run the Angular applications locally.

### 📸 ScreenShots

#### 1- Login Page

#### 2- Customer UI

#### 3- Merchant UI

#### 4- Admin UI

---

## 🚀 Run the Application

### Prerequisites

- **Java Development Kit (JDK) 21** or higher
- **Maven 3.9+** (or use the included Maven wrapper)
- **Docker & Docker Compose**
- **Ollama** (for AI embeddings) — [ollama.ai](https://ollama.ai)
- **Qdrant** (vector database) — [qdrant.tech](https://qdrant.tech)
- **Groq API Key** — [console.groq.com](https://console.groq.com)
- **Stripe API Keys** — [dashboard.stripe.com](https://dashboard.stripe.com)
- **Git**

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd spring-electronics-ecommerce
```

### Step 2: Configure Environment

Copy the `.env` file and update variables as needed:

```properties
KC_DB_PASSWORD=****
KC_ADMIN_USER=****
KC_ADMIN_PASSWORD=****
```

Set additional environment variables for the application:

```bash
# Required — AI
export GROQ_API_KEY=gsk_your_key_here

# Required — Payments
export STRIPE_SECRET_KEY=sk_test_...
export STRIPE_PUBLISHABLE_KEY=pk_test_...

# Required — Media
export CLOUDINARY_API_SECRET=your_secret

# Optional (defaults provided)
export DB_URL=jdbc:postgresql://localhost:5432/mydb
export DB_USERNAME=admin
export DB_PASSWORD=admin123
```

### Step 3: Start Infrastructure Services

```bash
# Start Elasticsearch, Keycloak, and Keycloak's PostgreSQL
docker compose up -d

# Verify all services are healthy
docker compose ps
```

### Step 4: Start AI Services

```bash
# Qdrant vector database
docker run -d -p 6333:6333 -p 6334:6334 qdrant/qdrant

# Ollama with embedding model
ollama pull nomic-embed-text
ollama serve
```

### Step 5: Build & Run the Application

```bash
# Build the project
./mvnw clean package -DskipTests

# Run with Maven
./mvnw spring-boot:run

# Or run the JAR directly
java -jar target/electronics-ecommerce-0.0.1-SNAPSHOT.jar
```

### Access the Application

| Service | URL |
|---------|-----|
| **Spring Boot API** | `http://localhost:8080` |
| **Swagger UI** | `http://localhost:8080/swagger-ui.html` |
| **OpenAPI Spec** | `http://localhost:8080/v3/api-docs` |
| **Keycloak Admin Console** | `http://localhost:8081/admin` |
| **Keycloak Realm** | `http://localhost:8081/realms/electronics` |
| **Elasticsearch** | `http://localhost:9200` |
| **App Health** | `http://localhost:8080/actuator/health` |
| **Qdrant Dashboard** | `http://localhost:6333/dashboard` |
| **Ollama API** | `http://localhost:11434` |

---

## 📖 API Documentation

### Public Endpoints (No Auth Required)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/products` | Browse products (filter, sort, paginate) |
| GET | `/api/v1/products/featured` | Featured products |
| GET | `/api/v1/products/{id}` | Product details |
| GET | `/api/v1/categories` | All categories |
| GET | `/api/v1/categories/{id}` | Category details |
| GET | `/api/v1/products/{id}/reviews` | Product reviews |
| GET | `/api/v1/products/{id}/reviews/summary` | Review aggregate |
| GET | `/api/v1/merchants/summary` | All merchants summary |
| GET | `/api/v1/merchants/{id}` | Merchant details |
| GET | `/swagger-ui/**` | Swagger documentation |
| GET | `/v3/api-docs/**` | OpenAPI specification |
| GET | `/actuator/health` | Health check |

### Customer Endpoints (Requires `CUSTOMER` role)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/cart` | View shopping cart |
| POST | `/api/cart/items` | Add item to cart |
| PUT | `/api/cart/items/{productId}` | Update cart item quantity |
| DELETE | `/api/cart/items/{productId}` | Remove item from cart |
| POST | `/api/v1/checkout/init` | Initiate Stripe checkout |
| POST | `/api/v1/checkout/confirm` | Confirm payment & create order |
| GET | `/api/v1/orders/me` | My orders |
| GET | `/api/v1/orders/me/{id}` | Order details |
| POST | `/api/v1/products/{id}/reviews` | Submit review |
| PATCH | `/api/v1/products/{id}/reviews` | Update review |
| DELETE | `/api/v1/products/{id}/reviews` | Delete review |
| GET | `/api/v1/wishlist` | View wishlist |
| POST | `/api/v1/wishlist` | Add to wishlist |
| DELETE | `/api/v1/wishlist/{productId}` | Remove from wishlist |
| GET | `/api/v1/profile` | View profile |
| PATCH | `/api/v1/profile` | Update profile |
| POST | `/api/v1/profile/picture` | Upload profile picture |
| GET | `/api/v1/profile/addresses` | List addresses |
| POST | `/api/v1/profile/addresses` | Add address |
| PATCH | `/api/v1/profile/addresses/{id}` | Update address |
| DELETE | `/api/v1/profile/addresses/{id}` | Delete address |
| GET | `/api/v1/payments/me` | My payments |
| GET | `/api/v1/payments/me/{id}` | Payment details |
| POST | `/recommendations` | **AI product recommendations** |

### Merchant Endpoints (Requires `MERCHANT` role)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/merchant/dashboard` | Sales dashboard |
| GET | `/api/v1/merchant/products` | Own products |
| POST | `/api/v1/merchant/products` | Create product |
| PUT | `/api/v1/merchant/products/{id}` | Update product |
| GET | `/api/v1/merchant/orders` | Incoming orders |
| GET | `/api/v1/merchant/profile` | Merchant profile |
| POST | `/api/v1/upload` | Upload image |

### Admin Endpoints (Requires `ADMIN` role)

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/orders/admin` | All orders |
| GET | `/api/v1/orders/admin/{id}` | Order details |
| PATCH | `/api/v1/orders/admin/{id}/status` | Update order status |
| GET | `/api/v1/payments/admin` | All payments |
| GET | `/api/v1/payments/admin/{id}` | Payment details |
| POST | `/api/v1/categories` | Create category |
| PUT | `/api/v1/categories/{id}` | Update category |
| DELETE | `/api/v1/categories/{id}` | Delete category |
| POST | `/api/v1/products` | Create product |
| PUT | `/api/v1/products/{id}` | Update product |
| DELETE | `/api/v1/products/{id}` | Soft-delete product |

Full interactive API documentation is available at **`/swagger-ui.html`** when the application is running.

---

## 🔄 CI/CD Pipeline

The project uses **GitHub Actions** for continuous integration:

```yaml
# .github/workflows/ci.yml
on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

steps:
  - Checkout repository
  - Setup Java 21 (Temurin)
  - Cache Maven dependencies
  - Run spotless:check (formatting validation)
  - Run mvn clean package -DskipTests (build)
```

**Triggered on:** Push to any branch, PR to `main` or `develop`

---

### 📽️ Demo Video


---

## 👥 Team Members

* **Abdelrahman Emad**
* **Ahmed Ramadan**
* **Mohammed Arabie**
* **Reem Mohy Eldin**
* **Youssef Abdallah**

---
