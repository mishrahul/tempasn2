package in.taxgenie.services.implementations;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.entities.*;
import in.taxgenie.entities.enums.OnboardingStatus;
import in.taxgenie.entities.enums.PaymentStatus;
import in.taxgenie.entities.enums.Environment;
import in.taxgenie.entities.enums.Status;
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
    public VendorRegistrationResponseViewModel registerVendor(VendorRegistrationRequestViewModel request, IAuthContextViewModel auth) {
        log.info("Registering new vendor: {}", request.getCompanyName());

        try {
            // Check if PAN already exists
            if (vendorRepository.findByPanNumber(request.getPanNumber()).isPresent()) {
                throw new RuntimeException("Vendor with this PAN number already exists");
            }

            // Check if email already exists (search in primary_contact JSON)
            // This is a simplified check - in production, you'd want a more robust solution

            // Generate company code (using timestamp + random)
            Long companyCode = auth.getCompanyCode();

            // Build primary contact JSON
            String primaryContactJson = String.format("""
                {
                    "name": "%s",
                    "email": "%s",
                    "mobile": "%s",
                    "designation": "%s"
                }""",
                request.getContactPersonName(),
                request.getContactEmail(),
                request.getContactMobile(),
                request.getContactDesignation() != null ? request.getContactDesignation() : "Primary Contact");

            // Build auth credentials JSON (no password for now - will be set via separate flow)
            String authCredentialsJson = """
                {
                    "password_hash": null,
                    "mfa_enabled": false,
                    "mfa_secret": null
                }""";

            // Create vendor entity
            Vendor vendor = Vendor.builder()
                .userId(companyCode) // Using companyCode as userId for now
                .companyName(request.getCompanyName())
                .panNumber(request.getPanNumber().toUpperCase())
                .cinNumber(null) // CIN can be added later via profile update
                .primaryContact(primaryContactJson)
                .authCredentials(authCredentialsJson)
                .status(Status.ACTIVE)
                .lastActivityAt(LocalDateTime.now())
                .build();

            vendor.setCompanyCode(companyCode);
            vendor.setCreatedAt(LocalDateTime.now());
            vendor.setUpdatedAt(LocalDateTime.now());
            vendor.setCreatedBy(UUID.randomUUID()); // System user
            vendor.setUpdatedBy(UUID.randomUUID()); // System user

            // Save vendor
            vendor = vendorRepository.save(vendor);
            log.info("Vendor created successfully with ID: {}", vendor.getVendorId());

            // Generate vendor code
            String vendorCode = "V" + vendor.getVendorId().toString().substring(0, 8).toUpperCase();

            // Build response
            return VendorRegistrationResponseViewModel.builder()
                .vendorId(vendor.getVendorId().toString())
                .vendorCode(vendorCode)
                .companyName(vendor.getCompanyName())
                .panNumber(vendor.getPanNumber())
                .status("ACTIVE")
                .registeredAt(vendor.getCreatedAt())
                .primaryContact(VendorRegistrationResponseViewModel.ContactInfoViewModel.builder()
                    .name(request.getContactPersonName())
                    .email(request.getContactEmail())
                    .mobile(request.getContactMobile())
                    .designation(request.getContactDesignation())
                    .build())
                .nextSteps(List.of(
                    "Complete your company profile",
                    "Add GSTIN details",
                    "Select OEM for onboarding",
                    "Choose subscription plan",
                    "Complete payment"
                ))
                .onboardingStatus(VendorRegistrationResponseViewModel.OnboardingStatusViewModel.builder()
                    .currentStep("PROFILE_SETUP")
                    .progressPercentage(20)
                    .completedSteps(1)
                    .totalSteps(5)
                    .nextStepTitle("Complete Profile")
                    .nextStepDescription("Add additional company details and GSTIN information")
                    .build())
                .welcomeMessage("Welcome to ASN Vendor Onboarding Portal! Your account has been created successfully.")
                .resourceLinks(List.of(
                    VendorRegistrationResponseViewModel.ResourceLinkViewModel.builder()
                        .title("Getting Started Guide")
                        .description("Learn how to onboard with OEMs")
                        .url("/docs/getting-started")
                        .type("DOCUMENTATION")
                        .build(),
                    VendorRegistrationResponseViewModel.ResourceLinkViewModel.builder()
                        .title("ASN 2.1 Overview")
                        .description("Understanding ASN 2.1 requirements")
                        .url("/docs/asn-overview")
                        .type("DOCUMENTATION")
                        .build()
                ))
                .build();

        } catch (Exception e) {
            log.error("Error registering vendor: {}", request.getCompanyName(), e);
            throw new RuntimeException("Failed to register vendor: " + e.getMessage());
        }
    }

    @Override
    public VendorCheckResponseViewModel checkVendorRegistration(IAuthContextViewModel auth) {
        log.info("Checking vendor registration status for companyCode: {}", auth.getCompanyCode());

        try {
            // Check if vendor exists by company code
            Optional<Vendor> vendorOptional = vendorRepository.findByCompanyCode(auth.getCompanyCode());

            if (vendorOptional.isEmpty()) {
                // Vendor not registered
                return VendorCheckResponseViewModel.builder()
                    .isRegistered(false)
                    .message("Vendor is not registered for this company")
                    .build();
            }

            // Vendor is registered - return details
            Vendor vendor = vendorOptional.get();

            // Parse primary contact JSON
            VendorCheckResponseViewModel.ContactInfoViewModel contactInfo = null;
            if (vendor.getPrimaryContact() != null && !vendor.getPrimaryContact().trim().isEmpty()) {
                try {
                    // Simple JSON parsing - in production, use ObjectMapper
                    String contactJson = vendor.getPrimaryContact();
                    contactInfo = VendorCheckResponseViewModel.ContactInfoViewModel.builder()
                        .name(extractJsonValue(contactJson, "name"))
                        .email(extractJsonValue(contactJson, "email"))
                        .mobile(extractJsonValue(contactJson, "mobile"))
                        .designation(extractJsonValue(contactJson, "designation"))
                        .build();
                } catch (Exception e) {
                    log.warn("Error parsing primary contact JSON for vendor: {}", vendor.getVendorId(), e);
                }
            }

            // Generate vendor code
            String vendorCode = "V" + vendor.getVendorId().toString().substring(0, 8).toUpperCase();

            return VendorCheckResponseViewModel.builder()
                .isRegistered(true)
                .vendorId(vendor.getVendorId().toString())
                .vendorCode(vendorCode)
                .companyName(vendor.getCompanyName())
                .panNumber(vendor.getPanNumber())
                .status(vendor.getStatus().name())
                .registeredAt(vendor.getCreatedAt())
                .primaryContact(contactInfo)
                .message("Vendor is registered")
                .build();

        } catch (Exception e) {
            log.error("Error checking vendor registration for companyCode: {}", auth.getCompanyCode(), e);
            throw new RuntimeException("Failed to check vendor registration: " + e.getMessage());
        }
    }

    /**
     * Helper method to extract value from JSON string
     * This is a simple implementation - in production, use ObjectMapper
     */
    private String extractJsonValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\"";
            int keyIndex = json.indexOf(searchKey);
            if (keyIndex == -1) return null;

            int colonIndex = json.indexOf(":", keyIndex);
            if (colonIndex == -1) return null;

            int valueStart = json.indexOf("\"", colonIndex) + 1;
            int valueEnd = json.indexOf("\"", valueStart);

            if (valueStart > 0 && valueEnd > valueStart) {
                return json.substring(valueStart, valueEnd);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

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
        return vendorRepository.findByCompanyCode(auth.getCompanyCode())
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
        // Check if this is an update or create request
        if (request.getCredentialId() != null && !request.getCredentialId().trim().isEmpty()) {
            log.info("Updating API credentials for vendor: {} and OEM: {}, credentialId: {}",
                auth.getUserId(), request.getOemId(), request.getCredentialId());
            return updateApiCredentials(auth, request);
        } else {
            log.info("Creating API credentials for vendor: {} and OEM: {}", auth.getUserId(), request.getOemId());
            return createNewApiCredentials(auth, request);
        }
    }

    /**
     * Create new API credentials
     */
    private ApiCredentialsResponseViewModel createNewApiCredentials(IAuthContextViewModel auth,
                                                                   ApiCredentialsRequestViewModel request) {
        try {
            // Get vendor and OEM
            Vendor vendor = getVendorByAuth(auth);
            OemMaster oem = getOemById(request.getOemId());

            // Validate e-Sakha credentials (mock validation)
            if (!validateESakhaCredentials(request.getESakhaUserId(), request.getESakhaPassword())) {
                throw new RuntimeException("Invalid e-Sakha credentials");
            }

            // Check if credentials already exist for this vendor, OEM, and environment
            Environment environment = Environment.valueOf(request.getEnvironment().toUpperCase());
            Optional<ApiCredential> existingCredential = apiCredentialRepository
                .findByVendorAndOemAndEnvironment(vendor, oem, environment);

            if (existingCredential.isPresent()) {
                throw new RuntimeException("API credentials already exist for this OEM and environment. " +
                    "Use credentialId in request to update existing credentials.");
            }

            // Generate secret
            String clientSecret = "SEC_" + UUID.randomUUID().toString().replace("-", "").substring(0, 32).toUpperCase();

            // Create API credentials
            ApiCredential credential = ApiCredential.builder()
                .vendor(vendor)
                .oem(oem)
                .apiKeyHash("ASN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase())
                .secretEncrypted(clientSecret) // Store the secret (in production, this should be encrypted)
                .environment(environment)
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
            return buildApiCredentialsResponse(credential, oem.getOemCode());

        } catch (Exception e) {
            log.error("Error creating API credentials for vendor: {} and OEM: {}",
                auth.getUserId(), request.getOemId(), e);
            throw new RuntimeException("Failed to create API credentials: " + e.getMessage());
        }
    }

    /**
     * Update existing API credentials
     */
    private ApiCredentialsResponseViewModel updateApiCredentials(IAuthContextViewModel auth,
                                                                ApiCredentialsRequestViewModel request) {
        try {
            // Get vendor and OEM
            Vendor vendor = getVendorByAuth(auth);
            OemMaster oem = getOemById(request.getOemId());

            // Parse credential ID
            UUID credentialId;
            try {
                credentialId = UUID.fromString(request.getCredentialId());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid credential ID format");
            }

            // Find existing credential
            ApiCredential credential = apiCredentialRepository.findById(credentialId)
                .orElseThrow(() -> new RuntimeException("API credential not found with ID: " + request.getCredentialId()));

            // Verify ownership - ensure the credential belongs to the authenticated vendor
            if (!credential.getVendor().getVendorId().equals(vendor.getVendorId())) {
                throw new RuntimeException("Unauthorized: This credential does not belong to your account");
            }

            // Verify company code matches
            if (!credential.getCompanyCode().equals(auth.getCompanyCode())) {
                throw new RuntimeException("Unauthorized: Company code mismatch");
            }

            // Validate e-Sakha credentials (mock validation)
            if (!validateESakhaCredentials(request.getESakhaUserId(), request.getESakhaPassword())) {
                throw new RuntimeException("Invalid e-Sakha credentials");
            }

            // Update credential fields
            credential.setEnvironment(Environment.valueOf(request.getEnvironment().toUpperCase()));
            credential.setUpdatedAt(LocalDateTime.now());
            credential.setUpdatedBy(vendor.getVendorId());

            // Update optional fields if provided
            if (request.getWebhookUrl() != null && !request.getWebhookUrl().trim().isEmpty()) {
                // Store webhook URL in additional config or create a new field
                log.info("Webhook URL updated: {}", request.getWebhookUrl());
            }

            if (request.getIpWhitelist() != null && !request.getIpWhitelist().trim().isEmpty()) {
                // Store IP whitelist in additional config
                log.info("IP Whitelist updated: {}", request.getIpWhitelist());
            }

            if (request.getRateLimitTier() != null && !request.getRateLimitTier().trim().isEmpty()) {
                // Update rate limits based on tier
                String rateLimits = switch (request.getRateLimitTier().toUpperCase()) {
                    case "BASIC" -> """
                        {
                            "per_minute": 60,
                            "per_hour": 1000,
                            "per_day": 10000
                        }""";
                    case "STANDARD" -> """
                        {
                            "per_minute": 120,
                            "per_hour": 5000,
                            "per_day": 50000
                        }""";
                    case "PREMIUM" -> """
                        {
                            "per_minute": 300,
                            "per_hour": 15000,
                            "per_day": 150000
                        }""";
                    default -> credential.getRateLimits();
                };
                credential.setRateLimits(rateLimits);
            }

            credential = apiCredentialRepository.save(credential);

            // Create onboarding event
            OnboardingProcess onboardingProcess = onboardingProcessRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, auth.getCompanyCode())
                .orElse(null);

            if (onboardingProcess != null) {
                createOnboardingEvent(onboardingProcess, "API_CREDENTIALS_UPDATED",
                    "API credentials updated successfully", auth.getUserId());
            }

            // Build response
            return buildApiCredentialsResponse(credential, oem.getOemCode());

        } catch (Exception e) {
            log.error("Error updating API credentials for vendor: {} and OEM: {}, credentialId: {}",
                auth.getUserId(), request.getOemId(), request.getCredentialId(), e);
            throw new RuntimeException("Failed to update API credentials: " + e.getMessage());
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
            Optional<ApiCredential> credentialOpt = apiCredentialRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, auth.getCompanyCode());

            // Return null if credentials not found (controller will handle this)
            if (credentialOpt.isEmpty()) {
                log.info("API credentials not found for vendor: {} and OEM ID: {}", auth.getUserId(), oemId);
                return null;
            } else {
                return buildApiCredentialsResponse(credentialOpt.get(), oem.getOemCode());
            }
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
