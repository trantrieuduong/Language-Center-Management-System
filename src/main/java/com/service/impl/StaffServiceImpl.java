package com.service.impl;

import com.dto.StaffDTO;
import com.exception.BusinessException;
import com.exception.ValidationException;
import com.model.user.Staff;
import com.model.user.UserRole;
import com.model.user.UserStatus;
import com.repository.StaffRepository;
import com.repository.UserAccountRepository;
import com.security.PermissionChecker;
import com.service.BaseService;

import java.time.LocalDateTime;
import java.util.List;

public class StaffServiceImpl implements BaseService<Staff, Long, StaffDTO> {

    private final StaffRepository repo = new StaffRepository();
    private final UserAccountServiceImpl userAccountService = new UserAccountServiceImpl();

    @Override
    public List<Staff> findAll() {
        PermissionChecker.requireAdmin();
        return repo.findAll();
    }

    @Override
    public List<Staff> search(String keyword) {
        return repo.findAll().stream()
                .filter(staff -> staff.toString().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    @Override
    public Staff findById(Long id) {
        PermissionChecker.requireAdmin();
        return repo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy nhân viên với ID: " + id));
    }

    @Override
    public Staff save(StaffDTO dto) {
        PermissionChecker.requireAdmin();

        if (dto.getFullName() == null || dto.getFullName().isBlank())
            throw new ValidationException("Họ tên nhân viên không được để trống.");
        if (dto.getRole() == null)
            throw new ValidationException("Vai trò nhân viên không được để trống.");

        // Validate username uniqueness
        UserAccountRepository accountRepo = new UserAccountRepository();
        if (dto.getUsername() != null && !dto.getUsername().isBlank()
                && accountRepo.findByUsername(dto.getUsername().trim()).isPresent())
            throw new BusinessException("Tên đăng nhập '" + dto.getUsername() + "' đã tồn tại.");

        if (dto.getPassword() == null || dto.getPassword().isBlank())
            throw new ValidationException("Mật khẩu không được để trống.");

        Staff staff = Staff.builder()
                .fullName(dto.getFullName().trim())
                .role(dto.getRole())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .status(dto.getStatus() != null ? dto.getStatus() : UserStatus.ACTIVE)
                .build();
        Staff saved = repo.save(staff);

        // Create linked UserAccount
        userAccountService.save(dto.getUsername(), dto.getUsername(), UserRole.STAFF, saved);

        return saved;
    }

    @Override
    public Staff update(Long id, StaffDTO dto) {
        PermissionChecker.requireAdmin();

        if (dto.getFullName() == null || dto.getFullName().isBlank())
            throw new ValidationException("Họ tên nhân viên không được để trống.");

        Staff existing = repo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy nhân viên với ID: " + id));

        existing.setFullName(dto.getFullName().trim());
        if (dto.getRole() != null)
            existing.setRole(dto.getRole());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        if (dto.getStatus() != null)
            existing.setStatus(dto.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());

        return repo.update(existing);
    }

    @Override
    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        Staff staff = repo.findById(id).orElseThrow(() -> new BusinessException("Không tìm thấy nhân viên."));
        staff.setStatus(UserStatus.INACTIVE);
        repo.save(staff);
    }
}
