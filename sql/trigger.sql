DELIMITER //

CREATE TRIGGER trg_insert_attendance_after_insert_schedule
    AFTER INSERT ON schedule
    FOR EACH ROW
BEGIN
    INSERT INTO attendance (student_id, class_id, schedule_id, status)
    SELECT
        e.student_id,
        NEW.class_id,
        NEW.schedule_id,
        'PRESENT'
    FROM enrollment e
    WHERE e.class_id = NEW.class_id
      AND NOT EXISTS (
        SELECT 1 FROM attendance a
        WHERE a.student_id = e.student_id
          AND a.schedule_id = NEW.schedule_id
    );
END //

DELIMITER ;