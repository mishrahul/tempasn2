import { Component, OnInit } from '@angular/core';
import { OnboardingService } from '../../../services/onboarding.service';
import { NotificationService } from '../../../services/notification.service';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user.model';
import { MatDialog } from '@angular/material/dialog';
import { PaymentDialogComponent } from '../../shared/dialog-boxes/payment-dialog/payment-dialog.component';
import { AppDialogComponent } from '../../shared/dialog-boxes/app-dialog/app-dialog.component';
import { Router } from '@angular/router';
import { OnboardingSteps } from 'src/app/constants/onboarding';

@Component({
  selector: 'app-onboarding',
  templateUrl: 'onboarding.component.html',
  styleUrls: ['./onboarding.component.scss']
})
export class OnboardingComponent implements OnInit {
  user: User | null = null;
  isLoading = false;
  payment = false;
  deployment = false;
  confirmation = true;
  currentStepName = OnboardingSteps.confirmation

  constructor(
    private onboardingService: OnboardingService,
    private userService: UserService,
    private notificationService: NotificationService,
    private dialog: MatDialog,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe(user => {
      this.user = user;
    });

    const onboardingStep = sessionStorage.getItem('onboardingStep')
    if(onboardingStep) this.setSection(onboardingStep)
  }

  confirmASN(): void {
    this.isLoading = true;
    
    this.onboardingService.confirmASN().subscribe({
      next: (success) => {
        if (success) {
          this.notificationService.success('ASN 2.1 activation confirmed! Next: Payment Method Selection.');
          // this.notificationService.success('ASN 2.1 activation confirmed! Next: Deployment Method Selection.');
        }
        this.isLoading = false;
        this.openConfirmation()
      },
      error: () => {
        this.notificationService.error('Failed to confirm ASN 2.1. Please try again.');
        this.isLoading = false;
        this.setSection('confirmation')
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
      console.log('res',result)
      if (result) {
        console.log('Payment Done:', result);
        this.setSection('deployment')
      }
    });
  }
  selectDeployment(s: any){
    this.router.navigate(['onboarding', 'self-deployment-setup']);
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
        this.setSection('payment')
      } else {
        console.log('User clicked Cancel');
      }
    });
  }

  setSection(section: string): string | null {
    sessionStorage.setItem('onboardingStep',section)
    const sections: string[] = ['confirmation', 'payment', 'deployment'];
    this.currentStepName = OnboardingSteps[section]
    sections.forEach(key => {
      (this as any)[key] = (key === section);
    });

    return section;
  }
}
