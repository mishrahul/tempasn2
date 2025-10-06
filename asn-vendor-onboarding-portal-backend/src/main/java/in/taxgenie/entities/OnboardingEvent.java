package in.taxgenie.entities;

import in.taxgenie.entities.enums.ActorType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing onboarding events log
 * Maps to the onboarding_events table
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "onboarding_events", indexes = {
    @Index(name = "idx_onboarding_events_onboarding", columnList = "onboarding_id"),
    @Index(name = "idx_onboarding_events_timestamp", columnList = "event_timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OnboardingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "event_id", columnDefinition = "uuid")
    private UUID eventId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_id", nullable = false, foreignKey = @ForeignKey(name = "fk_onboarding_events_onboarding"))
    @JsonBackReference
    private OnboardingProcess onboardingProcess;

    @NotBlank
    @Size(max = 50)
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_data", nullable = false, columnDefinition = "jsonb")
    private String eventData;

    @Builder.Default
    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp = LocalDateTime.now();

    @Column(name = "triggered_by", columnDefinition = "uuid")
    private UUID triggeredBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "triggered_by_type", length = 20)
    private ActorType triggeredByType;

    @Size(max = 100)
    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    @Override
    public String toString() {
        return "OnboardingEvent{" +
                "eventId=" + eventId +
                ", eventType='" + eventType + '\'' +
                ", eventTimestamp=" + eventTimestamp +
                ", triggeredByType=" + triggeredByType +
                ", idempotencyKey='" + idempotencyKey + '\'' +
                '}';
    }
}
