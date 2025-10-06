import { Component } from '@angular/core';
import { NotificationService } from '../../../services/notification.service';
import { AiConfigurationService } from '../../../services/ai-configuration.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-ai-specialist',
  templateUrl: './ai-specialist.component.html',
  styleUrls: ['./ai-specialist.component.scss']
})
export class AiSpecialistComponent {
  showChatInterface = false;

  constructor(
    private notificationService: NotificationService,
    private configService: AiConfigurationService
  ) {
    // Debug functions available in development only
    if (typeof window !== 'undefined' && !environment.production) {
      (window as any).debugApiRouting = {
        testWebhook: () => this.testWebhookConnectivity(),
        showEnvironment: () => this.showEnvironmentInfo(),
        showConfig: () => this.showConfigInfo()
      };
      console.log('🔧 Debug functions available in development console');
    }
  }

  launchAISpecialist(): void {
    // Instead of opening a new window, show the integrated chat interface
    this.showChatInterface = true;
    this.notificationService.success('AI Specialist activated! Start chatting below.');
  }

  closeChatInterface(): void {
    this.showChatInterface = false;
  }

  showDemo(): void {
    this.notificationService.info('Demo: Click "Launch AI Specialist" to see the integrated chat interface in action!');
  }

  // Debug functions
  async testWebhookConnectivity(): Promise<void> {
    console.log('🧪 Starting webhook connectivity test...');
    await this.configService.testWebhookConnectivity();
  }

  showEnvironmentInfo(): void {
    console.log('🌍 Environment Information:', {
      production: environment.production,
      apiUrl: environment.apiUrl,
      corsMode: environment.corsMode,
      fullEnvironment: environment,
      currentUrl: window.location.href,
      hostname: window.location.hostname
    });
  }

  showConfigInfo(): void {
    const config = this.configService.configuration;
    console.log('🔧 Configuration Information:', {
      webhookUrl: config.webhookUrl,
      isConfigured: config.isConfigured,
      timeout: config.timeout,
      hasAuthHeader: !!config.authHeader
    });
  }
}
