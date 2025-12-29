-- Family Details Duplicate Cleanup Script
-- This script removes duplicate family detail records, keeping only the latest (highest familyId) for each employee

-- 1. First, let's see what duplicates exist
SELECT employeeId, COUNT(*) as duplicate_count, 
       STRING_AGG(familyId::text, ', ') as family_ids,
       STRING_AGG(name, ' | ') as names
FROM family_details 
GROUP BY employeeId 
HAVING COUNT(*) > 1
ORDER BY employeeId;

-- 2. Show which records will be kept (highest familyId per employee)
SELECT employeeId, MAX(familyId) as keep_familyId
FROM family_details 
GROUP BY employeeId;

-- 3. Delete duplicates (keeps only the latest record per employee)
DELETE FROM family_details 
WHERE familyId NOT IN (
    SELECT MAX(familyId) 
    FROM family_details 
    GROUP BY employeeId
);

-- 4. Verify cleanup - should return no results
SELECT employeeId, COUNT(*) as record_count
FROM family_details 
GROUP BY employeeId 
HAVING COUNT(*) > 1;

-- 5. Show final state
SELECT familyId, employeeId, name, relationship, contact
FROM family_details 
ORDER BY employeeId, familyId;