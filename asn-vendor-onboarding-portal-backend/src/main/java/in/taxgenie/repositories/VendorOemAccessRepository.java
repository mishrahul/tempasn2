package in.taxgenie.repositories;

import in.taxgenie.entities.OemMaster;
import in.taxgenie.entities.Vendor;
import in.taxgenie.entities.VendorOemAccess;
import in.taxgenie.entities.enums.AccessLevel;
import in.taxgenie.entities.enums.AccessStatus;
import in.taxgenie.repositories.base.BaseRepository;
import in.taxgenie.viewmodels.onboarding.ApiCredentialsResponseViewModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for VendorOemAccess entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface VendorOemAccessRepository extends BaseRepository<VendorOemAccess> {

    /**
     * Find vendor-OEM access by vendor and OEM
     * 
     * @param vendor the vendor
     * @param oem the OEM
     * @return Optional containing the access record if found
     */
    Optional<VendorOemAccess> findByVendorAndOem(Vendor vendor, OemMaster oem);

    /**
     * Find access records by vendor
     * 
     * @param vendor the vendor
     * @return List of access records
     */
    List<VendorOemAccess> findByVendor(Vendor vendor);

    /**
     * Find access records by OEM
     * 
     * @param oem the OEM
     * @return List of access records
     */
    List<VendorOemAccess> findByOem(OemMaster oem);

    /**
     * Find access records by vendor code and OEM
     * 
     * @param vendorCode the vendor code
     * @param oem the OEM
     * @return Optional containing the access record if found
     */
    Optional<VendorOemAccess> findByVendorCodeAndOem(String vendorCode, OemMaster oem);

    /**
     * Find access records by access status
     * 
     * @param accessStatus the access status
     * @return List of access records
     */
    List<VendorOemAccess> findByAccessStatus(AccessStatus accessStatus);

    /**
     * Find active access records by vendor
     * 
     * @param vendor the vendor
     * @return List of active access records
     */
    List<VendorOemAccess> findByVendorAndAccessStatus(Vendor vendor, AccessStatus accessStatus);

    /**
     * Find access records by OEM and access status
     * 
     * @param oem the OEM
     * @param accessStatus the access status
     * @return List of access records
     */
    List<VendorOemAccess> findByOemAndAccessStatus(OemMaster oem, AccessStatus accessStatus);

    /**
     * Find access records by access level
     * 
     * @param accessLevel the access level
     * @return List of access records
     */
    List<VendorOemAccess> findByAccessLevel(AccessLevel accessLevel);

    /**
     * Find expired access records
     * 
     * @param currentDateTime the current date time
     * @return List of expired access records
     */
    List<VendorOemAccess> findByExpiresAtBefore(LocalDateTime currentDateTime);

    /**
     * Find access records with recent activity
     * 
     * @param dateTime the date time threshold
     * @return List of access records with recent activity
     */
    List<VendorOemAccess> findByLastAccessedAtAfter(LocalDateTime dateTime);

    /**
     * Check if vendor code exists for OEM
     * 
     * @param vendorCode the vendor code
     * @param oem the OEM
     * @return true if exists, false otherwise
     */
    boolean existsByVendorCodeAndOem(String vendorCode, OemMaster oem);

    /**
     * Check if vendor has access to OEM
     * 
     * @param vendor the vendor
     * @param oem the OEM
     * @return true if has access, false otherwise
     */
    boolean existsByVendorAndOem(Vendor vendor, OemMaster oem);

    /**
     * Count access records by OEM and status
     * 
     * @param oem the OEM
     * @param accessStatus the access status
     * @return count of access records
     */
    long countByOemAndAccessStatus(OemMaster oem, AccessStatus accessStatus);

    /**
     * Count access records by vendor and status
     * 
     * @param vendor the vendor
     * @param accessStatus the access status
     * @return count of access records
     */
    long countByVendorAndAccessStatus(Vendor vendor, AccessStatus accessStatus);

    /**
     * Find top API users by OEM
     *
     * @param oem the OEM
     * @return List of top API users (limited to 20)
     */
    @Query("SELECT voa FROM VendorOemAccess voa WHERE voa.oem = :oem ORDER BY voa.totalApiCalls DESC LIMIT 20")
    List<VendorOemAccess> findTopApiUsersByOem(@Param("oem") OemMaster oem);

    /**
     * Find vendors with high ASN generation
     *
     * @param threshold the ASN generation threshold
     * @return List of access records with high ASN generation
     */
    @Query("SELECT voa FROM VendorOemAccess voa WHERE voa.totalAsnGenerated >= :threshold ORDER BY voa.totalAsnGenerated DESC")
    List<VendorOemAccess> findHighAsnGenerators(@Param("threshold") Long threshold);

    /**
     * Find access records by vendor ID
     *
     * @param vendorId the vendor ID
     * @return List of access records
     */
    @Query("SELECT voa FROM VendorOemAccess voa WHERE voa.vendor.vendorId = :vendorId")
    List<VendorOemAccess> findByVendorId(@Param("vendorId") UUID vendorId);

    /**
     * Find access record by vendor ID and OEM ID
     *
     * @param vendorId the vendor ID
     * @param oemId the OEM ID
     * @return Optional containing the access record if found
     */
    @Query("SELECT voa FROM VendorOemAccess voa WHERE voa.vendor.vendorId = :vendorId AND voa.oem.oemId = :oemId")
    Optional<VendorOemAccess> findByVendorIdAndOemId(@Param("vendorId") UUID vendorId, @Param("oemId") UUID oemId);

    /**
     * Find access records by vendor and company code
     *
     * @param vendor the vendor
     * @param companyCode the company code
     * @return List of access records
     */
    @Query("SELECT voa FROM VendorOemAccess voa WHERE voa.vendor = :vendor AND voa.companyCode = :companyCode")
    List<VendorOemAccess> findByVendorAndCompanyCode(@Param("vendor") Vendor vendor, @Param("companyCode") Long companyCode);

    /**
     * Find access record by vendor, OEM and company code
     *
     * @param vendor the vendor
     * @param oem the OEM
     * @param companyCode the company code
     * @return Optional containing the access record if found
     */
    @Query("SELECT voa FROM VendorOemAccess voa WHERE voa.vendor = :vendor AND voa.oem = :oem AND voa.companyCode = :companyCode")
    Optional<VendorOemAccess> findByVendorAndOemAndCompanyCode(@Param("vendor") Vendor vendor, @Param("oem") OemMaster oem, @Param("companyCode") Long companyCode);

    Optional<VendorOemAccess> findByVendorAndVendorCode(Vendor vendor, String vendorCode);

    //ApiCredentialsResponseViewModel findCredentialByGstinAndVendorCode(String gstin, String vendorCode);

}
