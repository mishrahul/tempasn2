package in.taxgenie.entities;

import in.taxgenie.entities.base.BaseEntity;
import in.taxgenie.entities.enums.SubscriptionStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing active subscriptions
 * Maps to the subscriptions table
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "subscriptions", 
       indexes = {
           @Index(name = "idx_subscriptions_vendor", columnList = "vendor_id"),
           @Index(name = "idx_subscriptions_oem", columnList = "oem_id"),
           @Index(name = "idx_subscriptions_status", columnList = "status"),
           @Index(name = "idx_subscriptions_dates", columnList = "start_date, end_date")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_subscriptions_vendor_oem", 
                           columnNames = {"vendor_id", "oem_id"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Subscription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "subscription_id", columnDefinition = "uuid")
    private UUID subscriptionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_subscriptions_vendor"))
    @JsonBackReference
    private Vendor vendor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oem_id", nullable = false, foreignKey = @ForeignKey(name = "fk_subscriptions_oem"))
    @JsonBackReference
    private OemMaster oem;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false, foreignKey = @ForeignKey(name = "fk_subscriptions_plan"))
    @JsonBackReference
    private SubscriptionPlan plan;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Builder.Default
    @Column(name = "auto_renew", nullable = false)
    private Boolean autoRenew = true;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pricing_snapshot", nullable = false, columnDefinition = "jsonb")
    private String pricingSnapshot;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 20)
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(name = "next_billing_date")
    private LocalDate nextBillingDate;

    // Relationships
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<PaymentTransaction> paymentTransactions = new HashSet<>();

    @Override
    public String toString() {
        return "Subscription{" +
                "subscriptionId=" + subscriptionId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", autoRenew=" + autoRenew +
                ", status=" + status +
                ", nextBillingDate=" + nextBillingDate +
                '}';
    }
}
