package in.taxgenie.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import in.taxgenie.auth.AppJwtAuthenticationManager;
import in.taxgenie.auth.IAuthContextFactory;
import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.response.interfaces.factory.IServerResponseFactory;
import in.taxgenie.response.interfaces.infra.IServerResponseWithBody;
import in.taxgenie.services.interfaces.IDashboardService;
import in.taxgenie.viewmodels.dashboard.DashboardStatsViewModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        // We target only the controller we want to test.
        controllers = DashboardController.class,
        // We explicitly exclude your custom SecurityConfig from being loaded.
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE)
)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // These are the services injected into the Controller that we need to mock.
    @MockBean
    private IDashboardService dashboardService;
    @MockBean
    private IAuthContextFactory authContextFactory;
    @MockBean
    private IServerResponseFactory serverResponseFactory;

    // Even though we exclude SecurityConfig, AppJwtAuthenticationManager might still be
    // discovered. Mocking it ensures the context can load if it's needed.
    @MockBean
    private AppJwtAuthenticationManager appJwtAuthenticationManager;

    private IAuthContextViewModel mockAuthContext;

    private static final long TEST_USER_ID = 123L;
    private static final long TEST_COMPANY_CODE = 999L;
    private static final String TEST_OEM_ID = "OEM-001";

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockAuthContext = mock(IAuthContextViewModel.class);
        when(mockAuthContext.getUserId()).thenReturn(TEST_USER_ID);
        when(mockAuthContext.getCompanyCode()).thenReturn(TEST_COMPANY_CODE);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        when(authContextFactory.getAuthContext(any(SecurityContext.class))).thenReturn(mockAuthContext);
    }

    @Test
    @DisplayName("GET /test - Should return success message")
    void whenTestDashboardApi_thenReturnsOk() throws Exception {
        mockMvc.perform(get("/dashboard/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Dashboard API is working! All endpoints are available."));
    }

    // --- ALL OTHER TESTS FOLLOW THE SAME CORRECTED PATTERN ---

    @Nested
    @DisplayName("GET /stats/{id} Tests")
    class GetDashboardStatsTests {
        @Test
        @DisplayName("Should return DashboardStatsViewModel when stats are found")
        void whenGetDashboardStats_withValidId_thenReturnsStats() throws Exception {
            // Arrange
            DashboardStatsViewModel stats = DashboardStatsViewModel.builder().progress(50).build();
            when(dashboardService.getDashboardStats(anyString(), anyString(), anyString())).thenReturn(stats);

            IServerResponseWithBody<DashboardStatsViewModel> mockResponse = mock(IServerResponseWithBody.class);
            when(mockResponse.getBody()).thenReturn(stats);
            when(mockResponse.isOk()).thenReturn(true);

            doReturn(mockResponse).when(serverResponseFactory).getServerResponseWithBody(
                    anyInt(), anyString(), anyBoolean(), any(DashboardStatsViewModel.class)
            );

            // Act & Assert
            mockMvc.perform(get("/dashboard/stats/{id}", TEST_OEM_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.body.progress").value(50));
        }
    }

    @Nested
    @DisplayName("GET /activities/{id} Tests")
    class GetRecentActivitiesTests {
        @Test
        @DisplayName("Should return activities using default limit")
        void whenGetRecentActivities_withoutLimitParam_thenUsesDefaultLimit() throws Exception {
            // Arrange
            List<IDashboardService.ActivityViewModel> activities = Collections.singletonList(
                    new IDashboardService.ActivityViewModel("act-1", "Processed", "Desc", "type", LocalDateTime.now(), "ok", "ent", "id")
            );
            when(dashboardService.getRecentActivities(anyString(), anyString(), anyString(), eq(10))).thenReturn(activities);

            IServerResponseWithBody<List<IDashboardService.ActivityViewModel>> mockResponse = mock(IServerResponseWithBody.class);
            when(mockResponse.getBody()).thenReturn(activities);
            when(mockResponse.isOk()).thenReturn(true);

            doReturn(mockResponse).when(serverResponseFactory).getServerResponseWithBody(
                    anyInt(), anyString(), anyBoolean(), any(List.class)
            );

            // Act & Assert
            mockMvc.perform(get("/dashboard/activities/{id}", TEST_OEM_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.body[0].id").value("act-1"));
        }
    }

    // ... (Implement the rest of your tests using the same corrected mocking pattern) ...
}