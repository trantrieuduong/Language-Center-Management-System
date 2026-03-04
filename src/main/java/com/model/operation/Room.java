package com.model.operation;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    Long roomID;

    @Column(name = "room_name", unique = true, nullable = false)
    String roomName;

    @Column(name = "capacity", nullable = false)
    @Builder.Default
    Integer capacity = 0;

    @Column(name = "location")
    String location;

    @Column(name = "status")
    @Builder.Default
    @Enumerated(EnumType.STRING)
    RoomStatus status = RoomStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
