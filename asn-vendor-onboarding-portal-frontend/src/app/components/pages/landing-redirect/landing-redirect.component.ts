import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { hasSelectedOEM, isAuthenticated } from '../../shared/utils/auth-utils';

@Component({
  selector: 'app-landing-redirect',
  standalone: true,
  imports: [CommonModule],
  template: '',
  styles: ''
})
export class LandingRedirectComponent implements OnInit {
  
  private router = inject(Router);

  ngOnInit() {
    if (!isAuthenticated() && !hasSelectedOEM()) {
      this.router.navigate(['/sign-in']);
    } else if (!hasSelectedOEM()) {
      this.router.navigate(['/oem-portals']);
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

}
