// src/app/components/layout/header/header.component.ts
import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user.model';
import { OemService } from 'src/app/services/oem.service';
import { LoginApiService } from 'src/app/services/login-service/login-api.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  user: User | null = null;
  selectedOEM: string | null = null;
  showUserMenu = false;

  constructor(
    private userService: UserService,
    private loginApiService: LoginApiService,
    private router: Router,
    private oemService: OemService
  ) {}

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe(user => {
      this.user = user;
    });

    this.oemService.selectedOEM$.subscribe((oem: any) => {
      this.selectedOEM = oem;
    });

    this.selectedOEM = sessionStorage.getItem('selectedOEM');

    this.user = {
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
    }
  }

  getUserInitials(): string {
    if (!this.user) return 'NA';
    const words = this.user.companyName.split(' ');
    return words.slice(0, 2).map(word => word[0]).join('').toUpperCase();
  }

  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
  }

  closeUserMenu(): void {
    this.showUserMenu = false;
  }

  logout(): void {
    this.loginApiService.logout();
  }

  
  @HostListener('document:click', ['$event'])
  onClick(event: Event) {
    const target = event.target as HTMLElement;
    const insideMenu = target.closest('.user-menu');
    if (!insideMenu) {
      this.showUserMenu = false;
    }
  }
}