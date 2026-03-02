package com.model.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long staffID;
    @Column(nullable = false)
    String fullName;
    StaffRole role;
    String phone;
    String email;
    @Enumerated(EnumType.STRING)
            @Builder.Default
    UserStatus status = UserStatus.ACTIVE;
    @CreationTimestamp
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
