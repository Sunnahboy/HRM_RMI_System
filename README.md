# HRM RMI System 📌

## Project Overview
The HRM RMI System is a distributed Human Resource Management application built using **Java RMI**, **JavaFX**, and **PostgreSQL**. It demonstrates a client–server architecture where Employee and HR clients communicate securely with a centralized server to manage employee data, leave applications, and reporting.

The system is designed with clear separation of concerns, scalability, and **fault tolerance** in mind, following best practices for distributed systems. Key features include **SSL-secured communication**, **automatic failover** between primary and backup servers, and a robust proxy pattern for high availability.

---

## 🏗️ System Architecture

The system consists of four main layers:

### 1. Client Layer (JavaFX)
- **Employee Client** - JavaFX GUI for employee operations
- **HR Client** - JavaFX GUI for HR management operations
- **HRMServiceProxy** - Fault-tolerant proxy with automatic failover
- **Controllers** - Bridge between GUI and RMI services

### 2. RMI Communication Layer
- **HRMService Interface** - Remote interface defining all HR operations
- **SSL/TLS Security** - Encrypted communication between clients and servers
- **RMI Registry** - Service discovery and binding

### 3. Server Layer
- **HRMServiceImpl** - Core business logic implementation
- **Repository Pattern** - Data access layer (Employee, Leave, Family, Report)
- **Backup Server** - Secondary server for fault tolerance
- **SSL Configuration** - Server-side SSL/TLS setup

### 4. Database Layer
- **PostgreSQL** - Relational database for persistent storage
- **Repository Pattern** - Clean data access abstraction
- **Connection Pooling** - Efficient database connections

> **🔄 Fault Tolerance**: The system includes a backup RMI server and intelligent proxy that automatically switches between servers upon failure, ensuring continuous service availability.

---

## 🧩 Key Technologies Used
* **Java 17** - Programming language
* **Java RMI** – Distributed communication
* **JavaFX** – Graphical user interface
* **PostgreSQL** – Relational database
* **Gradle** – Build and dependency management
* **SSL/TLS** – Secure communication
* **JUnit 5** – Unit testing framework

---

## ✨ Features

### Shared Features
* Login / Authentication
* View/Update employee profile
* Apply for leave & view leave history
* Change password
* Family details management

### HR Features
* Register and Terminate employees
* Search employee profiles
* Approve or reject leave requests
* Generate yearly employee reports
* Update employee status and salary

### System Features
* **🔒 SSL-Secured Communication** - All RMI calls encrypted
* **🔄 Automatic Failover** - Seamless switching between servers
* **📊 Health Monitoring** - Real-time server availability checks
* **🛡️ Fault Tolerance** - Backup server for high availability

---

## 📁 Complete Project Structure
```
src/main/java/
├── common/                    # Shared interfaces and models
│   ├── HRMService.java       # RMI remote interface
│   └── model/                # Data models (Employee, Leave, etc.)
│
├── client/                    # Client-side code
│   ├── EmployeeClient.java   # Employee application entry point
│   ├── HRClient.java         # HR application entry point
│   ├── HRMServiceProxy.java  # Fault-tolerant proxy
│   ├── controller/           # Business logic controllers
│   ├── gui/                  # JavaFX user interfaces
│   └── SSLClientConfig.java # Client SSL configuration
│
├── server/                    # Server-side code
│   ├── HRMServer.java        # Primary server entry point
│   ├── HRMServiceImpl.java   # Service implementation
│   ├── replication/          # Backup server implementation
│   ├── repository/           # Data access layer
│   └── SSLConfig.java        # Server SSL configuration
│
└── util/                     # Utility classes
    ├── Config.java           # Configuration management
    ├── DBConnection.java     # Database utilities
    └── SSLConnectionTest.java # SSL testing tools

database/
├── hrm_schema.sql            # Database schema
├── hrm_test_data.sql         # Test data
└── cleanup_family_duplicates.sql

resources/
├── application.properties     # Application configuration
├── security/                  # SSL certificates and keystores
│   ├── server.keystore
│   ├── server.cer
│   └── client.truststore
└── log4j.properties          # Logging configuration

tests/
├── ChangePasswordTest.java   # Unit tests
└── DatabaseConnectionTest.java # Database connectivity tests
```

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java 17** or higher
- **PostgreSQL 14+**
- **Gradle** (included wrapper available)
- **Windows/Linux/macOS**

