package in.taxgenie.controllers;

import in.taxgenie.auth.IAuthContextFactory;
import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.services.interfaces.ISettingsService;
import in.taxgenie.viewmodels.settings.*;
import in.taxgenie.viewmodels.response.ServerResponseFactory;
import in.taxgenie.viewmodels.response.ServerResponseViewModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Settings Management
 * Handles all settings-related operations including company info, GSTIN management,
 * subscription billing, and OEM access management
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Settings Management", description = "APIs for managing vendor settings including company info, GSTIN, subscription, and OEM access")
public class SettingsController {

    private final ISettingsService settingsService;
    private final IAuthContextFactory authContextFactory;

    // ==================== Company Information APIs ====================

    @Operation(summary = "Get company information", description = "Retrieve current company information for the authenticated vendor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company information retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Company not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/company-info")
    public ResponseEntity<ServerResponseViewModel<CompanyInfoResponseViewModel>> getCompanyInfo() {

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            log.info("Getting company info for vendor: {}", auth.getUserId());

            CompanyInfoResponseViewModel response = settingsService.getCompanyInfo(auth);
            return ResponseEntity.ok(ServerResponseFactory.success(response, "Company information retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting company info", e);
            return ResponseEntity.internalServerError()
                    .body(ServerResponseFactory.error("Failed to retrieve company information"));
        }
    }

