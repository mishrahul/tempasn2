package in.taxgenie.entities;

import in.taxgenie.entities.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing vendor GSTIN information
 * Maps to the vendor_gstin table
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "vendor_gstin", indexes = {
    @Index(name = "idx_vendor_gstin_vendor", columnList = "vendor_id"),
    @Index(name = "idx_vendor_gstin_primary", columnList = "vendor_id, is_primary")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VendorGstin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "gstin_id", columnDefinition = "uuid")
    private UUID gstinId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vendor_gstin_vendor"))
    @JsonBackReference
    private Vendor vendor;

    @NotBlank
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9A-Z]{3}$", 
             message = "GSTIN must be in format: 22ABCDE1234F1Z5")
    @Column(name = "gstin", nullable = false, unique = true, length = 15)
    private String gstin;

    @NotBlank
    @Size(max = 2)
    @Column(name = "state_code", nullable = false, length = 2)
    private String stateCode;

    @Builder.Default
    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    // Relationships
    @OneToMany(mappedBy = "vendorGstin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<VendorCode> vendorCodes = new HashSet<>();

    @OneToMany(mappedBy = "vendorGstin", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<ApiCredential> apiCredentials = new HashSet<>();

//    @NotBlank
    @Size(max = 10)
    @Column(name = "vendor_code", nullable = false, length = 10)
    private String vendorCode;

    @Override
    public String toString() {
        return "VendorGstin{" +
                "gstinId=" + gstinId +
                ", gstin='" + gstin + '\'' +
                ", stateCode='" + stateCode + '\'' +
                ", isPrimary=" + isPrimary +
                ", isVerified=" + isVerified +
                '}';
    }
}
