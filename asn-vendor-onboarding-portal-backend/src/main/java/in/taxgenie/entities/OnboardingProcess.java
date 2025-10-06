package in.taxgenie.entities;

import in.taxgenie.entities.base.BaseEntity;
import in.taxgenie.entities.enums.OnboardingStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing vendor onboarding process tracking
 * Maps to the onboarding_process table
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "onboarding_process", 
       indexes = {
           @Index(name = "idx_onboarding_process_vendor", columnList = "vendor_id"),
           @Index(name = "idx_onboarding_process_oem", columnList = "oem_id"),
           @Index(name = "idx_onboarding_process_status", columnList = "status")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_onboarding_process_vendor_oem", 
                           columnNames = {"vendor_id", "oem_id"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OnboardingProcess extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "onboarding_id", columnDefinition = "uuid")
    private UUID onboardingId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_onboarding_process_vendor"))
    @JsonBackReference
    private Vendor vendor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oem_id", nullable = false, foreignKey = @ForeignKey(name = "fk_onboarding_process_oem"))
    @JsonBackReference
    private OemMaster oem;

    @NotBlank
    @Size(max = 50)
    @Column(name = "current_step", nullable = false, length = 50)
    private String currentStep;

    @Min(0)
    @Max(100)
    @Builder.Default
    @Column(name = "progress_percentage")
    private Integer progressPercentage = 0;

    @Size(max = 50)
    @Column(name = "deployment_method", length = 50)
    private String deploymentMethod;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "steps_completed", columnDefinition = "jsonb")
    private String stepsCompleted = "[]";

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "step_data", columnDefinition = "jsonb")
    private String stepData = "{}";

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 20)
    private OnboardingStatus status = OnboardingStatus.IN_PROGRESS;

    @Builder.Default
    @Column(name = "started_at")
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder.Default
    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt = LocalDateTime.now();

    @Column(name = "initiated_by", columnDefinition = "uuid")
    private UUID initiatedBy;

    @Column(name = "last_updated_by", columnDefinition = "uuid")
    private UUID lastUpdatedBy;

    // Relationships
    @OneToMany(mappedBy = "onboardingProcess", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<OnboardingEvent> onboardingEvents = new HashSet<>();

    @Override
    public String toString() {
        return "OnboardingProcess{" +
                "onboardingId=" + onboardingId +
                ", currentStep='" + currentStep + '\'' +
                ", progressPercentage=" + progressPercentage +
                ", deploymentMethod='" + deploymentMethod + '\'' +
                ", status=" + status +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                '}';
    }
}
