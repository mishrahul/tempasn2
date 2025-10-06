package in.taxgenie.entities;

import in.taxgenie.entities.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing subscription plans
 * Maps to the subscription_plans table
 *
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "subscription_plans",
       indexes = {
           @Index(name = "idx_subscription_plans_active", columnList = "is_active"),
           @Index(name = "idx_subscription_plans_code", columnList = "plan_code")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_subscription_plans_code",
                           columnNames = {"plan_code"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SubscriptionPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "plan_id", columnDefinition = "uuid")
    private UUID planId;

    @NotBlank
    @Size(max = 20)
    @Column(name = "plan_code", nullable = false, length = 20, unique = true)
    private String planCode;

    @NotBlank
    @Size(max = 50)
    @Column(name = "plan_name", nullable = false, length = 50)
    private String planName;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "pricing", nullable = false, columnDefinition = "jsonb")
    private String pricing = """
        {
            "yearly": 0,
            "monthly": 0,
            "setup_fee": 0,
            "gst_rate": 18,
            "currency": "INR"
        }""";

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "features", nullable = false, columnDefinition = "jsonb")
    private String features = "[]";

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "api_limits", nullable = false, columnDefinition = "jsonb")
    private String apiLimits = """
        {
            "requests_per_minute": 60,
            "requests_per_day": 10000,
            "requests_per_month": 300000
        }""";

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "support_config", nullable = false, columnDefinition = "jsonb")
    private String supportConfig = """
        {
            "type": "EMAIL",
            "sla_hours": 48,
            "priority": "NORMAL"
        }""";

    @Builder.Default
    @Column(name = "display_order")
    private Integer displayOrder = 100;

    @Builder.Default
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Relationships
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<Subscription> subscriptions = new HashSet<>();

    @Override
    public String toString() {
        return "SubscriptionPlan{" +
                "planId=" + planId +
                ", planCode='" + planCode + '\'' +
                ", planName='" + planName + '\'' +
                ", displayOrder=" + displayOrder +
                ", isFeatured=" + isFeatured +
                ", isActive=" + isActive +
                '}';
    }
}
