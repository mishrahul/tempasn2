package in.taxgenie.entities;

import in.taxgenie.entities.base.BaseEntity;
import in.taxgenie.entities.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing OEM (Original Equipment Manufacturer) master data
 * Maps to the oem_master table
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "oem_master", indexes = {
    @Index(name = "idx_oem_master_code", columnList = "oem_code", unique = true),
    @Index(name = "idx_oem_master_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OemMaster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "oem_id", columnDefinition = "uuid")
    private UUID oemId;

    @NotBlank
    @Size(max = 20)
    @Column(name = "oem_code", nullable = false, unique = true, length = 20)
    private String oemCode;

    @NotBlank
    @Size(max = 100)
    @Column(name = "oem_name", nullable = false, length = 100)
    private String oemName;

    @NotBlank
    @Size(max = 200)
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 20)
    private Status status = Status.ACTIVE;

    @Builder.Default
    @Column(name = "priority_rank")
    private Integer priorityRank = 100;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "config", nullable = false, columnDefinition = "jsonb")
    private String config = """
        {
            "branding": {},
            "api": {},
            "features": {},
            "limits": {}
        }""";

    @NotBlank
    @Size(max = 20)
    @Column(name = "asn_version", nullable = false, length = 20)
    private String asnVersion;

    @Column(name = "asn_deadline")
    private LocalDate asnDeadline;

    @Column(name = "go_live_date")
    private LocalDate goLiveDate;

    // Relationships
    @OneToMany(mappedBy = "oem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<VendorOemAccess> vendorOemAccesses = new HashSet<>();

    @Override
    public String toString() {
        return "OemMaster{" +
                "oemId=" + oemId +
                ", oemCode='" + oemCode + '\'' +
                ", oemName='" + oemName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", status=" + status +
                ", asnVersion='" + asnVersion + '\'' +
                '}';
    }
}
