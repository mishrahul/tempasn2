package in.taxgenie.services;

import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.entities.*;
import in.taxgenie.entities.enums.Status;
import in.taxgenie.repositories.*;
import in.taxgenie.services.UserSessionService;
import in.taxgenie.services.implementations.SettingsServiceImplementation;
import in.taxgenie.viewmodels.settings.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsServiceImplementationTest {

    @Mock private VendorRepository vendorRepository;
    @Mock private VendorGstinRepository vendorGstinRepository;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private PaymentTransactionRepository paymentTransactionRepository;
    @Mock private VendorOemAccessRepository vendorOemAccessRepository;
    @Mock private OemMasterRepository oemMasterRepository;
    @Mock private SubscriptionPlanRepository subscriptionPlanRepository;
    @Mock private UserSessionService userSessionService;

    @Mock private IAuthContextViewModel auth;

    @InjectMocks
    private SettingsServiceImplementation service;

    private Vendor vendor;
    private VendorGstin gstin;

    @BeforeEach
    void setup() {
        vendor = new Vendor();
        vendor.setCompanyCode("COMP123");
        vendor.setCompanyName("TaxGenie Pvt Ltd");
        vendor.setPanNumber("ABCDE1234F");
        vendor.setVendorId(UUID.randomUUID());
        vendor.setPrimaryContact("{\"name\":\"John Doe\",\"email\":\"john@doe.com\",\"mobile\":\"9999999999\"}");
        vendor.setStatus(Status.ACTIVE);
        vendor.setUpdatedAt(LocalDateTime.now());

        gstin = VendorGstin.builder()
                .gstinId(UUID.randomUUID())
                .vendor(vendor)
                .gstin("27ABCDE1234F1Z5")
                .stateCode("27")
                .isPrimary(true)
                .isVerified(true)
                .verifiedAt(LocalDateTime.now())
                .build();

        when(auth.getCompanyCode()).thenReturn("COMP123");
        when(auth.getUserId()).thenReturn("USER123");
    }

    // ---------- getCompanyInfo ----------
    @Test
    void getCompanyInfo_successful() {
        when(vendorRepository.findByCompanyCode("COMP123")).thenReturn(Optional.of(vendor));
        when(vendorGstinRepository.findByVendorAndIsPrimaryTrue(vendor)).thenReturn(Optional.of(gstin));

        CompanyInfoResponseViewModel result = service.getCompanyInfo(auth);

        assertEquals("TaxGenie Pvt Ltd", result.getCompanyName());
        assertEquals("ABCDE1234F", result.getPanNumber());
        assertNotNull(result.getPrimaryGstin());
        assertEquals("VERIFIED", result.getPrimaryGstin().getStatus());
    }

    @Test
    void getCompanyInfo_vendorNotFound_throwsException() {
        when(vendorRepository.findByCompanyCode(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getCompanyInfo(auth));
    }

    // ---------- updateCompanyInfo ----------
    @Test
    void updateCompanyInfo_success() {
        when(vendorRepository.findByCompanyCode(anyString())).thenReturn(Optional.of(vendor));
        when(vendorRepository.save(any())).thenReturn(vendor);
        when(vendorGstinRepository.findByVendorAndIsPrimaryTrue(vendor)).thenReturn(Optional.of(gstin));

        CompanyInfoUpdateRequestViewModel req = new CompanyInfoUpdateRequestViewModel();
        req.setCompanyName("Updated Co");
        req.setPanNumber("NEWPAN1234");
        req.setContactPerson("Jane Doe");
        req.setEmail("jane@doe.com");
        req.setPhone("8888888888");

        CompanyInfoResponseViewModel result = service.updateCompanyInfo(auth, req);

        assertEquals("Updated Co", result.getCompanyName());
        verify(vendorRepository, times(1)).save(any(Vendor.class));
    }

    // ---------- getGstinManagement ----------
    @Test
    void getGstinManagement_returnsAggregatedCounts() {
        when(vendorRepository.findByCompanyCode(anyString())).thenReturn(Optional.of(vendor));
        when(vendorGstinRepository.findByVendorAndCompanyCode(vendor, "COMP123"))
                .thenReturn(List.of(gstin, VendorGstin.builder().vendor(vendor).gstinId(UUID.randomUUID()).gstin("29ABCDE9999F1Z9").stateCode("29").isVerified(false).build()));

        GstinManagementViewModel result = service.getGstinManagement(auth);

        assertEquals(2, result.getTotalCount());
        assertEquals(1, result.getVerifiedCount());
        assertEquals(1, result.getPendingCount());
    }

    @Test
    void getGstinManagement_vendorNotFound_throwsException() {
        when(vendorRepository.findByCompanyCode(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getGstinManagement(auth));
    }

    // ---------- createGstin ----------
    @Test
    void createGstin_createsSuccessfully() {
        GstinCreateRequestViewModel req = new GstinCreateRequestViewModel();
        req.setGstin("27ABCDE1111F1Z1");
        req.setStateCode("27");
        req.setPrimary(true);

        when(vendorRepository.findByCompanyCode(anyString())).thenReturn(Optional.of(vendor));
        when(vendorGstinRepository.findByGstinAndCompanyCode(any(), any())).thenReturn(Optional.empty());
        when(vendorGstinRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        GstinManagementViewModel.GstinDetailViewModel result = service.createGstin(auth, req);

        assertEquals("27ABCDE1111F1Z1", result.getGstin());
        verify(vendorGstinRepository, times(1)).save(any());
    }

    @Test
    void createGstin_duplicate_throwsException() {
        when(vendorRepository.findByCompanyCode(anyString())).thenReturn(Optional.of(vendor));
        when(vendorGstinRepository.findByGstinAndCompanyCode(any(), any())).thenReturn(Optional.of(gstin));

        GstinCreateRequestViewModel req = new GstinCreateRequestViewModel();
        req.setGstin("27ABCDE1234F1Z5");
        assertThrows(RuntimeException.class, () -> service.createGstin(auth, req));
    }

    // ---------- updateGstin ----------
    @Test
    void updateGstin_primarySwitchesOthers() {
        VendorGstin existing = VendorGstin.builder()
                .vendor(vendor)
                .gstinId(UUID.randomUUID())
                .gstin("27ABCDE9999F1Z9")
                .stateCode("27")
                .isPrimary(false)
                .build();
        existing.setCompanyCode("COMP123");
        vendor.setUserId("USER123");

        when(vendorGstinRepository.findByGstinIdAndCompanyCode(any(), any())).thenReturn(Optional.of(existing));
        when(vendorGstinRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        GstinUpdateRequestViewModel req = new GstinUpdateRequestViewModel();
        req.setPrimary(true);

        GstinManagementViewModel.GstinDetailViewModel result = service.updateGstin(auth, existing.getGstinId().toString(), req);

        assertTrue(result.isPrimary());
        verify(vendorGstinRepository, times(1)).updatePrimaryStatusForVendor(any(), anyString());
    }

    @Test
    void updateGstin_accessDenied_throwsException() {
        Vendor otherVendor = new Vendor();
        otherVendor.setUserId("OTHER");
        VendorGstin existing = VendorGstin.builder().vendor(otherVendor).gstinId(UUID.randomUUID()).gstin("X").stateCode("27").isPrimary(false).build();
        when(vendorGstinRepository.findByGstinIdAndCompanyCode(any(), any())).thenReturn(Optional.of(existing));
        when(auth.getUserId()).thenReturn("USER123");

        GstinUpdateRequestViewModel req = new GstinUpdateRequestViewModel();
        assertThrows(RuntimeException.class, () -> service.updateGstin(auth, existing.getGstinId().toString(), req));
    }

    // ---------- deleteGstin ----------
    @Test
    void deleteGstin_primarySingleGstin_throwsException() {
        gstin.setIsPrimary(true);
        when(vendorGstinRepository.findByGstinIdAndCompanyCode(any(), any())).thenReturn(Optional.of(gstin));
        when(vendorGstinRepository.countByVendorAndCompanyCode(any(), any())).thenReturn(1L);
        assertThrows(RuntimeException.class, () -> service.deleteGstin(auth, gstin.getGstinId().toString()));
    }

    @Test
    void deleteGstin_success() {
        gstin.setIsPrimary(false);
        when(vendorGstinRepository.findByGstinIdAndCompanyCode(any(), any())).thenReturn(Optional.of(gstin));
        boolean result = service.deleteGstin(auth, gstin.getGstinId().toString());
        assertTrue(result);
        verify(vendorGstinRepository).delete(any());
    }

    // ---------- switchOem ----------
    @Test
    void switchOem_successful() {
        OemMaster oem = new OemMaster();
        oem.setOemId(UUID.randomUUID());
        oem.setFullName("OEM Full");
        oem.setOemName("OEMShort");
        oem.setConfig("{\"asnVersion\":\"1.0\"}");
        oem.setAsnVersion("1.0");

        VendorOemAccess access = new VendorOemAccess();
        access.setVendor(vendor);
        access.setOem(oem);
        access.setAccessStatus(VendorOemAccess.AccessStatus.ACTIVE);
        access.setPermissionsCache("{\"CREATE\":true,\"DELETE\":false}");
        access.setAccessLevel(VendorOemAccess.AccessLevel.ADMIN);
        access.setCompanyCode("COMP123");
        access.setVendorCode("VENDCODE");
        access.setTotalApiCalls(BigDecimal.ZERO);
        access.setTotalAsnGenerated(BigDecimal.ZERO);

        when(oemMasterRepository.findByOemIdAndCompanyCode(any(), any())).thenReturn(Optional.of(oem));
        when(vendorRepository.findByCompanyCode(any())).thenReturn(Optional.of(vendor));
        when(vendorOemAccessRepository.findByVendorAndOemAndCompanyCode(any(), any(), any())).thenReturn(Optional.of(access));

        OemSwitchRequestViewModel req = new OemSwitchRequestViewModel();
        req.setOemId(oem.getOemId().toString());

        OemSwitchResponseViewModel result = service.switchOem(auth, req);

        assertTrue(result.isSwitchSuccessful());
        assertEquals("OEM Full", result.getSelectedOemName());
    }

    @Test
    void switchOem_inactiveAccess_returnsFailureResponse() {
        OemMaster oem = new OemMaster();
        oem.setOemId(UUID.randomUUID());
        oem.setFullName("OEM Full");
        oem.setOemName("OEMShort");
        VendorOemAccess access = new VendorOemAccess();
        access.setVendor(vendor);
        access.setOem(oem);
        access.setAccessStatus(VendorOemAccess.AccessStatus.PENDING);
        access.setPermissionsCache("{}");
        when(oemMasterRepository.findByOemIdAndCompanyCode(any(), any())).thenReturn(Optional.of(oem));
        when(vendorRepository.findByCompanyCode(any())).thenReturn(Optional.of(vendor));
        when(vendorOemAccessRepository.findByVendorAndOemAndCompanyCode(any(), any(), any())).thenReturn(Optional.of(access));

        OemSwitchRequestViewModel req = new OemSwitchRequestViewModel();
        req.setOemId(oem.getOemId().toString());

        OemSwitchResponseViewModel result = service.switchOem(auth, req);

        assertFalse(result.isSwitchSuccessful());
        assertTrue(result.getMessage().contains("not active"));
    }
}
