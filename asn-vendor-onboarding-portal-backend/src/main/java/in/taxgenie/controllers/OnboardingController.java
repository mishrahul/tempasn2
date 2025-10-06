package in.taxgenie.controllers;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.auth.IAuthContextFactory;
import in.taxgenie.response.interfaces.factory.IServerResponseFactory;
import in.taxgenie.response.interfaces.infra.IServerResponseWithBody;
import in.taxgenie.services.interfaces.IOnboardingService;
import in.taxgenie.viewmodels.onboarding.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST Controller for onboarding operations
 *
 * @author ASN Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/onboarding")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OnboardingController {

    private final IOnboardingService onboardingService;
    private final IAuthContextFactory authContextFactory;
    private final IServerResponseFactory serverResponseFactory;

    /**
     * Test endpoint to verify onboarding APIs are working
     */
    @GetMapping("/test")
    public ResponseEntity<String> testOnboardingApi() {
        log.info("Testing onboarding API - method called");
        try {
            String response = "Onboarding API is working! All endpoints are available.";
            log.info("Returning response: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in test endpoint", e);
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Get onboarding progress for a specific OEM
     *
     * @param id the OEM ID
     * @return onboarding progress information
     */
    @GetMapping("/progress/{id}")
    public ResponseEntity<IServerResponseWithBody<OnboardingProgressViewModel>> getOnboardingProgress(@PathVariable String id) {
        log.info("Getting onboarding progress for OEM ID: {}", id);

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            OnboardingProgressViewModel progress = onboardingService.getOnboardingProgress(auth, id);
            IServerResponseWithBody<OnboardingProgressViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Onboarding progress retrieved successfully", true, progress);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting onboarding progress for OEM ID: {}", id, e);
            IServerResponseWithBody<OnboardingProgressViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(400, "Failed to get onboarding progress: " + e.getMessage(), false, null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Confirm ASN 2.1 activation
     *
     * @param request the ASN confirmation request
     * @return ASN confirmation response
     */
    @PostMapping("/confirm-asn")
    public ResponseEntity<IServerResponseWithBody<AsnConfirmationResponseViewModel>> confirmAsnActivation(
            @Valid @RequestBody AsnConfirmationRequestViewModel request) {
        log.info("Confirming ASN activation for OEM: {}", request.getOemCode());

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            AsnConfirmationResponseViewModel asnResponse = onboardingService.confirmAsnActivation(auth, request);
            IServerResponseWithBody<AsnConfirmationResponseViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "ASN activation confirmed successfully", true, asnResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error confirming ASN activation for OEM: {}", request.getOemCode(), e);
            IServerResponseWithBody<AsnConfirmationResponseViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(400, "Failed to confirm ASN activation: " + e.getMessage(), false, null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Initiate payment for onboarding
     *
     * @param request the payment initiation request
     * @return payment initiation response
     */
    @PostMapping("/initiate-payment")
    public ResponseEntity<IServerResponseWithBody<PaymentInitiationResponseViewModel>> initiatePayment(
            @Valid @RequestBody PaymentInitiationRequestViewModel request) {
        log.info("Initiating payment for OEM: {} and plan: {}", request.getOemCode(), request.getPlanCode());

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            PaymentInitiationResponseViewModel paymentResponse = onboardingService.initiatePayment(auth, request);
            IServerResponseWithBody<PaymentInitiationResponseViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Payment initiated successfully", true, paymentResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error initiating payment for OEM: {} and plan: {}",
                request.getOemCode(), request.getPlanCode(), e);
            IServerResponseWithBody<PaymentInitiationResponseViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(400, "Failed to initiate payment: " + e.getMessage(), false, null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get payment status
     *
     * @param transactionId the transaction ID
     * @return payment status information
     */
    @GetMapping("/payment-status/{transactionId}")
    public ResponseEntity<IServerResponseWithBody<PaymentStatusViewModel>> getPaymentStatus(@PathVariable String transactionId) {
        log.info("Getting payment status for transaction: {}", transactionId);

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            PaymentStatusViewModel status = onboardingService.getPaymentStatus(auth, transactionId);
            IServerResponseWithBody<PaymentStatusViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Payment status retrieved successfully", true, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting payment status for transaction: {}", transactionId, e);
            IServerResponseWithBody<PaymentStatusViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(400, "Failed to get payment status: " + e.getMessage(), false, null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Select deployment method
     *
     * @param request the deployment selection request
     * @return deployment selection response
     */
    @PostMapping("/select-deployment")
    public ResponseEntity<IServerResponseWithBody<DeploymentSelectionResponseViewModel>> selectDeploymentMethod(
            @Valid @RequestBody DeploymentSelectionRequestViewModel request) {
        log.info("Selecting deployment method: {} for OEM: {}",
            request.getDeploymentType(), request.getOemCode());

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            DeploymentSelectionResponseViewModel deploymentResponse = onboardingService.selectDeploymentMethod(auth, request);
            IServerResponseWithBody<DeploymentSelectionResponseViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Deployment method selected successfully", true, deploymentResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error selecting deployment method for OEM: {}", request.getOemCode(), e);
            IServerResponseWithBody<DeploymentSelectionResponseViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(400, "Failed to select deployment method: " + e.getMessage(), false, null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Create API credentials
     *
     * @param request the API credentials request
     * @return API credentials response
     */
    @PostMapping("/create-credentials")
    public ResponseEntity<IServerResponseWithBody<ApiCredentialsResponseViewModel>> createApiCredentials(
            @Valid @RequestBody ApiCredentialsRequestViewModel request) {
        log.info("Creating API credentials for OEM: {}", request.getOemCode());

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            ApiCredentialsResponseViewModel credentialsResponse = onboardingService.createApiCredentials(auth, request);
            IServerResponseWithBody<ApiCredentialsResponseViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "API credentials created successfully", true, credentialsResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error creating API credentials for OEM: {}", request.getOemCode(), e);
            IServerResponseWithBody<ApiCredentialsResponseViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(400, "Failed to create API credentials: " + e.getMessage(), false, null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get API credentials
     *
     * @param id the OEM ID
     * @return API credentials information
     */
    @GetMapping("/credentials/{id}")
    public ResponseEntity<IServerResponseWithBody<ApiCredentialsResponseViewModel>> getApiCredentials(@PathVariable String id) {
        log.info("Getting API credentials for OEM ID: {}", id);

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            ApiCredentialsResponseViewModel credentials = onboardingService.getApiCredentials(auth, id);
            IServerResponseWithBody<ApiCredentialsResponseViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "API credentials retrieved successfully", true, credentials);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting API credentials for OEM ID: {}", id, e);
            IServerResponseWithBody<ApiCredentialsResponseViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(400, "Failed to get API credentials: " + e.getMessage(), false, null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Update step status
     *
     * @param id the OEM ID
     * @param stepId the step ID
     * @param status the new status
     * @return updated onboarding progress
     */
    @PutMapping("/step-status/{id}/{stepId}")
    public ResponseEntity<IServerResponseWithBody<OnboardingProgressViewModel>> updateStepStatus(
            @PathVariable String id,
            @PathVariable String stepId,
            @RequestParam String status) {
        log.info("Updating step status for OEM ID: {}, step: {}, status: {}", id, stepId, status);

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            OnboardingProgressViewModel progress = onboardingService.updateStepStatus(auth, id, stepId, status);
            IServerResponseWithBody<OnboardingProgressViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Step status updated successfully", true, progress);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating step status for OEM ID: {}, step: {}", id, stepId, e);
            IServerResponseWithBody<OnboardingProgressViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(400, "Failed to update step status: " + e.getMessage(), false, null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get available subscription plans
     *
     * @param id the OEM ID
     * @return available subscription plans
     */
    @GetMapping("/plans/{id}")
    public ResponseEntity<IServerResponseWithBody<SubscriptionPlansViewModel>> getAvailablePlans(@PathVariable String id) {
        log.info("Getting available plans for OEM ID: {}", id);

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            SubscriptionPlansViewModel plans = onboardingService.getAvailablePlans(auth, id);
            IServerResponseWithBody<SubscriptionPlansViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Subscription plans retrieved successfully", true, plans);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting available plans for OEM ID: {}", id, e);
            IServerResponseWithBody<SubscriptionPlansViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(400, "Failed to get subscription plans: " + e.getMessage(), false, null);
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Complete onboarding process
     *
     * @param id the OEM ID
     * @return onboarding completion information
     */
    @PostMapping("/complete/{id}")
    public ResponseEntity<IServerResponseWithBody<OnboardingCompletionViewModel>> completeOnboarding(@PathVariable String id) {
        log.info("Completing onboarding for OEM ID: {}", id);

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            OnboardingCompletionViewModel completion = onboardingService.completeOnboarding(auth, id);
            IServerResponseWithBody<OnboardingCompletionViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Onboarding completed successfully", true, completion);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error completing onboarding for OEM ID: {}", id, e);
            IServerResponseWithBody<OnboardingCompletionViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(400, "Failed to complete onboarding: " + e.getMessage(), false, null);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
