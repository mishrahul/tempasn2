// src/app/components/layout/sidebar/sidebar.component.ts
import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

interface MenuItem {
  path: string;
  label: string;
  icon: string;
  exact?: boolean;
}

@Component({
  selector: 'app-sidebar',
  template: `
    <aside class="sidebar">
      <nav class="sidebar-nav">
        <ul>
          <li class="sidebar-item" *ngFor="let item of menuItems">
            <a 
              [routerLink]="item.path" 
              class="sidebar-link"
              routerLinkActive="active"
              [routerLinkActiveOptions]="{exact: item.exact || false}">
              <span class="sidebar-icon">{{item.icon}}</span>
              {{item.label}}
            </a>
          </li>
        </ul>
      </nav>
    </aside>
  `,
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  menuItems: MenuItem[] = [
    { path: '/dashboard', label: 'Dashboard', icon: '🏠', exact: true },
    { path: '/onboarding', label: 'Onboarding', icon: '🚀' },
    { path: '/plans', label: 'Subscription Plans', icon: '📊' },
    { path: '/self-deployment', label: 'Self-Deployment Support', icon: '🛠️' },
    { path: '/ai-specialist', label: 'ASN AI Specialist', icon: '🤖' },
    { path: '/settings', label: 'Settings', icon: '⚙️' }
  ];

  constructor(private router: Router) {
    // Scroll to top on route change
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      window.scrollTo(0, 0);
    });
  }
}
      