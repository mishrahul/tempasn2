import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OnboardingService } from '../../../services/onboarding.service';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { DashboardStats, StepProgress } from 'src/app/models/onboarding.model';


@Component({
  selector: 'app-dashboard',
  templateUrl: 'dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats = {
    progress: 30,
    completedSteps: '2/6',
    daysRemaining: 41,
    currentPlan: 'Basic'
  };

  onboardingSteps: StepProgress[] = [];
  onboardingStepsProgress: number = 0

  implementationSteps: StepProgress[] = [];
  implementationStepsProgress: number = 0

  constructor(
    private router: Router,
    private onboardingService: OnboardingService,
    private userService: UserService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadDashboardOnboardData();
    this.loadDashboardImplementationData();
  }

  private loadDashboardOnboardData(): void {
    // Load user stats and onboarding progress
    this.onboardingService.getOnboardingProgress().subscribe(progress => {
      console.log(progress)
      this.onboardingStepsProgress = progress.percentage;
      // this.stats.completedSteps = `${progress.completedSteps}/${progress.totalSteps}`;
      this.onboardingSteps = progress.steps;
    });

    this.userService.getCurrentUser().subscribe(user => {
      if (user) {
        this.stats.currentPlan = user.currentPlan;
      }
    });

    // Calculate days remaining
    const deadline = new Date('2025-09-30');
    const today = new Date();
    const timeDiff = deadline.getTime() - today.getTime();
    this.stats.daysRemaining = Math.ceil(timeDiff / (1000 * 3600 * 24));
  }

  private loadDashboardImplementationData(): void {
    this.onboardingService.getImplementationProgress().subscribe(progress => {
      this.implementationStepsProgress = progress.percentage;
      // this.stats.completedSteps = `${progress.completedSteps}/${progress.totalSteps}`;
      this.implementationSteps = progress.steps;
    });

    this.userService.getCurrentUser().subscribe(user => {
      if (user) {
        this.stats.currentPlan = user.currentPlan;
      }
    });

    // Calculate days remaining
    const deadline = new Date('2025-09-30');
    const today = new Date();
    const timeDiff = deadline.getTime() - today.getTime();
    this.stats.daysRemaining = Math.ceil(timeDiff / (1000 * 3600 * 24));
  }

  navigateToOnboarding(): void {
    this.router.navigate(['/onboarding']);
  }

  navigateToAISpecialist(): void {
    this.router.navigate(['/ai-specialist']);
  }

  navigateToPlans(): void {
    this.router.navigate(['/plans']);
  }
}