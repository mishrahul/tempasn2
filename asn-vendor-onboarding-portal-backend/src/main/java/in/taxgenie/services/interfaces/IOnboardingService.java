package in.taxgenie.services.interfaces;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.viewmodels.onboarding.*;

/**
 * Service interface for onboarding operations
 */
public interface IOnboardingService {

    /**
     * Get onboarding progress for the current vendor
     * @param auth Authentication context
     * @param oemId OEM ID
     * @return Onboarding progress details
     */
    OnboardingProgressViewModel getOnboardingProgress(IAuthContextViewModel auth, String oemId);

    /**
     * Confirm ASN 2.1 activation
     * @param auth Authentication context
     * @param request ASN confirmation request
     * @return ASN confirmation response
     */
    AsnConfirmationResponseViewModel confirmAsnActivation(IAuthContextViewModel auth, 
                                                         AsnConfirmationRequestViewModel request);

    /**
     * Initiate payment for onboarding
     * @param auth Authentication context
     * @param request Payment initiation request
     * @return Payment initiation response
     */
    PaymentInitiationResponseViewModel initiatePayment(IAuthContextViewModel auth, 
                                                      PaymentInitiationRequestViewModel request);

    /**
     * Get payment status
     * @param auth Authentication context
     * @param transactionId Transaction ID
     * @return Payment status details
     */
    PaymentStatusViewModel getPaymentStatus(IAuthContextViewModel auth, String transactionId);

    /**
     * Select deployment method
     * @param auth Authentication context
     * @param request Deployment selection request
     * @return Deployment selection response
     */
    DeploymentSelectionResponseViewModel selectDeploymentMethod(IAuthContextViewModel auth, 
                                                               DeploymentSelectionRequestViewModel request);

    /**
     * Create API credentials for self-deployment
     * @param auth Authentication context
     * @param request API credentials request
     * @return API credentials response
     */
    ApiCredentialsResponseViewModel createApiCredentials(IAuthContextViewModel auth, 
                                                        ApiCredentialsRequestViewModel request);

    /**
     * Get API credentials status
     * @param auth Authentication context
     * @param oemId OEM ID
     * @return API credentials details
     */
    ApiCredentialsResponseViewModel getApiCredentials(IAuthContextViewModel auth, String oemId);

    /**
     * Update onboarding step status
     * @param auth Authentication context
     * @param oemId OEM ID
     * @param stepId Step ID
     * @param status New status
     * @return Updated progress
     */
    OnboardingProgressViewModel updateStepStatus(IAuthContextViewModel auth,
                                                 String oemId,
                                                 String stepId,
                                                 String status);

    /**
     * Get available subscription plans for OEM
     * @param auth Authentication context
     * @param oemId OEM ID
     * @return List of available plans
     */
    SubscriptionPlansViewModel getAvailablePlans(IAuthContextViewModel auth, String oemId);

    /**
     * Complete onboarding process
     * @param auth Authentication context
     * @param oemId OEM ID
     * @return Completion status
     */
    OnboardingCompletionViewModel completeOnboarding(IAuthContextViewModel auth, String oemId);
}
