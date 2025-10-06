package in.taxgenie.services.interfaces;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.viewmodels.oem.AvailableOemsResponseViewModel;
import in.taxgenie.viewmodels.oem.OemViewModel;
import in.taxgenie.viewmodels.oem.SelectOemRequestViewModel;
import in.taxgenie.viewmodels.oem.SelectOemResponseViewModel;

/**
 * Interface for OEM portal service operations
 */
public interface IOemPortalService {

    /**
     * Gets available OEMs for the authenticated vendor
     * @param auth Authentication context
     * @return Available OEMs response
     */
    AvailableOemsResponseViewModel getAvailableOems(IAuthContextViewModel auth);

    /**
     * Gets specific OEM details
     * @param auth Authentication context
     * @param oemId OEM identifier
     * @return OEM view model
     */
    OemViewModel getOemDetails(IAuthContextViewModel auth, String oemId);

    /**
     * Selects an OEM for the user session
     * @param auth Authentication context
     * @param request OEM selection request
     * @return OEM selection response
     */
    SelectOemResponseViewModel selectOem(IAuthContextViewModel auth, SelectOemRequestViewModel request);

    /**
     * Refreshes OEM access permissions for the vendor
     * @param auth Authentication context
     * @return Updated access information
     */
    Object refreshOemAccess(IAuthContextViewModel auth);

    /**
     * Requests access to a specific OEM
     * @param auth Authentication context
     * @param oemId OEM identifier
     * @param requestData Access request data
     * @return Access request response
     */
    Object requestOemAccess(IAuthContextViewModel auth, String oemId, Object requestData);
}
