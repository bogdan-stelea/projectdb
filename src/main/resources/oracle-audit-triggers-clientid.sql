CREATE OR REPLACE TRIGGER student_insert_audit_trigger
AFTER INSERT ON students
FOR EACH ROW
DECLARE
    v_app_user VARCHAR2(100);
BEGIN

    v_app_user := NVL(SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER'), USER);
    
    INSERT INTO simple_audit_log (table_name, action, user_name, what_changed)
    VALUES ('STUDENTS', 'INSERT', v_app_user, 'Inserted new student ID: ' || :NEW.id || ', Name: ' || :NEW.first_name || ' ' || :NEW.last_name);
END student_insert_audit_trigger;


CREATE OR REPLACE TRIGGER student_update_audit_trigger
AFTER UPDATE ON students
FOR EACH ROW
DECLARE
    v_app_user VARCHAR2(100);
BEGIN
    v_app_user := NVL(SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER'), USER);
    
    INSERT INTO simple_audit_log (table_name, action, user_name, what_changed)
    VALUES ('STUDENTS', 'UPDATE', v_app_user, 'Updated student ID: ' || :NEW.id);
END student_update_audit_trigger;


CREATE OR REPLACE TRIGGER student_delete_audit_trigger
AFTER DELETE ON students
FOR EACH ROW
DECLARE
    v_app_user VARCHAR2(100);
BEGIN
    v_app_user := NVL(SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER'), USER);
    
    INSERT INTO simple_audit_log (table_name, action, user_name, what_changed)
    VALUES ('STUDENTS', 'DELETE', v_app_user, 'Deleted student ID: ' || :OLD.id || ', Name: ' || :OLD.first_name || ' ' || :OLD.last_name);
END student_delete_audit_trigger;


CREATE OR REPLACE TRIGGER instructor_insert_audit_trigger
AFTER INSERT ON instructors
FOR EACH ROW
DECLARE
    v_app_user VARCHAR2(100);
BEGIN
    v_app_user := NVL(SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER'), USER);
    
    INSERT INTO simple_audit_log (table_name, action, user_name, what_changed)
    VALUES ('INSTRUCTORS', 'INSERT', v_app_user, 'Inserted new instructor ID: ' || :NEW.id || ', Name: ' || :NEW.first_name || ' ' || :NEW.last_name);
END instructor_insert_audit_trigger;


CREATE OR REPLACE TRIGGER instructor_update_audit_trigger
AFTER UPDATE ON instructors
FOR EACH ROW
DECLARE
    v_app_user VARCHAR2(100);
BEGIN
    v_app_user := NVL(SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER'), USER);
    
    INSERT INTO simple_audit_log (table_name, action, user_name, what_changed)
    VALUES ('INSTRUCTORS', 'UPDATE', v_app_user, 'Updated instructor ID: ' || :NEW.id);
END instructor_update_audit_trigger;


CREATE OR REPLACE TRIGGER instructor_delete_audit_trigger
AFTER DELETE ON instructors
FOR EACH ROW
DECLARE
    v_app_user VARCHAR2(100);
BEGIN
    v_app_user := NVL(SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER'), USER);
    
    INSERT INTO simple_audit_log (table_name, action, user_name, what_changed)
    VALUES ('INSTRUCTORS', 'DELETE', v_app_user, 'Deleted instructor ID: ' || :OLD.id || ', Name: ' || :OLD.first_name || ' ' || :OLD.last_name);
END instructor_delete_audit_trigger;


CREATE OR REPLACE TRIGGER enrollment_insert_audit_trigger
AFTER INSERT ON enrollments
FOR EACH ROW
DECLARE
    v_app_user VARCHAR2(100);
BEGIN
    v_app_user := NVL(SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER'), USER);
    
    INSERT INTO simple_audit_log (table_name, action, user_name, what_changed)
    VALUES ('ENROLLMENTS', 'INSERT', v_app_user, 'Inserted new enrollment ID: ' || :NEW.id || ', Student ID: ' || :NEW.student_id || ', Course ID: ' || :NEW.course_id);
END enrollment_insert_audit_trigger;


CREATE OR REPLACE TRIGGER enrollment_update_audit_trigger
AFTER UPDATE ON enrollments
FOR EACH ROW
DECLARE
    v_app_user VARCHAR2(100);
BEGIN
    v_app_user := NVL(SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER'), USER);
    
    INSERT INTO simple_audit_log (table_name, action, user_name, what_changed)
    VALUES ('ENROLLMENTS', 'UPDATE', v_app_user, 'Updated enrollment - Student ID: ' || :NEW.student_id);
END enrollment_update_audit_trigger;


CREATE OR REPLACE TRIGGER enrollment_delete_audit_trigger
AFTER DELETE ON enrollments
FOR EACH ROW
DECLARE
    v_app_user VARCHAR2(100);
BEGIN
    v_app_user := NVL(SYS_CONTEXT('USERENV', 'CLIENT_IDENTIFIER'), USER);
    
    INSERT INTO simple_audit_log (table_name, action, user_name, what_changed)
    VALUES ('ENROLLMENTS', 'DELETE', v_app_user, 'Deleted enrollment ID: ' || :OLD.id || ', Student ID: ' || :OLD.student_id || ', Course ID: ' || :OLD.course_id);
END enrollment_delete_audit_trigger;


COMMIT;