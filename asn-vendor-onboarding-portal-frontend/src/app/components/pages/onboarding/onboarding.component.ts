import { Component, OnInit } from '@angular/core';
import { OnboardingService } from '../../../services/onboarding.service';
import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { MatDialog } from '@angular/material/dialog';
import { PaymentDialogComponent } from '../../shared/dialog-boxes/payment-dialog/payment-dialog.component';
import { AppDialogComponent } from '../../shared/dialog-boxes/app-dialog/app-dialog.component';
import { Router } from '@angular/router';
import { ConfirmationData, StepItem, StepProgress, SelectDeploymentRequest } from 'src/app/models/onboarding.model';
import { BaseSubscription, CompanyInfo, SubscriptionBilling } from 'src/app/models/settings.model';
import { SettingsService } from 'src/app/services/settings.service';
import { GSTINDetail } from 'src/app/models/user.model';
import { SelectedOEM } from 'src/app/models/oem.model';
import { PlanService } from 'src/app/services/plan.service';

@Component({
  selector: 'app-onboarding',
  templateUrl: 'onboarding.component.html',
  styleUrls: ['./onboarding.component.scss']
})
export class OnboardingComponent implements OnInit {
  isLoading = false;
  payment = false;
  deployment = false;
  confirmation = true;
  // currentStepName = OnboardingSteps.confirmation
  oemId: string | null = null;
  selectedOEM: SelectedOEM | null = null;
  activeIndex = 0;
  companyInfo: CompanyInfo | null = null;
  onboardingStep: StepProgress[] = [];
  gstinDetails: GSTINDetail[] = [];
  subscriptionInfo: BaseSubscription | null = null;

  constructor(
    private onboardingService: OnboardingService,
    private userService: UserService,
    private notificationService: NotificationService,
    private dialog: MatDialog,
    public router: Router,
    private settingsService: SettingsService,
    private planService: PlanService
  ) {}

  steps: StepItem[] = [
    { label: 'Confirmation', command: () => this.goToStep('confirmation') },
    { label: 'Payment', command: () => this.goToStep('payment') },
    { label: 'Deployment', command: () => this.goToStep('deployment') },
    { label: 'Self-Deployment', command: () => this.goToStep('self-deployment') }
  ];

  ngOnInit(): void {
    this.selectedOEM = JSON.parse(sessionStorage.getItem('selectedOEM') || '{}');
    this.oemId = this.selectedOEM?.id || null;
    this.companyInfo = JSON.parse(sessionStorage.getItem('companyInfo') || '{}');
    const onboardingStep = sessionStorage.getItem('onboardingStep')
    console.log('onboardingStep1',onboardingStep)
    if(onboardingStep) this.goToStep(onboardingStep)
    
    // Load initial data
    this.loadOnboardingData();
    this.getGSTINDetails();
  }

  goToStep(step: string): void {
    sessionStorage.setItem('onboardingStep',step)
    switch(step) {
      case 'confirmation':
        this.activeIndex = 0;
        // this.setSection('confirmation');
        break;
      case 'payment':
        this.activeIndex = 1;
        // this.setSection('payment');
        break;
      case 'deployment':
        this.activeIndex = 2;
        // this.setSection('deployment');
        break;
      case 'self-deployment':
        this.activeIndex = 3;
        // Handle self-deployment step
        // this.setSection('self-deployment');
        this.router.navigate(['onboarding', 'self-deployment-setup']);
        break;
    }
  }

