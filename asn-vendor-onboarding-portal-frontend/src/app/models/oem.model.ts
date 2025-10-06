export interface OEM {
  id: string;              // Unique identifier for each OEM
  logoBackground: string;  // Background gradient for the logo
  shortName: string;       // Short name for the OEM (e.g., TML, M&M, TAFE)
  fullName: string;        // Full name for the OEM (e.g., Tata Motors Limited)
  features: string[];      // List of features
  isComingSoon?: boolean;  // Optional flag for "coming soon" OEMs
  noAccess?: boolean;      // Optional flag for "no access" OEMs
}

export interface OEMConfig {
  shortName: string;
  branding: {
    asnVersion: string;
  };
}