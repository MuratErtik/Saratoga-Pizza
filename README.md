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
