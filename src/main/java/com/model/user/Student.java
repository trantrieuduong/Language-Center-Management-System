package com.model.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long studentID;
    @Column(nullable = false)
    String fullName;
    LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    Gender gender;
    String phone;
    String email;
    String address;
    @CreationTimestamp
    LocalDateTime registeredAt;
    LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
            @Builder.Default
    UserStatus status = UserStatus.ACTIVE;
}
