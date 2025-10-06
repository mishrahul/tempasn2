import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { OEM } from 'src/app/models/oem.model';
import { LoginApiService } from 'src/app/services/login-service/login-api.service';
import { OemService } from 'src/app/services/oem.service';

@Component({
  selector: 'app-choose-oem-portal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './choose-oem-portal.component.html',
  styleUrl: './choose-oem-portal.component.scss'
})
export class ChooseOemPortalComponent {

  userEmail!: string | null
  userName!:string | null

  // Using the OEM interface to define the 'oems' array
  oems: OEM[] = [
    {
      id: 'TML',
      logoBackground: 'linear-gradient(135deg, #1f4e79, #2563eb)',
      shortName: 'TML',
      fullName: 'Tata Motors Limited',
      features: [
        '✅ ASN 2.1 Implementation',
        '✅ Advanced Shipping Notice',
        '✅ ERP Integration Support',
        '✅ Compliance Management'
      ]
    },
    {
      id: 'MAHINDRA',
      logoBackground: 'linear-gradient(135deg, #c41e3a, #8b0000)',
      shortName: 'M&M',
      fullName: 'Mahindra & Mahindra Limited',
      features: [
        '✅ ASN Implementation',
        '✅ Vendor Portal Integration',
        '✅ Supply Chain Management',
        '✅ Real-time Tracking'
      ],
    },
    {
      id: 'TAFE',
      logoBackground: 'linear-gradient(135deg, #ff6b35, #f7931e)',
      shortName: 'TAFE',
      fullName: 'Tractors and Farm Equipment Limited',
      features: [
        '✅ ASN Integration',
        '✅ Agricultural Equipment',
        '✅ Supplier Management',
        '✅ Inventory Optimization'
      ],
      noAccess: true   // 👈 This OEM exists but is locked
    },
    {
      id: 'COMING_SOON',
      logoBackground: 'linear-gradient(135deg, #6b7280, #9ca3af)',
      shortName: '+',
      fullName: 'More OEMs',
      features: [
        '• Bajaj Auto',
        '• Hero MotoCorp',
        '• TVS Motors',
        '• Force Motors'
      ],
      isComingSoon: true
    }
  ];

  constructor(private router: Router, private oemService: OemService, private loginApiService: LoginApiService) { }

  ngOnInit(): void { 
    this.userEmail = sessionStorage.getItem('email')
    this.userName = sessionStorage.getItem('username') || 'User Name'
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

    // this.router.navigate(['dashboard'], {
    //   state: { selectedOEM: oem.fullName }
    // });
    this.router.navigate(['dashboard']);
    sessionStorage.setItem('selectedOEM', oem.fullName);
    this.oemService.setSelectedOEM(oem.fullName);  // ✅ Update via service
    console.log(`Selected OEM: ${oem.id}`);
  }

  handleLogout(){
    this.loginApiService.logout()
  }

  refreshOEMAccess(){

  }
}