package in.taxgenie.services;

import in.taxgenie.entities.*;
import in.taxgenie.entities.enums.OnboardingStatus;
import in.taxgenie.repositories.*;
import in.taxgenie.services.implementations.DashboardServiceImplementation;
import in.taxgenie.viewmodels.dashboard.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for DashboardServiceImplementation
 * covering key business logic, error handling, and default fallbacks.
 */
class DashboardServiceImplTest {

    private DashboardServiceImplementation service;

    private VendorRepository vendorRepository;
    private OemMasterRepository oemMasterRepository;
    private OnboardingProcessRepository onboardingProcessRepository;
    private OnboardingEventRepository onboardingEventRepository;
    private SubscriptionPlanRepository subscriptionPlanRepository;
    private PaymentTransactionRepository paymentTransactionRepository;
    private ApiCredentialRepository apiCredentialRepository;

    private Vendor vendor;
    private OemMaster oem;
    private OnboardingProcess onboarding;

    @BeforeEach
    void setUp() {
        service = new DashboardServiceImplementation();

        vendorRepository = mock(VendorRepository.class);
        oemMasterRepository = mock(OemMasterRepository.class);
        onboardingProcessRepository = mock(OnboardingProcessRepository.class);
        onboardingEventRepository = mock(OnboardingEventRepository.class);
        subscriptionPlanRepository = mock(SubscriptionPlanRepository.class);
        paymentTransactionRepository = mock(PaymentTransactionRepository.class);
        apiCredentialRepository = mock(ApiCredentialRepository.class);

        ReflectionTestUtils.setField(service, "vendorRepository", vendorRepository);
        ReflectionTestUtils.setField(service, "oemMasterRepository", oemMasterRepository);
        ReflectionTestUtils.setField(service, "onboardingProcessRepository", onboardingProcessRepository);
        ReflectionTestUtils.setField(service, "onboardingEventRepository", onboardingEventRepository);
        ReflectionTestUtils.setField(service, "subscriptionPlanRepository", subscriptionPlanRepository);
        ReflectionTestUtils.setField(service, "paymentTransactionRepository", paymentTransactionRepository);
        ReflectionTestUtils.setField(service, "apiCredentialRepository", apiCredentialRepository);

        vendor = new Vendor();
        vendor.setCompanyCode(123L);
        vendor.setVendorId(UUID.randomUUID());
        vendor.setCompanyName("Test Vendor");
        vendor.setPrimaryContact("{\"name\":\"John\",\"email\":\"john@test.com\",\"mobile\":\"9999999999\"}");
        vendor.setCreatedAt(LocalDateTime.now().minusDays(10));
        vendor.setLastActivityAt(LocalDateTime.now().minusDays(1));

        oem = new OemMaster();
        oem.setOemId(UUID.randomUUID());
        oem.setOemName("TATA");

        onboarding = new OnboardingProcess();
        onboarding.setOnboardingId(UUID.randomUUID());
        onboarding.setVendor(vendor);
        onboarding.setOem(oem);
        onboarding.setCompanyCode(123L);
        onboarding.setStatus(OnboardingStatus.IN_PROGRESS);
        onboarding.setCreatedAt(LocalDateTime.now().minusDays(20));
        onboarding.setUpdatedAt(LocalDateTime.now().minusDays(2));
    }

    // ---- getDashboardStats ----
    @Test
    void getDashboardStats_validData_returnsPopulatedStats() {
        when(vendorRepository.findByCompanyCode(123L)).thenReturn(Optional.of(vendor));
        when(oemMasterRepository.findById(oem.getOemId())).thenReturn(Optional.of(oem));
        when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(vendor, oem, 123L))
                .thenReturn(Optional.of(onboarding));

        DashboardStatsViewModel result = service.getDashboardStats("v1", oem.getOemId().toString(), "123");

