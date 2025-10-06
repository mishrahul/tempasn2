export interface StepProgress {
  id: number;
  title: string;
  description: string;
  status: 'completed' | 'current' | 'pending';
}

export interface Progress<T extends StepProgress> {
  percentage: number;
  completedSteps: number;
  totalSteps: number;
  steps: T[];
}

export type OnboardingProgress = Progress<StepProgress>;

export type ImplementationProgress = Progress<StepProgress>;

export interface DashboardStats {
  progress: number;
  completedSteps: string;
  daysRemaining: number;
  currentPlan: string;
}
