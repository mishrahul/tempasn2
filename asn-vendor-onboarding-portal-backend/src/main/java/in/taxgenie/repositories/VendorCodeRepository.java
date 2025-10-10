package in.taxgenie.repositories;

import in.taxgenie.entities.VendorCode;
import in.taxgenie.entities.VendorGstin;
import in.taxgenie.entities.enums.Status;
import in.taxgenie.repositories.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for VendorCode entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface VendorCodeRepository extends BaseRepository<VendorCode> {

    /**
     * Find vendor code by code string
     * 
     * @param vendorCode the vendor code string
     * @return Optional containing the vendor code if found
     */
    Optional<VendorCode> findByVendorCode(String vendorCode);

    /**
     * Find all vendor codes for a GSTIN
     * 
     * @param vendorGstin the vendor GSTIN
     * @return List of vendor codes
     */
    List<VendorCode> findByVendorGstin(VendorGstin vendorGstin);

    /**
     * Find all vendor codes for a GSTIN by GSTIN ID
     * 
     * @param gstinId the GSTIN ID
     * @return List of vendor codes
     */
    @Query("SELECT vc FROM VendorCode vc WHERE vc.vendorGstin.gstinId = :gstinId")
    List<VendorCode> findByGstinId(@Param("gstinId") UUID gstinId);

    /**
     * Find all vendor codes for a GSTIN and company code
     * 
     * @param vendorGstin the vendor GSTIN
     * @param companyCode the company code
     * @return List of vendor codes
     */
    @Query("SELECT vc FROM VendorCode vc WHERE vc.vendorGstin = :vendorGstin AND vc.companyCode = :companyCode")
    List<VendorCode> findByVendorGstinAndCompanyCode(@Param("vendorGstin") VendorGstin vendorGstin, @Param("companyCode") Long companyCode);

    /**
     * Find vendor code by ID and company code
     * 
     * @param vendorCodeId the vendor code ID
     * @param companyCode the company code
     * @return Optional containing the vendor code if found
     */
    @Query("SELECT vc FROM VendorCode vc WHERE vc.vendorCodeId = :vendorCodeId AND vc.companyCode = :companyCode")
    Optional<VendorCode> findByVendorCodeIdAndCompanyCode(@Param("vendorCodeId") UUID vendorCodeId, @Param("companyCode") Long companyCode);

    /**
     * Find vendor code by code string and company code
     * 
     * @param vendorCode the vendor code string
     * @param companyCode the company code
     * @return Optional containing the vendor code if found
     */
    @Query("SELECT vc FROM VendorCode vc WHERE vc.vendorCode = :vendorCode AND vc.companyCode = :companyCode")
    Optional<VendorCode> findByVendorCodeAndCompanyCode(@Param("vendorCode") String vendorCode, @Param("companyCode") Long companyCode);

    /**
     * Find active vendor codes for a GSTIN
     * 
     * @param vendorGstin the vendor GSTIN
     * @param status the status
     * @return List of vendor codes
     */
    List<VendorCode> findByVendorGstinAndStatus(VendorGstin vendorGstin, Status status);

    /**
     * Count vendor codes for a GSTIN
     * 
     * @param vendorGstin the vendor GSTIN
     * @return count of vendor codes
     */
    long countByVendorGstin(VendorGstin vendorGstin);

    /**
     * Count vendor codes for a GSTIN and company code
     * 
     * @param vendorGstin the vendor GSTIN
     * @param companyCode the company code
     * @return count of vendor codes
     */
    @Query("SELECT COUNT(vc) FROM VendorCode vc WHERE vc.vendorGstin = :vendorGstin AND vc.companyCode = :companyCode")
    long countByVendorGstinAndCompanyCode(@Param("vendorGstin") VendorGstin vendorGstin, @Param("companyCode") Long companyCode);

    /**
     * Check if vendor code exists
     * 
     * @param vendorCode the vendor code string
     * @return true if exists, false otherwise
     */
    boolean existsByVendorCode(String vendorCode);

    /**
     * Check if vendor code exists for company
     * 
     * @param vendorCode the vendor code string
     * @param companyCode the company code
     * @return true if exists, false otherwise
     */
    @Query("SELECT COUNT(vc) > 0 FROM VendorCode vc WHERE vc.vendorCode = :vendorCode AND vc.companyCode = :companyCode")
    boolean existsByVendorCodeAndCompanyCode(@Param("vendorCode") String vendorCode, @Param("companyCode") Long companyCode);
}

