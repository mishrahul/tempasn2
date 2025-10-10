package in.taxgenie.viewmodels.onboarding;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorCodesViewModel {
    private String gstin;
    private UUID vendorId;
    private String companyName;
    private List<String> vendorCodes;
}