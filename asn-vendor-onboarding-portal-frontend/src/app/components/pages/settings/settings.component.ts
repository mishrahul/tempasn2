import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { User } from '../../../models/user.model';
import { OEMConfig } from 'src/app/models/oem.model';
import { Router } from '@angular/router';
import { OemService } from 'src/app/services/oem.service';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent implements OnInit {
  companyForm: FormGroup;
  user: User | null = null;
  loading = false;
  
  oemConfigs: Record<string, OEMConfig> = {};
  selectedOEM: string | null = null;

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private notificationService: NotificationService,
    private oemService: OemService
  ) {
    this.companyForm = this.fb.group({
      companyName: ['', Validators.required],
      panNumber: ['', Validators.required],
      contactPerson: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', Validators.required],
      vendorCode: ['']
    });
  }

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe(user => {
      this.user = user;
      if (user) {
        this.companyForm.patchValue(user);
      }
    });
    this.companyForm.get('vendorCode')?.disable()
    this.fetchOEM()
  }

  fetchOEM(){
    // this.http.get<Record<string, OEMConfig>>('/api/oems').subscribe((res) => {
    //   this.oemConfigs = res;
    //   this.selectedOEM = Object.keys(this.oemConfigs)[0] ?? null; // pehla OEM default select
    // });

    this.oemConfigs = {
      "Tata": { "shortName": "Tata Motors Limited Vendor Portal", "branding": { "asnVersion": "v2.5" } },
      "toyota": { "shortName": "Toyota Motors Limited Vendor Portal", "branding": { "asnVersion": "v3.1" } },
      "bmw": { "shortName": "BMW Motors Limited Vendor Portal", "branding": { "asnVersion": "v1.8" } }
    }
    this.selectedOEM = Object.keys(this.oemConfigs)[0] ?? null; // pehla OEM default select
  }

  saveCompanyInfo(): void {
    if (this.companyForm.valid) {
      this.loading = true;
      
      this.userService.updateUser(this.companyForm.value).subscribe({
        next: () => {
          this.notificationService.success('Company information updated successfully!');
          this.loading = false;
        },
        error: () => {
          this.notificationService.error('Failed to update company information');
          this.loading = false;
        }
      });
    }
  }

  selectOEM(oem: any) {
    this.selectedOEM = oem.key;
    console.log('oem',oem.value.shortName)
    sessionStorage.setItem('selectedOEM', oem.value.shortName);
    this.oemService.setSelectedOEM(oem.value.shortName);  // ✅ Update via service
  }

}