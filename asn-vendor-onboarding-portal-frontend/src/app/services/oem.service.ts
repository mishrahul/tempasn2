// src/app/services/oem.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OemService {
  private selectedOEMSubject = new BehaviorSubject<string | null>(null);
  selectedOEM$ = this.selectedOEMSubject.asObservable();

  setSelectedOEM(oem: string) {
    sessionStorage.setItem('selectedOEM', oem);
    this.selectedOEMSubject.next(oem);
  }

  getSelectedOEM(): string | null {
    return sessionStorage.getItem('selectedOEM');
  }

  // Optional: load from sessionStorage on app start
  loadInitialOEM() {
    const oem = sessionStorage.getItem('selectedOEM');
    this.selectedOEMSubject.next(oem);
  }
}
