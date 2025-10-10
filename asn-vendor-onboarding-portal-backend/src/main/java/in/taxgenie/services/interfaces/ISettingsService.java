package in.taxgenie.services.interfaces;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.viewmodels.onboarding.ApiCredentialsResponseViewModel;
import in.taxgenie.viewmodels.settings.*;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for settings management operations
 */
public interface ISettingsService {
    
    /**
     * Get current company information
     * @param auth Authentication context
     * @return Company information
     */
    CompanyInfoResponseViewModel getCompanyInfo(IAuthContextViewModel auth);
    
    /**
     * Update company information
     * @param auth Authentication context
     * @param request Update request
     * @return Updated company information
     */
    CompanyInfoResponseViewModel updateCompanyInfo(IAuthContextViewModel auth, CompanyInfoUpdateRequestViewModel request);
    
    /**
     * Get GSTIN management information
     * @param auth Authentication context
     * @return GSTIN management details
     */
   // GstinManagementViewModel getGstinManagement(IAuthContextViewModel auth);
    
    /**
     * Create new GSTIN
     * @param auth Authentication context
     * @param request GSTIN creation request
     * @return Created GSTIN details
     */
    GstinManagementViewModel.GstinDetailViewModel createGstin(IAuthContextViewModel auth, GstinCreateRequestViewModel request);
    
    /**
     * Update GSTIN
     * @param auth Authentication context
     * @param gstinId GSTIN ID
     * @param request Update request
     * @return Updated GSTIN details
     */
    GstinManagementViewModel.GstinDetailViewModel updateGstin(IAuthContextViewModel auth, String gstinId, GstinUpdateRequestViewModel request);
    
    /**
     * Delete GSTIN
     * @param auth Authentication context
     * @param gstinId GSTIN ID
     * @return Success status
     */
    boolean deleteGstin(IAuthContextViewModel auth, String gstinId);
    
    /**
     * Get subscription and billing information
     * @param auth Authentication context
     * @return Subscription and billing details
     */
    SubscriptionBillingViewModel getSubscriptionBilling(IAuthContextViewModel auth);
    
    /**
     * Get OEM access management information
     * @param auth Authentication context
     * @return OEM access details
     */
    OemAccessManagementViewModel getOemAccessManagement(IAuthContextViewModel auth);
    
    /**
     * Switch to different OEM
     * @param auth Authentication context
     * @param request OEM switch request
     * @return Switch response
     */
    OemSwitchResponseViewModel switchOem(IAuthContextViewModel auth, OemSwitchRequestViewModel request);

    /**
     * Get vendor code management information
     * @param auth Authentication context
     * @return Vendor code management details
     */
    VendorCodeManagementViewModel getVendorCodeManagement(IAuthContextViewModel auth);

    /**
     * Get vendor codes for a specific GSTIN
     * @param auth Authentication context
     * @param gstinId GSTIN ID
     * @return Vendor code management details for the GSTIN
     */
    VendorCodeManagementViewModel getVendorCodesByGstin(IAuthContextViewModel auth, String gstinId);

    /**
     * Create new vendor code
     * @param auth Authentication context
     * @param request Vendor code creation request
     * @return Created vendor code details
     */
    VendorCodeManagementViewModel.VendorCodeDetailViewModel createVendorCode(IAuthContextViewModel auth, VendorCodeCreateRequestViewModel request);

    /**
     * Update vendor code
     * @param auth Authentication context
     * @param vendorCodeId Vendor code ID
     * @param request Update request
     * @return Updated vendor code details
     */
    VendorCodeManagementViewModel.VendorCodeDetailViewModel updateVendorCode(IAuthContextViewModel auth, String vendorCodeId, VendorCodeUpdateRequestViewModel request);

    /**
     * Delete vendor code
     * @param auth Authentication context
     * @param vendorCodeId Vendor code ID
     * @return Success status
     */
    boolean deleteVendorCode(IAuthContextViewModel auth, String vendorCodeId);

    GstinManagementViewModel getGstinManagement(IAuthContextViewModel auth, Pageable pageable);


   // ApiCredentialsResponseViewModel findCredentialByGstinAndVendorCode(String gstin, String vendorCode);

}