    @Operation(summary = "Update company information", description = "Update company information for the authenticated vendor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Company information updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Company not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/company-info")
    public ResponseEntity<ServerResponseViewModel<CompanyInfoResponseViewModel>> updateCompanyInfo(
            @Valid @RequestBody CompanyInfoUpdateRequestViewModel request) {

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            log.info("Updating company info for vendor: {}", auth.getUserId());

            CompanyInfoResponseViewModel response = settingsService.updateCompanyInfo(auth, request);
            return ResponseEntity.ok(ServerResponseFactory.success(response, "Company information updated successfully"));
        } catch (Exception e) {
            log.error("Error updating company info", e);
            return ResponseEntity.internalServerError()
                    .body(ServerResponseFactory.error("Failed to update company information"));
        }
    }

    // ==================== GSTIN Management APIs ====================

    @Operation(summary = "Get GSTIN management data", description = "Retrieve all GSTIN information for the authenticated vendor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "GSTIN data retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/gstin-management")
    public ResponseEntity<ServerResponseViewModel<GstinManagementViewModel>> getGstinManagement() {

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            log.info("Getting GSTIN management data for vendor: {}", auth.getUserId());

            GstinManagementViewModel response = settingsService.getGstinManagement(auth);
            return ResponseEntity.ok(ServerResponseFactory.success(response, "GSTIN data retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting GSTIN management data", e);
            return ResponseEntity.internalServerError()
                    .body(ServerResponseFactory.error("Failed to retrieve GSTIN data"));
        }
    }

    @Operation(summary = "Create new GSTIN", description = "Add a new GSTIN for the authenticated vendor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "GSTIN created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid GSTIN data"),
        @ApiResponse(responseCode = "409", description = "GSTIN already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/gstin")
    public ResponseEntity<ServerResponseViewModel<GstinManagementViewModel.GstinDetailViewModel>> createGstin(
            @Valid @RequestBody GstinCreateRequestViewModel request) {

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            log.info("Creating GSTIN for vendor: {}", auth.getUserId());

            GstinManagementViewModel.GstinDetailViewModel response = settingsService.createGstin(auth, request);
            return ResponseEntity.status(201)
                    .body(ServerResponseFactory.success(response, "GSTIN created successfully"));
        } catch (Exception e) {
            log.error("Error creating GSTIN", e);
            return ResponseEntity.internalServerError()
                    .body(ServerResponseFactory.error("Failed to create GSTIN"));
        }
    }

    @Operation(summary = "Update GSTIN", description = "Update an existing GSTIN for the authenticated vendor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "GSTIN updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid GSTIN data"),
        @ApiResponse(responseCode = "404", description = "GSTIN not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/gstin/{gstinId}")
    public ResponseEntity<ServerResponseViewModel<GstinManagementViewModel.GstinDetailViewModel>> updateGstin(
            @PathVariable UUID gstinId,
            @Valid @RequestBody GstinUpdateRequestViewModel request) {

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            log.info("Updating GSTIN {} for vendor: {}", gstinId, auth.getUserId());

            GstinManagementViewModel.GstinDetailViewModel response = settingsService.updateGstin(auth, gstinId.toString(), request);
            return ResponseEntity.ok(ServerResponseFactory.success(response, "GSTIN updated successfully"));
        } catch (Exception e) {
            log.error("Error updating GSTIN {}", gstinId, e);
            return ResponseEntity.internalServerError()
                    .body(ServerResponseFactory.error("Failed to update GSTIN"));
        }
    }

    @Operation(summary = "Delete GSTIN", description = "Delete an existing GSTIN for the authenticated vendor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "GSTIN deleted successfully"),
        @ApiResponse(responseCode = "404", description = "GSTIN not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete primary GSTIN"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/gstin/{gstinId}")
    public ResponseEntity<ServerResponseViewModel<Void>> deleteGstin(
            @PathVariable UUID gstinId) {

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            log.info("Deleting GSTIN {} for vendor: {}", gstinId, auth.getUserId());

            settingsService.deleteGstin(auth, gstinId.toString());
            return ResponseEntity.ok(ServerResponseFactory.success(null, "GSTIN deleted successfully"));
        } catch (Exception e) {
            log.error("Error deleting GSTIN {}", gstinId, e);
            return ResponseEntity.internalServerError()
                    .body(ServerResponseFactory.error("Failed to delete GSTIN"));
        }
    }

    // ==================== Subscription & Billing APIs ====================

    @Operation(summary = "Get subscription and billing information", description = "Retrieve subscription and billing data for the authenticated vendor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscription data retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/subscription-billing")
    public ResponseEntity<ServerResponseViewModel<SubscriptionBillingViewModel>> getSubscriptionBilling() {

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            log.info("Getting subscription billing data for vendor: {}", auth.getUserId());

            SubscriptionBillingViewModel response = settingsService.getSubscriptionBilling(auth);
            return ResponseEntity.ok(ServerResponseFactory.success(response, "Subscription data retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting subscription billing data", e);
            return ResponseEntity.internalServerError()
                    .body(ServerResponseFactory.error("Failed to retrieve subscription data"));
        }
    }

    // ==================== OEM Access Management APIs ====================

    @Operation(summary = "Get OEM access management data", description = "Retrieve OEM access information for the authenticated vendor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OEM access data retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/oem-access")
    public ResponseEntity<ServerResponseViewModel<OemAccessManagementViewModel>> getOemAccessManagement() {

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            log.info("Getting OEM access management data for vendor: {}", auth.getUserId());

            OemAccessManagementViewModel response = settingsService.getOemAccessManagement(auth);
            return ResponseEntity.ok(ServerResponseFactory.success(response, "OEM access data retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting OEM access management data", e);
            return ResponseEntity.internalServerError()
                    .body(ServerResponseFactory.error("Failed to retrieve OEM access data"));
        }
    }

    @Operation(summary = "Switch OEM access", description = "Switch to a different OEM for the authenticated vendor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OEM switched successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid OEM switch request"),
        @ApiResponse(responseCode = "403", description = "Access denied to the requested OEM"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/oem-access/switch")
    public ResponseEntity<ServerResponseViewModel<OemSwitchResponseViewModel>> switchOemAccess(
            @Valid @RequestBody OemSwitchRequestViewModel request) {

        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            log.info("Switching OEM access for vendor: {} to OEM: {}", auth.getUserId(), request.getOemId());

            OemSwitchResponseViewModel response = settingsService.switchOem(auth, request);
            return ResponseEntity.ok(ServerResponseFactory.success(response, "OEM switched successfully"));
        } catch (Exception e) {
            log.error("Error switching OEM access to OEM: {}", request.getOemId(), e);
            return ResponseEntity.internalServerError()
                    .body(ServerResponseFactory.error("Failed to switch OEM access"));
        }
    }
}
