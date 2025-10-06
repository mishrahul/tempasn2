package in.taxgenie.services.interfaces;

import in.taxgenie.viewmodels.subscriptionplan.SubscriptionPlanDetailViewModel;
import in.taxgenie.viewmodels.subscriptionplan.SubscriptionPlanListViewModel;

import java.util.UUID;

/**
 * Service interface for subscription plan operations
 */
public interface ISubscriptionPlanService {

    /**
     * Get all subscription plans
     *
     * @param activeOnly if true, return only active plans
     * @return list of subscription plans
     */
    SubscriptionPlanListViewModel getAllSubscriptionPlans(boolean activeOnly);

    /**
     * Get subscription plan by ID
     *
     * @param planId the plan ID
     * @return subscription plan details
     */
    SubscriptionPlanDetailViewModel getSubscriptionPlanById(UUID planId);
}

