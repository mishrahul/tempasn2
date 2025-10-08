import { Component, OnInit } from '@angular/core';
import { PlanService } from '../../../services/plan.service';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { Plan } from '../../../models/plan.model';
import { CompanyInfo, SubscriptionPlan } from 'src/app/models/settings.model';

@Component({
  selector: 'app-plans',
  templateUrl: 'plans.component.html',
  styleUrls: ['./plans.component.scss']
})
export class PlansComponent implements OnInit {
  plans: Plan[] = [];
  comapanyInfo: CompanyInfo | null = null;
  loadingPlanId: string | null = null;

  constructor(
    private planService: PlanService,
    private userService: UserService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.comapanyInfo = JSON.parse(sessionStorage.getItem('companyInfo') || '{}');
    // this.loadPlans();
    // this.loadCurrentUser();
    this.getPlans()
  }

  // private loadPlans(): void {
  //   this.planService.getPlans().subscribe(plans => {
  //     this.plans = plans;
  //   });
  // }

  // private loadCurrentUser(): void {
  //   // this.userService.companyInfo$.subscribe(response => {
  //   //   this.comapanyInfo = response;
  //   // });
  // }


  private convertSubscriptionPlanToPlan(subscriptionPlan: SubscriptionPlan): Plan {
    return {
      id: subscriptionPlan.planId,
      name: subscriptionPlan.planName as 'Basic' | 'Professional' | 'Enterprise',
      price: subscriptionPlan.pricing.yearly,
      setupFee: subscriptionPlan.pricing.setupFee,
      period: 'per year',
      features: subscriptionPlan.features,
      recommended: subscriptionPlan.featured
    };
  }

  getPlans(){
    this.planService.getSubscriptionPlans().subscribe({
      next: (response) => {
        // response.data will be of type SubscriptionPlansResponse
        const plansData = response.data;

        if (plansData) {
          console.log('Total plans:', plansData.totalPlans);
          console.log('Active plans:', plansData.activePlans);

          // Convert SubscriptionPlan[] to Plan[]
          this.plans = plansData.plans.map(plan => this.convertSubscriptionPlanToPlan(plan));
          // plansData.plans.forEach(plan => {
          //   console.log(`Plan: ${plan.planName} (${plan.planCode})`);
          //   console.log(`Yearly price: ₹${plan.pricing.yearly}`);
          //   console.log(`Featured: ${plan.featured}`);
          // });
        } else {
          console.warn('No plans data received from server');
        }
      },
      error: (error) => {
        console.error('Error loading subscription plans:', error);
      }
    });
  }



  isCurrentPlan(plan: Plan): boolean {
    return this.comapanyInfo?.currentPlan === plan.name;
  }

  upgradePlan(planId: string): void {
    this.loadingPlanId = planId;
    
    this.planService.upgradePlan(planId).subscribe({
      next: (success) => {
        if (success) {
          const plan = this.plans.find(p => p.id === planId);
          if (plan && this.comapanyInfo) {
            // Update user's current plan
            // this.userService.updateUser({ currentPlan: plan.name }).subscribe();
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
