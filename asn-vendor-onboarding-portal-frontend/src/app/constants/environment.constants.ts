import { Environment } from '../models/onboarding.model';

// Environment configuration constants
export const AVAILABLE_ENVIRONMENTS: Environment[] = [
  { 
    value: 'sandbox', 
    label: 'Sandbox (Testing)', 
    description: 'For development and testing purposes' 
  },
  // { 
  //   value: 'staging', 
  //   label: 'Staging (Pre-production)', 
  //   description: 'For final testing before production' 
  // },
  { 
    value: 'production', 
    label: 'Production (Live)', 
    description: 'Live environment for actual transactions' 
  }
];

// Default environment
export const DEFAULT_ENVIRONMENT = 'sandbox';
