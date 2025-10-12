# 🍕 Saratoga-Pizza

Saratoga-Pizza is a modern web application for a pizza restaurant, designed to handle online orders in a realistic way.  
The project leverages modern technologies and best practices to ensure scalability, reliability, and maintainability.

---

## 🚀 Technologies Used

- **Spring Boot** – Fast and reliable backend framework for building Java applications.
- **PostgreSQL** – A robust relational database for storing users, products, and orders.
- **Redis** – Caching layer to speed up frequently requested data like the product catalog.
- **RabbitMQ** – Asynchronous messaging system to handle order events and kitchen notifications.
- **Docker** – Containerized setup to make development and deployment seamless.
- **Cloudinary** – Cloud-based image storage and management service for uploading and serving images efficiently.
- **Spring Security & Validation** – Ensures secure and valid user interactions.
- **Logging** – Comprehensive logging system to monitor HTTP requests, database operations, and internal events.

---

## 🛠️ Project Highlights

- **Containerized Environment:** All services run in Docker containers, making it easy to replicate environments across machines.
- **Event-driven Architecture:** Orders are sent to the kitchen asynchronously via RabbitMQ for a realistic workflow.
- **Caching with Redis:** Frequently accessed data like pizzas and drinks are cached to improve performance.
- **Comprehensive Logging:** Application, database, HTTP, and messaging logs are captured for monitoring and debugging.

---

## 🎯 Goal

The goal of PizzaApp is to demonstrate a professional, full-featured pizza ordering system with modern backend practices, including asynchronous messaging, caching, security, and containerized deployment.

---

# 🔑 Auth Service

The **Auth Service** is responsible for handling user authentication, registration, and token management in a secure and scalable way.

---

## ✨ Features

### 📝 Signup (Registration)
- Checks if the email is already registered.  
- Creates a new user with a securely encoded password.  
- Assigns role as **Admin** or **Customer** depending on the request.  
- Saves the user into the database.  
- Sends a confirmation email after successful registration.  
- Generates an **Access Token** (short-lived) and a **Refresh Token** (7 days).  
- Stores the refresh token in **Redis** for quick validation.  

### 🔐 Signin (Login)
- Authenticates user credentials with `AuthenticationManager`.  
- Fetches the user from the database.  
- Issues a new **Access Token** and **Refresh Token**.  
- Saves the refresh token in **Redis**.  

### ♻️ Refresh Token
- Validates the refresh token against **Redis**.  
- If valid, generates a new **Access Token** without requiring login again.  
- Keeps the existing **Refresh Token** active until expiration.  

### 🚪 Logout
- Deletes the refresh token from **Redis**, immediately revoking the user’s session.

# 🔑 Password Management

## 🔄 Change Password
- Verifies the user by **userId**.  
- Validates that the provided **email** matches the account.  
- Checks if **oldPassword** is correct.  
- Ensures **newPassword** and **confirmPassword** match.  
- Encodes and updates the **new password**.  
- Saves changes to the **database**.  
- Returns a **success response**.  

---

## 📧 Reset Password (Forgot Password Flow)
- Accepts an **email address**.  
- If the user exists, generates a **unique reset token**.  
- Saves the token in **Redis** with **15 minutes expiration**.  
- Sends a **reset password link** (with **userId** & **token**) to the user via email.  
- Always responds with  
  *"If this email is registered, a reset link has been sent."*  
  to prevent **account enumeration**.  

---

## 🔑 Reset Password via Token
- Validates the token from **Redis** using **userId** and **token**.  
- If valid, updates the user’s **password** with the new one.  
- Deletes the used token from **Redis** (**one-time use**).  
- Returns a **success response**.  

  
# 👤 Customer Service

The **Customer Service** is responsible for handling customer-specific operations such as completing registration, verifying accounts, managing user info, addresses, and bank details in the Saratoga Pizza system.  

---

## ✨ Features

