import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { map } from 'rxjs';
import { AuthService } from 'src/app/core/services/auth.service';
import { OEM, SelectedOEM } from 'src/app/models/oem.model';
import { OemService } from 'src/app/services/oem.service';
import { SettingsService } from 'src/app/services/settings.service';
import { NotificationService } from 'src/app/services/notification.service';
import { CompanyInfo } from 'src/app/models/settings.model';

@Component({
  selector: 'app-choose-oem-portal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './choose-oem-portal.component.html',
  styleUrl: './choose-oem-portal.component.scss'
})
export class ChooseOemPortalComponent implements OnInit {

  userEmail!: string | null
  userName!: string | null
  loading = false;
  error: string | null = null;
  companyInfo: CompanyInfo | null = null;

  // Using the OEM interface to define the 'oems' array
  oems: any[] = [];

  constructor(
    private router: Router, 
    private oemService: OemService, 
    private authService: AuthService,
    private settingsService: SettingsService,
    private notificationService: NotificationService,
  ) { }

  ngOnInit(): void { 
    this.userEmail = sessionStorage.getItem('email')
    this.userName = sessionStorage.getItem('username') || 'User Name'
    this.loadOEMs();
    this.getCompanyInformation()
  }

  loadOEMs(): void {
    this.loading = true;
    this.error = null;
    
    this.oemService.getAllOEMs().pipe(map(res => res.body)).subscribe({
      next: (response) => {
        console.log('OEMs:', response);
        if (response) {
          this.oems = response.oems;
        } else {
          this.oems = [];
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading OEMs:', error);
        this.error = 'Failed to load OEM portals';
        this.loading = false;
      }
    });
  }

  selectOEM(oem: OEM): void {
    if (oem.isComingSoon) {
      console.log(`${oem.fullName} is coming soon.`);
      return;
    }

    if (oem.noAccess) {
      console.log(`No access to ${oem.fullName}`);
      return;
    }

    // Validate access before navigation
    this.oemService.validateOEMAccess(oem.id).subscribe({
      next: (response) => {
        if (response.ok) {
          this.router.navigate(['dashboard']);
          const oemDeails: SelectedOEM = {
            id: oem.id,
            fullName: oem.fullName,
            oemCode: oem.oemCode,
            logoBackground: oem.logoBackground
          }
          sessionStorage.setItem('selectedOEM', JSON.stringify(oemDeails));
          this.oemService.setSelectedOEM(oemDeails);  // ✅ Update via service
          console.log(`Selected OEM: ${oem.id}`);
        } else {
          this.error = `Access validation failed: ${response.message}`;
        }
      },
      error: (error) => {
        console.error('Access validation failed:', error);
        this.error = `Access denied to ${oem.fullName}`;
      }
    });
  }

  getCompanyInformation(): void {
    this.settingsService.getCompanyInfo().subscribe({
      next: (response) => {
        // console.log('response, getCompanyInfo',response)
          this.companyInfo = response.data || null;
          sessionStorage.setItem('companyInfo', JSON.stringify(this.companyInfo));
      },
      error: (error) => {
        console.error('Error loading company info:', error);
        this.notificationService.error('Failed to load company information');
      }
    });
  }

  refreshOEMAccess(): void {
    this.loadOEMs()
  }
  
  handleLogout(){
    this.authService.logout()
  }

}
