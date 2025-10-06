import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import * as CryptoJS from 'crypto-js';
import { HttpClient } from '@angular/common/http';
import { ENV_VARIABLES } from 'src/app/constants/env-variables';
import { API_URL } from 'src/app/constants/api-url';
import { BehaviorSubject, Observable } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserInfoViewModel } from 'src/app/models/app.models';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class LoginApiService {

  // private readonly BASE_API_URL = 'https://gst.taxgenie.online/cam-new/api/v1/';

  constructor(private http: HttpClient, private router: Router ) {}

  private jwtHelper = new JwtHelperService();
  private authSubject = new BehaviorSubject<UserInfoViewModel | null>(null);

  private require2FASubject = new BehaviorSubject<boolean>(false);
  require2FA$ = this.require2FASubject.asObservable();

  setRequire2FA(value: boolean) {
    this.require2FASubject.next(value);
  }

  getRequire2FA(): boolean {
    return this.require2FASubject.getValue();
  }
  
  complete2FA() {
    this.setRequire2FA(false);
  }
  

  signIn(credentials: any) {
   const encryptedString = CryptoJS.AES.encrypt(JSON.stringify(credentials).trim(), ENV_VARIABLES.SECRET_KEY.trim()).toString();
    // return this.http.post(this.BASE_API_URL + API_URL.login.login, encryptedString);
    return this.http.post(`${environment.apiBaseUrl}${API_URL.login.login}`, encryptedString);
  }

  signUp(signUpObject:any) {
    return this.http.post(`${environment.apiBaseUrl}${API_URL.login.signUp}`, signUpObject);
  }

  verifyOtp(loginOtpObject:any): Observable<any>  {
    return this.http.post(`${environment.apiBaseUrl}${API_URL.login.loginOtpCheck}`, loginOtpObject);
  }

  varifyMail(varifyCode: any) {
    return this.http.get(`${environment.apiBaseUrl}${API_URL.login.verifyMail}?verifyCode=${varifyCode}`);
  }

  resendOtp(resendOtpRequest:any) {
    return this.http.post(`${environment.apiBaseUrl}${API_URL.login.resendOtp}`, resendOtpRequest);
  }

  setJwt(jwt: string): boolean {
    const expired = this.jwtHelper.isTokenExpired(jwt);
    if (!expired) {
      sessionStorage.setItem('pxp_token', jwt);
      const viewModel = this.jwtHelper.decodeToken<UserInfoViewModel>(
        jwt
      );

      this.authSubject.next(viewModel);
    }
    return expired;
  }

  /**
   * Log out user by clearing the token
   */
   logout(): void {
    // Clear all local storage
    localStorage.clear();

    // Optional: Clear sessionStorage too if used
    sessionStorage.clear();

    // Navigate to login
    this.router.navigate(['/sign-in']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }


}
