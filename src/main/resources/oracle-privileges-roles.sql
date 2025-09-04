CREATE ROLE db_admin_role;
CREATE ROLE db_instructor_role;
CREATE ROLE db_student_role;
CREATE ROLE db_readonly_role;

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

GRANT SELECT, INSERT, UPDATE, DELETE ON PROJECTDB.students TO db_admin_role;

-- Instructor: Can view and update student records
GRANT SELECT, UPDATE ON PROJECTDB.students TO db_instructor_role;

-- Student: Can only view their own data (enforced by views)
GRANT SELECT ON PROJECTDB.students TO db_student_role;

-- Read-only: Can only view student data
GRANT SELECT ON PROJECTDB.students TO db_readonly_role;

GRANT SELECT, INSERT, UPDATE, DELETE ON PROJECTDB.instructors TO db_admin_role;

-- Instructor: Can view instructor information
GRANT SELECT ON PROJECTDB.instructors TO db_instructor_role;

-- Student: Can view instructor information (contact details)
GRANT SELECT ON PROJECTDB.instructors TO db_student_role;

-- Read-only: Can view instructor data
GRANT SELECT ON PROJECTDB.instructors TO db_readonly_role;

GRANT SELECT, INSERT, UPDATE, DELETE ON PROJECTDB.enrollments TO db_admin_role;

-- Instructor: Can view and update enrollments (for grading)
GRANT SELECT, UPDATE ON PROJECTDB.enrollments TO db_instructor_role;

-- Student: Can view their own enrollments
GRANT SELECT ON PROJECTDB.enrollments TO db_student_role;

-- Read-only: Can view enrollment data
GRANT SELECT ON PROJECTDB.enrollments TO db_readonly_role;

GRANT SELECT, INSERT, UPDATE, DELETE ON PROJECTDB.simple_audit_log TO db_admin_role;

-- Instructor: Can view audit logs for their actions
GRANT SELECT ON PROJECTDB.simple_audit_log TO db_instructor_role;

GRANT db_student_role TO db_instructor_role;

-- Grant instructor role to admin role (hierarchy)  
GRANT db_instructor_role TO db_admin_role;

COMMIT;