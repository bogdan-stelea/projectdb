-- Policy for session tracking
CREATE AUDIT POLICY session_audit_policy
ACTIONS LOGON, LOGOFF;

-- Policy for students table operations
CREATE AUDIT POLICY students_audit_policy
ACTIONS SELECT, INSERT, UPDATE, DELETE
ON PROJECTDB.STUDENTS;

-- Policy for instructors table operations
CREATE AUDIT POLICY instructors_audit_policy
ACTIONS SELECT, INSERT, UPDATE, DELETE
ON PROJECTDB.INSTRUCTORS;

-- Policy for enrollments table operations
CREATE AUDIT POLICY enrollments_audit_policy
ACTIONS SELECT, INSERT, UPDATE, DELETE
ON PROJECTDB.ENROLLMENTS;


-- Enable session tracking
AUDIT POLICY session_audit_policy;

AUDIT POLICY students_audit_policy;
AUDIT POLICY instructors_audit_policy;
AUDIT POLICY enrollments_audit_policy;

-- Check current audit policies
SELECT policy_name
FROM audit_unified_policies
WHERE policy_name IN ('SESSION_AUDIT_POLICY', 'STUDENTS_AUDIT_POLICY', 'INSTRUCTORS_AUDIT_POLICY', 'ENROLLMENTS_AUDIT_POLICY');

-- View recent audit events
SELECT event_timestamp, dbusername, action_name, object_name, unified_audit_policies
FROM unified_audit_trail
WHERE event_timestamp > SYSTIMESTAMP - INTERVAL '1' DAY
ORDER BY event_timestamp DESC;

-- View logon/logoff events
SELECT event_timestamp, dbusername, action_name, client_program_name
FROM unified_audit_trail
WHERE action_name IN ('LOGON', 'LOGOFF')
AND event_timestamp > SYSTIMESTAMP - INTERVAL '1' DAY
ORDER BY event_timestamp DESC;

-- View table access events
SELECT event_timestamp, dbusername, action_name, object_name, sql_text
FROM unified_audit_trail
WHERE object_name IN ('STUDENTS', 'INSTRUCTORS', 'ENROLLMENTS')
AND event_timestamp > SYSTIMESTAMP - INTERVAL '1' DAY
ORDER BY event_timestamp DESC;

COMMIT;