  loadOnboardingData(): void {
    this.isLoading = true;
    
    // Load onboarding progress
    this.onboardingService.getOnboardingProgressByOEM(this.oemId).subscribe({
      next: (response) => {
        console.log('response1',response)
        if (response.body) {
          console.log('Onboarding progress loaded:', response.body);
          console.log('data', response.body.steps);
          this.onboardingStep = response.body.steps
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading onboarding progress:', error);
        this.notificationService.error('Failed to load onboarding progress');
        this.isLoading = false;
      }
    });
  }


  getGSTINDetails(): void {
    this.settingsService.getGSTINManagement().subscribe({
      next: (response: any) => {
        if (response) {
          console.log('response, getGSTINManagement1',response.data)
          this.gstinDetails = this.sortPrimaryOnTop(response.data.gstinDetails);
        }
      },
      error: (error) => {
        console.error('Error loading GSTIN details:', error);
        this.notificationService.error('Failed to load GSTIN details');
      }
    });
  }
  sortPrimaryOnTop(data: any[]) {
    return data.sort((a, b) => {
      return Number(b.primary === true) - Number(a.primary === true);
    });
  }

  confirmASN(): void {
    this.isLoading = true;
    
    const confirmationData: ConfirmationData = {
      oemCode: this.selectedOEM?.oemCode || '',
      confirmationType: "ASN_2_1_ACTIVATION",
      acknowledgment: true,
      termsAccepted: true,
      complianceConfirmed: true,
      additionalNotes: ""
    }

    this.onboardingService.confirmASNOnboarding(confirmationData).subscribe({
      next: (response) => {
        if (response.ok) {
          this.notificationService.success('ASN 2.1 activation confirmed! Next: Payment Method Selection.');
          this.openConfirmation();
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error confirming ASN:', error);
        this.notificationService.error('Failed to confirm ASN 2.1. Please try again.');
        this.isLoading = false;
        // this.setSection('confirmation');
      }
    });
  }

  openPaymentModal(){
    const dialogRef = this.dialog.open(PaymentDialogComponent, {
      width: '500px',
      backdropClass: 'blurred-backdrop',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.goToStep('deployment')
      }
    });
  }

  selectDeployment(type: string) {
    const oemCode = JSON.parse(sessionStorage.getItem('selectedOEM') || '{}').oemCode || '';

    // Prepare the deployment selection payload
    const payload: SelectDeploymentRequest = {
      oemCode: oemCode,
      deploymentType: type, // 'self' or 'assisted'
      preferredTimeline: type === 'self' ? 'immediate' : '1-2 weeks',
      technicalExpertiseLevel: type === 'self' ? 'intermediate' : 'beginner',
      existingErpSystem: 'none', // This could be collected from a form
      integrationRequirements: type === 'self' ? 'basic' : 'full',
      additionalServicesRequired: type === 'assisted',
      notes: `User selected ${type} deployment option`
    };

    console.log('Selecting deployment with payload:', payload);

    // Call the API
    this.onboardingService.selectDeployment(payload).subscribe({
      next: (response) => {
        console.log('Deployment selection response:', response);
        if (response.ok) {
          this.notificationService.success(`${type === 'self' ? 'Self-deployment' : 'Assisted implementation'} selected successfully!`);

          // Navigate based on deployment type
          if (type === 'self') {
            this.router.navigate(['onboarding', 'self-deployment-setup']);
          } else {
            // For assisted deployment, you might want to navigate to a different page
            // or show additional information
            this.router.navigate(['dashboard']);
            this.notificationService.info('Our team will contact you within 24 hours to schedule your assisted implementation.');
          }
        } else {
          this.notificationService.error('Failed to select deployment option. Please try again.');
        }
      },
      error: (error) => {
        console.error('Error selecting deployment:', error);
        this.notificationService.error('Failed to select deployment option. Please try again.');
      }
    });
  }

  handleCredentialsSubmission(event: any){}
  goBackToDeployment(){}

  openConfirmation() {
    const dialogRef = this.dialog.open(AppDialogComponent, {
      disableClose: true,
      data: {
        title: 'Confirmation Successful!',
        message: 'Thank you for confirming ASN 2.1 activation. You can now proceed with payment and deployment setup.',
        buttons: [
          { text: 'OK', value: true, color: 'primary' }
        ]
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('User clicked OK', result);
        // this.setSection('payment')
        this.goToStep('payment')
      } else {
        this.goToStep('confirmation')
        console.log('User clicked Cancel');
      }
    });
  }

  // setSection(section: string): string | null {
  //   console.log('section',section)
  //   sessionStorage.setItem('onboardingStep',section)
  //   const sections: string[] = ['confirmation', 'payment', 'deployment', 'self-deployment'];
  //   this.currentStepName = OnboardingSteps[section]
  //   sections.forEach((key: string) => {
  //     if(key === section && section !== 'self-deployment') {
  //       this.activeIndex = sections.indexOf(key)
  //     }else if(key === section && section == 'self-deployment'){
  //       console.log('key----',key)
  //       this.router.navigate(['onboarding', 'self-deployment-setup']);
  //     }
  //   });

  //   return section;
  // }
}
