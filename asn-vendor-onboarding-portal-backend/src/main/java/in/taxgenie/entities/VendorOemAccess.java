package in.taxgenie.entities;

import in.taxgenie.entities.base.BaseEntity;
import in.taxgenie.entities.enums.AccessLevel;
import in.taxgenie.entities.enums.AccessStatus;
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
 * Entity representing vendor-OEM access mapping
 * Maps to the vendor_oem_access table
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "vendor_oem_access", 
       indexes = {
           @Index(name = "idx_vendor_oem_access_vendor", columnList = "vendor_id"),
           @Index(name = "idx_vendor_oem_access_oem", columnList = "oem_id"),
           @Index(name = "idx_vendor_oem_access_status", columnList = "access_status")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_vendor_oem_access_vendor_oem", 
                           columnNames = {"vendor_id", "oem_id"}),
           @UniqueConstraint(name = "uk_vendor_oem_access_oem_vendor_code", 
                           columnNames = {"oem_id", "vendor_code"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VendorOemAccess extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "access_id", columnDefinition = "uuid")
    private UUID accessId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vendor_oem_access_vendor"))
    @JsonBackReference
    private Vendor vendor;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oem_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vendor_oem_access_oem"))
    @JsonBackReference
    private OemMaster oem;

    @NotBlank
    @Size(max = 20)
    @Column(name = "vendor_code", nullable = false, length = 20)
    private String vendorCode;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "access_level", length = 20)
    private AccessLevel accessLevel = AccessLevel.BASIC;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "access_status", length = 20)
    private AccessStatus accessStatus = AccessStatus.PENDING;

    @Column(name = "granted_at")
    private LocalDateTime grantedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "last_accessed_at")
    private LocalDateTime lastAccessedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "permissions_cache", columnDefinition = "jsonb")
    private String permissionsCache = "[]";

    @Builder.Default
    @Column(name = "total_api_calls")
    private Long totalApiCalls = 0L;

    @Builder.Default
    @Column(name = "total_asn_generated")
    private Long totalAsnGenerated = 0L;

    @Override
    public String toString() {
        return "VendorOemAccess{" +
                "accessId=" + accessId +
                ", vendorCode='" + vendorCode + '\'' +
                ", accessLevel=" + accessLevel +
                ", accessStatus=" + accessStatus +
                ", totalApiCalls=" + totalApiCalls +
                ", totalAsnGenerated=" + totalAsnGenerated +
                '}';
    }
}
