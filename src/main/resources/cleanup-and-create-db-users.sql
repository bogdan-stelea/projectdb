-- ========================================
-- Clean up old users and create new database users
-- Run this script as SYS or SYSTEM user
-- ========================================

-- 1. DROP EXISTING PROJ_* USERS
-- ========================================
DROP USER PROJ_ADMIN CASCADE;
DROP USER PROJ_INSTRUCTOR CASCADE;
DROP USER PROJ_STUDENT CASCADE;
DROP USER PROJ_READONLY CASCADE;

-- 2. CREATE NEW DATABASE USERS
-- ========================================
CREATE USER db_admin_user IDENTIFIED BY "AdminPass123!";
CREATE USER db_instructor_user IDENTIFIED BY "InstructorPass123!";  
CREATE USER db_student_user IDENTIFIED BY "StudentPass123!";

-- 3. GRANT BASIC CONNECTION PRIVILEGES
-- ========================================
GRANT CREATE SESSION TO db_admin_user;
GRANT CREATE SESSION TO db_instructor_user;
GRANT CREATE SESSION TO db_student_user;

-- 4. ASSIGN ROLES TO USERS
-- ========================================
GRANT db_admin_role TO db_admin_user;
GRANT db_instructor_role TO db_instructor_user;
GRANT db_student_role TO db_student_user;

-- 5. SET DEFAULT ROLES
-- ========================================
ALTER USER db_admin_user DEFAULT ROLE db_admin_role;
ALTER USER db_instructor_user DEFAULT ROLE db_instructor_role;
ALTER USER db_student_user DEFAULT ROLE db_student_role;

-- 6. GRANT ACCESS TO TABLES (since PROJECTDB owns them)
-- ========================================
-- Admin user gets all privileges
GRANT SELECT, INSERT, UPDATE, DELETE ON projectdb.students TO db_admin_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON projectdb.instructors TO db_admin_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON projectdb.courses TO db_admin_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON projectdb.enrollments TO db_admin_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON projectdb.simple_audit_log TO db_admin_user;

-- Instructor user gets read/write on students, courses, enrollments
GRANT SELECT, INSERT, UPDATE, DELETE ON projectdb.students TO db_instructor_user;
GRANT SELECT ON projectdb.instructors TO db_instructor_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON projectdb.courses TO db_instructor_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON projectdb.enrollments TO db_instructor_user;
GRANT SELECT ON projectdb.simple_audit_log TO db_instructor_user;

-- Student user gets read-only access
GRANT SELECT ON projectdb.students TO db_student_user;
GRANT SELECT ON projectdb.instructors TO db_student_user;
GRANT SELECT ON projectdb.courses TO db_student_user;
GRANT SELECT ON projectdb.enrollments TO db_student_user;
GRANT SELECT ON projectdb.simple_audit_log TO db_student_user;

COMMIT;