import { Component, Input, OnInit } from '@angular/core';

interface ProgressStep {
  id: number;
  title: string;
  description: string;
  status: 'completed' | 'current' | 'pending';
}

@Component({
  selector: 'app-progress-section',
  templateUrl: './progress-section.component.html',
  styleUrls: ['./progress-section.component.scss']
})
export class ProgressSectionComponent implements OnInit {
  @Input() progress: number = 0;
  @Input() steps: ProgressStep[] = [];
  @Input() cardType: string = 'onboarding'

  ngOnInit() {
    
  }

  getStepIconClass(step: ProgressStep): string {
    switch (step.status) {
      case 'completed': return 'step-icon-completed';
      case 'current': return 'step-icon-current';
      default: return 'step-icon-pending';
    }
  }

  getStepIconContent(step: ProgressStep): string {
    if (step.status === 'completed') return '✓';
    return step.id.toString();
  }
}