### 1. Database Setup

#### Install PostgreSQL
```bash
# Windows - Download from postgresql.org
# Ubuntu/Debian
sudo apt update
sudo apt install postgresql postgresql-contrib

# macOS with Homebrew
brew install postgresql
brew services start postgresql
```

#### Create Database and User
```sql
-- Connect to PostgreSQL as superuser
psql -U postgres

-- Create database
CREATE DATABASE HRM_Service;

-- Create user (optional - uses postgres user by default)
CREATE USER hrm_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE HRM_Service TO hrm_user;
```

#### Load Schema and Test Data
```bash
# Navigate to project directory
cd HRM_RMI_System

# Load schema
psql -U postgres -d HRM_Service -f database/hrm_schema.sql

# Load test data
psql -U postgres -d HRM_Service -f database/hrm_test_data.sql

# Verify tables created
psql -U postgres -d HRM_Service -c "\dt"
```

### 2. Environment Configuration

#### Create .env file
```bash
# Copy example and edit
cp .env.example .env

# Edit .env file with your database credentials
MY_DB_USER=postgres
MY_DB_PASS=your_database_password
HRM_SERVER_IP=localhost  # or your server IP
```

#### Alternative: Environment Variables
```bash
# Windows (PowerShell)
$env:MY_DB_USER = "postgres"
$env:MY_DB_PASS = "your_password"
$env:HRM_SERVER_IP = "localhost"

# Linux/macOS (Bash)
export MY_DB_USER=postgres
export MY_DB_PASS=your_password
export HRM_SERVER_IP=localhost
```

### 3. Build the Project
```bash
# Using Gradle wrapper (recommended)
./gradlew build

# Or using system Gradle
gradle build
```

### 4. Start the Servers

#### Start Primary RMI Server
```bash
./gradlew run --args="server"
```
Expected output:
```
=== Database Configuration Status ===
Using config property: MY_DB_USER = [PROTECTED]
Using config property: MY_DB_PASS = [PROTECTED]
HRM_SERVER_IP: localhost
=====================================
Keystore found: server.keystore path...
SSL Enabled
HRM RMI Server bound as HRMService on port 54321 at IP: localhost
```

#### Start Backup Server (Optional)
```bash
# In a new terminal
./gradlew run --args="backup"
```

### 5. Run the Applications

#### Start HR Client
```bash
./gradlew run --args="hr"
```

#### Start Employee Client
```bash
./gradlew run --args="employee"
```

---

## 🔧 Configuration Details

### Database Configuration
The system supports multiple configuration sources (priority order):

1. **Environment Variables** (Highest Priority)
   - `MY_DB_USER`
   - `MY_DB_PASS`
   - `HRM_SERVER_IP`

2. **.env File** (Development)
   - Automatically loaded for local development

3. **application.properties** (Deployment)
   - `DB_URL`, `DB_USER`, `DB_PASSWORD`
   - `RMI_HOST`, `RMI_PORT`

4. **Hardcoded Defaults** (Fallback)
   - `postgres` / `password`
   - `localhost` / `54321`

### SSL Configuration
SSL certificates are located in `src/main/resources/security/`:
- **server.keystore** - Server private key and certificate
- **client.truststore** - Client trusted certificates
- **server.cer** - Server certificate for distribution

### RMI Configuration
- **Primary Server**: `localhost:54321`
- **Backup Server**: `localhost:54322`
- **Service Names**: `HRMService`, `HRMServiceBackup`

---

## 🔄 Fault Tolerance & Failover

### How It Works
1. **Client Connection**: Clients connect via `HRMServiceProxy`
2. **Health Monitoring**: Proxy continuously checks server availability
3. **Automatic Failover**: On server failure, proxy switches to backup
4. **Transparent Operation**: Client applications unaware of server switching

### Failover Sequence
```
Client Request → Primary Server (Available) → Success ✅
Client Request → Primary Server (Failed) → Switch to Backup → Success ✅
Client Request → Both Servers Failed → Error 🚫
```

### Monitoring Output
```
🔄 Attempt 1: Using PRIMARY server
✅ Operation successful on PRIMARY server

🔄 Attempt 1: Using PRIMARY server
❌ PRIMARY server failed on attempt 1: Connection refused
🔄 Attempting to switch to other server...
✅ Successfully switched to BACKUP server, retrying operation...
✅ Operation successful on BACKUP server
```

