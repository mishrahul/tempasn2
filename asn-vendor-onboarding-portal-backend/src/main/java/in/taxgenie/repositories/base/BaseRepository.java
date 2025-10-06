package in.taxgenie.repositories.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base repository interface providing common functionality for all repositories
 * Includes multi-tenancy support through company_code filtering
 *
 * @param <T> Entity type
 * @author ASN Development Team
 * @version 1.0.0
 */
@NoRepositoryBean
public interface BaseRepository<T> extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {

    /**
     * Find entity by ID and company code
     * @param id Entity ID
     * @param companyCode Company code for tenant isolation
     * @return Optional entity
     */
//    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.companyCode = :companyCode")
//    Optional<T> findByIdAndCompanyCode(@Param("id") UUID id, @Param("companyCode") Long companyCode);

    /**
     * Find all entities for a specific company
     * @param companyCode Company code for tenant isolation
     * @return List of entities
     */
//    @Query("SELECT e FROM #{#entityName} e WHERE e.companyCode = :companyCode")
//    List<T> findAllByCompanyCode(@Param("companyCode") Long companyCode);

    /**
     * Count entities for a specific company
     * @param companyCode Company code for tenant isolation
     * @return Count of entities
     */
//    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.companyCode = :companyCode")
//    long countByCompanyCode(@Param("companyCode") Long companyCode);

    /**
     * Check if entity exists by ID and company code
     * @param id Entity ID
     * @param companyCode Company code for tenant isolation
     * @return true if exists, false otherwise
     */
//    @Query("SELECT COUNT(e) > 0 FROM #{#entityName} e WHERE e.id = :id AND e.companyCode = :companyCode")
//    boolean existsByIdAndCompanyCode(@Param("id") UUID id, @Param("companyCode") Long companyCode);
}
