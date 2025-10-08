import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RestService } from './rest.service';
import { getUrlPathFragment } from './_static/util';
import { ServerResponseWithBody } from '../models/app.models';
import { CompanyInfo, CRUDRequest, SubscriptionBilling } from '../models/settings.model';
import { GSTINDetail } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {

  constructor(private restService: RestService) {}

  // GSTIN Management
  getGSTINManagement(): Observable<ServerResponseWithBody<GSTINDetail[]>> {
    return this.restService.read<ServerResponseWithBody<GSTINDetail[]>>(
      getUrlPathFragment('settings', 'gstin-management')
    );
  }

  createGSTIN(gstinData: CRUDRequest): Observable<ServerResponseWithBody<GSTINDetail>> {
    return this.restService.post<CRUDRequest, ServerResponseWithBody<GSTINDetail>>(
      getUrlPathFragment('settings', 'gstin'),
      gstinData
    );
  }

  updateGSTIN(gstinId: string, gstinData: CRUDRequest): Observable<ServerResponseWithBody<GSTINDetail>> {
    return this.restService.put<CRUDRequest, ServerResponseWithBody<GSTINDetail>>(
      getUrlPathFragment('settings', 'gstin', gstinId),
      gstinData
    );
  }

  deleteGSTIN(gstinId: string, gstinData: CRUDRequest): Observable<ServerResponseWithBody<GSTINDetail>> {
    return this.restService.deleteWithBody<ServerResponseWithBody<GSTINDetail>>(
      getUrlPathFragment('settings', 'gstin', gstinId),
      gstinData
    );
  }

  // Company Information
  // getCompanyInfo(): Observable<ServerResponseWithBody<CompanyInfo>> {
  //   return this.restService.read<ServerResponseWithBody<CompanyInfo>>(
  //     getUrlPathFragment('settings', 'company-info')
  //   );
  // }

  // updateCompanyInfo(companyData: Partial<CompanyInfo>): Observable<ServerResponseWithBody<CompanyInfo>> {
  //   return this.restService.put<Partial<CompanyInfo>, ServerResponseWithBody<CompanyInfo>>(
  //     getUrlPathFragment('settings', 'company-info'),
  //     companyData
  //   );
  // }

  getCompanyInfo(): Observable<ServerResponseWithBody<CompanyInfo>> {
    return this.restService.read<ServerResponseWithBody<CompanyInfo>>(
      getUrlPathFragment('settings', 'company-info')
    );
  }


  updateCompanyInfo(companyData: Partial<CompanyInfo>): Observable<ServerResponseWithBody<CompanyInfo>> {
    return this.restService.put<Partial<CompanyInfo>, ServerResponseWithBody<CompanyInfo>>(
      getUrlPathFragment('settings', 'company-info'),
      companyData
    );
  }

  // Subscription & Billing
  getSubscriptionBilling(): Observable<ServerResponseWithBody<SubscriptionBilling>> {
    return this.restService.read<ServerResponseWithBody<SubscriptionBilling>>(
      getUrlPathFragment('settings', 'subscription-billing')
    );
  }
  
  // getSubscriptionPlans(): Observable<ServerResponseWithBody<SubscriptionBilling>> {
  //   return this.restService.read<ServerResponseWithBody<SubscriptionBilling>>(
  //     getUrlPathFragment('subscription-plans')
  //   );
  // }

  getOnboardingPlans(): Observable<ServerResponseWithBody<SubscriptionBilling>> {
    return this.restService.read<ServerResponseWithBody<SubscriptionBilling>>(
      getUrlPathFragment('onboarding','plans')
    );
  }

  // settings/subscription-plans
}