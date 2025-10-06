package in.taxgenie.services.interfaces;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.viewmodels.settings.*;

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
    GstinManagementViewModel getGstinManagement(IAuthContextViewModel auth);
    
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
}
