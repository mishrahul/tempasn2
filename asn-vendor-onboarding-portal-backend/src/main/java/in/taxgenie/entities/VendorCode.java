package in.taxgenie.entities;

import in.taxgenie.entities.base.BaseEntity;
import in.taxgenie.entities.enums.Status;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

/**
 * Entity representing vendor codes mapped to GSTIN
 * Maps to the vendor_codes table
 * One GSTIN can have multiple vendor codes
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "vendor_codes", 
       indexes = {
           @Index(name = "idx_vendor_codes_gstin", columnList = "gstin_id"),
           @Index(name = "idx_vendor_codes_code", columnList = "vendor_code"),
           @Index(name = "idx_vendor_codes_status", columnList = "status")
       },
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_vendor_codes_code", 
                           columnNames = {"vendor_code"})
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VendorCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "vendor_code_id", columnDefinition = "uuid")
    private UUID vendorCodeId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gstin_id", nullable = false, foreignKey = @ForeignKey(name = "fk_vendor_codes_gstin"))
    @JsonBackReference
    private VendorGstin vendorGstin;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(name = "vendor_code", nullable = false, unique = true, length = 50)
    private String vendorCode;

    @Size(max = 200)
    @Column(name = "description", length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status", length = 20)
    private Status status = Status.ACTIVE;

    @Override
    public String toString() {
        return "VendorCode{" +
                "vendorCodeId=" + vendorCodeId +
                ", vendorCode='" + vendorCode + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}

