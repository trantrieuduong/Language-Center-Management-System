CREATE DATABASE IF NOT EXISTS languagecentermanagementsystem;
USE languagecentermanagementsystem;

-- ================================================
-- USER MODULE - Bảng người dùng và tài khoản
-- Bảng Student (Học viên)
CREATE TABLE IF NOT EXISTS student (
    student_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    phone VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    registered_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    INDEX idx_email (email),
    INDEX idx_phone (phone)
);

-- Bảng Teacher (Giáo viên)
CREATE TABLE IF NOT EXISTS teacher (
    teacher_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(255),
    specialty ENUM('IELTS', 'TOEIC', 'COMMUNICATION'),
    hire_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    INDEX idx_email (email),
    INDEX idx_specialty (specialty)
);

-- Bảng Staff (Nhân viên)
CREATE TABLE IF NOT EXISTS staff (
    staff_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(255) NOT NULL,
    role ENUM('ACCOUNTANT', 'CONSULTANT'),
    phone VARCHAR(20),
    email VARCHAR(255),
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- Bảng UserAccount (Tài khoản đăng nhập)
CREATE TABLE IF NOT EXISTS user_account (
    user_id CHAR(36) PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'TEACHER', 'STUDENT', 'STAFF') DEFAULT 'STUDENT',
    student_id BIGINT UNIQUE,
    teacher_id BIGINT UNIQUE,
    staff_id BIGINT UNIQUE,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE SET NULL,
    FOREIGN KEY (teacher_id) REFERENCES teacher(teacher_id) ON DELETE SET NULL,
    FOREIGN KEY (staff_id) REFERENCES staff(staff_id) ON DELETE SET NULL,
    INDEX idx_username (username),
    INDEX idx_role (role)
);

-- ================================================
-- ACADEMIC MODULE - Bảng học tập
-- Bảng Course (Khóa học)
CREATE TABLE IF NOT EXISTS course (
    course_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_name VARCHAR(255) NOT NULL,
    description TEXT,
    level ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED'),
    duration INT NOT NULL,
    fee DECIMAL(18, 2) DEFAULT 0.00,
    status ENUM('ACTIVE', 'INACTIVE', 'DRAFT') DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_level (level),
    INDEX idx_status (status)
);

-- Bảng Room (Phòng học)
CREATE TABLE IF NOT EXISTS room (
    room_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_name VARCHAR(255) NOT NULL UNIQUE,
    capacity INT NOT NULL DEFAULT 0,
    location VARCHAR(255),
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status)
);

-- Bảng Class (Lớp học)
CREATE TABLE IF NOT EXISTS class (
    class_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_name VARCHAR(255),
    course_id BIGINT NOT NULL,
    teacher_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    max_student INT DEFAULT 0,
    room_id BIGINT NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
    FOREIGN KEY (course_id) REFERENCES course(course_id),
    FOREIGN KEY (teacher_id) REFERENCES teacher(teacher_id),
    FOREIGN KEY (room_id) REFERENCES room(room_id),
    INDEX idx_course (course_id),
    INDEX idx_teacher (teacher_id),
    INDEX idx_status (status),
    INDEX idx_start_date (start_date)
);

-- Bảng Enrollment (Đăng ký lớp học)
CREATE TABLE IF NOT EXISTS enrollment (
    enrollment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    enrolled_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (class_id) REFERENCES class(class_id),
    UNIQUE KEY unq_enrollment (student_id, class_id),
    INDEX idx_student (student_id),
    INDEX idx_class (class_id)
);

-- Bảng Result (Kết quả học tập)
CREATE TABLE IF NOT EXISTS result (
    result_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    score DOUBLE,
    comment TEXT,
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (class_id) REFERENCES class(class_id),
    UNIQUE KEY unq_result (student_id, class_id),
    INDEX idx_student (student_id),
    INDEX idx_class (class_id)
);

-- ================================================
-- OPERATION MODULE - Bảng điểm danh và lịch học
-- Bảng Schedule (Lịch học)
CREATE TABLE IF NOT EXISTS schedule (
    schedule_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    schedule_date DATE NOT NULL,
    start_time TIME,
    end_time TIME,
    FOREIGN KEY (class_id) REFERENCES class(class_id),
    FOREIGN KEY (room_id) REFERENCES room(room_id),
    UNIQUE KEY unq_schedule (class_id, room_id, schedule_date, start_time, end_time),
    INDEX idx_class (class_id),
    INDEX idx_date (schedule_date)
);

-- Bảng Attendance (Điểm danh)
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    status ENUM('PRESENT', 'EXCUSED', 'ABSENT', 'LATE'),
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (class_id) REFERENCES class(class_id),
    UNIQUE KEY unq_attendance (class_id, student_id),
    INDEX idx_student (student_id),
    INDEX idx_class (class_id),
    INDEX idx_status (status)
);

-- ================================================
-- FINANCIAL MODULE - Bảng tài chính
-- Bảng Payment (Thanh toán)
CREATE TABLE IF NOT EXISTS payment (
    payment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    amount DECIMAL(18, 2) DEFAULT 0.00,
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('CASH', 'BANK', 'MOMO'),
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'),
    INDEX idx_invoice (invoice_id),
    INDEX idx_status (status)
);

-- Bảng Invoice (Hóa đơn)
CREATE TABLE IF NOT EXISTS invoice (
    invoice_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    payment_id BIGINT NOT NULL,
    total_amount DECIMAL(18, 2),
    issued_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('DRAFT', 'ISSUED', 'PAID', 'CANCELED'),
    FOREIGN KEY (student_id) REFERENCES student(student_id),
    FOREIGN KEY (payment_id) REFERENCES payment(payment_id),
    INDEX idx_student (student_id),
    INDEX idx_status (status)
);

ALTER TABLE payment ADD CONSTRAINT fk_payment_invoice 
    FOREIGN KEY (invoice_id) REFERENCES invoice(invoice_id);

CREATE INDEX idx_class_date ON enrollment(enrolled_at);
CREATE INDEX idx_attendance_date ON attendance(date);
CREATE INDEX idx_schedule_class_date ON schedule(class_id, schedule_date);