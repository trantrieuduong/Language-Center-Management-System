package com.model.financial;

import com.model.user.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long invoiceID;
    @ManyToOne
            @JoinColumn(name = "student_id", nullable = false)
    Student student;
    @OneToOne
            @JoinColumn(name = "payment_id", nullable = false)
    Payment payment;
    BigDecimal totalAmount;
    @CreationTimestamp
    LocalDateTime issuedAt;
    @Enumerated(EnumType.STRING)
    InvoiceStatus status;
}
