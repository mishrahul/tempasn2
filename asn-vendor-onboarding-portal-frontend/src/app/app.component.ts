import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  template: `
    <div class="app-container">
      <app-header *ngIf="!isSignInRoute"></app-header>
      
      <div class="main-container" *ngIf="!isSignInRoute">
        <app-sidebar></app-sidebar>
        <main class="main-content">
          <router-outlet></router-outlet>
        </main>
      </div>
      <!-- Render this for the SignIn Route -->
      <router-outlet *ngIf="isSignInRoute"></router-outlet>
    </div>
  `,
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'TaxGenie Vendor Portal';
  isSignInRoute: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.router.events.subscribe(() => {
      this.isSignInRoute = this.router.url.includes('/sign-in') || this.router.url.includes('/sign-up') || this.router.url.includes('/oem-portals') || this.router.url.includes('/2fa');
    });
  }
}
