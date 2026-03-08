USE languagecentermanagementsystem;

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;
INSERT INTO student (full_name, date_of_birth, gender, phone, email, address, status)
VALUES ('Nguyễn Văn A', '2000-01-15', 'MALE', '0901234567', 'nguyena@email.com', '123 Đường Láng, Hà Nội', 'ACTIVE'),
       ('Trần Thị B', '2001-05-20', 'FEMALE', '0987654321', 'tranb@email.com', '456 Trần Phú, Hà Nội', 'ACTIVE'),
       ('Phạm Quốc C', '2000-08-10', 'MALE', '0912345678', 'phamc@email.com', '789 Nguyễn Huệ, TP.HCM', 'ACTIVE'),
       ('Lê Thị D', '2002-02-25', 'FEMALE', '0923456789', 'led@email.com', '101 Cách Mạng, Đà Nẵng', 'ACTIVE'),
       ('Võ Văn E', '2001-11-08', 'MALE', '0934567890', 'voe@email.com', '202 Hoàng Văn Thụ, Hà Nội', 'ACTIVE'),
       ('Đỗ Thị F', '2000-07-12', 'FEMALE', '0945678901', 'dof@email.com', '302 Phan Bội Châu, Huế', 'ACTIVE'),
       ('Hoàng Văn G', '2002-02-28', 'MALE', '0956789012', 'hoang.g@email.com', '404 Ngô Quyền, Hải Phòng', 'ACTIVE'),
       ('Bùi Thị H', '2001-09-14', 'FEMALE', '0967890123', 'buih@email.com', '505 Tô Hiến Thành, Hà Nội', 'ACTIVE'),
       ('Nơn Văn I', '2000-12-30', 'MALE', '0978901234', 'noni@email.com', '606 Đinh Tiên Hoàng, Hà Nội', 'ACTIVE'),
       ('Tạ Thị J', '2002-06-17', 'FEMALE', '0989012345', 'taj@email.com', '707 Thanh Lương, Kiên Giang', 'ACTIVE');

INSERT INTO teacher (full_name, phone, email, specialty, status)
VALUES ('Ngô Minh Tuấn', '0911111111', 'tuanngominH@email.com', 'IELTS', 'ACTIVE'),
       ('Phạm Thanh Hương', '0922222222', 'phamhuong@email.com', 'TOEIC', 'ACTIVE'),
       ('Trần Hải David', '0933333333', 'tranhai@email.com', 'COMMUNICATION', 'ACTIVE'),
       ('Lê Văn Mark', '0944444444', 'levan.mark@email.com', 'IELTS', 'ACTIVE'),
       ('Đặng Thị Hoa', '0955555555', 'danghoa@email.com', 'TOEIC', 'ACTIVE'),
       ('Vũ Quốc Việt', '0966666666', 'vuquocviet@email.com', 'COMMUNICATION', 'ACTIVE'),
       ('Nguyễn Thanh Tâm', '0977777777', 'nguyentam@email.com', 'IELTS', 'ACTIVE'),
       ('Hoàng Minh Nhật', '0988888888', 'hoangminhnhat@email.com', 'TOEIC', 'ACTIVE'),
       ('Lý Thanh Bình', '0999999999', 'lythanhbinh@email.com', 'COMMUNICATION', 'ACTIVE'),
       ('Mạc Anh Tuấn', '0900000000', 'machanhTuan@email.com', 'IELTS', 'ACTIVE');

INSERT INTO staff (full_name, role, phone, email, status)
VALUES ('Trương Thị Linh', 'ACCOUNTANT', '0811111111', 'linhtruong@email.com', 'ACTIVE'),
       ('Dương Văn Kỳ', 'CONSULTANT', '0822222222', 'duongky@email.com', 'ACTIVE'),
       ('Tạ Thị Ngọc', 'ACCOUNTANT', '0833333333', 'tangoc@email.com', 'ACTIVE'),
       ('Võ Minh Đức', 'CONSULTANT', '0844444444', 'vominh@email.com', 'ACTIVE'),
       ('Nông Thị Yến', 'ACCOUNTANT', '0855555555', 'nongyen@email.com', 'ACTIVE'),
       ('Tô Đăng Huy', 'CONSULTANT', '0866666666', 'tohuydang@email.com', 'ACTIVE'),
       ('Phan Thị Tuyết', 'ACCOUNTANT', '0877777777', 'phantuyet@email.com', 'ACTIVE'),
       ('Bế Văn Cường', 'CONSULTANT', '0888888888', 'becuong@email.com', 'ACTIVE'),
       ('Hà Thị Hương', 'ACCOUNTANT', '0899999999', 'hahuong@email.com', 'ACTIVE'),
       ('Lưu Văn Hạo', 'CONSULTANT', '0800000000', 'luuhao@email.com', 'ACTIVE');

