package in.taxgenie.services;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.entities.OemMaster;
import in.taxgenie.entities.VendorOemAccess;
import in.taxgenie.entities.enums.AccessLevel;
import in.taxgenie.entities.enums.Status;
import in.taxgenie.repositories.OemMasterRepository;
import in.taxgenie.repositories.VendorOemAccessRepository;
import in.taxgenie.services.interfaces.IOemPortalService;
import in.taxgenie.viewmodels.oem.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementation for OEM portal operations
 */
@Service
@Transactional
public class OemPortalService implements IOemPortalService {

    private static final Logger logger = LoggerFactory.getLogger(OemPortalService.class);

    private final OemMasterRepository oemMasterRepository;
    private final VendorOemAccessRepository vendorOemAccessRepository;

    public OemPortalService(OemMasterRepository oemMasterRepository,
                           VendorOemAccessRepository vendorOemAccessRepository) {
        this.oemMasterRepository = oemMasterRepository;
        this.vendorOemAccessRepository = vendorOemAccessRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AvailableOemsResponseViewModel getAvailableOems(IAuthContextViewModel auth) {
        logger.info("Getting available OEMs for vendor ID: {}", auth.getUserId());
        
        try {
            List<OemMaster> allOems = oemMasterRepository.findAll();

            List<OemViewModel> oemViewModels = allOems.stream()
                    .map(oem -> mapToOemViewModel(oem))
                    .collect(Collectors.toList());

            return AvailableOemsResponseViewModel.builder()
                    .oems(oemViewModels)
                    .totalCount(oemViewModels.size())
                    .message("OEMs retrieved successfully")
                    .build();
                    
        } catch (Exception e) {
            logger.error("Error getting available OEMs for vendor ID: {}", auth.getUserId(), e);
            throw new RuntimeException("Failed to get available OEMs", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OemViewModel getOemDetails(IAuthContextViewModel auth, String oemId) {
        logger.info("Getting OEM details for OEM ID: {} and vendor ID: {}", oemId, auth.getUserId());
        
        try {
            OemMaster oem = oemMasterRepository.findById(UUID.fromString(oemId))
                    .orElseThrow(() -> new RuntimeException("OEM not found"));
            return mapToOemViewModel(oem);
            
        } catch (Exception e) {
            logger.error("Error getting OEM details for OEM ID: {} and vendor ID: {}", oemId, auth.getUserId(), e);
            throw new RuntimeException("Failed to get OEM details", e);
        }
    }

    @Override
    public SelectOemResponseViewModel selectOem(IAuthContextViewModel auth, SelectOemRequestViewModel request) {
        logger.info("Selecting OEM {} for vendor ID: {}", request.getOemId(), auth.getUserId());
        
        try {
            // Verify OEM exists and vendor has access
            OemMaster oem = oemMasterRepository.findById(UUID.fromString(request.getOemId()))
                    .orElseThrow(() -> new RuntimeException("OEM not found"));

            Optional<VendorOemAccess> vendorAccess = vendorOemAccessRepository
                    .findByVendorIdAndOemId(UUID.fromString(""), UUID.fromString(request.getOemId()));

            if (vendorAccess.isEmpty()){// || !vendorAccess.get().getHasAccess()) {
                throw new RuntimeException("Access denied to selected OEM");
            }

            VendorOemAccess access = vendorAccess.get();
            
            // TODO: Store selected OEM in user session or cache
            
            SelectOemResponseViewModel.SelectedOemViewModel selectedOem = 
                    SelectOemResponseViewModel.SelectedOemViewModel.builder()
                            .id(String.valueOf(oem.getOemId()))
                            .name(oem.getFullName())
                            .accessLevel(access.getAccessLevel().toString())
                            .permissions(getPermissionsForAccessLevel(access.getAccessLevel()))
                            .sessionExpiry("2024-12-31T23:59:59Z") // TODO: Calculate proper expiry
                            .build();

            return SelectOemResponseViewModel.builder()
                    .success(true)
                    .message("OEM selected successfully")
                    .selectedOEM(selectedOem)
                    .build();
                    
        } catch (Exception e) {
            logger.error("Error selecting OEM {} for vendor ID: {}", request.getOemId(), auth.getUserId(), e);
            return SelectOemResponseViewModel.builder()
                    .success(false)
                    .message("Failed to select OEM: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public Object refreshOemAccess(IAuthContextViewModel auth) {
        logger.info("Refreshing OEM access for vendor ID: {}", auth.getUserId());
        
        try {
            List<VendorOemAccess> vendorAccess = null;//vendorOemAccessRepository.findByVendorId(auth.getUserId());
            
            // TODO: Implement access refresh logic (check subscriptions, etc.)
            
            return new Object() {
                public final boolean success = true;
                public final String message = "Access refreshed successfully";
                public final Object updatedAccess = vendorAccess.stream()
                        .map(access -> new Object() {
                            public final String oemId = String.valueOf(access.getOem().getOemId());
                            public final boolean hasAccess = true;//access.getHasAccess();
                            public final String accessLevel = access.getAccessLevel().toString();
                            public final String lastUpdated = access.getUpdatedAt().toString();
                        })
                        .collect(Collectors.toList());
            };
            
        } catch (Exception e) {
            logger.error("Error refreshing OEM access for vendor ID: {}", auth.getUserId(), e);
            throw new RuntimeException("Failed to refresh OEM access", e);
        }
    }

    @Override
    public Object requestOemAccess(IAuthContextViewModel auth, String oemId, Object requestData) {
        logger.info("Requesting OEM access for OEM ID: {} and vendor ID: {}", oemId, auth.getUserId());
        
        try {
            // Verify OEM exists
            oemMasterRepository.findById(UUID.fromString(oemId))
                    .orElseThrow(() -> new RuntimeException("OEM not found"));

            // TODO: Implement access request logic
            
            return new Object() {
                public final boolean success = true;
                public final String requestId = "REQ-" + System.currentTimeMillis();
                public final String message = "Access request submitted successfully";
                public final String estimatedProcessingTime = "2-3 business days";
            };
            
        } catch (Exception e) {
            logger.error("Error requesting OEM access for OEM ID: {} and vendor ID: {}", oemId, auth.getUserId(), e);
            throw new RuntimeException("Failed to request OEM access", e);
        }
    }

    private OemViewModel mapToOemViewModel(OemMaster oem) {


        boolean hasAccess = true;
        boolean isComingSoon = oem.getStatus() == Status.COMING_SOON;
        boolean noAccess = !hasAccess && !isComingSoon && oem.getStatus() == Status.ACTIVE;

        if(isComingSoon){
            hasAccess =false;
        }
        return OemViewModel.builder()
                .id(String.valueOf(oem.getOemId()))
                .oemCode(oem.getOemCode())
                //.shortName("");//oem.getShortName())
                .fullName(oem.getFullName())
                .logoBackground(getLogoBackground(String.valueOf(oem.getOemId())))
                .features(getOemFeatures(String.valueOf(oem.getOemId())))
                .status(oem.getStatus().toString())
                .hasAccess(hasAccess)
                .isComingSoon(isComingSoon)
                .noAccess(noAccess)
                .noAccessReason(noAccess ? "Subscription required" : null)
                //.description(oem.getDescription())
                //.website(oem.getWebsite())
                //.supportEmail(oem.getSupportEmail())
                //.supportPhone(oem.getSupportPhone())
                .build();
    }

    private String getLogoBackground(String oemId) {
        // Return predefined gradients based on OEM ID
        switch (oemId) {
            case "TML":
                return "linear-gradient(135deg, #1f4e79, #2563eb)";
            case "MAHINDRA":
                return "linear-gradient(135deg, #c41e3a, #8b0000)";
            case "TAFE":
                return "linear-gradient(135deg, #ff6b35, #f7931e)";
            default:
                return "linear-gradient(135deg, #6b7280, #9ca3af)";
        }
    }

    private List<String> getOemFeatures(String oemId) {
        // Return predefined features based on OEM ID
        switch (oemId) {
            case "TML":
                return Arrays.asList(
                        "✅ ASN 2.1 Implementation",
                        "✅ Advanced Shipping Notice",
                        "✅ ERP Integration Support",
                        "✅ Compliance Management"
                );
            case "MAHINDRA":
                return Arrays.asList(
                        "✅ ASN Implementation",
                        "✅ Vendor Portal Integration",
                        "✅ Supply Chain Management",
                        "✅ Real-time Tracking"
                );
            case "TAFE":
                return Arrays.asList(
                        "✅ ASN Integration",
                        "✅ Agricultural Equipment",
                        "✅ Supplier Management",
                        "✅ Inventory Optimization"
                );
            default:
                return Arrays.asList(
                        "• Bajaj Auto",
                        "• Hero MotoCorp",
                        "• TVS Motors",
                        "• Force Motors"
                );
        }
    }

    private List<String> getPermissionsForAccessLevel(AccessLevel accessLevel) {
        switch (accessLevel) {
            case PREMIUM:
                return Arrays.asList("ASN_CREATE", "ASN_UPDATE", "ASN_VIEW", "ASN_DELETE", "ANALYTICS_VIEW");
            case BASIC:
                return Arrays.asList("ASN_CREATE", "ASN_UPDATE", "ASN_VIEW");
            case READ_ONLY:
                return Arrays.asList("ASN_VIEW");
            default:
                return Arrays.asList();
        }
    }
}
