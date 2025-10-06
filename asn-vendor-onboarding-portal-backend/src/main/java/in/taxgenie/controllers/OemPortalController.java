package in.taxgenie.controllers;

import in.taxgenie.auth.IAuthContextFactory;
import in.taxgenie.auth.IAuthContextViewModel;
import in.taxgenie.response.interfaces.factory.IServerResponseFactory;
import in.taxgenie.response.interfaces.infra.IServerResponseWithBody;
import in.taxgenie.services.interfaces.IOemPortalService;
import in.taxgenie.viewmodels.oem.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * REST Controller for OEM portal operations
 */
@RestController
@RequestMapping("/oems")
@Tag(name = "OEM Portal", description = "APIs for OEM portal management and access")
public class OemPortalController {

    private static final Logger logger = LoggerFactory.getLogger(OemPortalController.class);

    private final IOemPortalService oemPortalService;
    private final IAuthContextFactory authContextFactory;
    private final IServerResponseFactory serverResponseFactory;

    public OemPortalController(IOemPortalService oemPortalService,
                              IAuthContextFactory authContextFactory,
                              IServerResponseFactory serverResponseFactory) {
        this.oemPortalService = oemPortalService;
        this.authContextFactory = authContextFactory;
        this.serverResponseFactory = serverResponseFactory;
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyAuthority('user') or hasAuthority('Product: 31 Role: Admin(311)') or authentication.authorities.?[authority.contains('Admin')].size() > 0")
    @Operation(summary = "Get available OEMs", description = "Retrieves list of OEMs available to the current vendor")
    public ResponseEntity<IServerResponseWithBody<AvailableOemsResponseViewModel>> getAvailableOems() {
        logger.info("Getting available OEMs");
        
        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            AvailableOemsResponseViewModel response = oemPortalService.getAvailableOems(auth);
            
            IServerResponseWithBody<AvailableOemsResponseViewModel> serverResponse = serverResponseFactory
                    .getServerResponseWithBody(200, "Available OEMs retrieved successfully", true, response);
            
            return ResponseEntity.ok(serverResponse);
            
        } catch (Exception e) {
            logger.error("Error getting available OEMs", e);
            IServerResponseWithBody<AvailableOemsResponseViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to get available OEMs: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{oemId}")
    @Operation(summary = "Get OEM details", description = "Retrieves detailed information about a specific OEM")
    public ResponseEntity<IServerResponseWithBody<OemViewModel>> getOemDetails(@PathVariable String oemId) {
        logger.info("Getting OEM details for OEM ID: {}", oemId);
        
        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            OemViewModel oemDetails = oemPortalService.getOemDetails(auth, oemId);
            
            IServerResponseWithBody<OemViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "OEM details retrieved successfully", true, oemDetails);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting OEM details for OEM ID: {}", oemId, e);
            IServerResponseWithBody<OemViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to get OEM details: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/{oemId}/config")
    @Operation(summary = "Get OEM configuration", description = "Retrieves configuration settings for a specific OEM")
    public ResponseEntity<IServerResponseWithBody<OemConfigurationViewModel>> getOemConfig(@PathVariable String oemId) {
        logger.info("Getting OEM configuration for OEM ID: {}", oemId);
        
        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            OemViewModel oemDetails = oemPortalService.getOemDetails(auth, oemId);
            
            IServerResponseWithBody<OemConfigurationViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(200, "OEM configuration retrieved successfully", true, oemDetails.getConfiguration());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting OEM configuration for OEM ID: {}", oemId, e);
            IServerResponseWithBody<OemConfigurationViewModel> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to get OEM configuration: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/{oemId}/request-access")
    @Operation(summary = "Request OEM access", description = "Requests access to a specific OEM")
    public ResponseEntity<IServerResponseWithBody<Object>> requestOemAccess(
            @PathVariable String oemId,
            @RequestBody Object requestData) {
        logger.info("Requesting access to OEM ID: {}", oemId);
        
        try {
            IAuthContextViewModel auth = authContextFactory.getAuthContext(SecurityContextHolder.getContext());
            Object accessRequest = oemPortalService.requestOemAccess(auth, oemId, requestData);
            
            IServerResponseWithBody<Object> response = serverResponseFactory
                    .getServerResponseWithBody(200, "Access request submitted successfully", true, accessRequest);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error requesting access to OEM ID: {}", oemId, e);
            IServerResponseWithBody<Object> response = serverResponseFactory
                    .getServerResponseWithBody(500, "Failed to request access: " + e.getMessage(), false, null);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