### 📝 Complete Registration
- Enriches user profile after signup.  
- Requires a valid **address**.  
- Saves additional info such as **mobile number** and **addresses**.  
- Prevents duplicate addresses.  
- Returns success after completion.  

---

### 📧 Verify Account
- Uses a verification code stored in **Redis**.  
- Validates the provided code against the stored one.  
- If valid:
  - Marks the user as **verified**.  
  - Deletes the verification code from Redis.  
- If invalid or expired, throws an error.  

---

### 👀 Get Customer Info
- Fetches all customer details by **userId**.  
- Includes:
  - Personal info (name, lastname, email, mobileNo).  
  - Verification status.  
  - Addresses.  
  - Bank details.  
  - Used coupons.  
  - Loyalty points.  
  - Account creation & last login timestamps.  

---

## 🏠 Address Management (CRUD)

### 📋 Get Address Info
- Retrieves all customer addresses.  

### ➕ Add New Address
- Adds a new address for the user.  
- Prevents duplicate address names.  

### ❌ Delete Address
- Deletes an address by **addressId**.  
- Ensures the address exists before deletion.  

### 🔄 Change Address
- Updates fields of an existing address selectively.  
- Only provided fields are updated.  

---

## 💳 Bank Details Management (CRUD)

### 📋 Get Bank Details Info
- Retrieves all bank details linked to a user.  

### ➕ Add New Bank Details
- Adds a new bank account/credit card info.  
- Prevents duplicates by **accountName**.  
- Stores metadata (`createdAt`, `updatedAt`).  

### ❌ Delete Bank Details
- Deletes a bank detail by **bankDetailsId**.  
- Ensures the detail exists before deletion.  

### 🔄 Change Bank Details
- Updates fields of a bank detail record selectively.  
- Refreshes `updatedAt` timestamp.  

---

## 👤 Change User Info
- Allows user to update:
  - **Name, Lastname**.  
  - **Email** (triggers new email verification flow).  
  - **Mobile number**.  
- When email is changed:
  - Sets `verified=false`.  
  - Generates a new verification code (5 minutes expiry in Redis).  
  - Sends verification mail.  
- Saves changes to database.  

---


# 🍕 Product Management Service

The **Product Management Service** is responsible for handling all product-related operations in the Saratoga Pizza system, including product creation, updating, filtering, deletion, and management of related entities such as **Deals**, **Sizes**, and **Toppings**.  
It ensures the business logic for menu customization, validation, and association between products and their variants.

---

## ✨ Features Overview

| Category | Description |
|-----------|-------------|
| **Product CRUD** | Create, update, delete, and search for products with detailed validation and image handling. |
| **Filtering** | Filter products dynamically by price, category, dietary tags, spicy level, or rating. |
| **Deals CRUD** | Manage promotional combo deals (with multiple items, discounts, and images). |
| **Product Sizes CRUD** | Manage available sizes (S, M, L, XL) with additional prices for each. |
| **Product Toppings CRUD** | Manage customizable toppings linked to products with image upload and validation. |

---

## 🧱 Architecture and Dependencies

The `ProductService` class collaborates with multiple repositories and services:

- `ProductRepository` – Handles database operations for products.
- `CategoryRepository` – Ensures products are linked to valid categories.
- `DealRepository`, `DealItemRepository` – Manage combo deals and their items.
- `ProductSizeRepository`, `ProductToppingRepository` – Manage size and topping entities.
- `ImageUploadService` – Integrates with **Cloudinary** to upload and retrieve image URLs.

All major operations are **transactional**, ensuring data consistency across multiple entities.

---

# 🛒 Product Management

### 🧩 Create Product

**Method:** `createProduct(CreateProductRequest request)`

#### Workflow:
1. Checks if the product name already exists.
2. Validates price (cannot be negative).
3. Uploads all product images to **Cloudinary**.
4. Fetches and validates the assigned category.
5. Sets all product metadata:
   - Vegan / Vegetarian flags  
   - Spicy level  
   - Preparation time  
   - Tags and allergens  
   - Customizable option
6. Saves the product and returns success message.

---
