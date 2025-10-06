package in.taxgenie.viewmodels.onboarding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * View model for payment initiation response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiationResponseViewModel {

    private String transactionId;

    private String transactionRef;

    private String paymentUrl;

    private String paymentToken;

    private BigDecimal amount;

    private String currency;

    private String status; // "INITIATED", "PENDING", "PROCESSING"

    private LocalDateTime expiresAt;

    private GatewayDetailsViewModel gatewayDetails;

    /**
     * Payment gateway specific details
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GatewayDetailsViewModel {

        private String gatewayName;

        private String gatewayTransactionId;

        private String redirectUrl;

        private String qrCodeUrl;

        private String instructions;
    }
}
