import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-alert',
  template: `
    <div class="alert" [ngClass]="'alert-' + type">
      <span class="alert-icon">{{getIcon()}}</span>
      <div>
        <ng-content></ng-content>
        <div *ngIf="message"><span *ngIf="note" class="fw-bold">{{note + ' : '}}</span>{{message}}</div>
      </div>
    </div>
  `,
  styleUrls: ['./alert.component.scss']
})
export class AlertComponent {
  @Input() type: 'warning' | 'info' | 'success' | 'error' = 'info';
  @Input() message: string = '';
  @Input() note: string = '';
  @Input() icon: any = '';

  getIcon(): string {
    if (this.icon) {
      return this.icon;
    }
    switch (this.type) {
      case 'warning': return '⚠️';
      case 'success': return '✅';
      case 'error': return '❌';
      default: return '💡';
    }
  }
}
