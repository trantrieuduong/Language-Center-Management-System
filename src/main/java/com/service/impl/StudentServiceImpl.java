package com.service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.dto.StudentDTO;
import com.exception.BusinessException;
import com.exception.ValidationException;
import com.model.user.Student;
import com.model.user.UserAccount;
import com.model.user.UserRole;
import com.model.user.UserStatus;
import com.repository.StudentRepository;
import com.repository.UserAccountRepository;
import com.security.CurrentUser;
import com.security.PermissionChecker;
import com.security.SecurityContext;
import com.service.StudentService;

import java.time.LocalDateTime;
import java.util.List;

public class StudentServiceImpl implements StudentService {

    private final StudentRepository repo = new StudentRepository();

    @Override
    public List<Student> findAll() {
        CurrentUser u = PermissionChecker.requireAuthenticated();
        if (u.isStudent()) {
            Long id = u.relatedId();
            return id == null ? List.of() : repo.findById(id).map(List::of).orElse(List.of());
        }
        return repo.findAll();
    }

    @Override
    public List<Student> search(String keyword) {
        PermissionChecker.requireAuthenticated();
        if (keyword == null || keyword.isBlank())
            return findAll();
        CurrentUser u = SecurityContext.get();
        if (u != null && u.isStudent())
            return findAll(); // restriction applied
        return repo.searchByName(keyword.trim());
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
        if (dto.getUsername() != null && !dto.getUsername().isBlank()) {
            if (accountRepo.findByUsername(dto.getUsername().trim()).isPresent()) {
                throw new BusinessException("Tên đăng nhập '" + dto.getUsername() + "' đã tồn tại.");
            }
        }

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
        if (dto.getUsername() != null && !dto.getUsername().isBlank()
                && dto.getPassword() != null && !dto.getPassword().isBlank()) {
            UserAccount account = UserAccount.builder()
                    .username(dto.getUsername().trim())
                    .passwordHash(BCrypt.withDefaults().hashToString(12, dto.getPassword().toCharArray()))
                    .role(UserRole.STUDENT)
                    .student(saved)
                    .build();
            accountRepo.save(account);
        }
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
    public void softDelete(Long id) {
        CurrentUser u = PermissionChecker.requireAuthenticated();
        if (!u.isAdmin() && !u.isConsultant()) {
            throw new BusinessException("Chỉ Admin hoặc Tư vấn viên mới có thể xóa học viên.");
        }
        repo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy học viên với ID: " + id));
        repo.softDelete(id);
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
