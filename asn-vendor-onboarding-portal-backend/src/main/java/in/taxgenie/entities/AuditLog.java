package in.taxgenie.entities;

import in.taxgenie.entities.enums.ActorType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing audit logs for compliance and security
 * Maps to the audit_logs table
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_logs_actor", columnList = "actor_id"),
    @Index(name = "idx_audit_logs_resource", columnList = "resource_type, resource_id"),
    @Index(name = "idx_audit_logs_timestamp", columnList = "event_time")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "audit_id", columnDefinition = "uuid")
    private UUID auditId;

    @NotNull
    @Builder.Default
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime = LocalDateTime.now();

    @Column(name = "actor_id", columnDefinition = "uuid")
    private UUID actorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", length = 20)
    private ActorType actorType;

    @Column(name = "ip_address", columnDefinition = "inet")
    private InetAddress ipAddress;

    @NotBlank
    @Size(max = 50)
    @Column(name = "event_category", nullable = false, length = 50)
    private String eventCategory;

    @NotBlank
    @Size(max = 50)
    @Column(name = "event_action", nullable = false, length = 50)
    private String eventAction;

    @Size(max = 50)
    @Column(name = "resource_type", length = 50)
    private String resourceType;

    @Column(name = "resource_id", columnDefinition = "uuid")
    private UUID resourceId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "changes", columnDefinition = "jsonb")
    private String changes;

    @Size(max = 100)
    @Column(name = "request_id", length = 100)
    private String requestId;

    @Size(max = 100)
    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Override
    public String toString() {
        return "AuditLog{" +
                "auditId=" + auditId +
                ", eventTime=" + eventTime +
                ", actorId=" + actorId +
                ", actorType=" + actorType +
                ", ipAddress=" + ipAddress +
                ", eventCategory='" + eventCategory + '\'' +
                ", eventAction='" + eventAction + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", resourceId=" + resourceId +
                ", requestId='" + requestId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
