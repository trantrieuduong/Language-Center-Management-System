CREATE
DATABASE IF NOT EXISTS LanguageCenterManagementSystem;
USE
LanguageCenterManagementSystem;

-- USER TABLES
CREATE TABLE student
(
    student_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name     VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    gender        VARCHAR(20),
    phone         VARCHAR(20),
    email         VARCHAR(255),
    address       VARCHAR(255),
    registered_at DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME,
    status        VARCHAR(50) DEFAULT 'ACTIVE'
);

CREATE TABLE teacher
(
    teacher_id CHAR(36) PRIMARY KEY,
    full_name  VARCHAR(255) NOT NULL,
    phone      VARCHAR(20),
    email      VARCHAR(255),
    specialty  VARCHAR(100),
    hire_date  DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    status     VARCHAR(50) DEFAULT 'ACTIVE'
);

-- ACADEMIC TABLES
CREATE TABLE course
(
    course_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(255) NOT NULL,
    description TEXT,
    level       VARCHAR(50),
    duration    INT,
    fee         DECIMAL(18, 2) DEFAULT 0.00,
    status      VARCHAR(50)    DEFAULT 'ACTIVE',
    created_at  DATETIME       DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME
);

CREATE TABLE room
(
    room_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_name VARCHAR(100),
    capacity  INT,
    status    VARCHAR(50)
);

CREATE TABLE class
(
    class_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_name  VARCHAR(255),
    course_id   BIGINT   NOT NULL,
    teacher_id  CHAR(36) NOT NULL,
    start_date  DATE,
    end_date    DATE,
    max_student INT DEFAULT 0,
    room_id     BIGINT   NOT NULL,
    status      VARCHAR(50),

    CONSTRAINT fk_class_course
        FOREIGN KEY (course_id) REFERENCES course (course_id),

    CONSTRAINT fk_class_teacher
        FOREIGN KEY (teacher_id) REFERENCES teacher (teacher_id),

    CONSTRAINT fk_class_room
        FOREIGN KEY (room_id) REFERENCES room (room_id)
);

CREATE TABLE enrollment
(
    enrollment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id    BIGINT NOT NULL,
    class_id      BIGINT NOT NULL,
    enrolled_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME,

    CONSTRAINT fk_enrollment_student
        FOREIGN KEY (student_id) REFERENCES student (student_id),

    CONSTRAINT fk_enrollment_class
        FOREIGN KEY (class_id) REFERENCES class (class_id)
);

CREATE TABLE result
(
    result_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    enrollment_id BIGINT NOT NULL,
    score         DECIMAL(5, 2),
    grade         VARCHAR(10),
    evaluated_at  DATETIME,

    CONSTRAINT fk_result_enrollment
        FOREIGN KEY (enrollment_id) REFERENCES enrollment (enrollment_id)
);

-- OPERATION TABLES
CREATE TABLE schedule
(
    schedule_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_id      BIGINT NOT NULL,
    schedule_date DATE,
    start_time    TIME,
    end_time      TIME,

    CONSTRAINT fk_schedule_class
        FOREIGN KEY (class_id) REFERENCES class (class_id)
);

CREATE TABLE attendance
(
    attendance_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id    BIGINT NOT NULL,
    schedule_id   BIGINT NOT NULL,
    status        VARCHAR(50),
    note          VARCHAR(255),

    CONSTRAINT fk_attendance_student
        FOREIGN KEY (student_id) REFERENCES student (student_id),

    CONSTRAINT fk_attendance_schedule
        FOREIGN KEY (schedule_id) REFERENCES schedule (schedule_id)
);

-- FINANCIAL TABLES
CREATE TABLE invoice
(
    invoice_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id   BIGINT NOT NULL,
    total_amount DECIMAL(18, 2),
    status       VARCHAR(50),
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_invoice_student
        FOREIGN KEY (student_id) REFERENCES student (student_id)
);

CREATE TABLE payment
(
    payment_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_id     BIGINT NOT NULL,
    amount         DECIMAL(18, 2),
    payment_method VARCHAR(50),
    status         VARCHAR(50),
    payment_date   DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_invoice
        FOREIGN KEY (invoice_id) REFERENCES invoice (invoice_id)
);