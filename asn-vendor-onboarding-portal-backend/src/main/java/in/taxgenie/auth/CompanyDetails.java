package in.taxgenie.auth;

/**
 * Company details extracted from JWT token
 */
public class CompanyDetails {
    private final long companyCode;
    private final String companyPan;
    private final Long senderProductId;
    private final Long receiverProductId;

    public CompanyDetails(long companyCode, String companyPan, 
                         Long senderProductId, Long receiverProductId) {
        this.companyCode = companyCode;
        this.companyPan = companyPan;
        this.senderProductId = senderProductId;
        this.receiverProductId = receiverProductId;
    }

    public long getCompanyCode() {
        return companyCode;
    }

    public String getCompanyPan() {
        return companyPan;
    }

    public Long getSenderProductId() {
        return senderProductId;
    }

    public Long getReceiverProductId() {
        return receiverProductId;
    }
}