INSERT INTO user_account (user_id, username, password_hash, role, student_id, teacher_id, staff_id)
VALUES ('550e8400-e29b-41d4-a716-446655440001', 'student001',
        '$2a$12$MJQ/McVoRniCmlGuaiItgO82x2j0ELVaFjEH5Fprg3hJiF7kk3aE2', 'STUDENT', 1, NULL, NULL),
       ('550e8400-e29b-41d4-a716-446655440002', 'student002',
        '$2a$12$MJQ/McVoRniCmlGuaiItgO82x2j0ELVaFjEH5Fprg3hJiF7kk3aE2', 'STUDENT', 2, NULL, NULL),
       ('550e8400-e29b-41d4-a716-446655440003', 'student003',
        '$2a$12$MJQ/McVoRniCmlGuaiItgO82x2j0ELVaFjEH5Fprg3hJiF7kk3aE2', 'STUDENT', 3, NULL, NULL),
       ('550e8400-e29b-41d4-a716-446655440004', 'teacher001',
        '$2a$12$MJQ/McVoRniCmlGuaiItgO82x2j0ELVaFjEH5Fprg3hJiF7kk3aE2', 'TEACHER', NULL, 1, NULL),
       ('550e8400-e29b-41d4-a716-446655440005', 'teacher002',
        '$2a$12$MJQ/McVoRniCmlGuaiItgO82x2j0ELVaFjEH5Fprg3hJiF7kk3aE2', 'TEACHER', NULL, 2, NULL),
       ('550e8400-e29b-41d4-a716-446655440006', 'teacher003',
        '$2a$12$MJQ/McVoRniCmlGuaiItgO82x2j0ELVaFjEH5Fprg3hJiF7kk3aE2', 'TEACHER', NULL, 3, NULL),
       ('550e8400-e29b-41d4-a716-446655440007', 'staff001',
        '$2a$12$MJQ/McVoRniCmlGuaiItgO82x2j0ELVaFjEH5Fprg3hJiF7kk3aE2', 'STAFF', NULL, NULL, 1),
       ('550e8400-e29b-41d4-a716-446655440008', 'staff002',
        '$2a$12$MJQ/McVoRniCmlGuaiItgO82x2j0ELVaFjEH5Fprg3hJiF7kk3aE2', 'STAFF', NULL, NULL, 2),
       ('550e8400-e29b-41d4-a716-446655440009', 'admin001',
        '$2a$12$MJQ/McVoRniCmlGuaiItgO82x2j0ELVaFjEH5Fprg3hJiF7kk3aE2', 'ADMIN', NULL, NULL, NULL),
       ('550e8400-e29b-41d4-a716-446655440010', 'staff003',
        '$2a$12$MJQ/McVoRniCmlGuaiItgO82x2j0ELVaFjEH5Fprg3hJiF7kk3aE2', 'STAFF', NULL, NULL, 3);

INSERT INTO course (course_name, description, level, duration, fee, status)
VALUES ('IELTS Beginner', 'Khóa IELTS cơ bản cho người mới bắt đầu', 'BEGINNER', 20, 5000000.00, 'ACTIVE'),
       ('IELTS Intermediate', 'Khóa IELTS trung cấp nâng cao kỹ năng', 'INTERMEDIATE', 24, 6000000.00, 'ACTIVE'),
       ('IELTS Advanced', 'Khóa IELTS nâng cao chuẩn bị cho kì thi', 'ADVANCED', 28, 7000000.00, 'ACTIVE'),
       ('TOEIC Beginner', 'Khóa TOEIC cơ bản cho người mới', 'BEGINNER', 18, 4500000.00, 'ACTIVE'),
       ('TOEIC Intermediate', 'Khóa TOEIC trung cấp phát triển vốn từ', 'INTERMEDIATE', 22, 5500000.00, 'ACTIVE'),
       ('TOEIC Advanced', 'Khóa TOEIC nâng cao luyện đề thi', 'ADVANCED', 26, 6500000.00, 'ACTIVE'),
       ('Communication Beginner', 'Khóa giao tiếp cơ bản phát triển kỹ năng nói', 'BEGINNER', 16, 3500000.00, 'ACTIVE'),
       ('Communication Intermediate', 'Khóa giao tiếp trung cấp thực hành thực tế', 'INTERMEDIATE', 20, 4500000.00,
        'DRAFT'),
       ('Communication Advanced', 'Khóa giao tiếp nâng cao cho chuyên nghiệp', 'ADVANCED', 24, 5500000.00, 'ACTIVE'),
       ('English for Business', 'Khóa tiếng Anh dành cho kinh doanh', 'INTERMEDIATE', 20, 6500000.00, 'ACTIVE');

