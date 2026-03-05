package com.service.impl;

import com.dto.StudentDTO;
import com.exception.BusinessException;
import com.exception.ValidationException;
import com.model.user.Student;
import com.model.user.UserRole;
import com.model.user.UserStatus;
import com.repository.StudentRepository;
import com.repository.UserAccountRepository;
import com.security.CurrentUser;
import com.security.PermissionChecker;
import com.service.BaseService;

import java.time.LocalDateTime;
import java.util.List;

public class StudentServiceImpl implements BaseService<Student, Long, StudentDTO> {

    private final StudentRepository repo = new StudentRepository();
    private final UserAccountServiceImpl userAccountService = new UserAccountServiceImpl();

    @Override
    public List<Student> findAll() {
        CurrentUser u = PermissionChecker.requireAuthenticated();
        if (u.isStudent()) {
            Long id = u.relatedId();
            return id == null
                    ? List.of()
                    : repo.findById(id).map(List::of).orElse(List.of());
        }
        return repo.findAll();
    }

    @Override
    public List<Student> search(String keyword) {
        return findAll().stream()
                .filter(student -> student.toString().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    @Override
    public Student findById(Long id) {
        CurrentUser u = PermissionChecker.requireAuthenticated();
        if (u.isStudent()) {
            Long mine = u.relatedId();
            if (!id.equals(mine))
                throw new BusinessException("Bạn chỉ có thể xem thông tin của chính mình.");
        }
        return repo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy học viên với ID: " + id));
    }

    @Override
    public Student save(StudentDTO dto) {
        CurrentUser u = PermissionChecker.requireAuthenticated();
        if (!u.isAdmin() && !u.isConsultant()) {
            throw new BusinessException("Chỉ Admin hoặc Tư vấn viên mới có thể thêm học viên.");
        }
        validateDto(dto);

        // Validate username uniqueness if provided
        UserAccountRepository accountRepo = new UserAccountRepository();
        if (dto.getUsername() != null
                && !dto.getUsername().isBlank()
                && accountRepo.findByUsername(dto.getUsername().trim()).isPresent()) {
                throw new BusinessException("Tên đăng nhập '" + dto.getUsername() + "' đã tồn tại.");
            }

        if (dto.getPassword() == null || dto.getPassword().isBlank())
            throw new ValidationException("Mật khẩu không được để trống.");

        Student student = Student.builder()
                .fullName(dto.getFullName().trim())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .status(dto.getStatus() != null ? dto.getStatus() : UserStatus.ACTIVE)
                .build();
        Student saved = repo.save(student);

        // Create linked UserAccount if username + password provided
        userAccountService.save(dto.getUsername(), dto.getPassword(), UserRole.STUDENT, saved);

        return saved;
    }

    @Override
    public Student update(Long id, StudentDTO dto) {
        CurrentUser u = PermissionChecker.requireAuthenticated();
        if (!u.isAdmin() && !u.isConsultant()) {
            throw new BusinessException("Chỉ Admin hoặc Tư vấn viên mới có thể sửa thông tin học viên.");
        }
        validateDto(dto);

        Student existing = repo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy học viên với ID: " + id));

        existing.setFullName(dto.getFullName().trim());
        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setGender(dto.getGender());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setAddress(dto.getAddress());
        if (dto.getStatus() != null)
            existing.setStatus(dto.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());

        return repo.update(existing);
    }

    @Override
    public void delete(Long id) {
        CurrentUser u = PermissionChecker.requireAuthenticated();
        if (!u.isAdmin() && !u.isConsultant()) {
            throw new BusinessException("Chỉ Admin hoặc Tư vấn viên mới có thể xóa học viên.");
        }
        Student student = repo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy học viên với ID: " + id));
        student.setStatus(UserStatus.INACTIVE);
        repo.update(student);
    }

    // ---- validation ----

    private void validateDto(StudentDTO dto) {
        if (dto.getFullName() == null || dto.getFullName().isBlank())
            throw new ValidationException("Họ tên không được để trống.");
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && !dto.getEmail().matches("^[\\w.+\\-]+@[\\w\\-]+\\.[a-zA-Z]{2,}$"))
            throw new ValidationException("Địa chỉ email không hợp lệ.");
    }
}
