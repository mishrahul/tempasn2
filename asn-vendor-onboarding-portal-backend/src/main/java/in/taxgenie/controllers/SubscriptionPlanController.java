package in.taxgenie.controllers;

import in.taxgenie.services.interfaces.ISubscriptionPlanService;
import in.taxgenie.viewmodels.response.ServerResponseFactory;
import in.taxgenie.viewmodels.response.ServerResponseViewModel;
import in.taxgenie.viewmodels.subscriptionplan.SubscriptionPlanDetailViewModel;
import in.taxgenie.viewmodels.subscriptionplan.SubscriptionPlanListViewModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller for Subscription Plan Management
 * Handles all subscription plan-related operations
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/subscription-plans")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Subscription Plan Management", description = "APIs for managing and retrieving subscription plans")
public class SubscriptionPlanController {

    private final ISubscriptionPlanService subscriptionPlanService;

    /**
     * Get all subscription plans
     *
     * @param activeOnly optional parameter to filter only active plans (default: false)
     * @return list of subscription plans
     */
    @Operation(
        summary = "Get all subscription plans",
        description = "Retrieve all subscription plans with optional filtering for active plans only"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscription plans retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<ServerResponseViewModel<SubscriptionPlanListViewModel>> getAllSubscriptionPlans(
            @Parameter(description = "Filter to return only active plans")
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {

        try {
            log.info("Getting all subscription plans, activeOnly: {}", activeOnly);
            SubscriptionPlanListViewModel response = subscriptionPlanService.getAllSubscriptionPlans(activeOnly);
            return ResponseEntity.ok(ServerResponseFactory.success(response, "Subscription plans retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting subscription plans", e);
            return ResponseEntity.internalServerError()
                    .body(ServerResponseFactory.error("Failed to retrieve subscription plans: " + e.getMessage()));
        }
    }

    /**
     * Get subscription plan by ID
     *
     * @param planId the subscription plan ID
     * @return subscription plan details
     */
    @Operation(
        summary = "Get subscription plan by ID",
        description = "Retrieve detailed information about a specific subscription plan"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Subscription plan retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Subscription plan not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{planId}")
    public ResponseEntity<ServerResponseViewModel<SubscriptionPlanDetailViewModel>> getSubscriptionPlanById(
            @Parameter(description = "Subscription plan ID")
            @PathVariable UUID planId) {

        try {
            log.info("Getting subscription plan by ID: {}", planId);
            SubscriptionPlanDetailViewModel response = subscriptionPlanService.getSubscriptionPlanById(planId);
            return ResponseEntity.ok(ServerResponseFactory.success(response, "Subscription plan retrieved successfully"));
        } catch (RuntimeException e) {
            log.error("Error getting subscription plan by ID: {}", planId, e);
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(404)
                        .body(ServerResponseFactory.error("Subscription plan not found"));
            }
            return ResponseEntity.internalServerError()
                    .body(ServerResponseFactory.error("Failed to retrieve subscription plan: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error getting subscription plan by ID: {}", planId, e);
            return ResponseEntity.internalServerError()
                    .body(ServerResponseFactory.error("Failed to retrieve subscription plan: " + e.getMessage()));
        }
    }
}

