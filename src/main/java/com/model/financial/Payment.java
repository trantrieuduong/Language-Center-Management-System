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
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long paymentID;
    @OneToOne
            @JoinColumn(name = "invoice_id", nullable = false)
    Invoice invoice;
    @Builder.Default
    BigDecimal amount = BigDecimal.ZERO;
    @CreationTimestamp
    LocalDateTime paymentDate;
    PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    PaymentStatus status;
}
