export interface ProgressData {
  completedSteps?: number;
  totalSteps?: number;
  currentStepId?: number;
  estimatedCompletion?: string;
  lastUpdated?: string;
  percentage?: number;
  steps?: StepProgress[];
  type?: string;
}

export interface StepProgress {
  id: number;
  title: string;
  description: string;
  status: 'completed' | 'current' | 'pending';
  completedAt?: string;
  isActionable?: boolean;
  actionUrl?: string;
}

export interface Progress<T extends StepProgress> {
  completedSteps: number;
  totalSteps: number;
  currentStepId: number;
  estimatedCompletion: string;
  lastUpdated: string;
  percentage: number;
  steps: T[];
  type: string;
}

export type OnboardingProgress = Progress<StepProgress>;

export type ImplementationProgress = Progress<StepProgress>;

export interface DashboardStats {
  progress?: number;
  completedSteps?: string;
  daysRemaining?: number;
  currentPlan?: string;
  criticalAlert?: CriticalAlert;
  status?: string;
  nextAction?: string;
  deadline?: string;
}

export interface CriticalAlert {
  title: string;
  message: string;
  type: 'error' | 'warning';
  actionRequired: boolean;
  actionUrl?: string;
}

export interface StepItem {
  label: string;
  command?: () => void;
}

export interface ConfirmationData {
  oemCode: string,
  confirmationType: string,
  acknowledgment: boolean,
  termsAccepted: boolean,
  complianceConfirmed: boolean,
  additionalNotes: string
}

// Credentials API interfaces
export interface RateLimits {
  requestsPerMinute: number;
  requestsPerDay: number;
  requestsPerMonth: number;
  burstLimit: number;
}

export interface ApiEndpoint {
  name: string;
  url: string;
  method: string;
  description: string;
  authenticationRequired: boolean;
}

export interface CredentialsData {
  credentialId: string;
  developerId: string;
  apiKey: string;
  clientSecret: string;
  environment: string;
  endpointUrl: string;
  status: string;
  createdAt: string;
  expiresAt: string;
  rateLimits: RateLimits;
  endpoints: ApiEndpoint[];
  documentationUrl: string;
  supportContact: string;
  email: string;
}

export interface CredentialsResponse {
  body: CredentialsData;
  message: string;
  responseCode: number;
  ok: boolean;
}

export interface CredentialsErrorResponse {
  error: string;
  message: string;
  details?: string;
}

// Environment configuration interface
export interface Environment {
  value: string;
  label: string;
  description: string;
}

// Create credentials request interface
export interface CreateCredentialsRequest {
  oemId: string | null;
  environment: string;
  webhookUrl?: string; // Optional field
  // ipWhitelist?: string;
  // rateLimitTier?: string;
  // additionalConfig?: string;
  esakhaPassword: string;
  esakhaUserId: string;
  credentialId?: string;
}

// Select deployment request interface
export interface SelectDeploymentRequest {
  oemCode: string;
  deploymentType: string;
  preferredTimeline: string;
  technicalExpertiseLevel: string;
  existingErpSystem: string;
  integrationRequirements: string;
  additionalServicesRequired: boolean;
  notes: string;
}