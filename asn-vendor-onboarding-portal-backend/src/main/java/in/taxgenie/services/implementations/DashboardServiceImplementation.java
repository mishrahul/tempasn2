package in.taxgenie.services.implementations;

import in.taxgenie.entities.*;
import in.taxgenie.entities.enums.OnboardingStatus;
import in.taxgenie.entities.enums.PaymentStatus;
import in.taxgenie.repositories.*;
import in.taxgenie.services.interfaces.IDashboardService;
import in.taxgenie.viewmodels.dashboard.DashboardStatsViewModel;
import in.taxgenie.viewmodels.dashboard.ProgressViewModel;
import in.taxgenie.viewmodels.dashboard.UserProfileViewModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for dashboard operations
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Service
@Slf4j
@Transactional
public class DashboardServiceImplementation implements IDashboardService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private OnboardingProcessRepository onboardingProcessRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private ApiCredentialRepository apiCredentialRepository;

    @Autowired
    private OnboardingEventRepository onboardingEventRepository;

    @Autowired
    private OemMasterRepository oemMasterRepository;

    // ASN 2.1 deadline - September 30, 2025
    private static final LocalDateTime ASN_DEADLINE = LocalDateTime.of(2025, 11, 30, 23, 59, 59);

    @Override
    public DashboardStatsViewModel getDashboardStats(String vendorId, String oemId, String companyCode) {
        log.info("Getting dashboard stats for vendor: {}, OEM ID: {}, company: {}", vendorId, oemId, companyCode);

        try {
            // Get vendor information using company code
            Long companyCodeLong = Long.parseLong(companyCode);
            Optional<Vendor> vendorOpt = vendorRepository.findByCompanyCode(companyCodeLong);
            if (vendorOpt.isEmpty()) {
                log.warn("Vendor not found for company: {}", companyCode);
                return buildDefaultDashboardStats();
            }

            Vendor vendor = vendorOpt.get();

            // Get OEM information by ID
            Optional<OemMaster> oemOpt = oemMasterRepository.findById(java.util.UUID.fromString(oemId));
            if (oemOpt.isEmpty()) {
                log.warn("OEM not found with ID: {}", oemId);
                return buildDefaultDashboardStats();
            }

            OemMaster oem = oemOpt.get();

            // Get onboarding process
            Optional<OnboardingProcess> onboardingOpt = onboardingProcessRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, companyCodeLong);
            
            // Calculate overall progress
            int overallProgress = calculateOverallProgress(onboardingOpt.orElse(null));
            
            // Calculate completed steps
            String completedSteps = calculateCompletedSteps(onboardingOpt.orElse(null));
            
            // Calculate days remaining
            long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), ASN_DEADLINE);
            
            // Get current plan
            String currentPlan = getCurrentPlan(vendor);
            
            // Build critical alert
            DashboardStatsViewModel.CriticalAlertViewModel criticalAlert = buildCriticalAlert(daysRemaining, overallProgress);
            
            return DashboardStatsViewModel.builder()
                .progress(overallProgress)
                .completedSteps(completedSteps)
                .daysRemaining((int) Math.max(0, daysRemaining))
                .currentPlan(currentPlan)
                .deadline(ASN_DEADLINE)
                .status(getImplementationStatus(overallProgress, daysRemaining))
                .nextAction(getNextAction(onboardingOpt.orElse(null)))
                .criticalAlert(criticalAlert)
                .build();
                
        } catch (Exception e) {
            log.error("Error getting dashboard stats for vendor: {}", vendorId, e);
            return buildDefaultDashboardStats();
        }
    }

    @Override
    public ProgressViewModel getOnboardingProgress(String vendorId, String oemId, String companyCode) {
        log.info("Getting onboarding progress for vendor: {}, OEM ID: {}, company: {}", vendorId, oemId, companyCode);

        try {
            // Get vendor information using company code
            Long companyCodeLong = Long.parseLong(companyCode);
            Optional<Vendor> vendorOpt = vendorRepository.findByCompanyCode(companyCodeLong);
            if (vendorOpt.isEmpty()) {
                log.warn("Vendor not found for company: {}", companyCode);
                return buildDefaultOnboardingProgress();
            }

            Vendor vendor = vendorOpt.get();

            // Get OEM information by ID
            Optional<OemMaster> oemOpt = oemMasterRepository.findById(java.util.UUID.fromString(oemId));
            if (oemOpt.isEmpty()) {
                log.warn("OEM not found with ID: {}", oemId);
                return buildDefaultOnboardingProgress();
            }

            OemMaster oem = oemOpt.get();

            // Get onboarding process
            Optional<OnboardingProcess> onboardingOpt = onboardingProcessRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, companyCodeLong);
            
            if (onboardingOpt.isEmpty()) {
                return buildDefaultOnboardingProgress();
            }
            
            OnboardingProcess onboarding = onboardingOpt.get();
            
            // Build onboarding steps
            List<ProgressViewModel.StepProgressViewModel> steps = buildOnboardingSteps(onboarding);
            
            // Calculate progress
            int completedSteps = (int) steps.stream().filter(s -> "completed".equals(s.getStatus())).count();
            int totalSteps = steps.size();
            int percentage = totalSteps > 0 ? (completedSteps * 100) / totalSteps : 0;
            
            // Find current step
            Integer currentStepId = steps.stream()
                .filter(s -> "current".equals(s.getStatus()))
                .map(ProgressViewModel.StepProgressViewModel::getId)
                .findFirst()
                .orElse(null);
            
            return ProgressViewModel.builder()
                .percentage(percentage)
                .completedSteps(completedSteps)
                .totalSteps(totalSteps)
                .steps(steps)
                .type("onboarding")
                .currentStepId(currentStepId)
                .estimatedCompletion(calculateEstimatedCompletion(steps))
                .lastUpdated(onboarding.getUpdatedAt())
                .build();
                
        } catch (Exception e) {
            log.error("Error getting onboarding progress for vendor: {}", vendorId, e);
            return buildDefaultOnboardingProgress();
        }
    }

    @Override
    public ProgressViewModel getImplementationProgress(String vendorId, String oemId, String companyCode) {
        log.info("Getting implementation progress for vendor: {}, OEM ID: {}, company: {}", vendorId, oemId, companyCode);

        try {
            // Get vendor information using company code
            Long companyCodeLong = Long.parseLong(companyCode);
            Optional<Vendor> vendorOpt = vendorRepository.findByCompanyCode(companyCodeLong);
            if (vendorOpt.isEmpty()) {
                log.warn("Vendor not found for company: {}", companyCode);
                return buildDefaultImplementationProgress();
            }

            Vendor vendor = vendorOpt.get();

            // Get OEM information by ID
            Optional<OemMaster> oemOpt = oemMasterRepository.findById(java.util.UUID.fromString(oemId));
            if (oemOpt.isEmpty()) {
                log.warn("OEM not found with ID: {}", oemId);
                return buildDefaultImplementationProgress();
            }

            OemMaster oem = oemOpt.get();

            // Get onboarding process
            Optional<OnboardingProcess> onboardingOpt = onboardingProcessRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, companyCodeLong);
            
            if (onboardingOpt.isEmpty()) {
                return buildDefaultImplementationProgress();
            }
            
            OnboardingProcess onboarding = onboardingOpt.get();
            
            // Build implementation steps
            List<ProgressViewModel.StepProgressViewModel> steps = buildImplementationSteps(onboarding);
            
            // Calculate progress
            int completedSteps = (int) steps.stream().filter(s -> "completed".equals(s.getStatus())).count();
            int totalSteps = steps.size();
            int percentage = totalSteps > 0 ? (completedSteps * 100) / totalSteps : 0;
            
            // Find current step
            Integer currentStepId = steps.stream()
                .filter(s -> "current".equals(s.getStatus()))
                .map(ProgressViewModel.StepProgressViewModel::getId)
                .findFirst()
                .orElse(null);
            
            return ProgressViewModel.builder()
                .percentage(percentage)
                .completedSteps(completedSteps)
                .totalSteps(totalSteps)
                .steps(steps)
                .type("implementation")
                .currentStepId(currentStepId)
                .estimatedCompletion(calculateEstimatedCompletion(steps))
                .lastUpdated(onboarding.getUpdatedAt())
                .build();
                
        } catch (Exception e) {
            log.error("Error getting implementation progress for vendor: {}", vendorId, e);
            return buildDefaultImplementationProgress();
        }
    }

    @Override
    public UserProfileViewModel getUserProfile(String vendorId, String companyCode) {
        log.info("Getting user profile for vendor: {}, company: {}", vendorId, companyCode);

        try {
            // Get vendor information using company code
            Long companyCodeLong = Long.parseLong(companyCode);
            Optional<Vendor> vendorOpt = vendorRepository.findByCompanyCode(companyCodeLong);
            if (vendorOpt.isEmpty()) {
                log.warn("Vendor not found for company: {}", companyCode);
                return buildDefaultUserProfile();
            }

            Vendor vendor = vendorOpt.get();

            // Build GSTIN details
            List<UserProfileViewModel.GSTINDetailViewModel> gstinDetails = buildGSTINDetails(vendor);

            // Build subscription details
            UserProfileViewModel.SubscriptionDetailViewModel subscription = buildSubscriptionDetails(vendor);

            // Get OEM associations
            List<String> oemAssociations = getOemAssociations(vendor, companyCode);

            // Extract contact information from JSON
            String contactName = extractContactName(vendor.getPrimaryContact());
            String contactEmail = extractContactEmail(vendor.getPrimaryContact());
            String contactPhone = extractContactPhone(vendor.getPrimaryContact());

            return UserProfileViewModel.builder()
                .id(vendor.getVendorId().toString())
                .companyName(vendor.getCompanyName())
                .panNumber(vendor.getPanNumber())
                .contactPerson(contactName)
                .email(contactEmail)
                .phone(contactPhone)
                .vendorCode(vendor.getVendorId().toString()) // Using vendorId as vendorCode for now
                .currentPlan(getCurrentPlan(vendor))
                .gstinDetails(gstinDetails)
                .createdAt(vendor.getCreatedAt())
                .lastLogin(vendor.getLastActivityAt())
                .status(vendor.getStatus().toString())
                .oemAssociations(oemAssociations)
                .subscription(subscription)
                .build();
                
        } catch (Exception e) {
            log.error("Error getting user profile for vendor: {}", vendorId, e);
            return buildDefaultUserProfile();
        }
    }

    @Override
    public UserProfileViewModel updateUserProfile(String vendorId, UserProfileViewModel userProfile, String companyCode) {
        log.info("Updating user profile for vendor: {}, company: {}", vendorId, companyCode);

        try {
            // Get vendor information using company code
            Long companyCodeLong = Long.parseLong(companyCode);
            Optional<Vendor> vendorOpt = vendorRepository.findByCompanyCode(companyCodeLong);
            if (vendorOpt.isEmpty()) {
                log.warn("Vendor not found for company: {}", companyCode);
                throw new RuntimeException("Vendor not found");
            }

            Vendor vendor = vendorOpt.get();

            // Update vendor information
            vendor.setCompanyName(userProfile.getCompanyName());
            // Update primary contact JSON
            vendor.setPrimaryContact(buildPrimaryContactJson(
                userProfile.getContactPerson(),
                userProfile.getEmail(),
                userProfile.getPhone()
            ));
            vendor.setUpdatedAt(LocalDateTime.now());

            // Save updated vendor
            vendor = vendorRepository.save(vendor);

            // Return updated profile
            return getUserProfile(vendorId, companyCode);

        } catch (Exception e) {
            log.error("Error updating user profile for vendor: {}", vendorId, e);
            throw new RuntimeException("Failed to update user profile: " + e.getMessage());
        }
    }

    @Override
    public List<QuickActionViewModel> getQuickActions(String vendorId, String oemId, String companyCode) {
        log.info("Getting quick actions for vendor: {}, OEM ID: {}, company: {}", vendorId, oemId, companyCode);

        try {
            List<QuickActionViewModel> actions = new ArrayList<>();

            // Get vendor information using company code
            Long companyCodeLong = Long.parseLong(companyCode);
            Optional<Vendor> vendorOpt = vendorRepository.findByCompanyCode(companyCodeLong);
            if (vendorOpt.isEmpty()) {
                return actions;
            }

            Vendor vendor = vendorOpt.get();

            // Get OEM information by ID
            Optional<OemMaster> oemOpt = oemMasterRepository.findById(java.util.UUID.fromString(oemId));
            if (oemOpt.isEmpty()) {
                return actions;
            }

            OemMaster oem = oemOpt.get();

            // Get onboarding process
            Optional<OnboardingProcess> onboardingOpt = onboardingProcessRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, companyCodeLong);

            // Continue Onboarding action
            boolean onboardingComplete = onboardingOpt.map(o ->
                OnboardingStatus.COMPLETED.equals(o.getStatus())).orElse(false);

            actions.add(new QuickActionViewModel(
                "continue-onboarding",
                "Continue Onboarding",
                onboardingComplete ? "Review onboarding details" : "Complete your ASN 2.1 onboarding process",
                "🚀",
                "/onboarding",
                "navigate",
                true,
                "primary"
            ));

            // AI Specialist action
            actions.add(new QuickActionViewModel(
                "ai-specialist",
                "Launch AI Specialist",
                "Get help from our AI-powered implementation assistant",
                "🤖",
                "/ai-specialist",
                "navigate",
                true,
                "secondary"
            ));

            // Upgrade Plan action
            String currentPlan = getCurrentPlan(vendor);
            boolean canUpgrade = !"Enterprise".equals(currentPlan);

            actions.add(new QuickActionViewModel(
                "upgrade-plan",
                "Upgrade Plan",
                "Unlock advanced features and priority support",
                "⬆️",
                "/plans",
                "navigate",
                canUpgrade,
                "success"
            ));

            // Switch OEM action
            actions.add(new QuickActionViewModel(
                "switch-oem",
                "Switch OEM",
                "Access other OEM portals you're registered with",
                "🔄",
                "/oem-portals",
                "navigate",
                true,
                "secondary"
            ));

            return actions;

        } catch (Exception e) {
            log.error("Error getting quick actions for vendor: {}", vendorId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<ActivityViewModel> getRecentActivities(String vendorId, String oemId, String companyCode, Integer limit) {
        log.info("Getting recent activities for vendor: {}, OEM ID: {}, company: {}, limit: {}",
                vendorId, oemId, companyCode, limit);

        try {
            List<ActivityViewModel> activities = new ArrayList<>();

            // Get vendor information using company code
            Long companyCodeLong = Long.parseLong(companyCode);
            Optional<Vendor> vendorOpt = vendorRepository.findByCompanyCode(companyCodeLong);
            if (vendorOpt.isEmpty()) {
                return activities;
            }

            Vendor vendor = vendorOpt.get();

            // Get OEM information by ID
            Optional<OemMaster> oemOpt = oemMasterRepository.findById(java.util.UUID.fromString(oemId));
            if (oemOpt.isEmpty()) {
                return activities;
            }

            OemMaster oem = oemOpt.get();

            // Get onboarding process first
            Optional<OnboardingProcess> onboardingOpt = onboardingProcessRepository
                .findByVendorAndOemAndCompanyCode(vendor, oem, companyCodeLong);

            if (onboardingOpt.isEmpty()) {
                return activities;
            }

            // Get recent onboarding events for this specific onboarding process
            List<OnboardingEvent> events = onboardingEventRepository
                .findByOnboardingProcessOrderByEventTimestampDesc(onboardingOpt.get());

            // Convert events to activities
            int count = 0;
            for (OnboardingEvent event : events) {
                if (count >= (limit != null ? limit : 10)) {
                    break;
                }

                activities.add(new ActivityViewModel(
                    event.getEventId().toString(),
                    event.getEventType(),
                    "Onboarding event: " + event.getEventType(), // Use event type as description
                    "onboarding",
                    event.getEventTimestamp(),
                    "completed",
                    "onboarding_process",
                    event.getOnboardingProcess() != null ? event.getOnboardingProcess().getOnboardingId().toString() : null
                ));

                count++;
            }

            return activities;

        } catch (Exception e) {
            log.error("Error getting recent activities for vendor: {}", vendorId, e);
            return new ArrayList<>();
        }
    }

    // Helper methods

    private int calculateOverallProgress(OnboardingProcess onboarding) {
        if (onboarding == null) {
            return 0;
        }

        // Calculate based on onboarding status and implementation progress
        switch (onboarding.getStatus()) {
            case NOT_STARTED:
                return 10;
            case IN_PROGRESS:
                return 50;
            case COMPLETED:
                return 100;
            case FAILED:
                return 25;
            case CANCELLED:
                return 0;
            default:
                return 0;
        }
    }

    private String calculateCompletedSteps(OnboardingProcess onboarding) {
        if (onboarding == null) {
            return "0/6";
        }

        int completed = 0;
        int total = 6; // Total steps in the process

        // Count completed steps based on onboarding status
        switch (onboarding.getStatus()) {
            case NOT_STARTED:
                completed = 0;
                break;
            case IN_PROGRESS:
                completed = 3;
                break;
            case COMPLETED:
                completed = 6;
                break;
            case FAILED:
                completed = 1;
                break;
            case CANCELLED:
                completed = 0;
                break;
            default:
                completed = 0;
        }

        return completed + "/" + total;
    }



    private DashboardStatsViewModel.CriticalAlertViewModel buildCriticalAlert(long daysRemaining, int progress) {
        if (daysRemaining <= 7) {
            return DashboardStatsViewModel.CriticalAlertViewModel.builder()
                    .type("error")
                    .title("Urgent Action Required")
                    .message("Less than 7 days remaining for ASN 2.1 migration deadline!")
                    .actionRequired(true)
                    .actionUrl("/onboarding")
                    .build();
        }

        else if (daysRemaining <= 30 && progress < 80) {
            return DashboardStatsViewModel.CriticalAlertViewModel.builder()
                .type("warning")
                .title("Critical Deadline")
                .message("30th September 2025 - ASN 2.1 migration must be completed by this date to ensure continuity of your Auto ASN functionalities.")
                .actionRequired(true)
                .actionUrl("/onboarding")
                .build();
        }

        return DashboardStatsViewModel.CriticalAlertViewModel.builder()
            .type("info")
            .title("On Track")
            .message("Your ASN 2.1 implementation is progressing well.")
            .actionRequired(false)
            .actionUrl(null)
            .build();
    }

    private String getImplementationStatus(int progress, long daysRemaining) {
        if (progress >= 100) {
            return "Completed";
        } else if (progress >= 80) {
            return "Nearly Complete";
        } else if (progress >= 50) {
            return "In Progress";
        } else if (daysRemaining <= 30) {
            return "Behind Schedule";
        } else {
            return "Getting Started";
        }
    }

    private String getNextAction(OnboardingProcess onboarding) {
        if (onboarding == null) {
            return "Start onboarding process";
        }

        switch (onboarding.getStatus()) {
            case NOT_STARTED:
                return "Start onboarding process";
            case IN_PROGRESS:
                return "Continue with payment and deployment selection";
            case COMPLETED:
                return "Begin implementation phase";
            case FAILED:
                return "Retry onboarding process";
            case CANCELLED:
                return "Restart onboarding process";
            default:
                return "Start onboarding process";
        }
    }

    private DashboardStatsViewModel buildDefaultDashboardStats() {
        long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), ASN_DEADLINE);

        return DashboardStatsViewModel.builder()
            .progress(0)
            .completedSteps("0/6")
            .daysRemaining((int) Math.max(0, daysRemaining))
            .currentPlan("Basic")
            .deadline(ASN_DEADLINE)
            .status("Getting Started")
            .nextAction("Start onboarding process")
            .criticalAlert(buildCriticalAlert(daysRemaining, 0))
            .build();
    }

    private ProgressViewModel buildDefaultOnboardingProgress() {
        List<ProgressViewModel.StepProgressViewModel> steps = Arrays.asList(
            ProgressViewModel.StepProgressViewModel.builder()
                .id(1)
                .title("Account Setup")
                .description("Initial registration and vendor verification")
                .status("pending")
                .isActionable(true)
                .actionUrl("/onboarding")
                .build(),
            ProgressViewModel.StepProgressViewModel.builder()
                .id(2)
                .title("ASN 2.1 Confirmation")
                .description("Review and confirm implementation requirements")
                .status("pending")
                .isActionable(false)
                .build(),
            ProgressViewModel.StepProgressViewModel.builder()
                .id(3)
                .title("Payment")
                .description("Pay based on Deployment Method Selection")
                .status("pending")
                .isActionable(false)
                .build(),
            ProgressViewModel.StepProgressViewModel.builder()
                .id(4)
                .title("Deployment Method Selection")
                .description("Choose between self-deployment or assisted implementation")
                .status("pending")
                .isActionable(false)
                .build()
        );

        return ProgressViewModel.builder()
            .percentage(0)
            .completedSteps(0)
            .totalSteps(steps.size())
            .steps(steps)
            .type("onboarding")
            .currentStepId(1)
            .lastUpdated(LocalDateTime.now())
            .build();
    }

    private ProgressViewModel buildDefaultImplementationProgress() {
        List<ProgressViewModel.StepProgressViewModel> steps = Arrays.asList(
            ProgressViewModel.StepProgressViewModel.builder()
                .id(1)
                .title("Deployment")
                .description("ERP/Tally integration and system configuration")
                .status("pending")
                .isActionable(false)
                .build(),
            ProgressViewModel.StepProgressViewModel.builder()
                .id(2)
                .title("User Acceptance Testing")
                .description("Testing and validation of ASN 2.1 implementation")
                .status("pending")
                .isActionable(false)
                .build(),
            ProgressViewModel.StepProgressViewModel.builder()
                .id(3)
                .title("Go Live")
                .description("Final deployment and production activation")
                .status("pending")
                .isActionable(false)
                .build()
        );

        return ProgressViewModel.builder()
            .percentage(0)
            .completedSteps(0)
            .totalSteps(steps.size())
            .steps(steps)
            .type("implementation")
            .currentStepId(null)
            .lastUpdated(LocalDateTime.now())
            .build();
    }

    private UserProfileViewModel buildDefaultUserProfile() {
        return UserProfileViewModel.builder()
            .id("default")
            .companyName("Default Company")
            .currentPlan("Basic")
            .status("Active")
            .gstinDetails(new ArrayList<>())
            .oemAssociations(new ArrayList<>())
            .createdAt(LocalDateTime.now())
            .build();
    }

    private List<ProgressViewModel.StepProgressViewModel> buildOnboardingSteps(OnboardingProcess onboarding) {
        List<ProgressViewModel.StepProgressViewModel> steps = new ArrayList<>();

        // Step 1: Account Setup
        steps.add(ProgressViewModel.StepProgressViewModel.builder()
            .id(1)
            .title("Account Setup")
            .description("Initial registration and vendor verification completed")
            .status("completed")
            .completedAt(onboarding.getCreatedAt())
            .isActionable(false)
            .build());

        // Step 2: ASN 2.1 Confirmation
        String step2Status = OnboardingStatus.COMPLETED.equals(onboarding.getStatus()) ? "completed" : "current";
        steps.add(ProgressViewModel.StepProgressViewModel.builder()
            .id(2)
            .title("ASN 2.1 Confirmation")
            .description("Review and confirm implementation requirements")
            .status(step2Status)
            .completedAt("completed".equals(step2Status) ? onboarding.getUpdatedAt() : null)
            .isActionable("current".equals(step2Status))
            .actionUrl("/onboarding")
            .build());

        // Step 3: Payment
        String step3Status = OnboardingStatus.COMPLETED.equals(onboarding.getStatus()) ? "current" : "pending";
        steps.add(ProgressViewModel.StepProgressViewModel.builder()
            .id(3)
            .title("Payment")
            .description("Pay based on Deployment Method Selection")
            .status(step3Status)
            .isActionable("current".equals(step3Status))
            .actionUrl("/onboarding")
            .build());

        // Step 4: Deployment Method Selection
        steps.add(ProgressViewModel.StepProgressViewModel.builder()
            .id(4)
            .title("Deployment Method Selection")
            .description("Choose between self-deployment or assisted implementation")
            .status("pending")
            .isActionable(false)
            .build());

        return steps;
    }

    private List<ProgressViewModel.StepProgressViewModel> buildImplementationSteps(OnboardingProcess onboarding) {
        List<ProgressViewModel.StepProgressViewModel> steps = new ArrayList<>();

        // Implementation steps are only available after onboarding completion
        boolean onboardingComplete = OnboardingStatus.COMPLETED.equals(onboarding.getStatus());

        // Step 1: Deployment
        String step1Status = onboardingComplete ? "current" : "pending";
        steps.add(ProgressViewModel.StepProgressViewModel.builder()
            .id(1)
            .title("Deployment")
            .description("ERP/Tally integration and system configuration")
            .status(step1Status)
            .isActionable(onboardingComplete)
            .actionUrl(onboardingComplete ? "/implementation" : null)
            .build());

        // Step 2: User Acceptance Testing
        steps.add(ProgressViewModel.StepProgressViewModel.builder()
            .id(2)
            .title("User Acceptance Testing")
            .description("Testing and validation of ASN 2.1 implementation")
            .status("pending")
            .isActionable(false)
            .build());

        // Step 3: Go Live
        steps.add(ProgressViewModel.StepProgressViewModel.builder()
            .id(3)
            .title("Go Live")
            .description("Final deployment and production activation")
            .status("pending")
            .isActionable(false)
            .build());

        return steps;
    }

    private LocalDateTime calculateEstimatedCompletion(List<ProgressViewModel.StepProgressViewModel> steps) {
        // Simple estimation: 1 week per pending step
        long pendingSteps = steps.stream().filter(s -> "pending".equals(s.getStatus())).count();
        return LocalDateTime.now().plusWeeks(pendingSteps);
    }

    private List<UserProfileViewModel.GSTINDetailViewModel> buildGSTINDetails(Vendor vendor) {
        // This would typically come from a separate GSTIN entity
        // For now, return empty list or mock data
        return new ArrayList<>();
    }

    private UserProfileViewModel.SubscriptionDetailViewModel buildSubscriptionDetails(Vendor vendor) {
        return UserProfileViewModel.SubscriptionDetailViewModel.builder()
            .planName("Basic Plan")
            .planType("Basic")
            .startDate(vendor.getCreatedAt())
            .endDate(vendor.getCreatedAt().plusYears(1))
            .isActive(true)
            .features(Arrays.asList("Basic ASN Support", "Email Support", "Standard Documentation"))
            .limits(UserProfileViewModel.PlanLimitsViewModel.builder()
                .maxTransactions(1000)
                .maxUsers(5)
                .supportLevel("Email")
                .hasAISpecialist(false)
                .build())
            .build();
    }

    private List<String> getOemAssociations(Vendor vendor, String companyCode) {
        // This would typically query the onboarding processes to find associated OEMs
        return Arrays.asList("TATA_MOTORS"); // Default for now
    }

    // Helper methods for JSON parsing and building
    private String extractContactName(String primaryContactJson) {
        try {
            if (primaryContactJson == null || primaryContactJson.trim().isEmpty()) {
                return "";
            }
            // Simple JSON parsing - in production, use Jackson ObjectMapper
            if (primaryContactJson.contains("\"name\"")) {
                String[] parts = primaryContactJson.split("\"name\"\\s*:\\s*\"");
                if (parts.length > 1) {
                    String namePart = parts[1].split("\"")[0];
                    return namePart;
                }
            }
            return "";
        } catch (Exception e) {
            log.warn("Error extracting contact name from JSON: {}", e.getMessage());
            return "";
        }
    }

    private String extractContactEmail(String primaryContactJson) {
        try {
            if (primaryContactJson == null || primaryContactJson.trim().isEmpty()) {
                return "";
            }
            // Simple JSON parsing - in production, use Jackson ObjectMapper
            if (primaryContactJson.contains("\"email\"")) {
                String[] parts = primaryContactJson.split("\"email\"\\s*:\\s*\"");
                if (parts.length > 1) {
                    String emailPart = parts[1].split("\"")[0];
                    return emailPart;
                }
            }
            return "";
        } catch (Exception e) {
            log.warn("Error extracting contact email from JSON: {}", e.getMessage());
            return "";
        }
    }

    private String extractContactPhone(String primaryContactJson) {
        try {
            if (primaryContactJson == null || primaryContactJson.trim().isEmpty()) {
                return "";
            }
            // Simple JSON parsing - in production, use Jackson ObjectMapper
            if (primaryContactJson.contains("\"mobile\"")) {
                String[] parts = primaryContactJson.split("\"mobile\"\\s*:\\s*\"");
                if (parts.length > 1) {
                    String phonePart = parts[1].split("\"")[0];
                    return phonePart;
                }
            }
            return "";
        } catch (Exception e) {
            log.warn("Error extracting contact phone from JSON: {}", e.getMessage());
            return "";
        }
    }

    private String buildPrimaryContactJson(String name, String email, String phone) {
        return String.format("""
            {
                "name": "%s",
                "email": "%s",
                "mobile": "%s",
                "designation": ""
            }""",
            name != null ? name : "",
            email != null ? email : "",
            phone != null ? phone : ""
        );
    }

    private String getCurrentPlan(Vendor vendor) {
        // For now, return default plan - implement based on actual plan storage
        return "Basic Plan";
    }
}
