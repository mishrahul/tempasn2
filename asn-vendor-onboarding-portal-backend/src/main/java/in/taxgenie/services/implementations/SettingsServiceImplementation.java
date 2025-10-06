package in.taxgenie.services.implementations;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.entities.*;
import in.taxgenie.repositories.*;
import in.taxgenie.services.interfaces.ISettingsService;
import in.taxgenie.services.UserSessionService;
import in.taxgenie.viewmodels.settings.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of settings service
 */
@Service
@Transactional
public class SettingsServiceImplementation implements ISettingsService {

    private static final Logger logger = LoggerFactory.getLogger(SettingsServiceImplementation.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private VendorGstinRepository vendorGstinRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private VendorOemAccessRepository vendorOemAccessRepository;

    @Autowired
    private OemMasterRepository oemMasterRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private UserSessionService userSessionService;

    @Override
    @Transactional(readOnly = true)
    public CompanyInfoResponseViewModel getCompanyInfo(IAuthContextViewModel auth) {
        logger.info("Getting company info for vendor: {}", auth.getUserId());
        
        try {
            Vendor vendor = vendorRepository.findByCompanyCode(auth.getCompanyCode())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            // Parse primary contact JSON
            String primaryContactJson = vendor.getPrimaryContact();
            Map<String, Object> primaryContact = parseJsonToMap(primaryContactJson);

            return CompanyInfoResponseViewModel.builder()
                    .companyName(vendor.getCompanyName())
                    .panNumber(vendor.getPanNumber())
                    .contactPerson((String) primaryContact.get("name"))
                    .email((String) primaryContact.get("email"))
                    .phone((String) primaryContact.get("mobile"))
                    .vendorCode(generateVendorCode(vendor))
                    .status(vendor.getStatus().toString())
                    .lastUpdatedAt(vendor.getUpdatedAt() != null ? vendor.getUpdatedAt().format(DATE_FORMATTER) : null)
                    .build();
                    
        } catch (Exception e) {
            logger.error("Error getting company info for vendor: {}", auth.getUserId(), e);
            throw new RuntimeException("Failed to get company information: " + e.getMessage());
        }
    }

    @Override
    public CompanyInfoResponseViewModel updateCompanyInfo(IAuthContextViewModel auth, CompanyInfoUpdateRequestViewModel request) {
        logger.info("Updating company info for vendor: {}", auth.getUserId());
        
        try {
            Vendor vendor = vendorRepository.findByCompanyCodeAndUserId(auth.getCompanyCode(), auth.getUserId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            // Update vendor fields
            vendor.setCompanyName(request.getCompanyName());
            vendor.setPanNumber(request.getPanNumber());
            
            // Update primary contact JSON
            String primaryContactJson = String.format("""
                {
                    "name": "%s",
                    "email": "%s",
                    "mobile": "%s",
                    "designation": "Primary Contact"
                }""", 
                request.getContactPerson(),
                request.getEmail(),
                request.getPhone());
            
            vendor.setPrimaryContact(primaryContactJson);
            vendor.setUpdatedAt(LocalDateTime.now());

            vendorRepository.save(vendor);

            return getCompanyInfo(auth);
            
        } catch (Exception e) {
            logger.error("Error updating company info for vendor: {}", auth.getUserId(), e);
            throw new RuntimeException("Failed to update company information: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public GstinManagementViewModel getGstinManagement(IAuthContextViewModel auth) {
        logger.info("Getting GSTIN management for vendor: {}", auth.getUserId());
        
        try {
            Vendor vendor = vendorRepository.findByCompanyCode(auth.getCompanyCode())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            List<VendorGstin> vendorGstins = vendorGstinRepository.findByVendorAndCompanyCode(vendor, auth.getCompanyCode());

            List<GstinManagementViewModel.GstinDetailViewModel> gstinDetails = vendorGstins.stream()
                    .map(this::mapToGstinDetailViewModel)
                    .collect(Collectors.toList());

            int verifiedCount = (int) vendorGstins.stream().filter(VendorGstin::getIsVerified).count();
            int pendingCount = vendorGstins.size() - verifiedCount;

            return GstinManagementViewModel.builder()
                    .gstinDetails(gstinDetails)
                    .totalCount(vendorGstins.size())
                    .verifiedCount(verifiedCount)
                    .pendingCount(pendingCount)
                    .build();
                    
        } catch (Exception e) {
            logger.error("Error getting GSTIN management for vendor: {}", auth.getUserId(), e);
            throw new RuntimeException("Failed to get GSTIN management: " + e.getMessage());
        }
    }

    @Override
    public GstinManagementViewModel.GstinDetailViewModel createGstin(IAuthContextViewModel auth, GstinCreateRequestViewModel request) {
        logger.info("Creating GSTIN for vendor: {}", auth.getUserId());
        
        try {
            Vendor vendor = vendorRepository.findByCompanyCodeAndUserId(auth.getCompanyCode(), auth.getUserId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            // Check if GSTIN already exists
            Optional<VendorGstin> existingGstin = vendorGstinRepository.findByGstinAndCompanyCode(request.getGstin(), auth.getCompanyCode());
            if (existingGstin.isPresent()) {
                throw new RuntimeException("GSTIN already exists");
            }

            // If this is set as primary, unset other primary GSTINs
            if (request.isPrimary()) {
                vendorGstinRepository.updatePrimaryStatusForVendor(vendor, auth.getCompanyCode());
            }

            VendorGstin vendorGstin = VendorGstin.builder()
                    .vendor(vendor)
                    .gstin(request.getGstin())
                    .stateCode(request.getStateCode())
                    .isPrimary(request.isPrimary())
                    .isVerified(false)
                    .build();

            // Set company code from BaseEntity
            vendorGstin.setCompanyCode(auth.getCompanyCode());

            vendorGstin = vendorGstinRepository.save(vendorGstin);

            return mapToGstinDetailViewModel(vendorGstin);
            
        } catch (Exception e) {
            logger.error("Error creating GSTIN for vendor: {}", auth.getUserId(), e);
            throw new RuntimeException("Failed to create GSTIN: " + e.getMessage());
        }
    }

    @Override
    public GstinManagementViewModel.GstinDetailViewModel updateGstin(IAuthContextViewModel auth, String gstinId, GstinUpdateRequestViewModel request) {
        logger.info("Updating GSTIN {} for vendor: {}", gstinId, auth.getUserId());
        
        try {
            VendorGstin vendorGstin = vendorGstinRepository.findByGstinIdAndCompanyCode(UUID.fromString(gstinId), auth.getCompanyCode())
                    .orElseThrow(() -> new RuntimeException("GSTIN not found"));

            // Verify ownership
            if (!vendorGstin.getVendor().getUserId().equals(auth.getUserId())) {
                throw new RuntimeException("Access denied");
            }

            // If this is set as primary, unset other primary GSTINs
            if (request.isPrimary() && !vendorGstin.getIsPrimary()) {
                vendorGstinRepository.updatePrimaryStatusForVendor(vendorGstin.getVendor(), auth.getCompanyCode());
            }

            vendorGstin.setIsPrimary(request.isPrimary());
            vendorGstin.setUpdatedAt(LocalDateTime.now());

            vendorGstin = vendorGstinRepository.save(vendorGstin);

            return mapToGstinDetailViewModel(vendorGstin);
            
        } catch (Exception e) {
            logger.error("Error updating GSTIN {} for vendor: {}", gstinId, auth.getUserId(), e);
            throw new RuntimeException("Failed to update GSTIN: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteGstin(IAuthContextViewModel auth, String gstinId) {
        logger.info("Deleting GSTIN {} for vendor: {}", gstinId, auth.getUserId());
        
        try {
            VendorGstin vendorGstin = vendorGstinRepository.findByGstinIdAndCompanyCode(UUID.fromString(gstinId), auth.getCompanyCode())
                    .orElseThrow(() -> new RuntimeException("GSTIN not found"));

            // Verify ownership
            if (!vendorGstin.getVendor().getUserId().equals(auth.getUserId())) {
                throw new RuntimeException("Access denied");
            }

            // Don't allow deletion of primary GSTIN if it's the only one
            if (vendorGstin.getIsPrimary()) {
                long gstinCount = vendorGstinRepository.countByVendorAndCompanyCode(vendorGstin.getVendor(), auth.getCompanyCode());
                if (gstinCount <= 1) {
                    throw new RuntimeException("Cannot delete the only GSTIN");
                }
            }

            vendorGstinRepository.delete(vendorGstin);
            return true;
            
        } catch (Exception e) {
            logger.error("Error deleting GSTIN {} for vendor: {}", gstinId, auth.getUserId(), e);
            throw new RuntimeException("Failed to delete GSTIN: " + e.getMessage());
        }
    }

    // Helper methods
    private GstinManagementViewModel.GstinDetailViewModel mapToGstinDetailViewModel(VendorGstin vendorGstin) {
        return GstinManagementViewModel.GstinDetailViewModel.builder()
                .id(vendorGstin.getGstinId().toString())
                .gstin(vendorGstin.getGstin())
                .state(getStateName(vendorGstin.getStateCode()))
                .stateCode(vendorGstin.getStateCode())
                .vendorCode(generateVendorCodeForGstin(vendorGstin))
                .isPrimary(vendorGstin.getIsPrimary())
                .isVerified(vendorGstin.getIsVerified())
                .verifiedAt(vendorGstin.getVerifiedAt() != null ? vendorGstin.getVerifiedAt().format(DATE_FORMATTER) : null)
                .status(vendorGstin.getIsVerified() ? "VERIFIED" : "PENDING")
                .createdAt(vendorGstin.getCreatedAt() != null ? vendorGstin.getCreatedAt().format(DATE_FORMATTER) : null)
                .build();
    }

    private String generateVendorCode(Vendor vendor) {
        // Generate vendor code based on company name and ID
        String prefix = vendor.getCompanyName().replaceAll("[^A-Za-z]", "").toUpperCase();
        if (prefix.length() > 3) {
            prefix = prefix.substring(0, 3);
        }
        return String.format("V%s%04d", prefix, vendor.getVendorId().hashCode() % 10000);
    }

    private String generateVendorCodeForGstin(VendorGstin vendorGstin) {
        // Generate vendor code for GSTIN
        return String.format("V%s%03d", vendorGstin.getStateCode(), vendorGstin.getGstinId().hashCode() % 1000);
    }

    private String getStateName(String stateCode) {
        // Map state codes to state names
        Map<String, String> stateMap = new HashMap<>();
        stateMap.put("01", "Jammu and Kashmir");
        stateMap.put("02", "Himachal Pradesh");
        stateMap.put("03", "Punjab");
        stateMap.put("04", "Chandigarh");
        stateMap.put("05", "Uttarakhand");
        stateMap.put("06", "Haryana");
        stateMap.put("07", "Delhi");
        stateMap.put("08", "Rajasthan");
        stateMap.put("09", "Uttar Pradesh");
        stateMap.put("10", "Bihar");
        stateMap.put("11", "Sikkim");
        stateMap.put("12", "Arunachal Pradesh");
        stateMap.put("13", "Nagaland");
        stateMap.put("14", "Manipur");
        stateMap.put("15", "Mizoram");
        stateMap.put("16", "Tripura");
        stateMap.put("17", "Meghalaya");
        stateMap.put("18", "Assam");
        stateMap.put("19", "West Bengal");
        stateMap.put("20", "Jharkhand");
        stateMap.put("21", "Odisha");
        stateMap.put("22", "Chhattisgarh");
        stateMap.put("23", "Madhya Pradesh");
        stateMap.put("24", "Gujarat");
        stateMap.put("25", "Daman and Diu");
        stateMap.put("26", "Dadra and Nagar Haveli");
        stateMap.put("27", "Maharashtra");
        stateMap.put("28", "Andhra Pradesh");
        stateMap.put("29", "Karnataka");
        stateMap.put("30", "Goa");
        stateMap.put("31", "Lakshadweep");
        stateMap.put("32", "Kerala");
        stateMap.put("33", "Tamil Nadu");
        stateMap.put("34", "Puducherry");
        stateMap.put("35", "Andaman and Nicobar Islands");
        stateMap.put("36", "Telangana");
        stateMap.put("37", "Andhra Pradesh");

        return stateMap.getOrDefault(stateCode, "Unknown State");
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionBillingViewModel getSubscriptionBilling(IAuthContextViewModel auth) {
        logger.info("Getting subscription billing for vendor: {}", auth.getUserId());

        try {
            Vendor vendor = vendorRepository.findByCompanyCodeAndUserId(auth.getCompanyCode(), auth.getUserId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            // Get current active subscription
            Optional<Subscription> currentSubscription = subscriptionRepository.findActiveSubscriptionByVendor(vendor);

            SubscriptionBillingViewModel.CurrentSubscriptionViewModel currentSub = null;
            if (currentSubscription.isPresent()) {
                currentSub = mapToCurrentSubscriptionViewModel(currentSubscription.get());
            }

            // Get subscription history
            List<Subscription> subscriptionHistory = subscriptionRepository.findByVendorAndCompanyCodeOrderByCreatedAtDesc(vendor, auth.getCompanyCode());
            List<SubscriptionBillingViewModel.SubscriptionHistoryViewModel> historyList = subscriptionHistory.stream()
                    .map(this::mapToSubscriptionHistoryViewModel)
                    .collect(Collectors.toList());

            // Get payment history
            List<PaymentTransaction> paymentHistory = paymentTransactionRepository.findByVendorAndCompanyCodeOrderByCreatedAtDesc(vendor, auth.getCompanyCode());
            List<SubscriptionBillingViewModel.PaymentHistoryViewModel> paymentList = paymentHistory.stream()
                    .map(this::mapToPaymentHistoryViewModel)
                    .collect(Collectors.toList());

            // Calculate billing info
            SubscriptionBillingViewModel.BillingInfoViewModel billingInfo = calculateBillingInfo(paymentHistory, currentSubscription.orElse(null));

            // Get available subscription plans
            List<SubscriptionPlan> plans = subscriptionPlanRepository.findByIsActiveTrue();
            List<SubscriptionBillingViewModel.SubscriptionPlanViewModel> subscriptionPlans = plans.stream()
                    .map(this::mapToSubscriptionPlanViewModel)
                    .collect(Collectors.toList());

            return SubscriptionBillingViewModel.builder()
                    .currentSubscription(currentSub)
                    .subscriptionHistory(historyList)
                    .billingInfo(billingInfo)
                    .paymentHistory(paymentList)
                    .subscriptionPlans(subscriptionPlans)
                    .build();

        } catch (Exception e) {
            logger.error("Error getting subscription billing for vendor: {}", auth.getUserId(), e);
            throw new RuntimeException("Failed to get subscription billing: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OemAccessManagementViewModel getOemAccessManagement(IAuthContextViewModel auth) {
        logger.info("Getting OEM access management for vendor: {}", auth.getUserId());

        try {
            Vendor vendor = vendorRepository.findByCompanyCodeAndUserId(auth.getCompanyCode(), auth.getUserId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            // Get vendor OEM access
            List<VendorOemAccess> vendorOemAccesses = vendorOemAccessRepository.findByVendorAndCompanyCode(vendor, auth.getCompanyCode());

            List<OemAccessManagementViewModel.OemAccessDetailViewModel> oemAccessList = vendorOemAccesses.stream()
                    .map(this::mapToOemAccessDetailViewModel)
                    .collect(Collectors.toList());

            // Get selected OEM from session
            UserSessionService.SelectedOemData selectedOemData = userSessionService.getSelectedOem(auth);
            String selectedOemId = selectedOemData != null ? selectedOemData.getOemId() : null;
            String selectedOemName = selectedOemData != null ? selectedOemData.getOemName() : null;

            int activeAccess = (int) vendorOemAccesses.stream().filter(access -> "ACTIVE".equals(access.getAccessStatus().toString())).count();
            int pendingAccess = (int) vendorOemAccesses.stream().filter(access -> "PENDING".equals(access.getAccessStatus().toString())).count();

            return OemAccessManagementViewModel.builder()
                    .oemAccess(oemAccessList)
                    .selectedOemId(selectedOemId)
                    .selectedOemName(selectedOemName)
                    .totalOemAccess(vendorOemAccesses.size())
                    .activeAccess(activeAccess)
                    .pendingAccess(pendingAccess)
                    .build();

        } catch (Exception e) {
            logger.error("Error getting OEM access management for vendor: {}", auth.getUserId(), e);
            throw new RuntimeException("Failed to get OEM access management: " + e.getMessage());
        }
    }

    @Override
    public OemSwitchResponseViewModel switchOem(IAuthContextViewModel auth, OemSwitchRequestViewModel request) {
        logger.info("Switching OEM to {} for vendor: {}", request.getOemId(), auth.getUserId());

        try {
            // Verify OEM exists and vendor has access
            OemMaster oem = oemMasterRepository.findByOemIdAndCompanyCode(UUID.fromString(request.getOemId()), auth.getCompanyCode())
                    .orElseThrow(() -> new RuntimeException("OEM not found"));

            Vendor vendor = vendorRepository.findByCompanyCodeAndUserId(auth.getCompanyCode(), auth.getUserId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));

            VendorOemAccess vendorOemAccess = vendorOemAccessRepository.findByVendorAndOemAndCompanyCode(vendor, oem, auth.getCompanyCode())
                    .orElseThrow(() -> new RuntimeException("Access to this OEM not found"));

            if (!"ACTIVE".equals(vendorOemAccess.getAccessStatus().toString())) {
                throw new RuntimeException("Access to this OEM is not active");
            }

            // Update session
            userSessionService.setSelectedOem(auth, request.getOemId(), oem.getFullName());

            // Parse OEM config for ASN version
            Map<String, Object> oemConfig = parseJsonToMap(oem.getConfig());

            // Parse access permissions
            Map<String, Object> permissions = parseJsonToMap(vendorOemAccess.getPermissionsCache());
            List<String> permissionList = permissions.keySet().stream()
                    .filter(key -> Boolean.TRUE.equals(permissions.get(key)))
                    .collect(Collectors.toList());

            return OemSwitchResponseViewModel.builder()
                    .selectedOemId(oem.getOemId().toString())
                    .selectedOemName(oem.getFullName())
                    .shortName(oem.getOemName())
                    .asnVersion(oem.getAsnVersion())
                    .accessLevel(vendorOemAccess.getAccessLevel().toString())
                    .permissions(permissionList)
                    .sessionExpiry(LocalDateTime.now().plusHours(8).format(DATE_FORMATTER))
                    .switchSuccessful(true)
                    .message("OEM switched successfully")
                    .build();

        } catch (Exception e) {
            logger.error("Error switching OEM for vendor: {}", auth.getUserId(), e);
            return OemSwitchResponseViewModel.builder()
                    .switchSuccessful(false)
                    .message("Failed to switch OEM: " + e.getMessage())
                    .build();
        }
    }

    // Helper methods for mapping entities to view models
    private SubscriptionBillingViewModel.CurrentSubscriptionViewModel mapToCurrentSubscriptionViewModel(Subscription subscription) {
        // Parse pricing JSON
        Map<String, Object> pricing = parseJsonToMap(subscription.getPlan().getPricing());
        Map<String, Object> apiLimits = parseJsonToMap(subscription.getPlan().getApiLimits());

        return SubscriptionBillingViewModel.CurrentSubscriptionViewModel.builder()
                .subscriptionId(subscription.getSubscriptionId().toString())
                .planName(subscription.getPlan().getPlanName())
                .planCode(subscription.getPlan().getPlanCode())
                .status(subscription.getStatus().toString())
                .startDate(subscription.getStartDate() != null ? subscription.getStartDate().toString() : null)
                .endDate(subscription.getEndDate() != null ? subscription.getEndDate().toString() : null)
                .nextBillingDate(subscription.getNextBillingDate() != null ? subscription.getNextBillingDate().toString() : null)
                .pricing(SubscriptionBillingViewModel.PricingViewModel.builder()
                        .yearly(new BigDecimal(pricing.getOrDefault("yearly", "0").toString()))
                        .monthly(new BigDecimal(pricing.getOrDefault("monthly", "0").toString()))
                        .setupFee(new BigDecimal(pricing.getOrDefault("setup_fee", "0").toString()))
                        .gstRate(new BigDecimal(pricing.getOrDefault("gst_rate", "18").toString()))
                        .currency(pricing.getOrDefault("currency", "INR").toString())
                        .build())
                .annualFee(new BigDecimal(pricing.getOrDefault("yearly", "0").toString()))
                .setupFee(new BigDecimal(pricing.getOrDefault("setup_fee", "0").toString()))
                .currency(pricing.getOrDefault("currency", "INR").toString())
                .autoRenewal(true) // Default to true, add field to entity if needed
                .features(parseFeatures(subscription.getPlan().getFeatures()))
                .apiLimits(SubscriptionBillingViewModel.ApiLimitsViewModel.builder()
                        .requestsPerMinute(Integer.parseInt(apiLimits.getOrDefault("requests_per_minute", "60").toString()))
                        .requestsPerDay(Integer.parseInt(apiLimits.getOrDefault("requests_per_day", "10000").toString()))
                        .requestsPerMonth(Integer.parseInt(apiLimits.getOrDefault("requests_per_month", "300000").toString()))
                        .currentUsage(0) // TODO: Calculate from API logs
                        .resetDate(LocalDateTime.now().plusMonths(1).toString())
                        .build())
                .build();
    }

    private SubscriptionBillingViewModel.SubscriptionHistoryViewModel mapToSubscriptionHistoryViewModel(Subscription subscription) {
        Map<String, Object> pricing = parseJsonToMap(subscription.getPlan().getPricing());

        return SubscriptionBillingViewModel.SubscriptionHistoryViewModel.builder()
                .subscriptionId(subscription.getSubscriptionId().toString())
                .planName(subscription.getPlan().getPlanName())
                .status(subscription.getStatus().toString())
                .startDate(subscription.getStartDate() != null ? subscription.getStartDate().toString() : null)
                .endDate(subscription.getEndDate() != null ? subscription.getEndDate().toString() : null)
                .nextBillingDate(subscription.getNextBillingDate() != null ? subscription.getNextBillingDate().toString() : null)
                .amount(new BigDecimal(pricing.getOrDefault("yearly", "0").toString()))
                .currency(pricing.getOrDefault("currency", "INR").toString())
                .build();
    }

    private SubscriptionBillingViewModel.PaymentHistoryViewModel mapToPaymentHistoryViewModel(PaymentTransaction payment) {
        return SubscriptionBillingViewModel.PaymentHistoryViewModel.builder()
                .transactionId(payment.getTransactionId().toString())
                .subscriptionId(payment.getSubscription() != null ? payment.getSubscription().getSubscriptionId().toString() : null)
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().toString())
                .paymentMethod("Credit Card") // Default payment method
                .transactionDate(payment.getInitiatedAt() != null ? payment.getInitiatedAt().format(DATE_FORMATTER) : null)
                .description("Payment for subscription") // Default description
                .build();
    }

    private SubscriptionBillingViewModel.BillingInfoViewModel calculateBillingInfo(List<PaymentTransaction> paymentHistory, Subscription currentSubscription) {
        BigDecimal totalPaid = paymentHistory.stream()
                .filter(p -> "SUCCESS".equals(p.getStatus().toString()))
                .map(PaymentTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal pendingAmount = paymentHistory.stream()
                .filter(p -> "PENDING".equals(p.getStatus().toString()))
                .map(PaymentTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String lastPaymentDate = paymentHistory.stream()
                .filter(p -> "SUCCESS".equals(p.getStatus().toString()))
                .findFirst()
                .map(p -> p.getInitiatedAt() != null ? p.getInitiatedAt().format(DATE_FORMATTER) : null)
                .orElse(null);

        String nextBillingDate = currentSubscription != null && currentSubscription.getNextBillingDate() != null
                ? currentSubscription.getNextBillingDate().toString()
                : null;

        return SubscriptionBillingViewModel.BillingInfoViewModel.builder()
                .totalPaid(totalPaid)
                .pendingAmount(pendingAmount)
                .lastPaymentDate(lastPaymentDate)
                .nextBillingDate(nextBillingDate)
                .paymentMethod("Credit Card") // TODO: Get from user preferences
                .currency("INR")
                .build();
    }

    private SubscriptionBillingViewModel.SubscriptionPlanViewModel mapToSubscriptionPlanViewModel(SubscriptionPlan plan) {
        Map<String, Object> pricing = parseJsonToMap(plan.getPricing());
        Map<String, Object> apiLimits = parseJsonToMap(plan.getApiLimits());

        return SubscriptionBillingViewModel.SubscriptionPlanViewModel.builder()
                .planId(plan.getPlanId().toString())
                .planCode(plan.getPlanCode())
                .planName(plan.getPlanName())
                .pricing(SubscriptionBillingViewModel.PricingViewModel.builder()
                        .yearly(new BigDecimal(pricing.getOrDefault("yearly", "0").toString()))
                        .monthly(new BigDecimal(pricing.getOrDefault("monthly", "0").toString()))
                        .setupFee(new BigDecimal(pricing.getOrDefault("setup_fee", "0").toString()))
                        .gstRate(new BigDecimal(pricing.getOrDefault("gst_rate", "18").toString()))
                        .currency(pricing.getOrDefault("currency", "INR").toString())
                        .build())
                .features(parseFeatures(plan.getFeatures()))
                .apiLimits(SubscriptionBillingViewModel.ApiLimitsViewModel.builder()
                        .requestsPerMinute(Integer.parseInt(apiLimits.getOrDefault("requests_per_minute", "60").toString()))
                        .requestsPerDay(Integer.parseInt(apiLimits.getOrDefault("requests_per_day", "10000").toString()))
                        .requestsPerMonth(Integer.parseInt(apiLimits.getOrDefault("requests_per_month", "300000").toString()))
                        .currentUsage(0)
                        .resetDate(LocalDateTime.now().plusMonths(1).toString())
                        .build())
                .isFeatured(plan.getIsFeatured())
                .isActive(plan.getIsActive())
                .build();
    }

    private OemAccessManagementViewModel.OemAccessDetailViewModel mapToOemAccessDetailViewModel(VendorOemAccess vendorOemAccess) {
        OemMaster oem = vendorOemAccess.getOem();
        Map<String, Object> permissions = parseJsonToMap(vendorOemAccess.getPermissionsCache());

        List<String> permissionList = permissions.keySet().stream()
                .filter(key -> Boolean.TRUE.equals(permissions.get(key)))
                .collect(Collectors.toList());

        return OemAccessManagementViewModel.OemAccessDetailViewModel.builder()
                .oemId(oem.getOemId().toString())
                .oemName(oem.getFullName())
                .shortName(oem.getOemName()) // Use oemName as shortName
                .asnVersion(oem.getAsnVersion())
                .accessLevel(vendorOemAccess.getAccessLevel().toString())
                .status(vendorOemAccess.getAccessStatus().toString())
                .hasAccess("ACTIVE".equals(vendorOemAccess.getAccessStatus().toString()))
                .isActive("ACTIVE".equals(vendorOemAccess.getAccessStatus().toString()))
                .grantedAt(vendorOemAccess.getGrantedAt() != null ? vendorOemAccess.getGrantedAt().format(DATE_FORMATTER) : null)
                .expiresAt(vendorOemAccess.getExpiresAt() != null ? vendorOemAccess.getExpiresAt().format(DATE_FORMATTER) : null)
                .lastAccessedAt(vendorOemAccess.getLastAccessedAt() != null ? vendorOemAccess.getLastAccessedAt().format(DATE_FORMATTER) : null)
                .totalApiCalls(vendorOemAccess.getTotalApiCalls().intValue())
                .totalAsnGenerated(vendorOemAccess.getTotalAsnGenerated().intValue())
                .permissions(permissionList)
                .vendorCode(vendorOemAccess.getVendorCode())
                .build();
    }

    private List<String> parseFeatures(String featuresJson) {
        try {
            // Simple JSON array parsing
            featuresJson = featuresJson.trim().replaceAll("^\\[|\\]$", "");
            if (featuresJson.isEmpty()) {
                return new ArrayList<>();
            }

            return Arrays.stream(featuresJson.split(","))
                    .map(s -> s.trim().replaceAll("\"", ""))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.warn("Failed to parse features JSON: {}", featuresJson, e);
            return new ArrayList<>();
        }
    }



    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonToMap(String json) {
        try {
            // Simple JSON parsing - in production, use Jackson ObjectMapper
            json = json.trim().replaceAll("^\\{|\\}$", "");
            Map<String, Object> map = new HashMap<>();

            String[] pairs = json.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replaceAll("\"", "");
                    String value = keyValue[1].trim().replaceAll("\"", "");
                    map.put(key, value);
                }
            }
            return map;
        } catch (Exception e) {
            logger.warn("Failed to parse JSON: {}", json, e);
            return new HashMap<>();
        }
    }
}
