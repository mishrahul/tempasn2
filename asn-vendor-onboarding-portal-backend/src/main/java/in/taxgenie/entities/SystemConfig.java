package in.taxgenie.entities;

import in.taxgenie.entities.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Entity representing system configuration parameters
 * Maps to the system_config table
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Entity
@Table(name = "system_config", indexes = {
    @Index(name = "idx_system_config_key", columnList = "config_key", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SystemConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "config_id", columnDefinition = "uuid")
    private UUID configId;

    @NotBlank
    @Size(max = 100)
    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config_value", nullable = false, columnDefinition = "jsonb")
    private String configValue;

    @NotBlank
    @Size(max = 50)
    @Column(name = "config_type", nullable = false, length = 50)
    private String configType;

    @Builder.Default
    @Column(name = "is_encrypted", nullable = false)
    private Boolean isEncrypted = false;

    @Override
    public String toString() {
        return "SystemConfig{" +
                "configId=" + configId +
                ", configKey='" + configKey + '\'' +
                ", configType='" + configType + '\'' +
                ", isEncrypted=" + isEncrypted +
                '}';
    }
}
