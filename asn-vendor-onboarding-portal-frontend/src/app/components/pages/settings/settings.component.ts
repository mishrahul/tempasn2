import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { NotificationService } from '../../../services/notification.service';
import { GSTINDetail } from '../../../models/user.model';
import { OemService } from 'src/app/services/oem.service';
import { SettingsService } from 'src/app/services/settings.service';
import { BaseSubscription, CompanyInfo, CRUDRequest, CurrentSubscription, SubscriptionBilling } from 'src/app/models/settings.model';
import { ConfirmationDialogService } from 'src/app/services/confirmation-dialog.service';
import { MatDialog } from '@angular/material/dialog';
import { AddGstinComponent } from '../../shared/dialog-boxes/add-gstin/add-gstin.component';
import { map } from 'rxjs';
import { SelectedOEM } from 'src/app/models/oem.model';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent implements OnInit {
  companyForm: FormGroup;
  saveLoading: boolean = false;
  companyInfo: CompanyInfo | null = null;
  
  oemConfigs: any[] = [];
  selectedOEM: any | null = null;
  isAccountActive: boolean = true;

  // API Data
  gstinDetails: GSTINDetail[] = [];
  subscriptionInfo: CurrentSubscription = {} as CurrentSubscription;
  

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private notificationService: NotificationService,
    private oemService: OemService,
    private settingsService: SettingsService,
    private confirmationService: ConfirmationDialogService,
    private dialog: MatDialog
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
    this.companyInfo = JSON.parse(sessionStorage.getItem('companyInfo') || '{}');
    this.selectedOEM = JSON.parse(sessionStorage.getItem('selectedOEM') || '{}');

    this.companyForm.patchValue(this.companyInfo || {});
    this.companyForm.get('vendorCode')?.disable()
    this.fetchOEM()
    this.getGSTINDetails()
    this.loadSettingsData();
  }

  loadSettingsData(): void {
    // Load all settings data
    // this.loadCompanyInfo();
    this.loadSubscriptionInfo();
  }

  loadCompanyInfo(): void {
    this.settingsService.getCompanyInfo().subscribe({
      next: (response) => {
          this.companyForm.patchValue(response.data || {});
        // }
      },
      error: (error) => {
        console.error('Error loading company info:', error);
        this.notificationService.error('Failed to load company information');
      }
    });
  }

  getGSTINDetails(): void {
    this.settingsService.getGSTINManagement().subscribe({
      next: (response: any) => {
        if (response) {
          console.log('response, getGSTINManagement1',response.data)
          this.gstinDetails = this.sortPrimaryOnTop(response.data.gstinDetails);
        }
      },
      error: (error) => {
        console.error('Error loading GSTIN details:', error);
        this.notificationService.error('Failed to load GSTIN details');
      }
    });
  }

  sortPrimaryOnTop(data: any[]) {
    return data.sort((a, b) => {
      return Number(b.primary === true) - Number(a.primary === true);
    });
  }

  loadSubscriptionInfo(): void {
    this.settingsService.getSubscriptionBilling().subscribe({
      next: (response) => {
        console.log('Full subscription response structure:', response);
        const responseData = response.data || {} as SubscriptionBilling;
        this.subscriptionInfo = responseData.currentSubscription || {} as BaseSubscription;

      },
      error: (error) => {
        console.error('Error loading subscription info:', error);
        this.notificationService.error('Failed to load subscription information');
      }
    });
  }

  saveCompanyInfo(): void {
    if (this.companyForm.valid) {
      this.saveLoading = true;
      
      this.settingsService.updateCompanyInfo(this.companyForm.value).subscribe({
        next: (response) => {
          this.notificationService.success(response?.message || 'Company information updated successfully!');
          this.saveLoading = false;
          this.companyInfo = response.data || null;
          sessionStorage.setItem('companyInfo', JSON.stringify(this.companyInfo));
        },
        error: (error) => {
          console.error('Error updating company info:', error);
          this.notificationService.error('Failed to update company information');
          this.saveLoading = false;
        }
      });
    }
  }

  openAddEditGSTINModal(isEdit: boolean, editData: any = {}): void {
    console.log('editData',editData)
    const dialogRef = this.dialog.open(AddGstinComponent, {
      width: '500px',
      backdropClass: 'blurred-backdrop',
      data: { ...editData, isEditMode: isEdit }
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        console.log('result',result)
        isEdit ? this.editGSTIN(result) : this.addNewGSTIN(result)
      }
    });
  }

  addNewGSTIN(gstinData: any): void {
    const payload: CRUDRequest = {
      gstin: gstinData.gstin,
      stateCode: gstinData.stateCode,
      primary: gstinData.primary
    }

    this.settingsService.createGSTIN(payload).subscribe({
      next: (response) => {
        this.notificationService.success(response?.message || 'GSTIN added successfully!');
        this.getGSTINDetails(); // Reload list
      },
      error: (error) => {
        console.error('Error adding GSTIN:', error);
        this.notificationService.error('Failed to add GSTIN');
      }
    });
  }

  editGSTIN(editData: any): void {
    console.log('editData',editData)
    const gstinId = editData.id;
    const payload: CRUDRequest = {
      vendorCode: editData.vendorCode,
      primary: editData.primary
    }
    this.settingsService.updateGSTIN(gstinId, payload).subscribe({
      next: (response) => {
        this.notificationService.success( response?.message || 'GSTIN updated successfully!');
        this.getGSTINDetails(); // Reload list
      },
      error: (error) => {
        console.error('Error updating GSTIN:', error);
        this.notificationService.error('Failed to update GSTIN');
      }
    });
  }
  
  deleteGSTIN(editData: any): void {
    console.log('editData',editData)
    const gstinId = editData.id;
    const payload: CRUDRequest = {
      vendorCode: editData.vendorCode,
      primary: editData.primary
    }

    this.confirmationService.confirmDelete('GSTIN').subscribe(confirmed => {
      if (confirmed) {
        this.settingsService.deleteGSTIN(gstinId, payload).subscribe({
          next: (response) => {
            this.notificationService.success(response?.message ||'GSTIN deleted successfully!');
            this.getGSTINDetails();
          },
          error: (error) => {
            console.error('Error deleting GSTIN:', error);
            this.notificationService.error(error?.error?.message || 'Failed to delete GSTIN');
          }
        });
      }
    });
  }

  fetchOEM(){
    this.oemService.getAllOEMs().pipe(map(res => res.body)).subscribe({
      next: (response) => {
        console.log('OEMs:', response);
        if (response) {
          this.oemConfigs = response.oems;
        } else {
          this.oemConfigs = [];
        }
      },
      error: (error) => {
        this.notificationService.error(error.error?.message || 'Failed to load OEMs');
      }
    });
  }

  selectOEM(oem: any) {
    console.log('oem',oem) 
    const oemDeails: SelectedOEM = {
      id: oem.id,
      fullName: oem.fullName,
      oemCode: oem.oemCode,
      logoBackground: oem.logoBackground
    }
    this.selectedOEM = oemDeails;
    sessionStorage.setItem('selectedOEM', JSON.stringify(oemDeails));
    this.oemService.setSelectedOEM(oemDeails);  // ✅ Update via service
  }

}
