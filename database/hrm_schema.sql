-- 1. EMPLOYEES TABLE
DROP TABLE IF EXISTS employees CASCADE;
CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    department VARCHAR(50),
    ic_passport_num VARCHAR(50) NOT NULL,
    position VARCHAR(50),
    leaveBalance INT DEFAULT 20,
    salary DOUBLE PRECISION,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- 2. LEAVE TABLE
DROP TABLE IF EXISTS leaves CASCADE;
CREATE TABLE leaves (
    leaveId SERIAL PRIMARY KEY,
    employeeId INT REFERENCES employees(id) ON DELETE CASCADE,
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    reason TEXT,
    totalDays INT DEFAULT 0
);

-- 3. FAMILY_DETAILS TABLE
DROP TABLE IF EXISTS family_details CASCADE;
CREATE TABLE family_details (
    familyId SERIAL PRIMARY KEY,
    employeeId INT REFERENCES employees(id) ON DELETE CASCADE,
    name VARCHAR(100),
    relationship VARCHAR(50),
    contact VARCHAR(20)
);

-- 4. INSERT ADMIN USER (So you can log in immediately)
INSERT INTO employees (firstName, lastName, email, department, ic_passport_num, position, password, role, salary)
VALUES ('HR', 'Admin', 'HR.Admin@bhel.com', 'HR', '001122334455', 'Manager', 'admin123', 'HR', 5000);

INSERT INTO employees (firstName, lastName, email, department, ic_passport_num, position, password, role, salary)
VALUES ('John', 'Doe', 'john.doe@bhel.com', 'IT', '900101145678', 'Software Developer', 'password123', 'EMPLOYEE', 3500);
