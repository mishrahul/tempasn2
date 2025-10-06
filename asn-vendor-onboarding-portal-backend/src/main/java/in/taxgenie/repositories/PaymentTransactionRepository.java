package in.taxgenie.repositories;

import in.taxgenie.entities.OemMaster;
import in.taxgenie.entities.PaymentTransaction;
import in.taxgenie.entities.Subscription;
import in.taxgenie.entities.Vendor;
import in.taxgenie.entities.enums.PaymentStatus;
import in.taxgenie.repositories.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PaymentTransaction entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface PaymentTransactionRepository extends BaseRepository<PaymentTransaction> {

    /**
     * Find payment transaction by transaction reference
     * 
     * @param transactionRef the transaction reference
     * @return Optional containing the payment transaction if found
     */
    Optional<PaymentTransaction> findByTransactionRef(String transactionRef);

    /**
     * Find payment transactions by vendor
     * 
     * @param vendor the vendor
     * @return List of payment transactions
     */
    List<PaymentTransaction> findByVendor(Vendor vendor);

    /**
     * Find payment transactions by OEM
     * 
     * @param oem the OEM
     * @return List of payment transactions
     */
    List<PaymentTransaction> findByOem(OemMaster oem);

    /**
     * Find payment transactions by subscription
     * 
     * @param subscription the subscription
     * @return List of payment transactions
     */
    List<PaymentTransaction> findBySubscription(Subscription subscription);

    /**
     * Find payment transactions by status
     * 
     * @param status the payment status
     * @return List of payment transactions
     */
    List<PaymentTransaction> findByStatus(PaymentStatus status);

    /**
     * Find payment transactions by vendor and status
     * 
     * @param vendor the vendor
     * @param status the payment status
     * @return List of payment transactions
     */
    List<PaymentTransaction> findByVendorAndStatus(Vendor vendor, PaymentStatus status);

    /**
     * Find payment transactions by OEM and status
     * 
     * @param oem the OEM
     * @param status the payment status
     * @return List of payment transactions
     */
    List<PaymentTransaction> findByOemAndStatus(OemMaster oem, PaymentStatus status);

    /**
     * Find payment transactions initiated after given date
     * 
     * @param dateTime the date time threshold
     * @return List of payment transactions
     */
    List<PaymentTransaction> findByInitiatedAtAfter(LocalDateTime dateTime);

    /**
     * Find payment transactions initiated between dates
     * 
     * @param startDateTime the start date time
     * @param endDateTime the end date time
     * @return List of payment transactions
     */
    List<PaymentTransaction> findByInitiatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * Find completed payment transactions
     * 
     * @return List of completed payment transactions
     */
    List<PaymentTransaction> findByCompletedAtIsNotNull();

    /**
     * Find pending payment transactions older than given time
     * 
     * @param dateTime the date time threshold
     * @return List of stale pending transactions
     */
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.status = 'PENDING' AND pt.initiatedAt < :dateTime")
    List<PaymentTransaction> findStalePendingTransactions(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Find payment transactions by amount range
     * 
     * @param minAmount the minimum amount
     * @param maxAmount the maximum amount
     * @return List of payment transactions
     */
    List<PaymentTransaction> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    /**
     * Find payment transactions by currency
     * 
     * @param currency the currency code
     * @return List of payment transactions
     */
    List<PaymentTransaction> findByCurrency(String currency);

    /**
     * Calculate total amount by vendor and status
     * 
     * @param vendor the vendor
     * @param status the payment status
     * @return total amount
     */
    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt WHERE pt.vendor = :vendor AND pt.status = :status")
    BigDecimal calculateTotalAmountByVendorAndStatus(@Param("vendor") Vendor vendor, @Param("status") PaymentStatus status);

    /**
     * Calculate total amount by OEM and status
     * 
     * @param oem the OEM
     * @param status the payment status
     * @return total amount
     */
    @Query("SELECT COALESCE(SUM(pt.amount), 0) FROM PaymentTransaction pt WHERE pt.oem = :oem AND pt.status = :status")
    BigDecimal calculateTotalAmountByOemAndStatus(@Param("oem") OemMaster oem, @Param("status") PaymentStatus status);

    /**
     * Count payment transactions by vendor and status
     * 
     * @param vendor the vendor
     * @param status the payment status
     * @return count of payment transactions
     */
    long countByVendorAndStatus(Vendor vendor, PaymentStatus status);

    /**
     * Count payment transactions by OEM and status
     * 
     * @param oem the OEM
     * @param status the payment status
     * @return count of payment transactions
     */
    long countByOemAndStatus(OemMaster oem, PaymentStatus status);

    /**
     * Check if transaction reference exists
     * 
     * @param transactionRef the transaction reference
     * @return true if exists, false otherwise
     */
    boolean existsByTransactionRef(String transactionRef);

    /**
     * Find recent successful payments by vendor
     * 
     * @param vendor the vendor
     * @param dateTime the date time threshold
     * @return List of recent successful payments
     */
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.vendor = :vendor AND pt.status = 'SUCCESS' AND pt.completedAt >= :dateTime ORDER BY pt.completedAt DESC")
    List<PaymentTransaction> findRecentSuccessfulPaymentsByVendor(@Param("vendor") Vendor vendor, @Param("dateTime") LocalDateTime dateTime);

    /**
     * Find failed transactions for retry
     *
     * @param dateTime the date time threshold
     * @return List of failed transactions
     */
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.status = 'FAILED' AND pt.initiatedAt >= :dateTime")
    List<PaymentTransaction> findFailedTransactionsForRetry(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Find payment transactions by vendor and company code ordered by creation date
     *
     * @param vendor the vendor
     * @param companyCode the company code
     * @return List of payment transactions
     */
    @Query("SELECT pt FROM PaymentTransaction pt WHERE pt.vendor = :vendor AND pt.vendor.companyCode = :companyCode ORDER BY pt.initiatedAt DESC")
    List<PaymentTransaction> findByVendorAndCompanyCodeOrderByCreatedAtDesc(@Param("vendor") Vendor vendor, @Param("companyCode") Long companyCode);
}
