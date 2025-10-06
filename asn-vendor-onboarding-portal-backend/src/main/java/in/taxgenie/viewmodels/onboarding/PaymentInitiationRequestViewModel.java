package in.taxgenie.viewmodels.onboarding;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * View model for payment initiation request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiationRequestViewModel {

    @NotBlank(message = "OEM code is required")
    private String oemCode;

    @NotBlank(message = "Plan code is required")
    private String planCode;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // "CREDIT_CARD", "DEBIT_CARD", "NET_BANKING", "UPI", "WALLET"

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Builder.Default
    private String currency = "INR";

    private String returnUrl;

    private String cancelUrl;

    private String webhookUrl;

    private String additionalInfo;
}
