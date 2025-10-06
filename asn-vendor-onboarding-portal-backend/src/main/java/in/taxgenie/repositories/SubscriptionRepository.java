package in.taxgenie.repositories;

import in.taxgenie.entities.OemMaster;
import in.taxgenie.entities.Subscription;
import in.taxgenie.entities.SubscriptionPlan;
import in.taxgenie.entities.Vendor;
import in.taxgenie.entities.enums.SubscriptionStatus;
import in.taxgenie.repositories.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Subscription entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface SubscriptionRepository extends BaseRepository<Subscription> {

    /**
     * Find subscription by vendor and OEM
     * 
     * @param vendor the vendor
     * @param oem the OEM
     * @return Optional containing the subscription if found
     */
    Optional<Subscription> findByVendorAndOem(Vendor vendor, OemMaster oem);

    /**
     * Find subscriptions by vendor
     * 
     * @param vendor the vendor
     * @return List of subscriptions
     */
    List<Subscription> findByVendor(Vendor vendor);

    /**
     * Find subscriptions by OEM
     * 
     * @param oem the OEM
     * @return List of subscriptions
     */
    List<Subscription> findByOem(OemMaster oem);

    /**
     * Find subscriptions by plan
     * 
     * @param plan the subscription plan
     * @return List of subscriptions
     */
    List<Subscription> findByPlan(SubscriptionPlan plan);

    /**
     * Find subscriptions by status
     * 
     * @param status the subscription status
     * @return List of subscriptions
     */
    List<Subscription> findByStatus(SubscriptionStatus status);

    /**
     * Find active subscriptions by vendor
     * 
     * @param vendor the vendor
     * @return List of active subscriptions
     */
    List<Subscription> findByVendorAndStatus(Vendor vendor, SubscriptionStatus status);

    /**
     * Find active subscriptions by OEM
     * 
     * @param oem the OEM
     * @return List of active subscriptions
     */
    List<Subscription> findByOemAndStatus(OemMaster oem, SubscriptionStatus status);

    /**
     * Find subscriptions expiring before given date
     * 
     * @param date the expiration date threshold
     * @return List of expiring subscriptions
     */
    List<Subscription> findByEndDateBefore(LocalDate date);

    /**
     * Find subscriptions expiring between dates
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return List of subscriptions expiring in the date range
     */
    List<Subscription> findByEndDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find subscriptions with auto-renewal enabled
     * 
     * @return List of auto-renewable subscriptions
     */
    List<Subscription> findByAutoRenewTrue();

    /**
     * Find subscriptions due for billing
     * 
     * @param date the billing date threshold
     * @return List of subscriptions due for billing
     */
    List<Subscription> findByNextBillingDateLessThanEqual(LocalDate date);

    /**
     * Find expired subscriptions
     * 
     * @param currentDate the current date
     * @return List of expired subscriptions
     */
    @Query("SELECT s FROM Subscription s WHERE s.endDate < :currentDate AND s.status != 'EXPIRED'")
    List<Subscription> findExpiredSubscriptions(@Param("currentDate") LocalDate currentDate);

    /**
     * Find subscriptions expiring soon
     * 
     * @param currentDate the current date
     * @param daysAhead the number of days ahead to check
     * @return List of subscriptions expiring soon
     */
    @Query("SELECT s FROM Subscription s WHERE s.endDate BETWEEN :currentDate AND :expirationDate AND s.status = 'ACTIVE'")
    List<Subscription> findSubscriptionsExpiringSoon(@Param("currentDate") LocalDate currentDate, 
                                                     @Param("expirationDate") LocalDate expirationDate);

    /**
     * Find subscriptions by OEM and status with auto-renewal
     * 
     * @param oem the OEM
     * @param status the subscription status
     * @param autoRenew the auto-renewal flag
     * @return List of subscriptions
     */
    List<Subscription> findByOemAndStatusAndAutoRenew(OemMaster oem, SubscriptionStatus status, Boolean autoRenew);

    /**
     * Count subscriptions by OEM and status
     * 
     * @param oem the OEM
     * @param status the subscription status
     * @return count of subscriptions
     */
    long countByOemAndStatus(OemMaster oem, SubscriptionStatus status);

    /**
     * Count subscriptions by vendor and status
     * 
     * @param vendor the vendor
     * @param status the subscription status
     * @return count of subscriptions
     */
    long countByVendorAndStatus(Vendor vendor, SubscriptionStatus status);

    /**
     * Count subscriptions by plan
     * 
     * @param plan the subscription plan
     * @return count of subscriptions
     */
    long countByPlan(SubscriptionPlan plan);

    /**
     * Check if vendor has active subscription with OEM
     * 
     * @param vendor the vendor
     * @param oem the OEM
     * @return true if has active subscription, false otherwise
     */
    @Query("SELECT COUNT(s) > 0 FROM Subscription s WHERE s.vendor = :vendor AND s.oem = :oem AND s.status = 'ACTIVE'")
    boolean hasActiveSubscription(@Param("vendor") Vendor vendor, @Param("oem") OemMaster oem);

    /**
     * Find recently created subscriptions
     *
     * @param date the date threshold
     * @return List of recently created subscriptions
     */
    @Query("SELECT s FROM Subscription s WHERE s.createdAt >= :date ORDER BY s.createdAt DESC")
    List<Subscription> findRecentlyCreated(@Param("date") LocalDate date);

    /**
     * Find active subscription by vendor
     *
     * @param vendor the vendor
     * @return Optional containing the active subscription if found
     */
    @Query("SELECT s FROM Subscription s WHERE s.vendor = :vendor AND s.status = 'ACTIVE' ORDER BY s.startDate DESC")
    Optional<Subscription> findActiveSubscriptionByVendor(@Param("vendor") Vendor vendor);

    /**
     * Find subscriptions by vendor and company code ordered by creation date
     *
     * @param vendor the vendor
     * @param companyCode the company code
     * @return List of subscriptions
     */
    @Query("SELECT s FROM Subscription s WHERE s.vendor = :vendor AND s.companyCode = :companyCode ORDER BY s.createdAt DESC")
    List<Subscription> findByVendorAndCompanyCodeOrderByCreatedAtDesc(@Param("vendor") Vendor vendor, @Param("companyCode") Long companyCode);
}
