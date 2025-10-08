export interface PrimaryGstin {
  gstinId: string;
  gstin: string;
  state: string;
  stateCode: string;
  vendorCode: string;
  status: string;
  verified: boolean;
}

export interface CompanyInfo {
  companyName: string;
  panNumber: string;
  contactPerson: string;
  email: string;
  phone: string;
  vendorCode: string;
  address?: string;
  city?: string;
  state?: string;
  pincode?: string;
  status?: string;
  lastUpdatedAt?: string;
  currentPlan?: string;
  primaryGstin?: PrimaryGstin;
  data?: []
}

interface PricingInfo {
  yearly: number;
  monthly: number;
  setupFee: number;
  gstRate: number;
  currency: string;
}

interface ApiLimits {
  requestsPerMinute: number;
  requestsPerDay: number;
  requestsPerMonth: number;
  currentUsage?: number;
  resetDate?: string;
}

// New interfaces for subscription plans API response
export interface SubscriptionPlan {
  planId: string;
  planCode: string;
  planName: string;
  pricing: PricingInfo;
  features: { name: string; included: boolean; }[];
  apiLimits: ApiLimits;
  displayOrder: number;
  active: boolean;
  featured: boolean;
}

export interface SubscriptionPlansResponse {
  plans: SubscriptionPlan[];
  totalPlans: number;
  activePlans: number;
}

export interface BaseSubscription {
  subscriptionId: string;
  planName: string;
  status: string;
  startDate: string;
  endDate: string;
  nextBillingDate: string;
  currency: string;
}

export interface CurrentSubscription extends BaseSubscription {
  planName: string;
  planCode: string;
  pricing: PricingInfo;
  annualFee: number;
  setupFee: number;
  autoRenewal: boolean;
  features: string[];
  apiLimits: ApiLimits;
}

export interface SubscriptionBilling {
  currentSubscription: CurrentSubscription;
  subscriptionHistory: Array<BaseSubscription & {
    amount: number;
  }>;
  billingInfo: {
    totalPaid: number;
    pendingAmount: number;
    nextBillingDate: string;
    paymentMethod: string;
    currency: string;
  };
  paymentHistory: any[];
  subscriptionPlans: Array<{
    planId: string;
    planCode: string;
    planName: string;
    pricing: PricingInfo;
    features: string[];
    apiLimits: ApiLimits;
    active: boolean;
    featured: boolean;
  }>;
}

export interface CRUDRequest {
  gstin?: string;
  vendorCode?: string;
  stateCode?: string;
  primary: boolean;
}
