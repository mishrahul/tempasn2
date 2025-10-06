import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-self-deployment-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './self-deployment-form.component.html',
  styleUrls: ['./self-deployment-form.component.scss']
})
export class SelfDeploymentFormComponent implements OnInit {
  form!: FormGroup;
  response: any = null;
  success = false;

  // 🟢 Demo user info (replace with real auth user later)
  currentUser = {
    vendorCode: 'V123456',
    email: 'vendor@example.com'
  };

  constructor(private fb: FormBuilder, private router: Router, public notificationService: NotificationService) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      company: this.fb.group({
        vendorName: [{ value: 'Tata Vendor Pvt Ltd', disabled: true }],
        vendorCode: [{ value: 'V123456', disabled: true }],
        panNumber: [{ value: 'ABCDE1234F', disabled: true }],
        gstinNumber: [{ value: '27AAACJ9630N1ZV', disabled: true }]
      }),
      credentials: this.fb.group({
        userId: ['', Validators.required],
        password: ['', Validators.required]
      }),
      config: this.fb.group({
        environment: ['sandbox'],
        webhookUrl: ['']
      })
    });
  }

  handleCredentialsSubmission(): void {
    console.log('1111')
    if (this.form.valid) {
    console.log('2222')

      // Simulate API response (replace with real API call)
      const payload = this.form.getRawValue();

      // Demo success/failure toggle
      if (payload.credentials.userId === 'fail') {
        console.log('3333')

        this.success = false;
        this.response = {
          error: 'Invalid Credentials',
          message: 'The provided e-Sakha credentials are not valid.',
          details: 'User not found in e-Sakha system'
        };
      } else {
    console.log('4444')

        this.success = true;
        this.response = {
          developerId: 'DEV_' + this.currentUser.vendorCode,
          apiKey: 'ASN_' + Math.random().toString(36).substring(2, 10).toUpperCase(),
          clientSecret: 'SEC_' + Math.random().toString(36).substring(2, 12),
          environment: payload.config.environment,
          endpointUrl: 'https://api-tml.apigee.net/asn/v2.1/'
        };

        console.log('response',this.response)
      }
    } else {
    console.log('5555')

      this.success = false;
      this.notificationService.error('Please fill all required fields')
      this.response = {
        error: '❌ Please fill all required fields',
        message: 'Missing required inputs',
        details: 'User ID and Password are mandatory'
      };
    }
  }

  goBackToDeployment(): void {
    this.router.navigate(['onboarding']);
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
  }
}
