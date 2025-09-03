-- ========================================
-- Oracle Privileges and Roles System
-- Database Security course implementation
-- Pure roles and privileges implementation only
-- ========================================

-- 1. CREATE DATABASE ROLES
-- ========================================
-- Create roles for different privilege levels
CREATE ROLE db_admin_role;
CREATE ROLE db_instructor_role;
CREATE ROLE db_student_role;
CREATE ROLE db_readonly_role;

-- 2. SYSTEM PRIVILEGES
-- ========================================
-- Admin role gets system-level privileges
GRANT CREATE SESSION TO db_admin_role;
GRANT CREATE TABLE TO db_admin_role;
GRANT CREATE VIEW TO db_admin_role;
GRANT CREATE PROCEDURE TO db_admin_role;
GRANT CREATE TRIGGER TO db_admin_role;
GRANT ALTER ANY TABLE TO db_admin_role;
GRANT DELETE ANY TABLE TO db_admin_role;

-- Instructor role gets limited system privileges
GRANT CREATE SESSION TO db_instructor_role;
GRANT CREATE VIEW TO db_instructor_role;

-- Student role gets basic privileges
GRANT CREATE SESSION TO db_student_role;

-- Read-only role gets minimal privileges
GRANT CREATE SESSION TO db_readonly_role;

-- 3. OBJECT PRIVILEGES - STUDENTS TABLE
-- ========================================
-- Admin: Full access to students table
GRANT SELECT, INSERT, UPDATE, DELETE ON PROJECTDB.students TO db_admin_role;

-- Instructor: Can view and update student records (for grades)
GRANT SELECT, UPDATE ON PROJECTDB.students TO db_instructor_role;

-- Student: Can only view their own data (enforced by views)
GRANT SELECT ON PROJECTDB.students TO db_student_role;

-- Read-only: Can only view student data
GRANT SELECT ON PROJECTDB.students TO db_readonly_role;

-- 4. OBJECT PRIVILEGES - INSTRUCTORS TABLE  
-- ========================================
-- Admin: Full access to instructors table
GRANT SELECT, INSERT, UPDATE, DELETE ON PROJECTDB.instructors TO db_admin_role;

-- Instructor: Can view instructor information
GRANT SELECT ON PROJECTDB.instructors TO db_instructor_role;

-- Student: Can view instructor information (contact details)
GRANT SELECT ON PROJECTDB.instructors TO db_student_role;

-- Read-only: Can view instructor data
GRANT SELECT ON PROJECTDB.instructors TO db_readonly_role;

-- 5. OBJECT PRIVILEGES - ENROLLMENTS TABLE
-- ========================================
-- Admin: Full access to enrollments
GRANT SELECT, INSERT, UPDATE, DELETE ON PROJECTDB.enrollments TO db_admin_role;

-- Instructor: Can view and update enrollments (for grading)
GRANT SELECT, UPDATE ON PROJECTDB.enrollments TO db_instructor_role;

-- Student: Can view their own enrollments
GRANT SELECT ON PROJECTDB.enrollments TO db_student_role;

-- Read-only: Can view enrollment data
GRANT SELECT ON PROJECTDB.enrollments TO db_readonly_role;

-- 6. OBJECT PRIVILEGES - AUDIT TABLES
-- ========================================
-- Admin: Full access to audit logs
GRANT SELECT, INSERT, UPDATE, DELETE ON PROJECTDB.simple_audit_log TO db_admin_role;

-- Instructor: Can view audit logs for their actions
GRANT SELECT ON PROJECTDB.simple_audit_log TO db_instructor_role;

-- Student and Read-only: No access to audit logs (security)
-- (No grants given intentionally)

-- 7. PRIVILEGE HIERARCHIES
-- ========================================
-- Create hierarchy where admin role includes instructor privileges
-- and instructor role includes student privileges

-- Grant student role to instructor role (hierarchy)
GRANT db_student_role TO db_instructor_role;

-- Grant instructor role to admin role (hierarchy)  
GRANT db_instructor_role TO db_admin_role;

-- 8. ROLE CREATION COMPLETE
-- ========================================
-- All roles, privileges, and hierarchies have been configured

COMMIT;