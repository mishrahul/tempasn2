package in.taxgenie.entities;

import in.taxgenie.entities.base.BaseEntity;
import in.taxgenie.entities.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing vendor companies
 * Maps to the vendors table
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "vendors", indexes = {
    @Index(name = "idx_vendors_status", columnList = "status"),
    @Index(name = "idx_vendors_company_name", columnList = "company_name"),
    @Index(name = "idx_vendors_pan", columnList = "pan_number", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vendor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "vendor_id", columnDefinition = "uuid")
    private UUID vendorId;

    @NotNull
    @Column(name = "user_id", nullable = true)
    private Long userId;

    @NotBlank
    @Size(max = 200)
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @NotBlank
    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", 
             message = "PAN number must be in format: ABCDE1234F")
    @Column(name = "pan_number", nullable = false, unique = true, length = 10)
    private String panNumber;

    @Size(max = 21)
    @Column(name = "cin_number", length = 21)
    private String cinNumber;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "primary_contact", nullable = false, columnDefinition = "jsonb")
    private String primaryContact = """
        {
            "name": "",
            "email": "",
            "mobile": "",
            "designation": ""
        }""";

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Builder.Default
    @Column(name = "auth_credentials", nullable = false, columnDefinition = "jsonb")
    private String authCredentials = """
        {
            "password_hash": "",
            "mfa_enabled": false,
            "mfa_secret": null
        }""";

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 20)
    private Status status = Status.ACTIVE;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    // Relationships
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<VendorGstin> vendorGstins = new HashSet<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<VendorOemAccess> vendorOemAccesses = new HashSet<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<OnboardingProcess> onboardingProcesses = new HashSet<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<Subscription> subscriptions = new HashSet<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<PaymentTransaction> paymentTransactions = new HashSet<>();

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @Builder.Default
    private Set<ApiCredential> apiCredentials = new HashSet<>();

    @Override
    public String toString() {
        return "Vendor{" +
                "vendorId=" + vendorId +
                ", companyName='" + companyName + '\'' +
                ", panNumber='" + panNumber + '\'' +
                ", cinNumber='" + cinNumber + '\'' +
                ", status=" + status +
                '}';
    }
}
