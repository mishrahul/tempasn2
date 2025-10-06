package in.taxgenie.services;

import in.taxgenie.auth.CompanyDetails;
import in.taxgenie.auth.IJwtFacilities;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JWT service implementation for token validation and claim extraction
 */
@Component
public class JwtService implements IJwtFacilities {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${app.security.jwt.secret:defaultSecretKeyForDevelopmentOnlyNotForProduction}")
    private String serverSecret;

    @Value("${asn.vendor.portal.features.max-vendors-per-oem:1000}")
    private Integer maxVendorsPerOem;

    @Override
    public boolean isValid(String token) throws ExpiredJwtException, UnsupportedJwtException, 
                                              MalformedJwtException, SignatureException, IllegalArgumentException {
        try {
            return validateToken(token);
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public List<GrantedAuthority> getAuthorities(String token) {
        return extractRoles(token);
    }

    @Override
    public String getUserEmail(String token) {
        return extractUserEmail(token);
    }

    @Override
    public String getCompanyPan(String token) {
        return extractCompanyPan(token);
    }

    @Override
    public long getUserId(String token) {
        return extractUserId(token);
    }

    @Override
    public long getCompanyCode(String token) {
        return extractCompanyCode(token);
    }

    @Override
    public Long getCompanyCodeFromToken(String token) {
        return extractCompanyCodeNullable(token);
    }

    @Override
    public Long getSenderProductId(String token) {
        return extractSenderProductId(token);
    }

    @Override
    public Long getReceiverProductId(String token) {
        return extractReceiverProductId(token);
    }

    @Override
    public boolean isCompanyToken(String jwt) throws ExpiredJwtException {
        String tokenType = extractTokenType(jwt);
        return "company".equals(tokenType);
    }

    @Override
    public boolean isValidCompanyToken(String token) {
        try {
            return isValid(token) && isCompanyToken(token);
        } catch (Exception e) {
            logger.error("Error validating company token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public CompanyDetails getCompanyDetails(String token) {
        return new CompanyDetails(
                getCompanyCode(token),
                getCompanyPan(token),
                getSenderProductId(token),
                getReceiverProductId(token)
        );
    }

    // Private helper methods
    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = serverSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private <T> T extractClaim(String token, Function<Jws<Claims>, T> claimsResolver) {
        final Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
        return claimsResolver.apply(claimsJws);
    }

    private String extractUserEmail(String token) {
        return extractClaim(token, claimsJws -> claimsJws.getBody().getSubject());
    }

    private long extractUserId(String token) {
        Object userIdObj = extractClaim(token, claimsJws -> claimsJws.getBody().get("userId"));
        Long userId = convertToLong(userIdObj);
        if (userId == null) {
            throw new IllegalArgumentException("userId claim is required but not found in token");
        }
        return userId;
    }

    private long extractCompanyCode(String token) {
        Object companyIdObj = extractClaim(token, claimsJws -> claimsJws.getBody().get("companyId"));
        Long companyId = convertToLong(companyIdObj);
        if (companyId == null) {
            throw new IllegalArgumentException("companyId claim is required but not found in token");
        }
        return companyId;
    }

    private Long extractCompanyCodeNullable(String token) {
        try {
            Object companyIdObj = extractClaim(token, claimsJws -> claimsJws.getBody().get("companyId"));
            return convertToLong(companyIdObj);
        } catch (Exception e) {
            logger.warn("Error extracting company code from token: {}", e.getMessage());
            return null;
        }
    }

    private String extractCompanyPan(String token) {
        return extractClaim(token, claimsJws -> claimsJws.getBody().get("companyPan", String.class));
    }

    private Long extractSenderProductId(String token) {
        try {
            Object productIdObj = extractClaim(token, claimsJws -> claimsJws.getBody().get("senderProductId"));
            if (productIdObj == null) {
                // Fallback to productId if senderProductId is not present
                productIdObj = extractClaim(token, claimsJws -> claimsJws.getBody().get("productId"));
            }
            return convertToLong(productIdObj);
        } catch (Exception e) {
            logger.warn("Error extracting sender product ID from token: {}", e.getMessage());
            return null;
        }
    }

    private Long extractReceiverProductId(String token) {
        try {
            Object productIdObj = extractClaim(token, claimsJws -> claimsJws.getBody().get("receiverProductId"));
            if (productIdObj == null) {
                // Fallback to productId if receiverProductId is not present
                productIdObj = extractClaim(token, claimsJws -> claimsJws.getBody().get("productId"));
            }
            return convertToLong(productIdObj);
        } catch (Exception e) {
            logger.warn("Error extracting receiver product ID from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Helper method to convert various numeric types to Long
     */
    private Long convertToLong(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer) {
            return ((Integer) value).longValue();
        } else if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof String) {
            return Long.parseLong((String) value);
        } else {
            throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to Long");
        }
    }

    private String extractTokenType(String token) {
        return extractClaim(token, claimsJws -> claimsJws.getBody().get("tokenType", String.class));
    }

    @SuppressWarnings("unchecked")
    private List<GrantedAuthority> extractRoles(String token) {
        // First try to get roles from "roles" field
        List<String> roles = extractClaim(token, claimsJws ->
            (List<String>) claimsJws.getBody().get("roles"));

        if (roles != null) {
            return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        // If no roles field, try to extract from permissions field
        try {
            List<Object> permissions = extractClaim(token, claimsJws ->
                (List<Object>) claimsJws.getBody().get("permissions"));

            if (permissions != null && !permissions.isEmpty()) {
                return permissions.stream()
                        .map(permission -> {
                            if (permission instanceof Map) {
                                Map<String, Object> permMap = (Map<String, Object>) permission;
                                String authority = (String) permMap.get("authority");
                                return new SimpleGrantedAuthority(authority != null ? authority : "user");
                            }
                            return new SimpleGrantedAuthority("user");
                        })
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.warn("Error extracting permissions from token: {}", e.getMessage());
        }

        // Default fallback
        return List.of(new SimpleGrantedAuthority("user"));
    }
}
