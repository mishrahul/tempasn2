package in.taxgenie.entities;

import in.taxgenie.entities.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing payment transactions
 * Maps to the payment_transactions table
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "payment_transactions", indexes = {
    @Index(name = "idx_payment_transactions_vendor", columnList = "vendor_id"),
    @Index(name = "idx_payment_transactions_subscription", columnList = "subscription_id"),
    @Index(name = "idx_payment_transactions_status", columnList = "status"),
    @Index(name = "idx_payment_transactions_date", columnList = "initiated_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transaction_id", columnDefinition = "uuid")
    private UUID transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", foreignKey = @ForeignKey(name = "fk_payment_transactions_subscription"))
    @JsonBackReference
    private Subscription subscription;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_payment_transactions_vendor"))
    @JsonBackReference
    private Vendor vendor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oem_id", nullable = false, foreignKey = @ForeignKey(name = "fk_payment_transactions_oem"))
    @JsonBackReference
    private OemMaster oem;

    @NotBlank
    @Size(max = 50)
    @Column(name = "transaction_ref", nullable = false, unique = true, length = 50)
    private String transactionRef;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Builder.Default
    @Size(max = 3)
    @Column(name = "currency", length = 3)
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "gateway_data", nullable = false, columnDefinition = "jsonb")
    private String gatewayData = "{}";

    @Builder.Default
    @Column(name = "initiated_at", nullable = false)
    private LocalDateTime initiatedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Override
    public String toString() {
        return "PaymentTransaction{" +
                "transactionId=" + transactionId +
                ", transactionRef='" + transactionRef + '\'' +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", status=" + status +
                ", initiatedAt=" + initiatedAt +
                ", completedAt=" + completedAt +
                '}';
    }
}