INSERT INTO room (room_name, capacity, location, status)
VALUES ('Phòng A101', 30, 'Tầng 1, Khu A', 'ACTIVE'),
       ('Phòng A102', 25, 'Tầng 1, Khu A', 'ACTIVE'),
       ('Phòng A103', 35, 'Tầng 1, Khu A', 'ACTIVE'),
       ('Phòng B201', 28, 'Tầng 2, Khu B', 'ACTIVE'),
       ('Phòng B202', 32, 'Tầng 2, Khu B', 'ACTIVE'),
       ('Phòng B203', 26, 'Tầng 2, Khu B', 'INACTIVE'),
       ('Phòng C301', 30, 'Tầng 3, Khu C', 'ACTIVE'),
       ('Phòng C302', 24, 'Tầng 3, Khu C', 'ACTIVE'),
       ('Phòng Lab', 20, 'Tầng 4, Phòng Lab', 'ACTIVE'),
       ('Phòng Workshop', 40, 'Tầng 1, Khu D', 'ACTIVE');

INSERT INTO class (class_name, course_id, teacher_id, start_date, end_date, max_student, room_id, status)
VALUES ('IELTS Beginner - Lớp 1', 1, 1, '2026-02-01', '2026-05-01', 30, 1, 'ACTIVE'),
       ('IELTS Intermediate - Lớp 2', 2, 2, '2026-02-05', '2026-05-05', 25, 2, 'ACTIVE'),
       ('IELTS Advanced - Lớp 3', 3, 3, '2026-02-10', '2026-05-10', 28, 3, 'ACTIVE'),
       ('TOEIC Beginner - Lớp 4', 4, 4, '2026-02-01', '2026-04-15', 30, 4, 'ACTIVE'),
       ('TOEIC Intermediate - Lớp 5', 5, 5, '2026-02-08', '2026-05-10', 28, 5, 'ACTIVE'),
       ('Communication Beginner - Lớp 6', 7, 6, '2026-02-20', '2026-04-20', 25, 6, 'INACTIVE'),
       ('Communication Intermediate - Lớp 7', 8, 7, '2026-02-15', '2026-05-15', 30, 7, 'ACTIVE'),
       ('Business English - Lớp 8', 10, 8, '2026-02-10', '2026-05-10', 26, 8, 'ACTIVE'),
       ('IELTS Beginner - Lớp 9', 1, 9, '2026-02-20', '2026-05-20', 32, 9, 'ACTIVE'),
       ('TOEIC Advanced - Lớp 10', 6, 10, '2026-02-05', '2026-05-05', 24, 10, 'ACTIVE');

INSERT INTO enrollment (student_id, class_id, enrolled_at, updated_at)
VALUES (1, 1, '2026-02-15 10:00:00', '2026-02-15 10:00:00'),
       (2, 2, '2026-02-16 11:30:00', '2026-02-16 11:30:00'),
       (3, 3, '2026-02-17 09:00:00', '2026-02-17 09:00:00'),
       (4, 4, '2026-02-18 14:00:00', '2026-02-18 14:00:00'),
       (5, 5, '2026-02-19 15:30:00', '2026-02-19 15:30:00'),
       (6, 7, '2026-02-20 10:00:00', '2026-02-20 10:00:00'),
       (7, 8, '2026-02-21 11:00:00', '2026-02-21 11:00:00'),
       (8, 1, '2026-02-22 13:00:00', '2026-02-22 13:00:00'),
       (9, 2, '2026-02-23 14:30:00', '2026-02-23 14:30:00'),
       (10, 9, '2026-02-24 09:30:00', '2026-02-24 09:30:00');

INSERT INTO result (student_id, class_id, score, comment)
VALUES (1, 1, 7.5, 'Học sinh chăm chỉ, có tiến bộ rõ rệt'),
       (2, 2, 8.0, 'Xuất sắc về kỹ năng nghe và nói'),
       (3, 3, 8.5, 'Rất giỏi, sẵn sàng cho kỳ thi IELTS'),
       (4, 4, 7.0, 'Cần cải thiện kỹ năng đọc'),
       (5, 5, 7.8, 'Tiến bộ tốt, đạt các mục tiêu của khóa'),
       (6, 7, 7.2, 'Nên tập trung vào phát âm'),
       (7, 8, 8.2, 'Rất tốt về sử dụng từ vựng kinh doanh'),
       (8, 1, 6.8, 'Cần ôn lại các kiến thức cơ bản'),
       (9, 2, 8.1, 'Xuất sắc, có thể nghiên cứu thêm'),
       (10, 9, 7.6, 'Tốt, sẵn sàng bước sang level cao hơn');

