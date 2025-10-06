import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Plan } from '../models/plan.model';

@Injectable({
  providedIn: 'root'
})
export class PlanService {
  private plans: Plan[] = [
    {
      id: 'basic',
      name: 'Basic',
      price: 12000,
      setupFee: 5000,
      period: 'per year',
      features: [
        { name: 'ASN 2.1 Basic Implementation', included: true },
        { name: 'Digital Invoice Upload', included: true },
        { name: 'Standard ERP Connectors', included: true },
        { name: 'Email Support', included: true },
        { name: 'Basic Training Materials', included: true },
        { name: 'Priority Support', included: false },
        { name: 'Custom Integrations', included: false }
      ]
    },
    {
      id: 'professional',
      name: 'Professional',
      price: 25000,
      setupFee: 8000,
      period: 'per year',
      recommended: true,
      features: [
        { name: 'Everything in Basic', included: true },
        { name: 'Priority Support (24/7)', included: true },
        { name: 'Advanced Training & Webinars', included: true },
        { name: 'Custom ERP Integration (1 Free)', included: true },
        { name: 'Dedicated Account Manager', included: true },
        { name: 'Monthly Expert Consultations', included: true },
        { name: 'Advanced Analytics Dashboard', included: true }
      ]
    },
    {
      id: 'enterprise',
      name: 'Enterprise',
      price: 50000,
      setupFee: 15000,
      period: 'per year',
      features: [
        { name: 'Everything in Professional', included: true },
        { name: 'White-Label Solution', included: true },
        { name: 'Unlimited Custom Integrations', included: true },
        { name: 'On-Site Implementation Support', included: true },
        { name: 'SLA Guarantees (99.9% uptime)', included: true },
        { name: 'Custom Training Programs', included: true },
        { name: 'API Access & Customization', included: true }
      ]
    }
  ];

  getPlans(): Observable<Plan[]> {
    return of(this.plans);
  }

  upgradePlan(planId: string): Observable<boolean> {
    // Simulate API call
    return new Promise(resolve => {
      setTimeout(() => {
        resolve(true);
      }, 2000);
    }) as any;
  }
}
