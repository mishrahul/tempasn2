package in.taxgenie.entities;

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
 * Entity representing API request logs
 * Maps to the api_request_logs table
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "api_request_logs", indexes = {
    @Index(name = "idx_api_request_logs_credential", columnList = "credential_id"),
    @Index(name = "idx_api_request_logs_timestamp", columnList = "request_timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ApiRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "log_id", columnDefinition = "uuid")
    private UUID logId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id", nullable = false, foreignKey = @ForeignKey(name = "fk_api_request_logs_credential"))
    @JsonBackReference
    private ApiCredential credential;

    @NotNull
    @Builder.Default
    @Column(name = "request_timestamp", nullable = false)
    private LocalDateTime requestTimestamp = LocalDateTime.now();

    @NotBlank
    @Size(max = 100)
    @Column(name = "endpoint", nullable = false, length = 100)
    private String endpoint;

    @NotBlank
    @Size(max = 10)
    @Column(name = "method", nullable = false, length = 10)
    private String method;

    @Column(name = "status_code")
    private Short statusCode;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Size(max = 50)
    @Column(name = "error_code", length = 50)
    private String errorCode;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_data", columnDefinition = "jsonb")
    private String requestData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_data", columnDefinition = "jsonb")
    private String responseData;

    @Override
    public String toString() {
        return "ApiRequestLog{" +
                "logId=" + logId +
                ", requestTimestamp=" + requestTimestamp +
                ", endpoint='" + endpoint + '\'' +
                ", method='" + method + '\'' +
                ", statusCode=" + statusCode +
                ", responseTimeMs=" + responseTimeMs +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}