INSERT INTO schedule (class_id, room_id, schedule_date, start_time, end_time)
VALUES (1, 1, '2026-02-01', '08:00:00', '10:00:00'),
       (1, 1, '2026-02-02', '18:00:00', '19:00:00'),
       (2, 2, '2026-02-05', '10:30:00', '12:30:00'),
       (2, 2, '2026-02-07', '20:30:00', '22:30:00'),
       (3, 3, '2026-02-10', '14:00:00', '16:00:00'),
       (4, 4, '2026-02-11', '09:00:00', '11:00:00'),
       (5, 5, '2026-02-12', '13:00:00', '15:00:00'),
       (7, 7, '2026-02-13', '15:00:00', '17:00:00'),
       (8, 8, '2026-02-14', '10:00:00', '12:00:00'),
       (10, 10, '2026-02-15', '16:00:00', '18:00:00');

-- INSERT INTO attendance (student_id, class_id, created_at, updated_at, status) VALUES
--                                                                                   (1, 1,  '2026-02-01 08:00:00', '2026-02-01 10:00:00', 'PRESENT'),
--                                                                                   (2, 2,  '2026-02-05 10:30:00', '2026-02-05 12:30:00', 'PRESENT'),
--                                                                                   (3, 3,  '2026-02-10 14:00:00', '2026-02-10 16:00:00', 'LATE'),
--                                                                                   (4, 4,  '2026-02-01 09:00:00', '2026-02-01 11:00:00', 'ABSENT'),
--                                                                                   (5, 5,  '2026-02-08 13:00:00', '2026-02-08 15:00:00', 'PRESENT'),
--                                                                                   (6, 7,  '2026-02-15 15:00:00', '2026-02-15 17:00:00', 'EXCUSED'),
--                                                                                   (7, 8,  '2026-02-10 10:00:00', '2026-02-10 12:00:00', 'PRESENT'),
--                                                                                   (8, 1,  '2026-02-01 08:00:00', '2026-02-01 10:00:00', 'PRESENT'),
--                                                                                   (9, 2,  '2026-02-05 10:30:00', '2026-02-05 12:30:00', 'PRESENT'),
--                                                                                   (10, 9, '2026-02-20 08:00:00', '2026-02-20 10:00:00', 'LATE');

INSERT INTO payment (invoice_id, amount, payment_date, payment_method, status)
VALUES (1, 5000000.00, '2026-02-02 14:00:00', 'BANK', 'COMPLETED'),
       (2, 6000000.00, '2026-02-06 15:30:00', 'BANK', 'COMPLETED'),
       (3, 7000000.00, '2026-02-11 10:00:00', 'MOMO', 'PENDING'),
       (4, 4500000.00, '2026-02-02 11:00:00', 'CASH', 'COMPLETED'),
       (5, 5500000.00, '2026-02-09 16:00:00', 'BANK', 'COMPLETED'),
       (6, 3500000.00, '2026-02-28 09:00:00', 'BANK', 'COMPLETED'),
       (7, 4500000.00, '2026-02-16 13:00:00', 'MOMO', 'COMPLETED'),
       (8, 6500000.00, '2026-02-11 14:30:00', 'BANK', 'COMPLETED'),
       (9, 5000000.00, '2026-02-21 10:00:00', 'CASH', 'PENDING'),
       (10, 6500000.00, '2026-02-06 15:00:00', 'BANK', 'FAILED');

INSERT INTO invoice (student_id, payment_id, total_amount, issued_at, status)
VALUES (1, 1, 5000000.00, '2026-02-28 09:00:00', 'PAID'),
       (2, 2, 6000000.00, '2026-02-01 10:00:00', 'PAID'),
       (3, 3, 7000000.00, '2026-02-05 11:00:00', 'ISSUED'),
       (4, 4, 4500000.00, '2026-02-28 12:00:00', 'PAID'),
       (5, 5, 5500000.00, '2026-02-06 09:00:00', 'PAID'),
       (6, 6, 3500000.00, '2026-02-25 10:00:00', 'PAID'),
       (7, 7, 4500000.00, '2026-02-10 14:00:00', 'PAID'),
       (8, 8, 6500000.00, '2026-02-08 11:00:00', 'PAID'),
       (9, 9, 5000000.00, '2026-02-18 10:00:00', 'ISSUED'),
       (10, 10, 6500000.00, '2026-02-02 13:00:00', 'DRAFT');

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
