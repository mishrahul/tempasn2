package in.taxgenie.repositories;

import in.taxgenie.entities.SubscriptionPlan;
import in.taxgenie.repositories.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SubscriptionPlan entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface SubscriptionPlanRepository extends BaseRepository<SubscriptionPlan> {

    /**
     * Find active subscription plans
     *
     * @return List of active subscription plans
     */
    List<SubscriptionPlan> findByIsActiveTrue();

    /**
     * Find subscription plan by plan code
     *
     * @param planCode the plan code
     * @return Optional containing the subscription plan if found
     */
    Optional<SubscriptionPlan> findByPlanCode(String planCode);

    /**
     * Find featured subscription plans ordered by display order
     *
     * @return List of featured subscription plans
     */
    List<SubscriptionPlan> findByIsFeaturedTrueOrderByDisplayOrderAsc();

    /**
     * Find all active subscription plans ordered by display order
     *
     * @return List of active subscription plans
     */
    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.isActive = true ORDER BY sp.displayOrder ASC")
    List<SubscriptionPlan> findActiveOrderedByDisplayOrder();

    /**
     * Find subscription plans by active status ordered by display order
     *
     * @param isActive the active status
     * @return List of subscription plans
     */
    List<SubscriptionPlan> findByIsActiveOrderByDisplayOrderAsc(Boolean isActive);

    /**
     * Check if plan code exists
     *
     * @param planCode the plan code
     * @return true if exists, false otherwise
     */
    boolean existsByPlanCode(String planCode);

    /**
     * Count all subscription plans
     *
     * @return count of subscription plans
     */
    long count();

    /**
     * Count active subscription plans
     *
     * @return count of active subscription plans
     */
    long countByIsActiveTrue();
}
