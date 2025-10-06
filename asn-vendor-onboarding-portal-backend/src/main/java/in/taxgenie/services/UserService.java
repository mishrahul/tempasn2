package in.taxgenie.services;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.auth.IJwtFacilities;
import in.taxgenie.entities.Vendor;
import in.taxgenie.entities.VendorGstin;
import in.taxgenie.repositories.VendorRepository;
import in.taxgenie.repositories.VendorGstinRepository;
import in.taxgenie.services.interfaces.IUserService;
import in.taxgenie.viewmodels.user.UserProfileViewModel;
import in.taxgenie.viewmodels.user.GstinDetailViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for user operations
 */
@Service
@Transactional
public class UserService implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final VendorRepository vendorRepository;
    private final VendorGstinRepository vendorGstinRepository;
    private final IJwtFacilities jwtFacilities;
    private final UserSessionService userSessionService;

    public UserService(VendorRepository vendorRepository,
                      VendorGstinRepository vendorGstinRepository,
                      IJwtFacilities jwtFacilities,
                      UserSessionService userSessionService) {
        this.vendorRepository = vendorRepository;
        this.vendorGstinRepository = vendorGstinRepository;
        this.jwtFacilities = jwtFacilities;
        this.userSessionService = userSessionService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileViewModel getUserProfile(IAuthContextViewModel auth) {
        logger.info("Getting user profile for user ID: {}", auth.getUserId());
        
        try {
            Vendor vendor = new Vendor();//vendorRepository.findById(auth.getUserId()).orElseThrow(() -> new RuntimeException("Vendor not found"));

            List<VendorGstin> gstinDetails = vendorGstinRepository.findByVendorId(vendor.getVendorId());
            
            List<GstinDetailViewModel> gstinViewModels = gstinDetails.stream()
                    .map(this::mapToGstinDetailViewModel)
                    .collect(Collectors.toList());

            return UserProfileViewModel.builder()
                    .id(vendor.getVendorId().toString())
                    //.email(vendor.getEmail())
                    //.name(vendor.getContactPerson())
                    .companyName(vendor.getCompanyName())
                    .panNumber(vendor.getPanNumber())
                    //.contactPerson(vendor.getContactPerson())
                    //.phone(vendor.getPhone())
                    //.vendorCode(vendor.getVendorCode())
                    .currentPlan("Basic") // TODO: Get from subscription
                    .gstinDetails(gstinViewModels)
                    .status(vendor.getStatus().toString())
                    .createdAt(vendor.getCreatedAt().toString())
                    .build();
                    
        } catch (Exception e) {
            logger.error("Error getting user profile for user ID: {}", auth.getUserId(), e);
            throw new RuntimeException("Failed to get user profile", e);
        }
    }

    @Override
    public UserProfileViewModel updateUserProfile(IAuthContextViewModel auth, UserProfileViewModel userProfile) {
        logger.info("Updating user profile for user ID: {}", auth.getUserId());
        
        try {
            Vendor vendor = new Vendor();
            //vendorRepository.findById(auth.getUserId())
              //      .orElseThrow(() -> new RuntimeException("Vendor not found"));

            // Update vendor fields
            if (userProfile.getCompanyName() != null) {
                vendor.setCompanyName(userProfile.getCompanyName());
            }
            if (userProfile.getContactPerson() != null) {
               // vendor.setContactPerson(userProfile.getContactPerson());
            }
            if (userProfile.getPhone() != null) {
                //vendor.setPhone(userProfile.getPhone());
            }

            vendor = vendorRepository.save(vendor);
            
            return getUserProfile(auth);
            
        } catch (Exception e) {
            logger.error("Error updating user profile for user ID: {}", auth.getUserId(), e);
            throw new RuntimeException("Failed to update user profile", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getProfileImageUrl(IAuthContextViewModel auth) {
        logger.info("Getting profile image URL for user ID: {}", auth.getUserId());
        
        // TODO: Implement profile image storage and retrieval
        return "/api/v1/users/profile/image/default.png";
    }

    @Override
    public boolean validateToken(String token) {
        try {
            return jwtFacilities.isValid(token);
        } catch (Exception e) {
            logger.error("Token validation failed", e);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Object getUserSessionContext(IAuthContextViewModel auth) {
        logger.info("Getting user session context for user ID: {}", auth.getUserId());

        UserProfileViewModel userProfile = getUserProfile(auth);
        UserSessionService.SelectedOemData selectedOem = userSessionService.getSelectedOem(auth);

        return new Object() {
            public final Object user = new Object() {
                public final String id = userProfile.getId();
                public final String email = userProfile.getEmail();
                public final String name = userProfile.getName();
            };
            public final Object selectedOEM = selectedOem != null ? new Object() {
                public final String id = selectedOem.getOemId();
                public final String name = selectedOem.getOemName();
            } : null;
            public final String sessionExpiry = "2024-12-31T23:59:59Z"; // TODO: Calculate from JWT
        };
    }

    private GstinDetailViewModel mapToGstinDetailViewModel(VendorGstin vendorGstin) {
//        return GstinDetailViewModel.builder()
//                .id(vendorGstin.getId().toString())
//                .gstinNumber(vendorGstin.getGstinNumber())
//                .legalName(vendorGstin.getLegalName())
//                .tradeName(vendorGstin.getTradeName())
//                .state(vendorGstin.getState())
//                .stateCode(vendorGstin.getStateCode())
//                .address(vendorGstin.getAddress())
//                .pincode(vendorGstin.getPincode())
//                .status(vendorGstin.getStatus().toString())
//                .isPrimary(vendorGstin.getIsPrimary())
//                .registrationDate(vendorGstin.getRegistrationDate() != null ?
//                                vendorGstin.getRegistrationDate().toString() : null)
//                .lastVerifiedAt(vendorGstin.getLastVerifiedAt() != null ?
//                              vendorGstin.getLastVerifiedAt().toString() : null)
//                .build();
        return null;
    }
}
