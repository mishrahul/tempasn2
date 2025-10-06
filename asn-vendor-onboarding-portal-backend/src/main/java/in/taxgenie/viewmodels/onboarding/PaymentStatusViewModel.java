package in.taxgenie.viewmodels.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * View model for payment status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusViewModel {

    private String transactionId;

    private String transactionRef;

    private String status; // "PENDING", "PROCESSING", "COMPLETED", "FAILED", "CANCELLED"

    private BigDecimal amount;

    private String currency;

    private String paymentMethod;

    private LocalDateTime initiatedAt;

    private LocalDateTime completedAt;

    private String failureReason;

    private String gatewayResponse;

    private String receiptUrl;

    private String refundStatus;

    private String nextAction;
}
