import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';
import { NotificationService } from 'src/app/services/notification.service';
import { OnboardingService } from 'src/app/services/onboarding.service';
import { CredentialsData, CredentialsResponse, CredentialsErrorResponse, Environment, CreateCredentialsRequest } from 'src/app/models/onboarding.model';
import { AVAILABLE_ENVIRONMENTS, DEFAULT_ENVIRONMENT } from 'src/app/constants/environment.constants';
import { CompanyInfo } from 'src/app/models/settings.model';
import { SettingsService } from 'src/app/services/settings.service';

@Component({
  selector: 'app-self-deployment-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './self-deployment-form.component.html',
  styleUrls: ['./self-deployment-form.component.scss']
})
export class SelfDeploymentFormComponent implements OnInit {
  form!: FormGroup;
  response: CredentialsData | CredentialsErrorResponse | null = null;
  success = false;
  isPasswordVisible: boolean = false;
  isLoading = false;
  environments: Environment[] = AVAILABLE_ENVIRONMENTS;
  oemId: string | null = null;
  companyInfo: CompanyInfo | null = null;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    public notificationService: NotificationService,
    private onboardingService: OnboardingService,
  ) {}

  ngOnInit(): void {
    this.oemId = JSON.parse(sessionStorage.getItem('selectedOEM') || '{}').id || null;
    this.companyInfo = JSON.parse(sessionStorage.getItem('companyInfo') || '{}');

    this.form = this.fb.group({
      company: this.fb.group({
        vendorName: [{ value: this.companyInfo?.companyName || '', disabled: true }],
        vendorCode: [{ value: this.companyInfo?.vendorCode || '', disabled: true }],
        panNumber: [{ value: this.companyInfo?.panNumber || '', disabled: true }],
        gstinNumber: [{ value: this.companyInfo?.primaryGstin?.gstin || '', disabled: true }]
      }),
      credentials: this.fb.group({
        userId: ['', Validators.required],
        password: ['', Validators.required]
      }),
      config: this.fb.group({
        environment: [DEFAULT_ENVIRONMENT],
        webhookUrl: ['', [this.urlValidator]],
        // ipWhitelist: [''],
        // rateLimitTier: ['standard'],
        // additionalConfig: ['']
      })
    });

    this.loadCredentials()
  }

  // Toggle password visibility
  togglePassword() {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  // URL validator - only validates if value is provided
  urlValidator(control: AbstractControl): ValidationErrors | null {
    const value = control.value;
    if (!value || value.trim() === '') {
      return null; // Allow empty values since webhookUrl is optional
    }

    try {
      const url = new URL(value);
      if (url.protocol !== 'http:' && url.protocol !== 'https:') {
        return { invalidUrl: true };
      }
      return null;
    } catch {
      return { invalidUrl: true };
    }
  }

  createCredentials(): void {
    if (this.form.valid) {
      this.isLoading = true;
      const formData = this.form.getRawValue();

      // Prepare the payload according to CreateCredentialsRequest interface
      const payload: CreateCredentialsRequest = {
        oemId: this.oemId,
        environment: formData.config.environment,
        esakhaUserId: formData.credentials.userId,
        esakhaPassword: formData.credentials.password
      };

      // Only add webhookUrl if it's provided and valid
      if (formData.config.webhookUrl && formData.config.webhookUrl.trim() !== '') {
        payload.webhookUrl = formData.config.webhookUrl;
      }

      if(this.response && this.isCredentialsData(this.response)){
        payload.credentialId = this.response.credentialId;
      }

      this.onboardingService.createCredentials(payload).subscribe({
        next: (response: CredentialsResponse) => {
          console.log('Create credentials response:', response);
          this.isLoading = false;
          if (response.ok && response.body) {
            this.success = true;
            this.response = response.body;
            this.notificationService.success('Credentials created successfully!');
          } else {
            this.success = false;
            this.response = {
              error: 'API Error',
              message: response.message || 'Failed to create credentials',
              details: 'Please try again or contact support'
            };
            this.notificationService.error('Failed to create credentials');
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.success = false;
          this.response = {
            error: error.name || 'Network Error',
            message: error.error.message || 'Unable to connect to the server',
            details: error.message || 'Please check your internet connection and try again'
          };
          this.notificationService.error('Failed to create credentials');
          console.error('API Error:', error);
        }
      });
    } else {
      this.success = false;
      this.notificationService.error('Please fill all required fields');
      this.response = {
        error: '❌ Please fill all required fields',
        message: 'Missing required inputs',
        details: 'User ID and Password are mandatory'
      };
    }
  }

  goBackToDeployment(): void {
    this.router.navigate(['onboarding']);
    sessionStorage.setItem('onboardingStep','deployment')
  }

  downloadCredentials(): void {
    if (!this.response) return;

    const blob = new Blob(
      [JSON.stringify(this.response, null, 2)],
      { type: 'application/json' }
    );
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'asn-credentials.json';
    a.click();
    window.URL.revokeObjectURL(url);
  }

  proceedToImplementation(): void {
    this.router.navigate(['self-deployment']); // update route as needed
  }

  retry(): void {
    this.response = null;
    this.success = false;
    this.createCredentials()
  }

  loadCredentials(): void {
    this.isLoading = true;
    const credentialId = this.oemId;
    this.onboardingService.getOnboardingCredentials(credentialId).subscribe({
      next: (response) => {
          console.log('Create credentials response:', response);
          this.isLoading = false;
          if (response.ok && response.body) {
            this.success = true;
            this.response = response.body;
            this.notificationService.success('Credentials created successfully!');
          } else {
            this.success = false;
          }
        },
        error: (error) => {
          this.isLoading = false;
          this.success = false;
          this.response = {
            error: error.name || 'Network Error',
            message: error.error.message || 'Unable to connect to the server',
            details: error.message || 'Please check your internet connection and try again'
          };
          this.notificationService.error('Failed to create credentials');
          console.error('API Error:', error);
        }
    });
  }

  // Helper methods for template type checking
  isCredentialsData(response: any): response is CredentialsData {
    return response && 'credentialId' in response;
  }

  isErrorResponse(response: any): response is CredentialsErrorResponse {
    return response && 'error' in response;
  }

  // Get environment description by value
  getEnvironmentDescription(value: string): string {
    const environment = this.environments.find(env => env.value === value);
    return environment?.description || '';
  }
}
