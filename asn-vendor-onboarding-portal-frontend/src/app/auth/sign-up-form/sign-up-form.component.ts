import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { UppercaseDirective } from 'src/app/directives/uppercase.directive';
import { PLAN, PRODUCT, ROLE } from 'src/app/services/_static/product-constants';
import { LoginApiService } from 'src/app/services/login-service/login-api.service';
import { NotificationService } from 'src/app/services/notification.service';

@Component({
  selector: 'app-sign-up-form',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule, UppercaseDirective],
  templateUrl: './sign-up-form.component.html',
  styleUrl: './sign-up-form.component.scss'
})
export class SignUpFormComponent implements OnInit{

  signUpForm: FormGroup;
  isPasswordVisible: boolean = false;
  loginUrl!: string
  isLoading = false

  constructor(private fb: FormBuilder, private router: Router, private loginApi: LoginApiService, private notificationService: NotificationService) {
    this.signUpForm = this.fb.group({
      companyName: ['', [Validators.required]],
      panNumber: ['', [Validators.required, Validators.pattern('^[A-Z]{5}[0-9]{4}[A-Z]{1}$')]],
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      mobileNumber: ['', [Validators.required, Validators.pattern('[0-9]{10}')]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  ngOnInit(): void {
    this.loginUrl = `${window.location.origin}/`;
  }

  // Toggle password visibility
  togglePassword() {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  // Handle Sign Up submission
  handleSignUp() {
    
    if (this.signUpForm.valid) {
      this.isLoading = true
      let payload = {
        panNumber: this.signUpForm.value.panNumber,
        companyName: this.signUpForm.value.companyName,
        userName: this.signUpForm.value.firstName + ' ' +this.signUpForm.value.lastName,
        roleId: ROLE.ID,
        planId: PLAN.ID,
        emailId: this.signUpForm.value.email?.toLowerCase(),
        mobileNumber: this.signUpForm.value.mobileNumber,
        productId: PRODUCT.ID,
        password: this.signUpForm.value.password,
        fwdLink: "/sign-in",
        loginUrl: this.loginUrl
      }
      // Handle the sign-up logic, call API or whatever you need
      this.loginApi.signUp(payload).subscribe({
        next: (res: any) => {
          this.isLoading = false
          this.notificationService.success(res?.response);
          this.router.navigate(['/sign-in']);
        },
        error: (res) => {
          this.isLoading = false
          this.notificationService.error(res?.error?.message);
        }
      })
    } else {
      this.notificationService.error('Form is invalid');
      console.log('Form is invalid');
    }
  }

}