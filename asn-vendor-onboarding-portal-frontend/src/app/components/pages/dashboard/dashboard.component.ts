import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OnboardingService } from '../../../services/onboarding.service';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { DashboardStats, ProgressData, StepProgress } from 'src/app/models/onboarding.model';
import { SelectedOEM } from 'src/app/models/oem.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: 'dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {
  
  stats: DashboardStats = {};

  onboardingSteps: ProgressData = {};
  onboardingStepsProgress: number = 0;

  implementationSteps: ProgressData = {};
  implementationStepsProgress: number = 0;

  loading = false;
  selectedOEM: SelectedOEM | null = null;

  constructor(
    private router: Router,
    private onboardingService: OnboardingService,
    private notificationService: NotificationService,
    private userService: UserService
  ) {
    this.selectedOEM = JSON.parse(sessionStorage.getItem('selectedOEM') || '{}');
  }

  ngOnInit(): void {
    this.loadDashboardStats();
    this.loadDashboardOnboardData();
    // this.loadDashboardImplementationData();
  }

  private loadDashboardStats(): void {
    const oemId = this.selectedOEM?.id || null;
    this.onboardingService.getDashboardStats(oemId).subscribe({
      next: (response) => {
        console.log('response1',response)
        if (response.ok && response.body) {
          this.stats = response.body;
          this.userService.setCurrentPlan(this.stats.currentPlan || '');
        }
      },
      error: (error) => {
        console.error('Error loading dashboard stats:', error);
        this.notificationService.error(error.error?.message || 'Failed to load dashboard stats');
      }
    });
  }

  private loadDashboardOnboardData(): void {
    const oemId = this.selectedOEM?.id || null;
    this.onboardingService.getOnboardingProgress(oemId).subscribe({
      next: (response) => {
        console.log('response2 getOnboardingProgress',response)
        if (response.ok && response.body) {
          this.onboardingStepsProgress = response.body.percentage || 0;
          this.onboardingSteps = response.body;
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading onboarding progress:', error);
        // this.error = 'Failed to load onboarding data';
        this.notificationService.error(error.error?.message || 'Failed to load onboarding data');
        this.loading = false;
      }
    });
  }

  private loadDashboardImplementationData(): void {
    const oemId = this.selectedOEM?.id || null;
    this.onboardingService.getImplementationProgress(oemId).subscribe({
      next: (response) => {
        console.log('response3 getImplementationProgress',response)
        if (response.ok && response.body) {
          this.implementationStepsProgress = response.body.percentage || 0;
          this.implementationSteps = response.body;
        }
      },
      error: (error) => {
        console.error('Error loading implementation progress:', error);
      }
    });
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
