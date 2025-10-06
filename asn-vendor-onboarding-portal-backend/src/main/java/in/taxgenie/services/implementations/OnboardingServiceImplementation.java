package in.taxgenie.services.implementations;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.entities.*;
import in.taxgenie.entities.enums.OnboardingStatus;
import in.taxgenie.entities.enums.PaymentStatus;
import in.taxgenie.entities.enums.Environment;
import in.taxgenie.repositories.*;
import in.taxgenie.services.interfaces.IOnboardingService;
import in.taxgenie.viewmodels.onboarding.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of onboarding service
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OnboardingServiceImplementation implements IOnboardingService {

    private final VendorRepository vendorRepository;
    private final OemMasterRepository oemMasterRepository;
    private final OnboardingProcessRepository onboardingProcessRepository;
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ApiCredentialRepository apiCredentialRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final OnboardingEventRepository onboardingEventRepository;

    @Override
    public OnboardingProgressViewModel getOnboardingProgress(IAuthContextViewModel auth, String oemId) {
        log.info("Getting onboarding progress for vendor: {} and OEM ID: {}", auth.getUserId(), oemId);

        try {
            // Get vendor
            Vendor vendor = getVendorByAuth(auth);

            // Get OEM by ID
            OemMaster oem = getOemById(oemId);

            // Get or create onboarding process
            OnboardingProcess onboardingProcess = onboardingProcessRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, auth.getCompanyCode())
                .orElseGet(() -> createInitialOnboardingProcess(vendor, oem, auth.getCompanyCode()));

            // Build progress response
            return buildOnboardingProgress(onboardingProcess);

        } catch (Exception e) {
            log.error("Error getting onboarding progress for vendor: {} and OEM ID: {}", auth.getUserId(), oemId, e);
            throw new RuntimeException("Failed to get onboarding progress: " + e.getMessage());
        }
    }

    @Override
    public AsnConfirmationResponseViewModel confirmAsnActivation(IAuthContextViewModel auth, 
                                                               AsnConfirmationRequestViewModel request) {
        log.info("Confirming ASN activation for vendor: {} and OEM: {}", auth.getUserId(), request.getOemCode());

        try {
            // Get vendor and OEM
            Vendor vendor = getVendorByAuth(auth);
            OemMaster oem = getOemByCode(request.getOemCode());

            // Get onboarding process
            OnboardingProcess onboardingProcess = onboardingProcessRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, auth.getCompanyCode())
                .orElseThrow(() -> new RuntimeException("Onboarding process not found"));

            // Update onboarding process
            onboardingProcess.setCurrentStep("PAYMENT");
            onboardingProcess.setStatus(OnboardingStatus.IN_PROGRESS);
            onboardingProcess.setUpdatedAt(LocalDateTime.now());
            
            // Update step data
            String stepData = """
                {
                    "asn_confirmation": {
                        "confirmed_at": "%s",
                        "acknowledgment": %s,
                        "terms_accepted": %s,
                        "compliance_confirmed": %s,
                        "notes": "%s"
                    }
                }
                """.formatted(
                    LocalDateTime.now(),
                    request.getAcknowledgment(),
                    request.getTermsAccepted(),
                    request.getComplianceConfirmed(),
                    request.getAdditionalNotes() != null ? request.getAdditionalNotes() : ""
                );
            onboardingProcess.setStepData(stepData);
            
            onboardingProcessRepository.save(onboardingProcess);

            // Create onboarding event
            createOnboardingEvent(onboardingProcess, "ASN_CONFIRMATION", 
                "ASN 2.1 activation confirmed", auth.getUserId());

            // Build response
            return AsnConfirmationResponseViewModel.builder()
                .confirmationId(UUID.randomUUID().toString())
                .status("CONFIRMED")
                .confirmedAt(LocalDateTime.now())
                .nextStep("PAYMENT")
                .nextStepDescription("Complete payment to proceed with deployment")
                .estimatedCompletionTime("2-3 business days")
                .requirements(AsnConfirmationResponseViewModel.RequirementsViewModel.builder()
                    .paymentRequired(true)
                    .deploymentSelectionRequired(true)
                    .apiCredentialsRequired(false)
                    .documentationRequired(false)
                    .build())
                .build();

        } catch (Exception e) {
            log.error("Error confirming ASN activation for vendor: {} and OEM: {}", 
                auth.getUserId(), request.getOemCode(), e);
            throw new RuntimeException("Failed to confirm ASN activation: " + e.getMessage());
        }
    }

    @Override
    public PaymentInitiationResponseViewModel initiatePayment(IAuthContextViewModel auth, 
                                                            PaymentInitiationRequestViewModel request) {
        log.info("Initiating payment for vendor: {} and OEM: {}", auth.getUserId(), request.getOemCode());

        try {
            // Get vendor and OEM
            Vendor vendor = getVendorByAuth(auth);
            OemMaster oem = getOemByCode(request.getOemCode());

            // Get subscription plan to validate it exists
            subscriptionPlanRepository
                .findByPlanCode(request.getPlanCode())
                .orElseThrow(() -> new RuntimeException("Subscription plan not found"));

            // Create payment transaction
            PaymentTransaction transaction = PaymentTransaction.builder()
                .vendor(vendor)
                .oem(oem)
                .transactionRef("TXN_" + System.currentTimeMillis())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(PaymentStatus.PENDING)
                .gatewayData(buildGatewayData(request))
                .initiatedAt(LocalDateTime.now())
                .build();

            transaction = paymentTransactionRepository.save(transaction);

            // Build response
            return PaymentInitiationResponseViewModel.builder()
                .transactionId(transaction.getTransactionId().toString())
                .transactionRef(transaction.getTransactionRef())
                .paymentUrl("https://payment-gateway.example.com/pay/" + transaction.getTransactionRef())
                .paymentToken("TOKEN_" + UUID.randomUUID().toString())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status("INITIATED")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .gatewayDetails(PaymentInitiationResponseViewModel.GatewayDetailsViewModel.builder()
                    .gatewayName("RazorPay")
                    .gatewayTransactionId("rzp_" + UUID.randomUUID().toString())
                    .redirectUrl("https://payment-gateway.example.com/redirect")
                    .instructions("Complete payment within 1 hour")
                    .build())
                .build();

        } catch (Exception e) {
            log.error("Error initiating payment for vendor: {} and OEM: {}", 
                auth.getUserId(), request.getOemCode(), e);
            throw new RuntimeException("Failed to initiate payment: " + e.getMessage());
        }
    }

    // Helper methods
    private Vendor getVendorByAuth(IAuthContextViewModel auth) {
        return vendorRepository.findByCompanyCodeAndUserId(auth.getCompanyCode(), auth.getUserId())
            .orElseThrow(() -> new RuntimeException("Vendor not found for user: " + auth.getUserId()));
    }

    private OemMaster getOemByCode(String oemCode) {
        return oemMasterRepository.findByOemCode(oemCode)
            .orElseThrow(() -> new RuntimeException("OEM not found: " + oemCode));
    }

    private OemMaster getOemById(String oemId) {
        return oemMasterRepository.findById(java.util.UUID.fromString(oemId))
            .orElseThrow(() -> new RuntimeException("OEM not found with ID: " + oemId));
    }

    private OnboardingProcess createInitialOnboardingProcess(Vendor vendor, OemMaster oem, Long companyCode) {
        OnboardingProcess process = OnboardingProcess.builder()
            .vendor(vendor)
            .oem(oem)
            .currentStep("CONFIRMATION")
            .status(OnboardingStatus.IN_PROGRESS) // Use IN_PROGRESS instead of INITIATED
            .stepData("{}")
            .startedAt(LocalDateTime.now()) // Ensure startedAt is set as fallback
            .build();
        process.setCompanyCode(companyCode);

        // Save and flush to ensure JPA lifecycle callbacks are executed
        OnboardingProcess savedProcess = onboardingProcessRepository.save(process);
        onboardingProcessRepository.flush();

        return savedProcess;
    }

    private OnboardingProgressViewModel buildOnboardingProgress(OnboardingProcess process) {
        // Get creation timestamp with null safety
        String creationTimestamp = process.getCreatedAt() != null
            ? process.getCreatedAt().toString()
            : process.getStartedAt() != null
                ? process.getStartedAt().toString()
                : LocalDateTime.now().toString();

        List<OnboardingProgressViewModel.StepProgressViewModel> steps = Arrays.asList(
            OnboardingProgressViewModel.StepProgressViewModel.builder()
                .id(1)
                .title("Account Setup")
                .description("Initial registration and vendor verification completed")
                .status(OnboardingProgressViewModel.StepStatus.COMPLETED)
                .completedAt(creationTimestamp)
                .estimatedDurationHours(1)
                .dependencies(Collections.emptyList())
                .build(),
            OnboardingProgressViewModel.StepProgressViewModel.builder()
                .id(2)
                .title("ASN 2.1 Confirmation")
                .description("Review and confirm implementation requirements")
                .status(getStepStatus(process.getCurrentStep(), "CONFIRMATION"))
                .estimatedDurationHours(1)
                .dependencies(List.of("Account Setup"))
                .build(),
            OnboardingProgressViewModel.StepProgressViewModel.builder()
                .id(3)
                .title("Payment")
                .description("Pay based on Deployment Method Selection")
                .status(getStepStatus(process.getCurrentStep(), "PAYMENT"))
                .estimatedDurationHours(1)
                .dependencies(List.of("ASN 2.1 Confirmation"))
                .build(),
            OnboardingProgressViewModel.StepProgressViewModel.builder()
                .id(4)
                .title("Deployment Method Selection")
                .description("Choose between self-deployment or assisted implementation")
                .status(getStepStatus(process.getCurrentStep(), "DEPLOYMENT"))
                .estimatedDurationHours(2)
                .dependencies(List.of("Payment"))
                .build()
        );

        int completedSteps = (int) steps.stream()
            .filter(step -> step.getStatus() == OnboardingProgressViewModel.StepStatus.COMPLETED)
            .count();

        return OnboardingProgressViewModel.builder()
            .percentage((completedSteps * 100) / steps.size())
            .completedSteps(completedSteps)
            .totalSteps(steps.size())
            .steps(steps)
            .currentStep(process.getCurrentStep())
            .nextStep(getNextStep(process.getCurrentStep()))
            .estimatedCompletionDays(calculateEstimatedDays(process.getCurrentStep()))
            .build();
    }

    private OnboardingProgressViewModel.StepStatus getStepStatus(String currentStep, String stepName) {
        List<String> stepOrder = Arrays.asList("CONFIRMATION", "PAYMENT", "DEPLOYMENT", "COMPLETION");
        int currentIndex = stepOrder.indexOf(currentStep);
        int stepIndex = stepOrder.indexOf(stepName);
        
        if (stepIndex < currentIndex) {
            return OnboardingProgressViewModel.StepStatus.COMPLETED;
        } else if (stepIndex == currentIndex) {
            return OnboardingProgressViewModel.StepStatus.CURRENT;
        } else {
            return OnboardingProgressViewModel.StepStatus.PENDING;
        }
    }

    private String getNextStep(String currentStep) {
        Map<String, String> nextSteps = Map.of(
            "CONFIRMATION", "PAYMENT",
            "PAYMENT", "DEPLOYMENT",
            "DEPLOYMENT", "COMPLETION",
            "COMPLETION", "COMPLETED"
        );
        return nextSteps.getOrDefault(currentStep, "UNKNOWN");
    }

    private Integer calculateEstimatedDays(String currentStep) {
        Map<String, Integer> estimatedDays = Map.of(
            "CONFIRMATION", 3,
            "PAYMENT", 2,
            "DEPLOYMENT", 1,
            "COMPLETION", 0
        );
        return estimatedDays.getOrDefault(currentStep, 5);
    }

    private String buildGatewayData(PaymentInitiationRequestViewModel request) {
        return """
            {
                "payment_method": "%s",
                "return_url": "%s",
                "cancel_url": "%s",
                "webhook_url": "%s",
                "additional_info": "%s"
            }
            """.formatted(
                request.getPaymentMethod(),
                request.getReturnUrl() != null ? request.getReturnUrl() : "",
                request.getCancelUrl() != null ? request.getCancelUrl() : "",
                request.getWebhookUrl() != null ? request.getWebhookUrl() : "",
                request.getAdditionalInfo() != null ? request.getAdditionalInfo() : ""
            );
    }

    private void createOnboardingEvent(OnboardingProcess process, String eventType,
                                     String description, Long triggeredBy) {
        OnboardingEvent event = OnboardingEvent.builder()
            .onboardingProcess(process)
            .eventType(eventType)
            .eventData("""
                {
                    "description": "%s",
                    "step": "%s",
                    "status": "%s"
                }
                """.formatted(description, process.getCurrentStep(), process.getStatus()))
            .eventTimestamp(LocalDateTime.now())
            .triggeredBy(UUID.randomUUID()) // Convert Long to UUID if needed
            .build();

        onboardingEventRepository.save(event);
    }

    @Override
    public PaymentStatusViewModel getPaymentStatus(IAuthContextViewModel auth, String transactionId) {
        log.info("Getting payment status for transaction: {} and vendor: {}", transactionId, auth.getUserId());

        try {
            PaymentTransaction transaction = paymentTransactionRepository
                .findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new RuntimeException("Payment transaction not found"));

            return PaymentStatusViewModel.builder()
                .transactionId(transaction.getTransactionId().toString())
                .transactionRef(transaction.getTransactionRef())
                .status(transaction.getStatus().toString())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .paymentMethod("ONLINE") // Extract from gateway data if needed
                .initiatedAt(transaction.getInitiatedAt())
                .completedAt(transaction.getCompletedAt())
                .failureReason(null) // Extract from gateway data if failed
                .gatewayResponse(transaction.getGatewayData())
                .receiptUrl(transaction.getStatus() == PaymentStatus.COMPLETED ?
                    "https://receipts.example.com/" + transaction.getTransactionRef() : null)
                .refundStatus("NOT_APPLICABLE")
                .nextAction(transaction.getStatus() == PaymentStatus.PENDING ?
                    "Complete payment" : "Proceed to deployment selection")
                .build();

        } catch (Exception e) {
            log.error("Error getting payment status for transaction: {}", transactionId, e);
            throw new RuntimeException("Failed to get payment status: " + e.getMessage());
        }
    }

    @Override
    public DeploymentSelectionResponseViewModel selectDeploymentMethod(IAuthContextViewModel auth,
                                                                     DeploymentSelectionRequestViewModel request) {
        log.info("Selecting deployment method for vendor: {} and OEM: {}", auth.getUserId(), request.getOemCode());

        try {
            // Get vendor and OEM
            Vendor vendor = getVendorByAuth(auth);
            OemMaster oem = getOemByCode(request.getOemCode());

            // Get onboarding process
            OnboardingProcess onboardingProcess = onboardingProcessRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, auth.getCompanyCode())
                .orElseThrow(() -> new RuntimeException("Onboarding process not found"));

            // Update onboarding process
            onboardingProcess.setCurrentStep("API_CREDENTIALS");
            onboardingProcess.setStatus(OnboardingStatus.IN_PROGRESS);
            onboardingProcess.setUpdatedAt(LocalDateTime.now());

            // Update step data with deployment selection
            String stepData = """
                {
                    "deployment_selection": {
                        "deployment_type": "%s",
                        "selected_at": "%s",
                        "preferred_timeline": "%s",
                        "technical_expertise": "%s",
                        "erp_system": "%s",
                        "notes": "%s"
                    }
                }
                """.formatted(
                    request.getDeploymentType(),
                    LocalDateTime.now(),
                    request.getPreferredTimeline() != null ? request.getPreferredTimeline() : "",
                    request.getTechnicalExpertiseLevel() != null ? request.getTechnicalExpertiseLevel() : "",
                    request.getExistingErpSystem() != null ? request.getExistingErpSystem() : "",
                    request.getNotes() != null ? request.getNotes() : ""
                );
            onboardingProcess.setStepData(stepData);

            onboardingProcessRepository.save(onboardingProcess);

            // Create onboarding event
            createOnboardingEvent(onboardingProcess, "DEPLOYMENT_SELECTION",
                "Deployment method selected: " + request.getDeploymentType(), auth.getUserId());

            // Build response based on deployment type
            return buildDeploymentSelectionResponse(request.getDeploymentType());

        } catch (Exception e) {
            log.error("Error selecting deployment method for vendor: {} and OEM: {}",
                auth.getUserId(), request.getOemCode(), e);
            throw new RuntimeException("Failed to select deployment method: " + e.getMessage());
        }
    }

    @Override
    public ApiCredentialsResponseViewModel createApiCredentials(IAuthContextViewModel auth,
                                                              ApiCredentialsRequestViewModel request) {
        log.info("Creating API credentials for vendor: {} and OEM: {}", auth.getUserId(), request.getOemCode());

        try {
            // Get vendor and OEM
            Vendor vendor = getVendorByAuth(auth);
            OemMaster oem = getOemByCode(request.getOemCode());

            // Validate e-Sakha credentials (mock validation)
            if (!validateESakhaCredentials(request.getESakhaUserId(), request.getESakhaPassword())) {
                throw new RuntimeException("Invalid e-Sakha credentials");
            }

            // Create API credentials
            ApiCredential credential = ApiCredential.builder()
                .vendor(vendor)
                .oem(oem)
                .apiKeyHash("ASN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase())
                .environment(Environment.valueOf(request.getEnvironment().toUpperCase()))
                .isActive(true)
                .lastRotatedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusYears(1))
                .usageStats("""
                    {
                        "total_requests": 0,
                        "successful_requests": 0,
                        "failed_requests": 0,
                        "last_request_at": null
                    }""")
                .build();
            credential.setCompanyCode(auth.getCompanyCode());

            credential = apiCredentialRepository.save(credential);

            // Update onboarding process
            OnboardingProcess onboardingProcess = onboardingProcessRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, auth.getCompanyCode())
                .orElseThrow(() -> new RuntimeException("Onboarding process not found"));

            onboardingProcess.setCurrentStep("COMPLETION");
            onboardingProcess.setStatus(OnboardingStatus.COMPLETED);
            onboardingProcess.setCompletedAt(LocalDateTime.now());
            onboardingProcessRepository.save(onboardingProcess);

            // Create onboarding event
            createOnboardingEvent(onboardingProcess, "API_CREDENTIALS_CREATED",
                "API credentials created successfully", auth.getUserId());

            // Build response
            return buildApiCredentialsResponse(credential, request.getOemCode());

        } catch (Exception e) {
            log.error("Error creating API credentials for vendor: {} and OEM: {}",
                auth.getUserId(), request.getOemCode(), e);
            throw new RuntimeException("Failed to create API credentials: " + e.getMessage());
        }
    }

    @Override
    public ApiCredentialsResponseViewModel getApiCredentials(IAuthContextViewModel auth, String oemId) {
        log.info("Getting API credentials for vendor: {} and OEM ID: {}", auth.getUserId(), oemId);

        try {
            // Get vendor and OEM
            Vendor vendor = getVendorByAuth(auth);
            OemMaster oem = getOemById(oemId);

            // Get API credentials
            ApiCredential credential = apiCredentialRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, auth.getCompanyCode())
                .orElseThrow(() -> new RuntimeException("API credentials not found"));

            return buildApiCredentialsResponse(credential, oem.getOemCode());

        } catch (Exception e) {
            log.error("Error getting API credentials for vendor: {} and OEM ID: {}",
                auth.getUserId(), oemId, e);
            throw new RuntimeException("Failed to get API credentials: " + e.getMessage());
        }
    }

    @Override
    public OnboardingProgressViewModel updateStepStatus(IAuthContextViewModel auth,
                                                       String oemId,
                                                       String stepId,
                                                       String status) {
        log.info("Updating step status for vendor: {}, OEM ID: {}, step: {}, status: {}",
            auth.getUserId(), oemId, stepId, status);

        try {
            // Get vendor and OEM
            Vendor vendor = getVendorByAuth(auth);
            OemMaster oem = getOemById(oemId);

            // Get onboarding process
            OnboardingProcess onboardingProcess = onboardingProcessRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, auth.getCompanyCode())
                .orElseThrow(() -> new RuntimeException("Onboarding process not found"));

            // Update step status
            onboardingProcess.setCurrentStep(stepId);
            onboardingProcess.setUpdatedAt(LocalDateTime.now());

            if ("COMPLETED".equals(status)) {
                onboardingProcess.setStatus(OnboardingStatus.COMPLETED);
                onboardingProcess.setCompletedAt(LocalDateTime.now());
            }

            onboardingProcessRepository.save(onboardingProcess);

            // Create onboarding event
            createOnboardingEvent(onboardingProcess, "STEP_STATUS_UPDATED",
                "Step " + stepId + " status updated to " + status, auth.getUserId());

            return buildOnboardingProgress(onboardingProcess);

        } catch (Exception e) {
            log.error("Error updating step status for vendor: {} and OEM ID: {}",
                auth.getUserId(), oemId, e);
            throw new RuntimeException("Failed to update step status: " + e.getMessage());
        }
    }

    @Override
    public SubscriptionPlansViewModel getAvailablePlans(IAuthContextViewModel auth, String oemId) {
        log.info("Getting available plans for vendor: {} and OEM ID: {}", auth.getUserId(), oemId);

        try {
            // Get OEM by ID
            OemMaster oem = getOemById(oemId);

            // Get active subscription plans
            List<SubscriptionPlan> plans = subscriptionPlanRepository
                .findByIsActiveTrue();

            // Build response
            List<SubscriptionPlansViewModel.PlanViewModel> planViewModels = plans.stream()
                .map(this::buildPlanViewModel)
                .collect(Collectors.toList());

            return SubscriptionPlansViewModel.builder()
                .oemCode(oem.getOemCode())
                .oemName(oem.getOemName())
                .plans(planViewModels)
                .recommendedPlan(findRecommendedPlan(plans))
                .build();

        } catch (Exception e) {
            log.error("Error getting available plans for vendor: {} and OEM ID: {}",
                auth.getUserId(), oemId, e);
            throw new RuntimeException("Failed to get available plans: " + e.getMessage());
        }
    }

    @Override
    public OnboardingCompletionViewModel completeOnboarding(IAuthContextViewModel auth, String oemId) {
        log.info("Completing onboarding for vendor: {} and OEM ID: {}", auth.getUserId(), oemId);

        try {
            // Get vendor and OEM
            Vendor vendor = getVendorByAuth(auth);
            OemMaster oem = getOemById(oemId);

            // Get onboarding process
            OnboardingProcess onboardingProcess = onboardingProcessRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, auth.getCompanyCode())
                .orElseThrow(() -> new RuntimeException("Onboarding process not found"));

            // Mark as completed
            onboardingProcess.setStatus(OnboardingStatus.COMPLETED);
            onboardingProcess.setCompletedAt(LocalDateTime.now());
            onboardingProcess.setCurrentStep("COMPLETED");
            onboardingProcessRepository.save(onboardingProcess);

            // Create completion event
            createOnboardingEvent(onboardingProcess, "ONBOARDING_COMPLETED",
                "Onboarding process completed successfully", auth.getUserId());

            // Build completion response
            return OnboardingCompletionViewModel.builder()
                .completionId(UUID.randomUUID().toString())
                .status("COMPLETED")
                .completedAt(LocalDateTime.now())
                .vendorCode("V" + vendor.getVendorId().toString().substring(0, 8))
                .oemCode(oem.getOemCode())
                .subscriptionId(UUID.randomUUID().toString()) // Get from actual subscription
                .apiCredentialsId(UUID.randomUUID().toString()) // Get from actual credentials
                .goLiveDate(LocalDateTime.now().plusDays(1))
                .nextSteps(List.of(
                    "Start using ASN 2.1 APIs",
                    "Monitor API usage and performance",
                    "Contact support for any issues"
                ))
                .supportContacts(buildSupportContacts())
                .documentationLinks(buildDocumentationLinks())
                .completionCertificateUrl("https://certificates.example.com/" + UUID.randomUUID())
                .build();

        } catch (Exception e) {
            log.error("Error completing onboarding for vendor: {} and OEM ID : {}",
                auth.getCompanyCode(), oemId, e);
            throw new RuntimeException("Failed to complete onboarding: " + e.getMessage());
        }
    }

    // Additional helper methods
    private DeploymentSelectionResponseViewModel buildDeploymentSelectionResponse(String deploymentType) {
        if ("SELF_DEPLOYMENT".equals(deploymentType)) {
            return DeploymentSelectionResponseViewModel.builder()
                .selectionId(UUID.randomUUID().toString())
                .deploymentType(deploymentType)
                .status("SELECTED")
                .estimatedTimeline("2-3 business days")
                .estimatedCompletionDate(LocalDateTime.now().plusDays(3))
                .additionalCost(BigDecimal.ZERO)
                .nextSteps(List.of(
                    DeploymentSelectionResponseViewModel.NextStepViewModel.builder()
                        .stepId("API_CREDENTIALS")
                        .title("Create API Credentials")
                        .description("Set up your e-Sakha credentials for API access")
                        .estimatedDuration("30 minutes")
                        .dependencies(List.of("Payment Completed"))
                        .build()
                ))
                .assignedResources(DeploymentSelectionResponseViewModel.AssignedResourcesViewModel.builder()
                    .supportTeam(DeploymentSelectionResponseViewModel.ContactViewModel.builder()
                        .name("Self-Deployment Support")
                        .email("support@taxgenie.com")
                        .phone("+91-80-1234-5678")
                        .role("Technical Support")
                        .build())
                    .build())
                .documentationLinks(List.of(
                    DeploymentSelectionResponseViewModel.DocumentationLinkViewModel.builder()
                        .title("Self-Deployment Guide")
                        .url("https://docs.taxgenie.com/self-deployment")
                        .type("GUIDE")
                        .description("Complete guide for self-deployment")
                        .build()
                ))
                .build();
        } else {
            return DeploymentSelectionResponseViewModel.builder()
                .selectionId(UUID.randomUUID().toString())
                .deploymentType(deploymentType)
                .status("SELECTED")
                .estimatedTimeline("5-7 business days")
                .estimatedCompletionDate(LocalDateTime.now().plusDays(7))
                .additionalCost(new BigDecimal("8000"))
                .nextSteps(List.of(
                    DeploymentSelectionResponseViewModel.NextStepViewModel.builder()
                        .stepId("ACCOUNT_MANAGER_ASSIGNMENT")
                        .title("Account Manager Assignment")
                        .description("Dedicated account manager will be assigned")
                        .estimatedDuration("1 business day")
                        .dependencies(List.of("Payment Completed"))
                        .build()
                ))
                .assignedResources(DeploymentSelectionResponseViewModel.AssignedResourcesViewModel.builder()
                    .accountManager(DeploymentSelectionResponseViewModel.ContactViewModel.builder()
                        .name("Rajesh Kumar")
                        .email("rajesh.kumar@taxgenie.com")
                        .phone("+91-80-9876-5432")
                        .role("Account Manager")
                        .build())
                    .technicalLead(DeploymentSelectionResponseViewModel.ContactViewModel.builder()
                        .name("Priya Sharma")
                        .email("priya.sharma@taxgenie.com")
                        .phone("+91-80-8765-4321")
                        .role("Technical Lead")
                        .build())
                    .build())
                .documentationLinks(List.of(
                    DeploymentSelectionResponseViewModel.DocumentationLinkViewModel.builder()
                        .title("Assisted Implementation Process")
                        .url("https://docs.taxgenie.com/assisted-implementation")
                        .type("GUIDE")
                        .description("Overview of assisted implementation process")
                        .build()
                ))
                .build();
        }
    }

    private boolean validateESakhaCredentials(String userId, String password) {
        // Mock validation - in real implementation, this would call e-Sakha API
        return !"fail".equals(userId) && password != null && !password.trim().isEmpty();
    }

    private ApiCredentialsResponseViewModel buildApiCredentialsResponse(ApiCredential credential, String oemCode) {
        return ApiCredentialsResponseViewModel.builder()
            .credentialId(credential.getCredentialId().toString())
            .developerId("DEV_" + credential.getVendor().getVendorId().toString().substring(0, 8))
            .apiKey(credential.getApiKeyHash())
            .clientSecret("SEC_" + UUID.randomUUID().toString().replace("-", "").substring(0, 20))
            .environment(credential.getEnvironment().toString())
            .endpointUrl("https://api-tml.apigee.net/asn/v2.1/")
            .status(credential.getIsActive() ? "ACTIVE" : "INACTIVE")
            .createdAt(credential.getCreatedAt())
            .expiresAt(credential.getExpiresAt())
            .rateLimits(ApiCredentialsResponseViewModel.RateLimitsViewModel.builder()
                .requestsPerMinute(60)
                .requestsPerDay(10000)
                .requestsPerMonth(300000)
                .burstLimit(100)
                .build())
            .endpoints(List.of(
                ApiCredentialsResponseViewModel.EndpointViewModel.builder()
                    .name("Create ASN")
                    .url("/asn")
                    .method("POST")
                    .description("Create a new ASN")
                    .authenticationRequired(true)
                    .build(),
                ApiCredentialsResponseViewModel.EndpointViewModel.builder()
                    .name("Get ASN Status")
                    .url("/asn/{id}/status")
                    .method("GET")
                    .description("Get ASN status")
                    .authenticationRequired(true)
                    .build()
            ))
            .documentationUrl("https://docs.taxgenie.com/api/asn-2.1")
            .supportContact("api-support@taxgenie.com")
            .build();
    }

    private SubscriptionPlansViewModel.PlanViewModel buildPlanViewModel(SubscriptionPlan plan) {
        // Parse pricing JSON - TODO: implement actual JSON parsing
        // String pricingJson = plan.getPricing();
        BigDecimal annualFee = new BigDecimal("12000"); // Parse from JSON
        BigDecimal setupFee = new BigDecimal("5000"); // Parse from JSON
        BigDecimal gstRate = new BigDecimal("18"); // Parse from JSON

        BigDecimal totalSetupCost = setupFee.add(annualFee)
            .multiply(BigDecimal.ONE.add(gstRate.divide(new BigDecimal("100"))));

        return SubscriptionPlansViewModel.PlanViewModel.builder()
            .planCode(plan.getPlanCode())
            .planName(plan.getPlanName())
            .description("Subscription plan for " + plan.getPlanName())
            .pricing(SubscriptionPlansViewModel.PricingViewModel.builder()
                .annualFee(annualFee)
                .setupFee(setupFee)
                .gstRate(gstRate)
                .totalSetupCost(totalSetupCost)
                .currency("INR")
                .build())
            .features(List.of(
                "ASN 2.1 Implementation",
                "API Access",
                "Email Support",
                "Documentation Access"
            ))
            .apiLimits(SubscriptionPlansViewModel.ApiLimitsViewModel.builder()
                .requestsPerMinute(60)
                .requestsPerDay(10000)
                .requestsPerMonth(300000)
                .burstLimit(100)
                .build())
            .isFeatured(plan.getIsFeatured())
            .isRecommended("BASIC".equals(plan.getPlanCode()))
            .supportLevel("Email")
            .build();
    }

    private String findRecommendedPlan(List<SubscriptionPlan> plans) {
        return plans.stream()
            .filter(plan -> "BASIC".equals(plan.getPlanCode()))
            .map(SubscriptionPlan::getPlanCode)
            .findFirst()
            .orElse(plans.isEmpty() ? null : plans.get(0).getPlanCode());
    }

    private List<OnboardingCompletionViewModel.SupportContactViewModel> buildSupportContacts() {
        return List.of(
            OnboardingCompletionViewModel.SupportContactViewModel.builder()
                .name("Technical Support")
                .role("API Support")
                .email("api-support@taxgenie.com")
                .phone("+91-80-1234-5678")
                .availability("24/7")
                .build(),
            OnboardingCompletionViewModel.SupportContactViewModel.builder()
                .name("Account Manager")
                .role("Business Support")
                .email("account-manager@taxgenie.com")
                .phone("+91-80-9876-5432")
                .availability("9 AM - 6 PM IST")
                .build()
        );
    }

    private List<OnboardingCompletionViewModel.DocumentationLinkViewModel> buildDocumentationLinks() {
        return List.of(
            OnboardingCompletionViewModel.DocumentationLinkViewModel.builder()
                .title("ASN 2.1 API Documentation")
                .url("https://docs.taxgenie.com/api/asn-2.1")
                .type("API_DOC")
                .description("Complete API reference for ASN 2.1")
                .build(),
            OnboardingCompletionViewModel.DocumentationLinkViewModel.builder()
                .title("Integration Guide")
                .url("https://docs.taxgenie.com/integration-guide")
                .type("GUIDE")
                .description("Step-by-step integration guide")
                .build(),
            OnboardingCompletionViewModel.DocumentationLinkViewModel.builder()
                .title("Sample Code")
                .url("https://github.com/taxgenie/asn-samples")
                .type("SAMPLE")
                .description("Sample code and examples")
                .build()
        );
    }
}
