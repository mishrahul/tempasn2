export interface Plan {
  id: string;
  name: 'Basic' | 'Professional' | 'Enterprise';
  price: number;
  setupFee: number;
  period: string;
  features: Feature[];
  recommended?: boolean;
}

export interface Feature {
  name: string;
  included: boolean;
}