        assertNotNull(result);
        assertTrue(result.getProgress() > 0);
        assertNotNull(result.getCriticalAlert());
        verify(vendorRepository).findByCompanyCode(123L);
    }

    @Test
    void getDashboardStats_vendorNotFound_returnsDefault() {
        when(vendorRepository.findByCompanyCode(123L)).thenReturn(Optional.empty());

        DashboardStatsViewModel result = service.getDashboardStats("v1", oem.getOemId().toString(), "123");

        assertEquals(0, result.getProgress());
        assertEquals("0/6", result.getCompletedSteps());
        assertEquals("Getting Started", result.getStatus());
    }

    @Test
    void getDashboardStats_oemNotFound_returnsDefault() {
        when(vendorRepository.findByCompanyCode(123L)).thenReturn(Optional.of(vendor));
        when(oemMasterRepository.findById(oem.getOemId())).thenReturn(Optional.empty());

        DashboardStatsViewModel result = service.getDashboardStats("v1", oem.getOemId().toString(), "123");
        assertEquals(0, result.getProgress());
    }

    // ---- getOnboardingProgress ----
    @Test
    void getOnboardingProgress_withValidData_buildsStepsCorrectly() {
        when(vendorRepository.findByCompanyCode(123L)).thenReturn(Optional.of(vendor));
        when(oemMasterRepository.findById(oem.getOemId())).thenReturn(Optional.of(oem));
        when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(vendor, oem, 123L))
                .thenReturn(Optional.of(onboarding));

        var result = service.getOnboardingProgress("v1", oem.getOemId().toString(), "123");
        assertEquals("onboarding", result.getType());
        assertTrue(result.getTotalSteps() > 0);
    }

    @Test
    void getOnboardingProgress_vendorMissing_returnsDefault() {
        when(vendorRepository.findByCompanyCode(123L)).thenReturn(Optional.empty());
        var result = service.getOnboardingProgress("v1", oem.getOemId().toString(), "123");
        assertEquals("onboarding", result.getType());
        assertEquals(0, result.getCompletedSteps());
    }

    // ---- getImplementationProgress ----
    @Test
    void getImplementationProgress_valid_returnsSteps() {
        onboarding.setStatus(OnboardingStatus.COMPLETED);
        when(vendorRepository.findByCompanyCode(123L)).thenReturn(Optional.of(vendor));
        when(oemMasterRepository.findById(oem.getOemId())).thenReturn(Optional.of(oem));
        when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(vendor, oem, 123L))
                .thenReturn(Optional.of(onboarding));

        var result = service.getImplementationProgress("v1", oem.getOemId().toString(), "123");
        assertEquals("implementation", result.getType());
        assertTrue(result.getTotalSteps() > 0);
    }

    // ---- getUserProfile ----
    @Test
    void getUserProfile_validVendor_returnsProfile() {
        when(vendorRepository.findByCompanyCode(123L)).thenReturn(Optional.of(vendor));

        var result = service.getUserProfile("v1", "123");
        assertEquals("Test Vendor", result.getCompanyName());
        assertEquals("John", result.getContactPerson());
        assertEquals("john@test.com", result.getEmail());
        assertEquals("9999999999", result.getPhone());
    }

    @Test
    void getUserProfile_vendorMissing_returnsDefault() {
        when(vendorRepository.findByCompanyCode(123L)).thenReturn(Optional.empty());
        var result = service.getUserProfile("v1", "123");
        assertEquals("Default Company", result.getCompanyName());
    }

    // ---- updateUserProfile ----
    @Test
    void updateUserProfile_existingVendor_updatesSuccessfully() {
        when(vendorRepository.findByCompanyCode(123L)).thenReturn(Optional.of(vendor));
        when(vendorRepository.save(any(Vendor.class))).thenReturn(vendor);

        var viewModel = new UserProfileViewModel();
        viewModel.setCompanyName("Updated Co");
        viewModel.setContactPerson("Jane");
        viewModel.setEmail("jane@test.com");
        viewModel.setPhone("12345");

        var result = service.updateUserProfile("v1", viewModel, "123");
        assertNotNull(result);
        verify(vendorRepository, times(1)).save(any());
    }

    @Test
    void updateUserProfile_vendorMissing_throwsException() {
        when(vendorRepository.findByCompanyCode(123L)).thenReturn(Optional.empty());
        var viewModel = new UserProfileViewModel();
        assertThrows(RuntimeException.class, () -> service.updateUserProfile("v1", viewModel, "123"));
    }

    // ---- getQuickActions ----
    @Test
    void getQuickActions_validVendor_returnsDefaultActions() {
        when(vendorRepository.findByCompanyCode(123L)).thenReturn(Optional.of(vendor));
        when(oemMasterRepository.findById(oem.getOemId())).thenReturn(Optional.of(oem));
        when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(vendor, oem, 123L))
                .thenReturn(Optional.of(onboarding));

        var result = service.getQuickActions("v1", oem.getOemId().toString(), "123");
        assertTrue(result.size() >= 3);
        assertTrue(result.stream().anyMatch(a -> a.getTitle().contains("Upgrade Plan")));
    }

    // ---- getRecentActivities ----
    @Test
    void getRecentActivities_withEvents_returnsList() {
        OnboardingEvent e1 = new OnboardingEvent();
        e1.setEventId(UUID.randomUUID());
        //e1.setEventType("STEP_COMPLETED");
        e1.setEventTimestamp(LocalDateTime.now());
        e1.setOnboardingProcess(onboarding);

        when(vendorRepository.findByCompanyCode(123L)).thenReturn(Optional.of(vendor));
        when(oemMasterRepository.findById(oem.getOemId())).thenReturn(Optional.of(oem));
        when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(vendor, oem, 123L))
                .thenReturn(Optional.of(onboarding));
        when(onboardingEventRepository.findByOnboardingProcessOrderByEventTimestampDesc(onboarding))
                .thenReturn(List.of(e1));

        var result = service.getRecentActivities("v1", oem.getOemId().toString(), "123", 10);
        assertEquals(1, result.size());
        assertEquals("onboarding", result.get(0).getType());
    }

    // ---- Private helper methods ----
    @Test
    void calculateOverallProgress_variousStatuses() {
        var statuses = List.of(OnboardingStatus.NOT_STARTED, OnboardingStatus.IN_PROGRESS,
                OnboardingStatus.COMPLETED, OnboardingStatus.FAILED, OnboardingStatus.CANCELLED);
        for (var s : statuses) {
            onboarding.setStatus(s);
            int result = (int) ReflectionTestUtils.invokeMethod(service, "calculateOverallProgress", onboarding);
            assertTrue(result >= 0 && result <= 100);
        }
    }

    @Test
    void calculateCompletedSteps_handlesStatuses() {
        onboarding.setStatus(OnboardingStatus.IN_PROGRESS);
        String result = (String) ReflectionTestUtils.invokeMethod(service, "calculateCompletedSteps", onboarding);
        assertEquals("3/6", result);
    }

    @Test
    void extractContactDetails_validJson_returnsExpectedValues() {
        String json = "{\"name\":\"Alice\",\"email\":\"a@test.com\",\"mobile\":\"888\"}";
        String name = (String) ReflectionTestUtils.invokeMethod(service, "extractContactName", json);
        String email = (String) ReflectionTestUtils.invokeMethod(service, "extractContactEmail", json);
        String phone = (String) ReflectionTestUtils.invokeMethod(service, "extractContactPhone", json);
        assertEquals("Alice", name);
        assertEquals("a@test.com", email);
        assertEquals("888", phone);
    }

    @Test
    void extractContactDetails_invalidJson_returnsEmpty() {
        String json = "{ invalid json }";
        String name = (String) ReflectionTestUtils.invokeMethod(service, "extractContactName", json);
        assertEquals("", name);
    }

    @Test
    void buildCriticalAlert_variousScenarios_returnsCorrectType() {
        var alert1 = (DashboardStatsViewModel.CriticalAlertViewModel)
                ReflectionTestUtils.invokeMethod(service, "buildCriticalAlert", 5L, 10);
        assertEquals("error", alert1.getType());

        var alert2 = (DashboardStatsViewModel.CriticalAlertViewModel)
                ReflectionTestUtils.invokeMethod(service, "buildCriticalAlert", 20L, 10);
        assertEquals("warning", alert2.getType());

        var alert3 = (DashboardStatsViewModel.CriticalAlertViewModel)
                ReflectionTestUtils.invokeMethod(service, "buildCriticalAlert", 60L, 90);
        assertEquals("info", alert3.getType());
    }

    @Test
    void getNextAction_basedOnStatus_returnsExpectedMessage() {
        for (OnboardingStatus s : OnboardingStatus.values()) {
            onboarding.setStatus(s);
            String result = (String) ReflectionTestUtils.invokeMethod(service, "getNextAction", onboarding);
            assertNotNull(result);
        }
    }

    @Test
    void getImplementationStatus_variousConditions_returnsCorrectText() {
        assertEquals("Completed", ReflectionTestUtils.invokeMethod(service, "getImplementationStatus", 100, 50L));
        assertEquals("Nearly Complete", ReflectionTestUtils.invokeMethod(service, "getImplementationStatus", 90, 50L));
        assertEquals("In Progress", ReflectionTestUtils.invokeMethod(service, "getImplementationStatus", 60, 50L));
        assertEquals("Behind Schedule", ReflectionTestUtils.invokeMethod(service, "getImplementationStatus", 40, 10L));
        assertEquals("Getting Started", ReflectionTestUtils.invokeMethod(service, "getImplementationStatus", 10, 100L));
    }
}

