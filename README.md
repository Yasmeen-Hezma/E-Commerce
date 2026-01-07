# 🛍️ E-Commerce API
> A simple and secure e-commerce backend built with Spring Boot
---
## ✨ Main Features
### 🔐 Authentication
- Register / Login with JWT
- Refresh token support
- Email verification with OTP
- Forgot password & reset password functionality
### 👤 User Features
- View and update profile
- Change password
- View order history
### 🛒 Shopping Experience
- **Cart Management**: Add items, sync cart, clear cart, get cart details
- **Wishlist**: Add to wishlist, remove from wishlist, view wishlist
### 💳 Orders & Payment
- Place orders seamlessly
- Multiple payment methods:
  - PayPal integration
  - Cash on Delivery (COD)
### 📦 Product Management
- Full CRUD operations for products, brands, categories, and reviews
- Product image upload with validation
- Seller/Admin-only access for management features
### 👥 Role-Based Access Control
- **Customer**: Browse and purchase products
- **Seller**: Manage product inventory
- **Admin**: Full system access and management
---
## 🚀 Technologies
| Category | Technologies |
|----------|-------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3 |
| **Security** | Spring Security + JWT |
| **Database** | JPA / Hibernate + MySQL |
| **Payment** | PayPal Integration |
| **Documentation** | Swagger (OpenAPI) |
| **Utilities** | Lombok |
| **Testing** | JUnit + Mockito |
---
## 📊 Database Design (ER Diagram)
![ER Diagram](docs/ER-Diagram.png)
The database schema shows key relationships:
- Users own one Cart and one Wishlist
- Users can place many Orders
- Orders contain multiple OrderItems
- Products belong to Categories and Brands
- Products can have multiple Reviews
---
## ⚡ Quick Start

### 🐳 Option 1: Using Docker (Recommended)

**Prerequisites:** Docker and Docker Compose installed on your machine

This is the fastest and easiest way to run the application with zero configuration!

#### Step 1: Clone the Repository
```bash
git clone https://github.com/Yasmeen-Hezma/e-commerce-api.git
cd e-commerce-api
```

#### Step 2: Set Up Environment Variables
```bash
# Copy the example environment file
cp .env.example .env

# Edit .env with your credentials (or use defaults for testing)
nano .env  # or use any text editor
```

#### Step 3: Start the Application
```bash
# Build and start all services (MySQL + Spring Boot)
docker-compose up -d

# First time setup takes 2-3 minutes
# Check if services are running
docker-compose ps
```

#### Step 4: Access the Application
- **API Base URL**: http://localhost:8000
- **Swagger UI**: http://localhost:8000/swagger-ui.html

#### Step 5: View Logs (Optional)
```bash
# View application logs
docker-compose logs -f app

# View database logs
docker-compose logs -f mysql
```

#### Stop the Application
```bash
# Stop all services
docker-compose down

# Stop and remove all data (including database)
docker-compose down -v
```

---

### 💻 Option 2: Local Development (Without Docker)

**Prerequisites:** Java 21, Maven, and MySQL installed

#### Step 1: Clone the Repository
```bash
git clone https://github.com/Yasmeen-Hezma/e-commerce-api.git
cd e-commerce-api
```

#### Step 2: Set Up MySQL Database
Create a database named `e-commerce` and update `application.properties` with your credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/e-commerce
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

#### Step 3: Configure Email & PayPal
Add your SMTP and PayPal sandbox credentials in `application.properties`:
```properties
# Email Configuration
spring.mail.host=smtp.mailtrap.io
spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_APP_PASSWORD

# PayPal Configuration
paypal.client-id=YOUR_PAYPAL_CLIENT_ID
paypal.client-secret=YOUR_PAYPAL_SECRET
```

#### Step 4: Run the Application
```bash
mvn spring-boot:run
```

Server starts at: **http://localhost:8000**

#### Step 5: Access API Documentation
Open Swagger UI: **http://localhost:8000/swagger-ui.html**
#### Step 6: Run Tests
```bash
mvn test
```
---
## 🧪 Testing
The project includes comprehensive unit and integration tests using JUnit and Mockito.
Run all tests:
```bash
mvn test
```
Run specific test class:
```bash
mvn test -Dtest=ProductServiceTest
```
Generate test coverage report:
```bash
mvn jacoco:report
```
