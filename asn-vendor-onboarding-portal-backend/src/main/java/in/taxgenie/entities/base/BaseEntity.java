package in.taxgenie.entities.base;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base entity class providing common fields for all entities
 * Includes multi-tenancy support through company_code field
 *
 * @author ASN Development Team
 * @version 1.0.0
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@FilterDef(name = "companyFilter", parameters = @ParamDef(name = "companyCode", type = Long.class))
@Filter(name = "companyFilter", condition = "company_code = :companyCode")
public abstract class BaseEntity {

    /**
     * Company code for multi-tenancy support
     * This field is required for all entities to ensure proper data isolation
     */
    @NotNull
    @Column(name = "company_code", length = 20)
    private Long companyCode;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by")
    private UUID createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private UUID updatedBy;
}
