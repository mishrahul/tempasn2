import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_URL } from '../constants/api-url';
// 👇 Correct import
import * as CryptoJS from 'crypto-js';
import { ENV_VARIABLES } from '../constants/env-variables';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class LoginServiceService {
 // 🔑 Directly define base API URL here
  // private readonly BASE_API_URL = 'https://gst.taxgenie.online/cam-new/api/v1/';

  constructor(private http: HttpClient) {}

  signIn(credentials: any) {
   const encryptedString = CryptoJS.AES.encrypt(JSON.stringify(credentials).trim(), ENV_VARIABLES.SECRET_KEY.trim()).toString();
    // return this.http.post(this.BASE_API_URL + API_URL.login.login, encryptedString);
    return this.http.post(`${environment.apiBaseUrl}${API_URL.login.login}`, encryptedString);
  }

  signUp(signUpObject:any) {
    return this.http.post(`${environment.apiBaseUrl}${API_URL.login.signUp}`, signUpObject);
  }

   loginOtpCheck(loginOtpObject:any) {
    return this.http.post(`${environment.apiBaseUrl}${API_URL.login.loginOtpCheck}`, loginOtpObject);
  }

  verifyMail(verifyCode: any) {
    return this.http.get(`${environment.apiBaseUrl}${API_URL.login.verifyMail}?verifyCode=${verifyCode}`);
  }

  resendOtp(resendOtpRequest:any) {
    return this.http.post(`${environment.apiBaseUrl}${API_URL.login.resendOtp}`, resendOtpRequest);
  }
}
