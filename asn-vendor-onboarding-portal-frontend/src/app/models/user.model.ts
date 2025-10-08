// export interface User {
//   id: string;
//   companyName: string;
//   panNumber: string;
//   contactPerson: string;
//   email: string;
//   phone: string;
//   vendorCode: string;
//   currentPlan: 'Basic' | 'Professional' | 'Enterprise';
//   gstinDetails: GSTINDetail[];
// }

export interface GSTINDetail {
  id?: string;
  gstin: string;
  state: string;
  vendorCode: string;
  stateCode?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
  verifiedAt?: string;
  status?: string;
  verified?: boolean;
  primary?: boolean;
  data?: [];
}