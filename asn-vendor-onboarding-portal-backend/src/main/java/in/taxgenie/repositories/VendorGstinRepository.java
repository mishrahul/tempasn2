package in.taxgenie.repositories;

import in.taxgenie.entities.Vendor;
import in.taxgenie.entities.VendorGstin;
import in.taxgenie.repositories.base.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for VendorGstin entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface VendorGstinRepository extends BaseRepository<VendorGstin> {

    /**
     * Find vendor GSTIN by GSTIN number
     * 
     * @param gstin the GSTIN number
     * @return Optional containing the vendor GSTIN if found
     */
    Optional<VendorGstin> findByGstin(String gstin);

    /**
     * Find all GSTINs for a vendor
     * 
     * @param vendor the vendor
     * @return List of vendor GSTINs
     */
    List<VendorGstin> findByVendor(Vendor vendor);

    /**
     * Find primary GSTIN for a vendor
     * 
     * @param vendor the vendor
     * @return Optional containing the primary GSTIN if found
     */
    Optional<VendorGstin> findByVendorAndIsPrimaryTrue(Vendor vendor);

    /**
     * Find verified GSTINs for a vendor
     * 
     * @param vendor the vendor
     * @return List of verified vendor GSTINs
     */
    List<VendorGstin> findByVendorAndIsVerifiedTrue(Vendor vendor);

    /**
     * Find GSTINs by state code
     * 
     * @param stateCode the state code
     * @return List of vendor GSTINs
     */
    List<VendorGstin> findByStateCode(String stateCode);

    /**
     * Find GSTINs by vendor and state code
     * 
     * @param vendor the vendor
     * @param stateCode the state code
     * @return List of vendor GSTINs
     */
    List<VendorGstin> findByVendorAndStateCode(Vendor vendor, String stateCode);

    /**
     * Find unverified GSTINs
     * 
     * @return List of unverified vendor GSTINs
     */
    List<VendorGstin> findByIsVerifiedFalse();

    /**
     * Check if GSTIN exists
     * 
     * @param gstin the GSTIN number
     * @return true if exists, false otherwise
     */
    boolean existsByGstin(String gstin);

    /**
     * Check if vendor has primary GSTIN
     * 
     * @param vendor the vendor
     * @return true if has primary GSTIN, false otherwise
     */
    boolean existsByVendorAndIsPrimaryTrue(Vendor vendor);

    /**
     * Count GSTINs for a vendor
     * 
     * @param vendor the vendor
     * @return count of GSTINs
     */
    long countByVendor(Vendor vendor);

    /**
     * Count verified GSTINs for a vendor
     * 
     * @param vendor the vendor
     * @return count of verified GSTINs
     */
    long countByVendorAndIsVerifiedTrue(Vendor vendor);

    /**
     * Find GSTINs by state code and verification status
     *
     * @param stateCode the state code
     * @param isVerified the verification status
     * @return List of vendor GSTINs
     */
    @Query("SELECT vg FROM VendorGstin vg WHERE vg.stateCode = :stateCode AND vg.isVerified = :isVerified")
    List<VendorGstin> findByStateCodeAndVerificationStatus(@Param("stateCode") String stateCode,
                                                           @Param("isVerified") Boolean isVerified);

    /**
     * Find all GSTINs for a vendor by vendor ID
     *
     * @param vendorId the vendor ID
     * @return List of vendor GSTINs
     */
    @Query("SELECT vg FROM VendorGstin vg WHERE vg.vendor.vendorId = :vendorId")
    List<VendorGstin> findByVendorId(@Param("vendorId") UUID vendorId);

    /**
     * Update primary status for vendor GSTINs
     *
     * @param vendor the vendor
     * @param companyCode the company code
     */
    @Modifying
    @Query("UPDATE VendorGstin vg SET vg.isPrimary = false WHERE vg.vendor = :vendor AND vg.companyCode = :companyCode")
    void updatePrimaryStatusForVendor(@Param("vendor") Vendor vendor, @Param("companyCode") Long companyCode);

    /**
     * Count GSTINs by vendor and company code
     *
     * @param vendor the vendor
     * @param companyCode the company code
     * @return count of GSTINs
     */
    @Query("SELECT COUNT(vg) FROM VendorGstin vg WHERE vg.vendor = :vendor AND vg.companyCode = :companyCode")
    long countByVendorAndCompanyCode(@Param("vendor") Vendor vendor, @Param("companyCode") Long companyCode);

    /**
     * Find GSTINs by vendor and company code
     *
     * @param vendor the vendor
     * @param companyCode the company code
     * @return List of vendor GSTINs
     */
    @Query("SELECT vg FROM VendorGstin vg WHERE vg.vendor = :vendor AND vg.companyCode = :companyCode")
    List<VendorGstin> findByVendorAndCompanyCode(@Param("vendor") Vendor vendor, @Param("companyCode") Long companyCode);

    /**
     * Find GSTIN by GSTIN number and company code
     *
     * @param gstin the GSTIN number
     * @param companyCode the company code
     * @return Optional containing the vendor GSTIN if found
     */
    @Query("SELECT vg FROM VendorGstin vg WHERE vg.gstin = :gstin AND vg.companyCode = :companyCode")
    Optional<VendorGstin> findByGstinAndCompanyCode(@Param("gstin") String gstin, @Param("companyCode") Long companyCode);

    /**
     * Find GSTIN by GSTIN ID and company code
     *
     * @param gstinId the GSTIN ID
     * @param companyCode the company code
     * @return Optional containing the vendor GSTIN if found
     */
    @Query("SELECT vg FROM VendorGstin vg WHERE vg.gstinId = :gstinId AND vg.companyCode = :companyCode")
    Optional<VendorGstin> findByGstinIdAndCompanyCode(@Param("gstinId") UUID gstinId, @Param("companyCode") Long companyCode);

    Optional<VendorGstin> findByGstinIgnoreCase(String gstin);

}
