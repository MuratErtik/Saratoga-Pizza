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

  

