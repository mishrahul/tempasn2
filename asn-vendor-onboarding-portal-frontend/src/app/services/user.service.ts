import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { SettingsService } from './settings.service';
import { CompanyInfo } from '../models/settings.model';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  // private companyInfoSubject = new BehaviorSubject<CompanyInfo | null>(null);
  // public companyInfo$ = this.companyInfoSubject.asObservable();
  
  private currentPlanSubject = new BehaviorSubject<string | null>(null);
  public currentPlan$ = this.currentPlanSubject.asObservable();

  constructor(private settingsService: SettingsService) {}

  setCurrentPlan(plan: string): void {
    this.currentPlanSubject.next(plan);
    sessionStorage.setItem('currentPlan', plan);
  }

  // getCurrentPlan(): string | null {
  //   return this.currentPlanSubject.value;
  // }

  // /**
  //  * Load company info from API only once, then cache in BehaviorSubject
  //  */
  // loadCompanyInfo(forceReload: boolean = false): void {
  //   if (!forceReload && this.companyInfoSubject.value) {
  //     // Already loaded, skip API call
  //     return;
  //   }

  //   this.settingsService.getCompanyInfo().subscribe({
  //     next: (response) => {
  //       this.companyInfoSubject.next(response.data || null);
  //     },
  //     error: (error) => {
  //       console.error('Error loading company info:', error);
  //       this.companyInfoSubject.next(null);
  //     }
  //   });
  // }

  // /**
  //  * Get the latest company info synchronously (if already loaded)
  //  */
  // getCompanyInfoSync(): CompanyInfo | null {
  //   return this.companyInfoSubject.value;
  // }

  // /**
  //  * Clear cached company info (e.g. on logout)
  //  */
  // clearCompanyInfo(): void {
  //   this.companyInfoSubject.next(null);
  // }
}
