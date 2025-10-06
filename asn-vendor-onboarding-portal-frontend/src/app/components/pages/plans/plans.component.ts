import { Component, OnInit } from '@angular/core';
import { PlanService } from '../../../services/plan.service';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { Plan } from '../../../models/plan.model';
import { User } from '../../../models/user.model';

@Component({
  selector: 'app-plans',
  templateUrl: 'plans.component.html',
  styleUrls: ['./plans.component.scss']
})
export class PlansComponent implements OnInit {
  plans: Plan[] = [];
  currentUser: User | null = null;
  loadingPlanId: string | null = null;

  constructor(
    private planService: PlanService,
    private userService: UserService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadPlans();
    this.loadCurrentUser();
  }

  private loadPlans(): void {
    this.planService.getPlans().subscribe(plans => {
      this.plans = plans;
    });
  }

  private loadCurrentUser(): void {
    this.userService.getCurrentUser().subscribe(user => {
      this.currentUser = user;
    });
  }

  isCurrentPlan(plan: Plan): boolean {
    return this.currentUser?.currentPlan === plan.name;
  }

  upgradePlan(planId: string): void {
    this.loadingPlanId = planId;
    
    this.planService.upgradePlan(planId).subscribe({
      next: (success) => {
        if (success) {
          const plan = this.plans.find(p => p.id === planId);
          if (plan && this.currentUser) {
            // Update user's current plan
            this.userService.updateUser({ currentPlan: plan.name }).subscribe();
            this.notificationService.success(`Successfully upgraded to ${plan.name} plan!`);
          }
        }
        this.loadingPlanId = null;
      },
      error: (error) => {
        this.notificationService.error('Failed to upgrade plan. Please try again.');
        this.loadingPlanId = null;
      }
    });
  }

  contactSales(): void {
    this.notificationService.info('Redirecting to sales team...');
    // In a real app, this would open a contact form or redirect to sales page
    console.log('Contact sales initiated');
  }
}
