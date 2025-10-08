import { Injectable } from '@angular/core';
import { from, Observable, of } from 'rxjs';
import { RestService } from './rest.service';
import { getUrlPathFragment } from './_static/util';
import { ServerResponseWithBody } from '../models/app.models';
import { ConfirmationData, CreateCredentialsRequest, CredentialsResponse, DashboardStats, ImplementationProgress, OnboardingProgress, ProgressData, SelectDeploymentRequest } from '../models/onboarding.model';

@Injectable({
  providedIn: 'root'
})
export class OnboardingService {

  constructor(private restService: RestService) {}

  // API Methods
  getOnboardingProgress(oemId: string | null): Observable<ServerResponseWithBody<ProgressData>> {
    return this.restService.read<ServerResponseWithBody<ProgressData>>(
      getUrlPathFragment('dashboard', 'onboarding-progress', oemId)
    );
  }

  getImplementationProgress(oemId: string | null): Observable<ServerResponseWithBody<ImplementationProgress>> {
    return this.restService.read<ServerResponseWithBody<ImplementationProgress>>(
      getUrlPathFragment('dashboard', 'implementation-progress', oemId)
    );
  }

  getDashboardStats(oemId: string | null): Observable<ServerResponseWithBody<DashboardStats>> {
    return this.restService.read<ServerResponseWithBody<DashboardStats>>(
      getUrlPathFragment('dashboard', 'stats', oemId)
    );
  }


  confirmASN(): Observable<ServerResponseWithBody<any>> {
    const confirmationData = {
      timestamp: new Date().toISOString(),
      status: 'CONFIRMED'
    };

    return this.restService.post<any, ServerResponseWithBody<any>>(
      getUrlPathFragment('onboarding', 'confirm-asn'),
      confirmationData
    );
  }

   // API Methods for Onboarding Component   // --------------------------------------
  getOnboardingProgressByOEM(oemId: string | null): Observable<ServerResponseWithBody<OnboardingProgress>> {
    return this.restService.read<ServerResponseWithBody<OnboardingProgress>>(
      getUrlPathFragment('onboarding', 'progress', oemId)
    );
  }

  getOnboardingCredentials(oemId: string | null): Observable<ServerResponseWithBody<any>> {
    return this.restService.read<ServerResponseWithBody<any>>(
      getUrlPathFragment('onboarding', 'credentials', oemId)
    );
  }

  getCredentialsById(credentialId: string): Observable<CredentialsResponse> {
    return this.restService.read<CredentialsResponse>(
      getUrlPathFragment('onboarding', 'credentials', credentialId)
    );
  }

  createCredentials(payload: CreateCredentialsRequest): Observable<CredentialsResponse> {
    return this.restService.post<CreateCredentialsRequest, CredentialsResponse>(
      getUrlPathFragment('onboarding', 'create-credentials'),
      payload
    );
  }

  selectDeployment(payload: SelectDeploymentRequest): Observable<ServerResponseWithBody<any>> {
    return this.restService.post<SelectDeploymentRequest, ServerResponseWithBody<any>>(
      getUrlPathFragment('onboarding', 'select-deployment'),
      payload
    );
  }

  confirmASNOnboarding(confirmationData: ConfirmationData): Observable<ServerResponseWithBody<ConfirmationData>> {
    // const confirmationData = {
    //   timestamp: new Date().toISOString(),
    //   status: 'CONFIRMED'
    // };

    return this.restService.post<any, ServerResponseWithBody<ConfirmationData>>(
      getUrlPathFragment('onboarding', 'confirm-asn'),
      confirmationData
    );
  }

  // --------------------------------------
}
