package com.service.impl;

import com.dto.TeacherDTO;
import com.exception.BusinessException;
import com.exception.ValidationException;
import com.model.user.Teacher;
import com.model.user.UserRole;
import com.model.user.UserStatus;
import com.repository.TeacherRepository;
import com.repository.UserAccountRepository;
import com.security.PermissionChecker;
import com.service.BaseService;

import java.time.LocalDateTime;
import java.util.List;

public class TeacherServiceImpl implements BaseService<Teacher, Long, TeacherDTO> {

    private final TeacherRepository repo = new TeacherRepository();
    private final UserAccountServiceImpl userAccountService = new UserAccountServiceImpl();

    @Override
    public List<Teacher> findAll() {
        PermissionChecker.requireAuthenticated();
        return repo.findAll();
    }

    @Override
    public List<Teacher> search(String keyword) {
        return findAll().stream()
                .filter(teacher -> teacher.getFullName().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    @Override
    public Teacher findById(Long id) {
        PermissionChecker.requireAuthenticated();
        return repo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy giáo viên với ID: " + id));
    }

    @Override
    public Teacher save(TeacherDTO dto) {
        var u = PermissionChecker.requireAuthenticated();
        if (!u.isAdmin() && !u.isConsultant())
            throw new BusinessException("Chỉ Admin hoặc Tư vấn viên mới có thể thêm giáo viên.");

        if (dto.getFullName() == null || dto.getFullName().isBlank())
            throw new ValidationException("Họ tên giáo viên không được để trống.");

        // Validate username uniqueness
        UserAccountRepository accountRepo = new UserAccountRepository();
        if (dto.getUsername() != null && !dto.getUsername().isBlank()
                && accountRepo.findByUsername(dto.getUsername().trim()).isPresent())
            throw new BusinessException("Tên đăng nhập '" + dto.getUsername() + "' đã tồn tại.");

        if (dto.getPassword() == null || dto.getPassword().isBlank())
            throw new ValidationException("Mật khẩu không được để trống.");

        Teacher teacher = Teacher.builder()
                .fullName(dto.getFullName().trim())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .specialty(dto.getSpecialty())
                .status(dto.getStatus() != null ? dto.getStatus() : UserStatus.ACTIVE)
                .build();
        Teacher saved = repo.save(teacher);

        // Create linked UserAccount
        userAccountService.save(dto.getUsername(), dto.getUsername(), UserRole.TEACHER, saved);

        return saved;
    }

    @Override
    public Teacher update(Long id, TeacherDTO dto) {
        var u = PermissionChecker.requireAuthenticated();
        if (!u.isAdmin() && !u.isConsultant())
            throw new BusinessException("Chỉ Admin hoặc Tư vấn viên mới có thể sửa thông tin giáo viên.");

        if (dto.getFullName() == null || dto.getFullName().isBlank())
            throw new ValidationException("Họ tên giáo viên không được để trống.");

        Teacher existing = repo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy giáo viên với ID: " + id));

        existing.setFullName(dto.getFullName().trim());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setSpecialty(dto.getSpecialty());
        if (dto.getStatus() != null)
            existing.setStatus(dto.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());

        return repo.update(existing);
    }

    @Override
    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        Teacher teacher = repo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy giáo viên."));
        teacher.setStatus(UserStatus.INACTIVE);
        repo.save(teacher);
    }
}
