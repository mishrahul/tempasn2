package in.taxgenie.repositories;

import in.taxgenie.entities.SystemConfig;
import in.taxgenie.repositories.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SystemConfig entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface SystemConfigRepository extends BaseRepository<SystemConfig> {

    /**
     * Find system configuration by key
     * 
     * @param configKey the configuration key
     * @return Optional containing the system configuration if found
     */
    Optional<SystemConfig> findByConfigKey(String configKey);

    /**
     * Find all system configurations by type
     * 
     * @param configType the configuration type
     * @return List of system configurations
     */
    List<SystemConfig> findByConfigType(String configType);

    /**
     * Find all encrypted configurations
     * 
     * @return List of encrypted system configurations
     */
    List<SystemConfig> findByIsEncryptedTrue();

    /**
     * Check if a configuration key exists
     * 
     * @param configKey the configuration key
     * @return true if exists, false otherwise
     */
    boolean existsByConfigKey(String configKey);

    /**
     * Find configurations by type and encryption status
     * 
     * @param configType the configuration type
     * @param isEncrypted the encryption status
     * @return List of system configurations
     */
    @Query("SELECT sc FROM SystemConfig sc WHERE sc.configType = :configType AND sc.isEncrypted = :isEncrypted")
    List<SystemConfig> findByConfigTypeAndEncryption(@Param("configType") String configType, 
                                                     @Param("isEncrypted") Boolean isEncrypted);
}
