export interface User {
  id: string;
  companyName: string;
  panNumber: string;
  contactPerson: string;
  email: string;
  phone: string;
  vendorCode: string;
  currentPlan: 'Basic' | 'Professional' | 'Enterprise';
  gstinDetails: GSTINDetail[];
}

export interface GSTINDetail {
  gstin: string;
  state: string;
  vendorCode: string;
}