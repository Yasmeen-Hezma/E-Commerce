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
### 1️⃣ Clone the Repository
```bash
git clone https://github.com/your-username/e-commerce.git
cd e-commerce
```
### 2️⃣ Set Up MySQL Database
Create a database named `e-commerce` and update `application.properties` with your credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/e-commerce
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```
### 3️⃣ Configure Email & PayPal
Add your SMTP and PayPal sandbox credentials in `application.properties`:
```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.username=YOUR_EMAIL
spring.mail.password=YOUR_APP_PASSWORD
# PayPal Configuration
paypal.client.id=YOUR_PAYPAL_CLIENT_ID
paypal.client.secret=YOUR_PAYPAL_SECRET
```
### 4️⃣ Run the Application
```bash
mvn spring-boot:run
```
Server starts at: **http://localhost:8000**
### 5️⃣ Access API Documentation
Open Swagger UI: **http://localhost:8000/swagger-ui.html**
### 6️⃣ Run Tests
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