---

## 🧪 Testing

### Database Connectivity Test
```bash
# Test database connection
javac -cp ".:build/libs/*" src/main/java/com/hrmrmi/common/util/DatabaseConnectionTest.java
java -cp ".:build/libs/*" com.hrmrmi.common.util.DatabaseConnectionTest
```

### SSL Connection Test
```bash
# Test SSL connectivity
javac -cp ".:build/libs/*" src/main/java/com/hrmrmi/common/util/SSLConnectionTest.java
java -cp ".:build/libs/*" com.hrmrmi.common.util.SSLConnectionTest
```

### Unit Tests
```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests ChangePasswordTest
```

---

## 👤 Default Login Credentials

### Test Accounts
After loading test data, use these credentials:

**HR Administrator:**
- Email: `admin@admin.com`
- Password: `admin123`

**Employee:**
- Email: `abdi@gmail.com`
- Password: `password123`

---

## 🛠️ Troubleshooting

### Common Issues

#### 1. Database Connection Failed
```
FATAL: password authentication failed for user "postgres"
```
**Solutions:**
- Verify PostgreSQL is running
- Check credentials in `.env` file
- Ensure `pg_hba.conf` allows password authentication
- Test with: `psql -U postgres -d HRM_Service`

#### 2. SSL Handshake Failure
```
javax.net.ssl.SSLHandshakeException
```
**Solutions:**
- Verify SSL certificates exist in `security/` directory
- Check keystore password (default: `changeit`)
- Ensure client truststore contains server certificate

#### 3. RMI Server Not Found
```
java.rmi.ConnectException
```
**Solutions:**
- Ensure RMI server is running on correct port
- Check firewall settings
- Verify `RMI_HOST` configuration matches server IP

#### 4. Port Already in Use
```
Address already in use
```
**Solutions:**
- Change ports in `application.properties`
- Kill existing processes: `lsof -ti:54321 | xargs kill`

### Debug Tools
- **Config.printConfigStatus()** - Shows configuration loading
- **DatabaseConnectionTest** - Standalone database connectivity test
- **SSLConnectionTest** - SSL handshake verification
- **SystemDiagnostics** - Complete system health check

---

## 📋 Development Guide

### Adding New Features

1. **Define Interface**: Add method to `HRMService` interface
2. **Implement Server**: Add implementation to `HRMServiceImpl`
3. **Add Repository**: Create repository method if needed
4. **Update Proxy**: Add method to `HRMServiceProxy`
5. **Update Controllers**: Add business logic
6. **Update GUI**: Add user interface elements

### Code Structure Patterns

**Repository Pattern:**
```java
public interface EmployeeRepository {
    Employee findById(String id);
    List<Employee> findAll();
    Employee save(Employee employee);
}
```

**Proxy Pattern:**
```java
public class HRMServiceProxy implements HRMService {
    private <T> T executeWithFailover(FailoverOperation<T> operation) {
        // Automatic failover logic
    }
}
```

**Controller Pattern:**
```java
public class EmployeeController {
    private HRMServiceProxy service;
    // Business logic and validation
}
```

---

## 🔐 Security Features

- **SSL/TLS Encryption**: All RMI communication encrypted
- **Certificate-Based Authentication**: Mutual SSL authentication
- **Secure Password Storage**: Database-level password hashing
- **Input Validation**: Server-side validation for all inputs
- **Connection Pooling**: Secure database connection management

---

## 📈 Performance Features

- **Connection Pooling**: Efficient database connections
- **Lazy Loading**: On-demand data loading
- **Caching**: Configurable caching strategies
- **Health Monitoring**: Real-time performance monitoring
- **Resource Management**: Proper cleanup of RMI resources

---

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Standards
- Follow Java naming conventions
- Add unit tests for new features
- Update documentation for API changes
- Use meaningful commit messages

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 📞 Support

For issues and questions:
1. Check the troubleshooting section above
2. Review the documentation in `database/README_DB.txt`
3. Check test files for usage examples
4. Examine the `DATABASE_SETUP.md` guide

---

## 🏆 Acknowledgments

- Java RMI documentation and best practices
- PostgreSQL community for excellent database tools
- JavaFX team for modern GUI framework
- SSL/TLS implementation guidelines

---

**Built with ❤️ for distributed systems education and enterprise HR management**
t e s t  
 