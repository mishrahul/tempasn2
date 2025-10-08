// src/app/components/two-factor-form/two-factor-form.component.ts
import { CommonModule } from '@angular/common';
import { Component, ViewChild } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NgOtpInputModule } from 'ng-otp-input';
import { NotificationService } from '../../services/notification.service';
import { PRODUCT } from 'src/app/services/_static/product-constants';
import { map } from 'rxjs';
import { AuthService } from 'src/app/core/services/auth.service';
import { IVerificationOTPRequest } from 'src/app/models/app.models';

@Component({
  selector: 'app-two-factor-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgOtpInputModule],
  templateUrl: './two-factor-form.component.html',
  styleUrl: './two-factor-form.component.scss',
})
export class TwoFactorFormComponent {
  otp: string = '';
  resendIn = 0;
  email!: string;
  isLoading = false;
  @ViewChild('otpInput') otpInput: any;

  constructor(
    private router: Router,
    private authService: AuthService,
    private notificationService: NotificationService,
  ) {
    this.email = sessionStorage.getItem('email') || ''
  }

  otpConfig = {
    length: 6,
    inputClass: 'otp-input',
    allowNumbersOnly: true,
    isPasswordInput: false,
    disableAutoFocus: false,
    placeholder: '',
  };

  onOtpChange(value: string) {
    this.otp = value;
  }

  onSubmit(event: Event) {
    event.preventDefault();

    if (this.otp?.length !== 6) return;

    this.isLoading = true

    const payload: IVerificationOTPRequest = {
      username: this.email,
      otp: this.otp,
      productId: PRODUCT.ID,
      jwt: ''
    };

    this.authService.verifyOtp(payload)
    .pipe(
      map((v) => {
        return this.authService.setJwt(v.jwt);
      })
    )
    .subscribe({
      next: (res) => {
        this.isLoading = false
        this.notificationService.success('OTP verified!');
        this.router.navigate(['/oem-portals']);
      },
      error: (err) => {
        this.isLoading = false
        this.notificationService.error(err.error?.message || 'Invalid OTP');
        this.clearOtp()
        this.otp = '';
      }
    });
  }

  resendOTP() {
    if (this.resendIn > 0) return;
    const payload = {
      username: this.email,
      jwt: "",
      productId: 31,
      otp: ""
    };

    // You can call resend API here
    this.authService.resendOtp(payload).subscribe({
      next: () => {
        this.notificationService.success('OTP resent successfully');
        this.startResendTimer(30); // 30 seconds timer
      },
      error: (err) => {
        this.notificationService.error(err.error?.message || 'Failed to resend OTP');
      }
    });
    
  }

  private startResendTimer(seconds: number) {
    this.resendIn = seconds;
    const interval = setInterval(() => {
      this.resendIn--;
      if (this.resendIn <= 0) clearInterval(interval);
    }, 1000);
  }

  clearOtp() {
    if(this.otpInput) {
      this.otpInput.setValue('');
    }
  }

  switchToSignIn() {
    this.router.navigate(['/sign-in']);
  }
}
