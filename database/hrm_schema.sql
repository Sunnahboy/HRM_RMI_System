-- 1. EMPLOYEES TABLE
CREATE TABLE employees (
    id SERIAL PRIMARY KEY,   --auto gen unique ids
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    department VARCHAR(50),
    ic_passport_num VARCHAR(50) NOT NULL,
    position VARCHAR(50),
    leaveBalance INT DEFAULT 20,  --initial 20 days
    salary DOUBLE PRECISION
);

-- 2. LEAVE TABLE
CREATE TABLE leaves (
    leaveId SERIAL PRIMARY KEY,
    employeeId INT REFERENCES employees(id) ON DELETE CASCADE, -- link to employee
    startDate DATE NOT NULL,
    endDate DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- moves from default Pending -> Approved/Rejected
    reason TEXT
);

-- 3. FAMILY_DETAILS TABLE
-- emergency contacts or family info
CREATE TABLE family_details (
    familyId SERIAL PRIMARY KEY,
    employeeId INT REFERENCES employees(id) ON DELETE CASCADE, -- link to employee
    name VARCHAR(100),
    relationship VARCHAR(50),
    contact VARCHAR(20)
);