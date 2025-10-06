import { Component } from '@angular/core';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-self-deployment',
  templateUrl: './self-deployment.component.html',
  styleUrls: ['./self-deployment.component.scss']
})
export class SelfDeploymentComponent {
  
  constructor(private notificationService: NotificationService) {}

  accessTraining(): void {
    this.notificationService.info('Redirecting to training materials...');
  }

  joinWebinar(): void {
    this.notificationService.info('Webinar registration will open soon.');
  }

  bookSession(): void {
    this.notificationService.info('Specialist booking system will be available soon.');
  }
}
