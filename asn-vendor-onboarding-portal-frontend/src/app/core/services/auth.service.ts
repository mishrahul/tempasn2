import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, throwError, BehaviorSubject } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { API_URL } from 'src/app/constants/api-url';
import { ENV_VARIABLES } from 'src/app/constants/env-variables';
import { UserInfoViewModel } from 'src/app/models/app.models';
import * as CryptoJS from 'crypto-js';

interface LoginResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
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


  
  // private readonly TOKEN_KEY = 'auth_token';

  // constructor(private http: HttpClient) {}

  // /**
  //  * Send login request to backend
  //  */
  // login(username: string, password: string): Observable<LoginResponse> {
  //   return this.http.post<LoginResponse>(`${environment.apiUrl}/login`, { username, password }).pipe(
  //     tap((res) => {
  //       sessionStorage.setItem(this.TOKEN_KEY, res.token);
  //     }),
  //     catchError((error) => {
  //       console.error('Login error:', error);
  //       return throwError(() => new Error('Login failed. Please try again.'));
  //     })
  //   );
  // }

  // /**
  //  * Log out user by clearing the token
  //  */
  // logout(): void {
  //   sessionStorage.removeItem(this.TOKEN_KEY);
  // }

  // /**
  //  * Check if user is authenticated (token exists and not expired)
  //  */
  // isAuthenticated(): boolean {
  //   const token = this.getToken();
  //   if (!token) return false;

  //   const payload = this.decodeToken(token);
  //   if (!payload || !payload.exp) return false;

  //   // Check expiration (exp is in seconds)
  //   return payload.exp * 1000 > Date.now();
  // }

  // /**
  //  * Get saved auth token
  //  */
  // getToken(): string | null {
  //   return sessionStorage.getItem(this.TOKEN_KEY);
  // }

  // /**
  //  * Decode JWT token payload
  //  */
  // private decodeToken(token: string): any | null {
  //   try {
  //     const payload = token.split('.')[1];
  //     const decoded = atob(payload);
  //     return JSON.parse(decoded);
  //   } catch (e) {
  //     console.warn('Invalid token:', e);
  //     return null;
  //   }
  // }
}
