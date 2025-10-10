package in.taxgenie.repositories;

import in.taxgenie.entities.ApiCredential;
import in.taxgenie.entities.OemMaster;
import in.taxgenie.entities.Vendor;
import in.taxgenie.entities.enums.Environment;
import in.taxgenie.repositories.base.BaseRepository;
import in.taxgenie.viewmodels.onboarding.ApiCredentialsResponseViewModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ApiCredential entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface ApiCredentialRepository extends BaseRepository<ApiCredential> {

    /**
     * Find API credential by API key hash
     * 
     * @param apiKeyHash the API key hash
     * @return Optional containing the API credential if found
     */
    Optional<ApiCredential> findByApiKeyHash(String apiKeyHash);

    /**
     * Find API credentials by vendor
     * 
     * @param vendor the vendor
     * @return List of API credentials
     */
    List<ApiCredential> findByVendor(Vendor vendor);

    /**
     * Find API credentials by OEM
     * 
     * @param oem the OEM
     * @return List of API credentials
     */
    List<ApiCredential> findByOem(OemMaster oem);

    /**
     * Find API credential by vendor, OEM, and environment
     * 
     * @param vendor the vendor
     * @param oem the OEM
     * @param environment the environment
     * @return Optional containing the API credential if found
     */
    Optional<ApiCredential> findByVendorAndOemAndEnvironment(Vendor vendor, OemMaster oem, Environment environment);

    /**
     * Find active API credentials by vendor
     * 
     * @param vendor the vendor
     * @return List of active API credentials
     */
    List<ApiCredential> findByVendorAndIsActiveTrue(Vendor vendor);

    /**
     * Find active API credentials by OEM
     * 
     * @param oem the OEM
     * @return List of active API credentials
     */
    List<ApiCredential> findByOemAndIsActiveTrue(OemMaster oem);

    /**
     * Find API credentials by environment
     * 
     * @param environment the environment
     * @return List of API credentials
     */
    List<ApiCredential> findByEnvironment(Environment environment);

    /**
     * Find API credentials by active status
     * 
     * @param isActive the active status
     * @return List of API credentials
     */
    List<ApiCredential> findByIsActive(Boolean isActive);

    /**
     * Find expired API credentials
     * 
     * @param currentDateTime the current date time
     * @return List of expired API credentials
     */
    List<ApiCredential> findByExpiresAtBefore(LocalDateTime currentDateTime);

    /**
     * Find API credentials expiring soon
     * 
     * @param currentDateTime the current date time
     * @param expirationThreshold the expiration threshold
     * @return List of API credentials expiring soon
     */
    @Query("SELECT ac FROM ApiCredential ac WHERE ac.expiresAt BETWEEN :currentDateTime AND :expirationThreshold AND ac.isActive = true")
    List<ApiCredential> findCredentialsExpiringSoon(@Param("currentDateTime") LocalDateTime currentDateTime, 
                                                    @Param("expirationThreshold") LocalDateTime expirationThreshold);

    /**
     * Find API credentials that need rotation
     * 
     * @param rotationThreshold the rotation threshold date
     * @return List of API credentials that need rotation
     */
    @Query("SELECT ac FROM ApiCredential ac WHERE ac.lastRotatedAt < :rotationThreshold AND ac.isActive = true")
    List<ApiCredential> findCredentialsNeedingRotation(@Param("rotationThreshold") LocalDateTime rotationThreshold);

    /**
     * Find API credentials by vendor and environment
     * 
     * @param vendor the vendor
     * @param environment the environment
     * @return List of API credentials
     */
    List<ApiCredential> findByVendorAndEnvironment(Vendor vendor, Environment environment);

    /**
     * Find API credentials by OEM and environment
     * 
     * @param oem the OEM
     * @param environment the environment
     * @return List of API credentials
     */
    List<ApiCredential> findByOemAndEnvironment(OemMaster oem, Environment environment);

    /**
     * Check if API key hash exists
     * 
     * @param apiKeyHash the API key hash
     * @return true if exists, false otherwise
     */
    boolean existsByApiKeyHash(String apiKeyHash);

    /**
     * Check if vendor has credentials for OEM and environment
     * 
     * @param vendor the vendor
     * @param oem the OEM
     * @param environment the environment
     * @return true if exists, false otherwise
     */
    boolean existsByVendorAndOemAndEnvironment(Vendor vendor, OemMaster oem, Environment environment);

    /**
     * Count API credentials by vendor
     * 
     * @param vendor the vendor
     * @return count of API credentials
     */
    long countByVendor(Vendor vendor);

    /**
     * Count active API credentials by vendor
     * 
     * @param vendor the vendor
     * @return count of active API credentials
     */
    long countByVendorAndIsActiveTrue(Vendor vendor);

    /**
     * Count API credentials by OEM
     * 
     * @param oem the OEM
     * @return count of API credentials
     */
    long countByOem(OemMaster oem);

    /**
     * Count active API credentials by OEM
     * 
     * @param oem the OEM
     * @return count of active API credentials
     */
    long countByOemAndIsActiveTrue(OemMaster oem);

    /**
     * Find recently created API credentials
     *
     * @param dateTime the date time threshold
     * @return List of recently created API credentials
     */
    @Query("SELECT ac FROM ApiCredential ac WHERE ac.createdAt >= :dateTime ORDER BY ac.createdAt DESC")
    List<ApiCredential> findRecentlyCreated(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Find API credential by vendor, OEM and company code
     *
     * @param vendor the vendor
     * @param oem the OEM
     * @param companyCode the company code
     * @return Optional containing the API credential if found
     */
    @Query("SELECT ac FROM ApiCredential ac WHERE ac.vendor = :vendor AND ac.oem = :oem AND ac.companyCode = :companyCode")
    Optional<ApiCredential> findByVendorAndOemAndCompanyCode(@Param("vendor") Vendor vendor,
                                                            @Param("oem") OemMaster oem,
                                                            @Param("companyCode") Long companyCode);

    boolean existsByVendor(Vendor vendor);

   // Optional<ApiCredential> findByVendorAndOem(Vendor vendor, OemMaster oem);


}
