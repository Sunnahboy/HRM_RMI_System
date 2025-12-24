# HRM RMI System 📌

## Project Overview
The HRM RMI System is a distributed Human Resource Management application built using **Java RMI**, **JavaFX**, and **PostgreSQL**. It demonstrates a client–server architecture where Employee and HR clients communicate securely with a centralized server to manage employee data, leave applications, and reporting.

The system is designed with clear separation of concerns, scalability, and fault tolerance in mind, following best practices for distributed systems.

---

## 🏗️ System Architecture
The system consists of four main layers:

### 1. Client Layer (JavaFX)
* **Employee Client**
* **HR Client**
* JavaFX-based GUI components

### 2. Controller Layer
* Acts as a bridge between GUI and RMI services.
* Handles remote calls and exceptions.

### 3. RMI Server Layer
* Exposes remote services via `HRMService`.
* Implements business logic and connects to PostgreSQL using the repository pattern.

### 4. Database Layer
* **PostgreSQL** database stores employee records, leave data, and reports.

> **Note:** A backup RMI server is included to demonstrate basic fault-tolerance through server replication.

---

## 🧩 Key Technologies Used
* **Java 17**
* **Java RMI** – Distributed communication
* **JavaFX** – Graphical user interface
* **PostgreSQL** – Relational database
* **Gradle** – Build and dependency management
* **SSL/TLS** – Prepared for secure communication

---

## ✨ Features

### Shared Features
* Login / Authentication
* View/Update employee profile
* Apply for leave & view leave history
* Change password

### HR Features
* Register and Terminate employees
* Search employee profiles
* Approve or reject leave requests
* Generate yearly employee reports

---

## 📁 Project Structure
```text
src/main/java/
├── common/     # Shared interfaces, models, utilities
├── server/     # RMI server logic and repositories
├── client/     # Employee & HR clients (JavaFX)
└── resources/  # SSL and configuration files

database/
├── hrm_schema.sql
└── hrm_test_data.sql
