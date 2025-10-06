import { Injectable } from '@angular/core';
import { from, Observable, of } from 'rxjs';
import { ImplementationProgress, OnboardingProgress } from '../models/onboarding.model';

@Injectable({
  providedIn: 'root'
})
export class OnboardingService {
  private mockOnboardingProgress: OnboardingProgress = {
    percentage: 75,
    completedSteps: 3,
    totalSteps: 4,
    steps: [
      {
        id: 1,
        title: 'Account Setup',
        description: 'Initial registration and vendor verification completed',
        status: 'completed'
      },
      {
        id: 2,
        title: 'ASN 2.1 Confirmation',
        description: 'Review and confirm implementation requirements',
        status: 'completed'
      },
      
      {
        id: 3,
        title: 'Payment',
        description: 'Pay based on Deployment Method Selection',
        status: 'current'
      },
      {
        id: 5,
        title: 'Deployment Method Selection',
        description: 'Choose between self-deployment or assisted implementation',
        status: 'pending'
      },
      // {
      //   id: 5,
      //   title: 'Implementation and UAT',
      //   description: 'ERP/Tally Integration & UAT',
      //   status: 'pending'
      // },
      // {
      //   id: 6,
      //   title: 'Production Go Live',
      //   description: 'Final deployment and activation',
      //   status: 'pending'
      // }
    ]
  };


  private mockImplementationProgress: ImplementationProgress = {
    percentage: 33,
    completedSteps: 2,
    totalSteps: 6,
    steps: [
      {
        id: 1,
        title: 'Deployment',
        description: 'ERP/Tally integration and system configuration',
        status: 'completed'
      },
      {
        id: 2,
        title: 'User Acceptance Testing',
        description: 'Testing and validation of ASN 2.1 implementation',
        status: 'current'
      },
      {
        id: 3,
        title: 'Go Live',
        description: 'Final deployment and production activation',
        status: 'pending'
      },
    ]
  };


  getOnboardingProgress(): Observable<OnboardingProgress> {
    return of(this.mockOnboardingProgress);
  }

  getImplementationProgress(): Observable<ImplementationProgress> {
    return of(this.mockImplementationProgress);
  }

  confirmASN(): Observable<boolean> {
    const promise = new Promise<boolean>(resolve => {
      setTimeout(() => {
        this.mockOnboardingProgress.percentage = 45;
        this.mockOnboardingProgress.completedSteps = 3;
        this.mockOnboardingProgress.steps[1].status = 'completed';
        this.mockOnboardingProgress.steps[2].status = 'current';
        resolve(true);
      }, 2000);
    });

    return from(promise);
  }
}
