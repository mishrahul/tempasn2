import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Plan } from '../../../models/plan.model';

@Component({
  selector: 'app-plan-card',
  template: `
    <div class="plan-card" [ngClass]="{'featured': plan.recommended}">
      <div class="plan-header">
        <h3 class="plan-name">{{plan.name}}</h3>
        <div class="plan-price">
          <span class="currency">₹</span>{{plan.price | number}}
        </div>
        <div class="plan-period">{{plan.period}} + ₹{{plan.setupFee | number}} setup</div>
      </div>
      
      <ul class="plan-features">
        <li class="feature-item" *ngFor="let feature of plan.features">
          <span class="feature-icon" [ngClass]="{'unavailable': !feature.included}">
            {{feature.included ? '✓' : '✗'}}
          </span>
          <span>{{feature.name}}</span>
        </li>
      </ul>
      
      <button 
        class="btn btn-full"
        [ngClass]="isCurrentPlan ? 'btn-secondary' : 'btn-primary'"
        [disabled]="isCurrentPlan || loading"
        (click)="onUpgrade()">
        <span *ngIf="loading" class="spinner"></span>
        {{getButtonText()}}
      </button>
    </div>
  `,
  styleUrls: ['./plan-card.component.scss']
})
export class PlanCardComponent {
  @Input() plan!: Plan;
  @Input() isCurrentPlan: boolean = false;
  @Input() loading: boolean = false;
  @Output() upgrade = new EventEmitter<string>();

  onUpgrade(): void {
    if (!this.isCurrentPlan && !this.loading) {
      this.upgrade.emit(this.plan.id);
    }
  }

  getButtonText(): string {
    if (this.isCurrentPlan) return 'Current Plan';
    if (this.loading) return 'Processing...';
    return `Upgrade to ${this.plan.name}`;
  }
}
