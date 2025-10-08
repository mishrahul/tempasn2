import { Component, Input, OnInit } from '@angular/core';
import { StepProgress } from 'src/app/models/onboarding.model';

@Component({
  selector: 'app-progress-section',
  templateUrl: './progress-section.component.html',
  styleUrls: ['./progress-section.component.scss']
})
export class ProgressSectionComponent implements OnInit {
  @Input() progress: number = 0;
  @Input() steps: StepProgress[] = [];
  @Input() cardType: string = 'Onboarding'
  @Input() title: string = 'Onboarding Progress';
  @Input() subtitle: string = 'Complete your ASN 2.1 setup journey';

  ngOnInit() {
    
  }

  getStepIconClass(step: StepProgress): string {
    switch (step?.status) {
      case 'completed': return 'step-icon-completed';
      case 'current': return 'step-icon-current';
      default: return 'step-icon-pending';
    }
  }

  getStepIconContent(step: StepProgress): string {
    if (step?.status === 'completed') return '✓';
    return step?.id?.toString();
  }
}