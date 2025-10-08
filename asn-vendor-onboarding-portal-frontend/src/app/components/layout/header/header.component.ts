import { Component, HostListener, OnInit } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { OemService } from 'src/app/services/oem.service';
import { AuthService } from 'src/app/core/services/auth.service';
import { SelectedOEM } from 'src/app/models/oem.model';
import { CompanyInfo } from 'src/app/models/settings.model';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  selectedOEM: SelectedOEM | null = null;
  showUserMenu = false;
  companyInfo: CompanyInfo | null = null;
  currentPlan: string | null = null;

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private oemService: OemService,
  ) {}

  ngOnInit(): void {
    
    this.companyInfo = JSON.parse(sessionStorage.getItem('companyInfo') || '{}');

    this.userService.currentPlan$.subscribe(plan => {
      this.currentPlan = plan;
    });

    this.oemService.selectedOEM$.subscribe((oemJson: string | null) => {
      if (oemJson) {
        this.selectedOEM = JSON.parse(oemJson);
      } else {
        this.selectedOEM = null;
      }
    });

    this.selectedOEM = JSON.parse(sessionStorage.getItem('selectedOEM') || '{}');
    this.currentPlan = sessionStorage.getItem('currentPlan');
  }

  getUserInitials(): string {
    if (!this.companyInfo || !this.companyInfo.companyName) return 'NA';
    const words = this.companyInfo.companyName.split(' ');
    return words.slice(0, 2).map(word => word[0]).join('').toUpperCase();
  }

  toggleUserMenu(): void {
    this.showUserMenu = !this.showUserMenu;
  }

  closeUserMenu(): void {
    this.showUserMenu = false;
  }

  logout(): void {
    this.authService.logout();
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