/*
package in.taxgenie.services;

import in.taxgenie.entities.OnboardingEvent;
import in.taxgenie.entities.OnboardingProcess;
import in.taxgenie.entities.OemMaster;
import in.taxgenie.entities.Vendor;
import in.taxgenie.entities.enums.OnboardingStatus;
import in.taxgenie.entities.enums.Status;
import in.taxgenie.repositories.*;
import in.taxgenie.services.implementations.DashboardServiceImplementation;
import in.taxgenie.services.interfaces.IDashboardService;
import in.taxgenie.viewmodels.dashboard.DashboardStatsViewModel;
import in.taxgenie.viewmodels.dashboard.ProgressViewModel;
import in.taxgenie.viewmodels.dashboard.UserProfileViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplementationTest {

    // Mocks for all the actual dependencies in your service
    @Mock private VendorRepository vendorRepository;
    @Mock private OnboardingProcessRepository onboardingProcessRepository;
    @Mock private SubscriptionPlanRepository subscriptionPlanRepository; // Not used in tested methods but good to have
    @Mock private PaymentTransactionRepository paymentTransactionRepository; // Not used
    @Mock private ApiCredentialRepository apiCredentialRepository; // Not used
    @Mock private OnboardingEventRepository onboardingEventRepository;
    @Mock private OemMasterRepository oemMasterRepository;

    // The actual class under test
    @InjectMocks
    private DashboardServiceImplementation dashboardService;

    private Vendor testVendor;
    private OemMaster testOem;
    private OnboardingProcess testOnboardingProcess;
    private Long companyCode;
    private UUID oemId;
    private String vendorId;

    @BeforeEach
    void setUp() {
        companyCode = 12345L;
        oemId = UUID.randomUUID();
        vendorId = UUID.randomUUID().toString();

        testVendor = new Vendor();
        testVendor.setVendorId(UUID.fromString(vendorId));
        testVendor.setCompanyCode(companyCode);
        testVendor.setCompanyName("Test Corp");
        testVendor.setPrimaryContact("{\"name\":\"Test User\",\"email\":\"test@corp.com\",\"mobile\":\"1234567890\"}");
        testVendor.setCreatedAt(LocalDateTime.now().minusDays(10));
        testVendor.setStatus(Status.ACTIVE);

        testOem = new OemMaster();
        testOem.setOemId(oemId);

        testOnboardingProcess = new OnboardingProcess();
        testOnboardingProcess.setOnboardingId(UUID.randomUUID());
        testOnboardingProcess.setVendor(testVendor);
        testOnboardingProcess.setOem(testOem);
        testOnboardingProcess.setStatus(OnboardingStatus.IN_PROGRESS);
    }

    @Nested
    @DisplayName("getDashboardStats Tests")
    class GetDashboardStats {
        @Test
        @DisplayName("Should return full stats when all entities are found")
        void getDashboardStats_Success() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));
            when(oemMasterRepository.findById(oemId)).thenReturn(Optional.of(testOem));
            when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(testVendor, testOem, companyCode))
                    .thenReturn(Optional.of(testOnboardingProcess));

            // Act
            DashboardStatsViewModel stats = dashboardService.getDashboardStats(vendorId, oemId.toString(), String.valueOf(companyCode));

            // Assert
            assertNotNull(stats);
            assertEquals(50, stats.getProgress()); // 50 for IN_PROGRESS status
            assertEquals("3/6", stats.getCompletedSteps());
            assertEquals("In Progress", stats.getStatus());
            assertEquals("Continue with payment and deployment selection", stats.getNextAction());

            verify(vendorRepository).findByCompanyCode(companyCode);
            verify(oemMasterRepository).findById(oemId);
            verify(onboardingProcessRepository).findByVendorAndOemAndCompanyCode(testVendor, testOem, companyCode);
        }

        @Test
        @DisplayName("Should return default stats when vendor is not found")
        void getDashboardStats_VendorNotFound() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.empty());

            // Act
            DashboardStatsViewModel stats = dashboardService.getDashboardStats(vendorId, oemId.toString(), String.valueOf(companyCode));

            // Assert
            assertNotNull(stats);
            assertEquals(0, stats.getProgress());
            assertEquals("0/6", stats.getCompletedSteps());
            verify(oemMasterRepository, never()).findById(any());
        }
    }

    @Nested
    @DisplayName("getUserProfile Tests")
    class GetUserProfile {
        @Test
        @DisplayName("Should return user profile when vendor is found")
        void getUserProfile_Success() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));

            // Act
            UserProfileViewModel profile = dashboardService.getUserProfile(vendorId, String.valueOf(companyCode));

            // Assert
            assertNotNull(profile);
            assertEquals("Test Corp", profile.getCompanyName());
            assertEquals("Test User", profile.getContactPerson());
            assertEquals("test@corp.com", profile.getEmail());
            assertEquals("1234567890", profile.getPhone());
            assertEquals(testVendor.getStatus().toString(), profile.getStatus());
        }

        @Test
        @DisplayName("Should return default profile when vendor is not found")
        void getUserProfile_VendorNotFound() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.empty());

            // Act
            UserProfileViewModel profile = dashboardService.getUserProfile(vendorId, String.valueOf(companyCode));

            // Assert
            assertNotNull(profile);
            assertEquals("default", profile.getId());
            assertEquals("Default Company", profile.getCompanyName());
        }
    }

    @Nested
    @DisplayName("updateUserProfile Tests")
    class UpdateUserProfile {
        @Test
        @DisplayName("Should update vendor and return updated profile")
        void updateUserProfile_Success() {
            // Arrange
            UserProfileViewModel profileUpdate = new UserProfileViewModel();
            profileUpdate.setCompanyName("New Corp Name");
            profileUpdate.setContactPerson("New Contact");
            profileUpdate.setEmail("new@corp.com");
            profileUpdate.setPhone("9876543210");

            // This is a spy to test the internal call to getUserProfile
            DashboardServiceImplementation serviceSpy = spy(dashboardService);
            doReturn(profileUpdate).when(serviceSpy).getUserProfile(vendorId, String.valueOf(companyCode));

            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));
            when(vendorRepository.save(any(Vendor.class))).thenReturn(testVendor);

            // Act
            UserProfileViewModel result = serviceSpy.updateUserProfile(vendorId, profileUpdate, String.valueOf(companyCode));

            // Assert
            assertNotNull(result);
            assertEquals("New Corp Name", result.getCompanyName());
            verify(vendorRepository).findByCompanyCode(companyCode);
            verify(vendorRepository).save(any(Vendor.class));
            verify(serviceSpy).getUserProfile(vendorId, String.valueOf(companyCode));
        }

        @Test
        @DisplayName("Should throw exception when vendor not found")
        void updateUserProfile_VendorNotFound() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                dashboardService.updateUserProfile(vendorId, new UserProfileViewModel(), String.valueOf(companyCode));
            });
            verify(vendorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getRecentActivities Tests")
    class GetRecentActivities {
        @Test
        @DisplayName("Should return list of activities")
        void getRecentActivities_Success() {
            // Arrange
            OnboardingEvent event = new OnboardingEvent();
            event.setEventId(UUID.randomUUID());
            event.setEventType("TEST_EVENT");
            event.setEventTimestamp(LocalDateTime.now());
            event.setOnboardingProcess(testOnboardingProcess);
            List<OnboardingEvent> events = Collections.singletonList(event);

            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));
            when(oemMasterRepository.findById(oemId)).thenReturn(Optional.of(testOem));
            when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(testVendor, testOem, companyCode))
                    .thenReturn(Optional.of(testOnboardingProcess));
            when(onboardingEventRepository.findByOnboardingProcessOrderByEventTimestampDesc(testOnboardingProcess))
                    .thenReturn(events);

            // Act
            List<IDashboardService.ActivityViewModel> activities = dashboardService.getRecentActivities(vendorId, oemId.toString(), String.valueOf(companyCode), 5);

            // Assert
            assertNotNull(activities);
            assertEquals(1, activities.size());
            assertEquals(event.getEventId().toString(), activities.get(0).getId());
            assertEquals("TEST_EVENT", activities.get(0).getTitle());
        }
    }

    @Nested
    @DisplayName("getDashboardStats Additional Tests")
    class GetDashboardStatsAdditional {
        @Test
        @DisplayName("Should return default stats when OEM is not found")
        void getDashboardStats_OemNotFound() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));
            when(oemMasterRepository.findById(oemId)).thenReturn(Optional.empty());

            // Act
            DashboardStatsViewModel stats = dashboardService.getDashboardStats(vendorId, oemId.toString(), String.valueOf(companyCode));

            // Assert
            assertNotNull(stats);
            assertEquals(0, stats.getProgress()); // Asserts it's the default object
            verify(onboardingProcessRepository, never()).findByVendorAndOemAndCompanyCode(any(), any(), any());
        }

        @Test
        @DisplayName("Should return stats calculated with no onboarding process if not found")
        void getDashboardStats_OnboardingProcessNotFound() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));
            when(oemMasterRepository.findById(oemId)).thenReturn(Optional.of(testOem));
            when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(testVendor, testOem, companyCode))
                    .thenReturn(Optional.empty());

            // Act
            DashboardStatsViewModel stats = dashboardService.getDashboardStats(vendorId, oemId.toString(), String.valueOf(companyCode));

            // Assert
            assertNotNull(stats);
            assertEquals(0, stats.getProgress()); // calculateOverallProgress(null) returns 0
            assertEquals("0/6", stats.getCompletedSteps()); // calculateCompletedSteps(null) returns "0/6"
        }
    }

    @Nested
    @DisplayName("getImplementationProgress Tests")
    class GetImplementationProgressTests {
        @Test
        @DisplayName("Should return implementation progress when all entities are found")
        void getImplementationProgress_Success() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));
            when(oemMasterRepository.findById(oemId)).thenReturn(Optional.of(testOem));
            when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(testVendor, testOem, companyCode))
                    .thenReturn(Optional.of(testOnboardingProcess));

            // Act
            ProgressViewModel progress = dashboardService.getImplementationProgress(vendorId, oemId.toString(), String.valueOf(companyCode));

            // Assert
            assertNotNull(progress);
            assertEquals("implementation", progress.getType());
            // Based on helper, IN_PROGRESS onboarding status results in 0 completed implementation steps
            assertEquals(0, progress.getCompletedSteps());
            assertEquals(3, progress.getTotalSteps());
        }

        @Test
        @DisplayName("Should return default implementation progress when vendor is not found")
        void getImplementationProgress_VendorNotFound() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.empty());

            // Act
            ProgressViewModel progress = dashboardService.getImplementationProgress(vendorId, oemId.toString(), String.valueOf(companyCode));

            // Assert
            assertNotNull(progress);
            assertEquals("implementation", progress.getType());
            assertEquals(0, progress.getPercentage());
            assertEquals(3, progress.getTotalSteps()); // Verifies it's the default object
        }
    }

    @Nested
    @DisplayName("getUserProfile Edge Cases")
    class GetUserProfileEdgeCases {
        @Test
        @DisplayName("Should return empty contact info when primaryContact JSON is null")
        void getUserProfile_NullContactJson() {
            // Arrange
            testVendor.setPrimaryContact(null); // Set contact to null
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));

            // Act
            UserProfileViewModel profile = dashboardService.getUserProfile(vendorId, String.valueOf(companyCode));

            // Assert
            assertNotNull(profile);
            assertEquals("", profile.getContactPerson());
            assertEquals("", profile.getEmail());
            assertEquals("", profile.getPhone());
        }
    }

    @Nested
    @DisplayName("getQuickActions Tests")
    class GetQuickActionsTests {
        @Test
        @DisplayName("Should return quick actions for in-progress onboarding")
        void getQuickActions_OnboardingInProgress() {
            // Arrange
            testOnboardingProcess.setStatus(OnboardingStatus.IN_PROGRESS);
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));
            when(oemMasterRepository.findById(oemId)).thenReturn(Optional.of(testOem));
            when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(any(), any(), any()))
                    .thenReturn(Optional.of(testOnboardingProcess));

            // Act
            List<IDashboardService.QuickActionViewModel> actions = dashboardService.getQuickActions(vendorId, oemId.toString(), String.valueOf(companyCode));

            // Assert
            assertEquals(4, actions.size());
            IDashboardService.QuickActionViewModel onboardingAction = actions.get(0);
            assertEquals("Continue Onboarding", onboardingAction.getTitle());
            assertEquals("Complete your ASN 2.1 onboarding process", onboardingAction.getDescription());
        }

        @Test
        @DisplayName("Should return updated quick action when onboarding is complete")
        void getQuickActions_OnboardingComplete() {
            // Arrange
            testOnboardingProcess.setStatus(OnboardingStatus.COMPLETED);
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));
            when(oemMasterRepository.findById(oemId)).thenReturn(Optional.of(testOem));
            when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(any(), any(), any()))
                    .thenReturn(Optional.of(testOnboardingProcess));
            // Act
            List<IDashboardService.QuickActionViewModel> actions = dashboardService.getQuickActions(vendorId, oemId.toString(), String.valueOf(companyCode));

            // Assert
            assertEquals(4, actions.size());
            IDashboardService.QuickActionViewModel onboardingAction = actions.get(0);
            assertEquals("Continue Onboarding", onboardingAction.getTitle());
            assertEquals("Review onboarding details", onboardingAction.getDescription());
        }

        @Test
        @DisplayName("Should return an empty list when vendor is not found")
        void getQuickActions_VendorNotFound() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.empty());

            // Act
            List<IDashboardService.QuickActionViewModel> actions = dashboardService.getQuickActions(vendorId, oemId.toString(), String.valueOf(companyCode));

            // Assert
            assertTrue(actions.isEmpty());
        }
    }

    @Nested
    @DisplayName("getRecentActivities Additional Tests")
    class GetRecentActivitiesAdditional {

        @Test
        @DisplayName("Should return an empty list if no events are found")
        void getRecentActivities_NoEventsFound() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));
            when(oemMasterRepository.findById(oemId)).thenReturn(Optional.of(testOem));
            when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(any(), any(), any()))
                    .thenReturn(Optional.of(testOnboardingProcess));
            // Mock repository to return an empty list
            when(onboardingEventRepository.findByOnboardingProcessOrderByEventTimestampDesc(testOnboardingProcess))
                    .thenReturn(Collections.emptyList());

            // Act
            List<IDashboardService.ActivityViewModel> activities = dashboardService.getRecentActivities(vendorId, oemId.toString(), String.valueOf(companyCode), 10);

            // Assert
            assertTrue(activities.isEmpty());
        }

        @Test
        @DisplayName("Should respect the limit parameter")
        void getRecentActivities_RespectsLimit() {
            // Arrange
            // Create a list of 10 events
            List<OnboardingEvent> events = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                OnboardingEvent event = new OnboardingEvent();
                event.setEventId(UUID.randomUUID());
                event.setEventType("EVENT_" + i);
                event.setEventTimestamp(LocalDateTime.now());
                events.add(event);
            }

            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));
            when(oemMasterRepository.findById(oemId)).thenReturn(Optional.of(testOem));
            when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(any(), any(), any()))
                    .thenReturn(Optional.of(testOnboardingProcess));
            when(onboardingEventRepository.findByOnboardingProcessOrderByEventTimestampDesc(testOnboardingProcess))
                    .thenReturn(events);

            // Act
            // Call the service with a limit of 5
            List<IDashboardService.ActivityViewModel> activities = dashboardService.getRecentActivities(vendorId, oemId.toString(), String.valueOf(companyCode), 5);

            // Assert
            // Verify that only 5 activities were returned
            assertEquals(5, activities.size());
        }
    }

    @Nested
    @DisplayName("getOnboardingProgress Tests")
    class GetOnboardingProgressTests {

        @Test
        @DisplayName("Should return onboarding progress when all entities are found")
        void getOnboardingProgress_Success() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.of(testVendor));
            when(oemMasterRepository.findById(oemId)).thenReturn(Optional.of(testOem));
            when(onboardingProcessRepository.findByVendorAndOemAndCompanyCode(testVendor, testOem, companyCode))
                    .thenReturn(Optional.of(testOnboardingProcess));

            // Act
            ProgressViewModel progress = dashboardService.getOnboardingProgress(vendorId, oemId.toString(), String.valueOf(companyCode));

            // Assert
            assertNotNull(progress);
            assertEquals("onboarding", progress.getType());
            // Based on the service's helper logic for an "IN_PROGRESS" status:
            // Step 1 is "completed", Step 2 is "current". This means 1 step is completed out of 4.
            assertEquals(1, progress.getCompletedSteps());
            assertEquals(4, progress.getTotalSteps());
            assertEquals(25, progress.getPercentage()); // 1 of 4 steps = 25%
        }

        @Test
        @DisplayName("Should return default progress when vendor is not found")
        void getOnboardingProgress_VendorNotFound() {
            // Arrange
            when(vendorRepository.findByCompanyCode(companyCode)).thenReturn(Optional.empty());

            // Act
            ProgressViewModel progress = dashboardService.getOnboardingProgress(vendorId, oemId.toString(), String.valueOf(companyCode));

            // Assert
            assertNotNull(progress);
            assertEquals("onboarding", progress.getType());
            assertEquals(0, progress.getPercentage());
            assertEquals(4, progress.getTotalSteps()); // Verifies it's the default object

            // Ensure other repositories are not queried
            verify(oemMasterRepository, never()).findById(any());
            verify(onboardingProcessRepository, never()).findByVendorAndOemAndCompanyCode(any(), any(), any());
        }
    }
}
*/