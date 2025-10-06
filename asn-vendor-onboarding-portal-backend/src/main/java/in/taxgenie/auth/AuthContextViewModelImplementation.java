package in.taxgenie.auth;

import java.util.List;

/**
 * Implementation of authentication context view model
 * Holds authenticated user information and context data
 */
public class AuthContextViewModelImplementation implements IAuthContextViewModel {

    private final long userId;
    private final String userEmail;
    private final String userClientCode;
    private final long companyCode;
    private final String token;
    private final List<String> authorities;
    private final boolean isCompanyToken;
    private final String companyPan;
    private final Long senderProductId;
    private final Long receiverProductId;

    // Constructor for user tokens
    public AuthContextViewModelImplementation(long userId, String userEmail, String userClientCode,
                                            long companyCode, String token, List<String> authorities,
                                            boolean isCompanyToken) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userClientCode = userClientCode;
        this.companyCode = companyCode;
        this.token = token;
        this.authorities = authorities;
        this.isCompanyToken = isCompanyToken;
        this.companyPan = null;
        this.senderProductId = null;
        this.receiverProductId = null;
    }

    // Constructor for company tokens
    public AuthContextViewModelImplementation(String companyPan, Long companyCode, 
                                            Long senderProductId, Long receiverProductId,
                                            boolean isCompanyToken) {
        this.userId = 0L;
        this.userEmail = null;
        this.userClientCode = null;
        this.companyCode = companyCode != null ? companyCode : 0L;
        this.token = null;
        this.authorities = List.of("COMPANY");
        this.isCompanyToken = isCompanyToken;
        this.companyPan = companyPan;
        this.senderProductId = senderProductId;
        this.receiverProductId = receiverProductId;
    }

    @Override
    public long getUserId() {
        return userId;
    }

    @Override
    public String getUserEmail() {
        return userEmail;
    }

    @Override
    public long getCompanyCode() {
        return companyCode;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public List<String> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUserClientCode() {
        return userClientCode;
    }

    @Override
    public boolean isCompanyToken() {
        return isCompanyToken;
    }

    @Override
    public String getCompanyPan() {
        return companyPan;
    }

    @Override
    public Long getSenderProductId() {
        return senderProductId;
    }

    @Override
    public Long getReceiverProductId() {
        return receiverProductId;
    }
}
