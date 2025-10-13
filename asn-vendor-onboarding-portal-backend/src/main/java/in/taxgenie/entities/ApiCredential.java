package in.taxgenie.entities;

import in.taxgenie.entities.base.BaseEntity;
import in.taxgenie.entities.enums.Environment;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing API credentials for vendor-OEM integration
 * Maps to the api_credentials table
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "api_credentials", 
       indexes = {
           @Index(name = "idx_api_credentials_vendor_oem", columnList = "vendor_id, oem_id"),
           @Index(name = "idx_api_credentials_active", columnList = "is_active")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_api_credentials_vendor_oem_env", 
                           columnNames = {"vendor_id", "oem_id", "environment"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ApiCredential extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "credential_id", columnDefinition = "uuid")
    private UUID credentialId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_api_credentials_vendor"))
    @JsonBackReference
    private Vendor vendor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oem_id", nullable = false, foreignKey = @ForeignKey(name = "fk_api_credentials_oem"))
    @JsonBackReference
    private OemMaster oem;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_code", nullable = false, foreignKey = @ForeignKey(name = "fk_api_credentials_vendor_code"))
    @JsonBackReference
    private VendorCode vendorCode;

    @NotBlank
    @Size(max = 255)
    @Column(name = "api_key_hash", nullable = false, unique = true)
    private String apiKeyHash;

    @NotBlank
    @Column(name = "secret_encrypted", nullable = false, columnDefinition = "TEXT")
    private String secretEncrypted;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "environment", length = 20)
    private Environment environment = Environment.SANDBOX;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "rate_limits", nullable = true, columnDefinition = "jsonb")
    private String rateLimits = """
        {
            "per_minute": 60,
            "per_hour": 1000,
            "per_day": 10000
        }""";

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "last_rotated_at")
    private LocalDateTime lastRotatedAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "usage_stats", columnDefinition = "jsonb")
    private String usageStats = """
        {
            "total_requests": 0,
            "successful_requests": 0,
            "failed_requests": 0,
            "last_request_at": null
        }""";

    // Relationships
    @OneToMany(mappedBy = "credential", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<ApiRequestLog> apiRequestLogs = new HashSet<>();

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gstin", nullable = false, foreignKey = @ForeignKey(name = "fk_api_credentials_gstin"))
    @JsonBackReference
    private VendorGstin vendorGstin;

    @Override
    public String toString() {
        return "ApiCredential{" +
                "credentialId=" + credentialId +
                ", apiKeyHash='" + apiKeyHash + '\'' +
                ", environment=" + environment +
                ", isActive=" + isActive +
                ", lastRotatedAt=" + lastRotatedAt +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
