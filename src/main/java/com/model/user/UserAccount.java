package com.model.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID userID;
    @Column(nullable = false, unique = true)
    String username;
    @Column(nullable = false)
    String passwordHash;
    @Enumerated(EnumType.STRING)
            @Builder.Default
    UserRole role = UserRole.STUDENT;
    @OneToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "student_id")
    Student student;
    @OneToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "teacher_id")
    Teacher teacher;
    @OneToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "staff_id")
    Staff staff;
}
