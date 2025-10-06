import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private currentUserSubject = new BehaviorSubject<User | null>(this.getMockUser());
  public currentUser$ = this.currentUserSubject.asObservable();

  getCurrentUser(): Observable<User | null> {
    return this.currentUser$;
  }

  updateUser(user: Partial<User>): Observable<User> {
    const currentUser = this.currentUserSubject.value;
    if (currentUser) {
      const updatedUser = { ...currentUser, ...user };
      this.currentUserSubject.next(updatedUser);
      return of(updatedUser);
    }
    throw new Error('No current user');
  }

  logout(): void {
    this.currentUserSubject.next(null);
  }

  private getMockUser(): User {
    return {
      id: '1',
      companyName: 'XYZ Solutions Pvt Ltd',
      panNumber: 'DAKOU6543G',
      contactPerson: 'Aditya Mehta',
      email: 'adiinternational@gmail.com',
      phone: '+91 XXX XXX 1234',
      currentPlan: 'Basic',
      vendorCode: 'V01244',
      gstinDetails: [
        {
          gstin: '27AAEPM1234C1ZV',
          state: 'Maharashtra',
          vendorCode: 'V00254'
        },
        {
          gstin: '29AABFR7890K1ZW',
          state: 'Karnataka',
          vendorCode: 'V00675'
        }
      ]
    };
  }
}
