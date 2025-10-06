package in.taxgenie.repositories;

import in.taxgenie.entities.OemMaster;
import in.taxgenie.entities.enums.Status;
import in.taxgenie.repositories.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OemMaster entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface OemMasterRepository extends BaseRepository<OemMaster> {

    /**
     * Find OEM by code
     * 
     * @param oemCode the OEM code
     * @return Optional containing the OEM if found
     */
    Optional<OemMaster> findByOemCode(String oemCode);

    /**
     * Find all OEMs by status
     * 
     * @param status the OEM status
     * @return List of OEMs
     */
    List<OemMaster> findByStatus(Status status);

    /**
     * Find all active OEMs ordered by priority
     * 
     * @return List of active OEMs ordered by priority rank
     */
    @Query("SELECT o FROM OemMaster o WHERE o.status = 'ACTIVE' ORDER BY o.priorityRank ASC")
    List<OemMaster> findActiveOemsOrderedByPriority();

    /**
     * Find OEMs by ASN version
     * 
     * @param asnVersion the ASN version
     * @return List of OEMs
     */
    List<OemMaster> findByAsnVersion(String asnVersion);

    /**
     * Find OEMs with ASN deadline before given date
     * 
     * @param date the deadline date
     * @return List of OEMs
     */
    List<OemMaster> findByAsnDeadlineBefore(LocalDate date);

    /**
     * Find OEMs by name containing (case insensitive)
     * 
     * @param name the name to search for
     * @return List of OEMs
     */
    @Query("SELECT o FROM OemMaster o WHERE LOWER(o.oemName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(o.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<OemMaster> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Check if OEM code exists
     * 
     * @param oemCode the OEM code
     * @return true if exists, false otherwise
     */
    boolean existsByOemCode(String oemCode);

    /**
     * Count OEMs by status
     *
     * @param status the OEM status
     * @return count of OEMs
     */
    long countByStatus(Status status);

    /**
     * Find OEM by OEM ID and company code
     *
     * @param oemId the OEM ID
     * @param companyCode the company code
     * @return Optional containing the OEM if found
     */
    @Query("SELECT o FROM OemMaster o WHERE o.oemId = :oemId AND o.companyCode = :companyCode")
    Optional<OemMaster> findByOemIdAndCompanyCode(@Param("oemId") java.util.UUID oemId, @Param("companyCode") Long companyCode);
}
