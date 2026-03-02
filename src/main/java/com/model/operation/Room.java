package com.model.operation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long roomID;
    @Column(unique = true, nullable = false)
    String roomName;
    @Column(nullable = false)
            @Builder.Default
    Integer capacity = 0;
    String location;
    @Builder.Default
    RoomStatus status = RoomStatus.ACTIVE;
    @CreationTimestamp
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
