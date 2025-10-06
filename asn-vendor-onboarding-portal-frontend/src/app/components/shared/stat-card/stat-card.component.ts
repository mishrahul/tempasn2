// src/app/components/shared/stat-card/stat-card.component.ts
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-stat-card',
  template: `
    <div class="stat-card">
      <div class="stat-header">
        <div class="stat-icon" [style.background]="iconColor">{{icon}}</div>
      </div>
      <div class="stat-value">{{value}}</div>
      <div class="stat-label">{{label}}</div>
    </div>
  `,
  styleUrls: ['./stat-card.component.scss']
})
export class StatCardComponent {
  @Input() value: string = '';
  @Input() label: string = '';
  @Input() icon: string = '';
  @Input() iconColor: string = 'var(--taxgenie-primary)';
}

// src/app/components/shared/stat-card/stat-card.component.scss