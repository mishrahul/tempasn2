import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';
import { PRODUCT } from 'src/app/services/_static/product-constants';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-sign-in-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './sign-in-form.component.html',
  styleUrl: './sign-in-form.component.scss'
})
export class SignInFormComponent implements OnInit {

  isLoading = false;
  signInForm: FormGroup;
  isPasswordVisible: boolean = false;

  constructor(private fb: FormBuilder, private router: Router, private authService: AuthService, private notificationService: NotificationService) {
    // Initialize the form with validations
    this.signInForm = this.fb.group({
      email: new FormControl('', [Validators.required, Validators.email]),  // Type-safe form control
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),  // Type-safe form control
      rememberMe: new FormControl(false)  // Type-safe form control
    });
  }

  ngOnInit(): void {
    const params = new URLSearchParams(window.location.search);
    const code = params.get('verifyCode');
    if (code) {
      this.verifyEmail(code)
    }
  };

  // Password visibility toggle
  togglePassword() {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  // Form submission handler
  handleSignIn() {
    if (this.signInForm.valid) {
      this.isLoading = true
      const payload = { 
        username: this.signInForm.controls['email'].value,  
        password: this.signInForm.controls['password'].value, 
        productId: PRODUCT.ID 
      };

      this.authService.signIn(payload).subscribe({
        next: (res) => {
          sessionStorage.setItem("email", this.signInForm.controls['email'].value);
          this.authService.setRequire2FA(true)
          this.router.navigate(['/2fa']);
          this.notificationService.success('Sign-In successfully!');
          this.isLoading = false
        },
        error: (error) => {
          const errorMessage = error.error?.message || error?.message || 'Something went wrong';
          this.notificationService.error(errorMessage);
          this.isLoading = false
        }
      });
    
    } else {
      this.notificationService.error('Form is invalid');
    }
  }

  verifyEmail(verifyCode: string): void {
    this.authService.varifyMail(verifyCode).subscribe({
      next: (res: any) => {
        this.notificationService.success('Email verified successfully!');
        // Optional: Redirect to login page after success
        setTimeout(() => {
          this.router.navigate(['/sign-in']);
        }, 1500);
      },
      error: (err: any) => {
        this.notificationService.error('Email verification failed');
      }
    });
  }

  // Forgot password handler (optional)
  showForgotPassword() {
    console.log('Redirect to forgot password page');
  }

  // Switch to sign-up page (optional)
  switchToSignUp() {
    this.router.navigate(['/sign-up']);
    console.log('Redirect to sign-up page');
  }

  // Terms of Service handler (optional)
  showTerms() {
    console.log('Show Terms of Service');
  }

  // Privacy Policy handler (optional)
  showPrivacy() {
    console.log('Show Privacy Policy');
  }
}