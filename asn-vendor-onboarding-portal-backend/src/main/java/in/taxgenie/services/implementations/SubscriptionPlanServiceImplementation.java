package in.taxgenie.services.implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.taxgenie.entities.SubscriptionPlan;
import in.taxgenie.repositories.SubscriptionPlanRepository;
import in.taxgenie.services.interfaces.ISubscriptionPlanService;
import in.taxgenie.viewmodels.subscriptionplan.SubscriptionPlanDetailViewModel;
import in.taxgenie.viewmodels.subscriptionplan.SubscriptionPlanListViewModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of subscription plan service
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class SubscriptionPlanServiceImplementation implements ISubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public SubscriptionPlanListViewModel getAllSubscriptionPlans(boolean activeOnly) {
        log.info("Getting all subscription plans, activeOnly: {}", activeOnly);

        List<SubscriptionPlan> plans;
        if (activeOnly) {
            plans = subscriptionPlanRepository.findByIsActiveTrue();
        } else {
            plans = subscriptionPlanRepository.findAll();
        }

        List<SubscriptionPlanListViewModel.SubscriptionPlanItemViewModel> planItems = plans.stream()
                .map(this::mapToSubscriptionPlanItemViewModel)
                .collect(Collectors.toList());

        long activePlansCount = plans.stream().filter(SubscriptionPlan::getIsActive).count();

        return SubscriptionPlanListViewModel.builder()
                .plans(planItems)
                .totalPlans(plans.size())
                .activePlans((int) activePlansCount)
                .build();
    }

    @Override
    public SubscriptionPlanDetailViewModel getSubscriptionPlanById(UUID planId) {
        log.info("Getting subscription plan by ID: {}", planId);

        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found with ID: " + planId));

        return mapToSubscriptionPlanDetailViewModel(plan);
    }

    // Helper methods for mapping entities to view models
    private SubscriptionPlanListViewModel.SubscriptionPlanItemViewModel mapToSubscriptionPlanItemViewModel(SubscriptionPlan plan) {
        Map<String, Object> pricing = parseJsonToMap(plan.getPricing());
        Map<String, Object> apiLimits = parseJsonToMap(plan.getApiLimits());

        return SubscriptionPlanListViewModel.SubscriptionPlanItemViewModel.builder()
                .planId(plan.getPlanId().toString())
                .planCode(plan.getPlanCode())
                .planName(plan.getPlanName())
                .pricing(SubscriptionPlanListViewModel.PricingViewModel.builder()
                        .yearly(new BigDecimal(pricing.getOrDefault("yearly", "0").toString()))
                        .monthly(new BigDecimal(pricing.getOrDefault("monthly", "0").toString()))
                        .setupFee(new BigDecimal(pricing.getOrDefault("setup_fee", "0").toString()))
                        .gstRate(new BigDecimal(pricing.getOrDefault("gst_rate", "18").toString()))
                        .currency(pricing.getOrDefault("currency", "INR").toString())
                        .build())
                .features(parseFeatures(plan.getFeatures()))
                .apiLimits(SubscriptionPlanListViewModel.ApiLimitsViewModel.builder()
                        .requestsPerMinute(Integer.parseInt(apiLimits.getOrDefault("requests_per_minute", "60").toString()))
                        .requestsPerDay(Integer.parseInt(apiLimits.getOrDefault("requests_per_day", "10000").toString()))
                        .requestsPerMonth(Integer.parseInt(apiLimits.getOrDefault("requests_per_month", "300000").toString()))
                        .build())
                .isFeatured(plan.getIsFeatured())
                .isActive(plan.getIsActive())
                .displayOrder(plan.getDisplayOrder())
                .build();
    }

    private SubscriptionPlanDetailViewModel mapToSubscriptionPlanDetailViewModel(SubscriptionPlan plan) {
        Map<String, Object> pricing = parseJsonToMap(plan.getPricing());
        Map<String, Object> apiLimits = parseJsonToMap(plan.getApiLimits());
        Map<String, Object> supportConfig = parseJsonToMap(plan.getSupportConfig());

        return SubscriptionPlanDetailViewModel.builder()
                .planId(plan.getPlanId().toString())
                .planCode(plan.getPlanCode())
                .planName(plan.getPlanName())
                .pricing(SubscriptionPlanDetailViewModel.PricingViewModel.builder()
                        .yearly(new BigDecimal(pricing.getOrDefault("yearly", "0").toString()))
                        .monthly(new BigDecimal(pricing.getOrDefault("monthly", "0").toString()))
                        .setupFee(new BigDecimal(pricing.getOrDefault("setup_fee", "0").toString()))
                        .gstRate(new BigDecimal(pricing.getOrDefault("gst_rate", "18").toString()))
                        .currency(pricing.getOrDefault("currency", "INR").toString())
                        .build())
                .features(parseFeatures(plan.getFeatures()))
                .apiLimits(SubscriptionPlanDetailViewModel.ApiLimitsViewModel.builder()
                        .requestsPerMinute(Integer.parseInt(apiLimits.getOrDefault("requests_per_minute", "60").toString()))
                        .requestsPerDay(Integer.parseInt(apiLimits.getOrDefault("requests_per_day", "10000").toString()))
                        .requestsPerMonth(Integer.parseInt(apiLimits.getOrDefault("requests_per_month", "300000").toString()))
                        .build())
                .supportConfig(SubscriptionPlanDetailViewModel.SupportConfigViewModel.builder()
                        .type(supportConfig.getOrDefault("type", "EMAIL").toString())
                        .slaHours(Integer.parseInt(supportConfig.getOrDefault("sla_hours", "48").toString()))
                        .priority(supportConfig.getOrDefault("priority", "NORMAL").toString())
                        .build())
                .isFeatured(plan.getIsFeatured())
                .isActive(plan.getIsActive())
                .displayOrder(plan.getDisplayOrder())
                .createdAt(plan.getCreatedAt() != null ? plan.getCreatedAt().toString() : null)
                .updatedAt(plan.getUpdatedAt() != null ? plan.getUpdatedAt().toString() : null)
                .build();
    }

    private Map<String, Object> parseJsonToMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("Error parsing JSON: {}", json, e);
            return Map.of();
        }
    }

    private List<String> parseFeatures(String featuresJson) {
        try {
            return objectMapper.readValue(featuresJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("Error parsing features JSON: {}", featuresJson, e);
            return List.of();
        }
    }
}

