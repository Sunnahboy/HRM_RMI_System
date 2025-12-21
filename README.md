HRM RMI System
📌 Project Overview

The HRM RMI System is a distributed Human Resource Management application built using Java RMI, JavaFX, and PostgreSQL.
It demonstrates a client–server architecture where Employee and HR clients communicate securely with a centralized server to manage employee data, leave applications, and reporting.

The system is designed with clear separation of concerns, scalability, and fault tolerance in mind, following best practices for distributed systems.

🏗️ System Architecture

The system consists of four main layers:

Client Layer (JavaFX)

Employee Client

HR Client

JavaFX-based GUI components

Controller Layer

Acts as a bridge between GUI and RMI services

Handles remote calls and exceptions

RMI Server Layer

Exposes remote services via HRMService

Implements business logic

Connects to PostgreSQL using repository pattern

Database Layer

PostgreSQL database

Stores employee records, leave data, and reports

A backup RMI server is included to demonstrate basic fault-tolerance through server replication.

🧩 Key Technologies Used

Java 17

Java RMI – distributed communication

JavaFX – graphical user interface

PostgreSQL – relational database

Gradle – build and dependency management

SSL/TLS (prepared) – secure communication (configurable)

✨ Features
Shared Features

Login / authentication

View employee profile

Update profile details

Apply for leave

View leave history

Change password

HR Features

Register employees

Search employee profiles

Approve or reject leave requests

Generate yearly employee reports

Terminate employee records

📁 Project Structure
src/main/java/
├── common/        # Shared interfaces, models, utilities
├── server/        # RMI server logic and repositories
├── client/        # Employee & HR clients (JavaFX)
└── resources/     # SSL and configuration files

database/
├── hrm_schema.sql
└── hrm_test_data.sql

🔐 Security Design

SSL/TLS support is prepared using:

SSLConfig (server-side)

SSLClientConfig (client-side)

Centralized SSL configuration allows secure RMI communication once enabled.

Security is modular and can be activated without refactoring the system.

🔁 Fault Tolerance (Replication)

A Backup RMI Server is implemented as a secondary service instance.

Runs independently on a different port.

Can be used for manual or automatic failover.

Demonstrates replication concepts without unnecessary complexity.
