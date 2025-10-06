package in.taxgenie.repositories;

import in.taxgenie.entities.Vendor;
import in.taxgenie.entities.enums.Status;
import in.taxgenie.repositories.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Vendor entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface VendorRepository extends BaseRepository<Vendor> {

    /**
     * Find vendor by PAN number
     * 
     * @param panNumber the PAN number
     * @return Optional containing the vendor if found
     */
    Optional<Vendor> findByPanNumber(String panNumber);

    /**
     * Find vendor by CIN number
     * 
     * @param cinNumber the CIN number
     * @return Optional containing the vendor if found
     */
    Optional<Vendor> findByCinNumber(String cinNumber);

    /**
     * Find vendors by status
     * 
     * @param status the vendor status
     * @return List of vendors
     */
    List<Vendor> findByStatus(Status status);

    /**
     * Find vendors by company name containing (case insensitive)
     * 
     * @param companyName the company name to search for
     * @return List of vendors
     */
    @Query("SELECT v FROM Vendor v WHERE LOWER(v.companyName) LIKE LOWER(CONCAT('%', :companyName, '%'))")
    List<Vendor> findByCompanyNameContainingIgnoreCase(@Param("companyName") String companyName);

    /**
     * Find vendors with last activity before given date
     * 
     * @param dateTime the date time threshold
     * @return List of vendors
     */
    List<Vendor> findByLastActivityAtBefore(LocalDateTime dateTime);

    /**
     * Find vendors with last activity after given date
     * 
     * @param dateTime the date time threshold
     * @return List of vendors
     */
    List<Vendor> findByLastActivityAtAfter(LocalDateTime dateTime);

    /**
     * Find vendors with null last activity (never logged in)
     * 
     * @return List of vendors
     */
    List<Vendor> findByLastActivityAtIsNull();

    /**
     * Check if PAN number exists
     * 
     * @param panNumber the PAN number
     * @return true if exists, false otherwise
     */
    boolean existsByPanNumber(String panNumber);

    /**
     * Check if CIN number exists
     * 
     * @param cinNumber the CIN number
     * @return true if exists, false otherwise
     */
    boolean existsByCinNumber(String cinNumber);

    /**
     * Count vendors by status
     *
     * @param status the vendor status
     * @return count of vendors
     */
    long countByStatus(Status status);

    /**
     * Find vendor by company code and vendor ID
     *
     * @param companyCode the company code
     * @param vendorId the vendor ID
     * @return Optional containing the vendor if found
     */
    @Query("SELECT v FROM Vendor v WHERE v.companyCode = :companyCode AND v.vendorId = :vendorId")
    Optional<Vendor> findByCompanyCodeAndVendorId(@Param("companyCode") Long companyCode, @Param("vendorId") UUID vendorId);

    /**
     * Find vendor by company code and user ID
     *
     * @param companyCode the company code
     * @param userId the user ID
     * @return Optional containing the vendor if found
     */
    @Query("SELECT v FROM Vendor v WHERE v.companyCode = :companyCode AND v.userId = :userId")
    Optional<Vendor> findByCompanyCodeAndUserId(@Param("companyCode") Long companyCode, @Param("userId") Long userId);

    /**
     * Find recently registered vendors
     * 
     * @param dateTime the date time threshold
     * @return List of recently registered vendors
     */
    @Query("SELECT v FROM Vendor v WHERE v.createdAt >= :dateTime ORDER BY v.createdAt DESC")
    List<Vendor> findRecentlyRegistered(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Find active vendors with recent activity
     * 
     * @param dateTime the date time threshold
     * @return List of active vendors
     */
    @Query("SELECT v FROM Vendor v WHERE v.status = 'ACTIVE' AND v.lastActivityAt >= :dateTime ORDER BY v.lastActivityAt DESC")
    List<Vendor> findActiveWithRecentActivity(@Param("dateTime") LocalDateTime dateTime);

    Optional<Vendor> findByCompanyCode(long companyCode);
}
