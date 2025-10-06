import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AiConfiguration, AI_LOCAL_STORAGE_KEYS, DEFAULT_TIMEOUT } from '../models/ai.models';

@Injectable({
  providedIn: 'root'
})
export class AiConfigurationService {
  private configurationSubject = new BehaviorSubject<AiConfiguration>(this.getDefaultConfiguration());
  public configuration$ = this.configurationSubject.asObservable();

  constructor() {
    this.loadConfiguration();

    // Log configuration in development only
    if (typeof window !== 'undefined' && (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1')) {
      console.log('🔧 AI Configuration Service initialized (dev mode)');
    }
  }

  /**
   * Get current configuration
   */
  get configuration(): AiConfiguration {
    return this.configurationSubject.value;
  }

  /**
   * Update configuration
   */
  updateConfiguration(config: Partial<AiConfiguration>): void {
    const currentConfig = this.configuration;
    const newConfig: AiConfiguration = {
      ...currentConfig,
      ...config,
      isConfigured: !!(config.webhookUrl || currentConfig.webhookUrl)
    };

    this.configurationSubject.next(newConfig);
    this.saveConfiguration(newConfig);
  }

  /**
   * Reset configuration to defaults
   */
  resetConfiguration(): void {
    const defaultConfig = this.getDefaultConfiguration();
    this.configurationSubject.next(defaultConfig);
    this.clearStoredConfiguration();
  }

  /**
   * Check if configuration is valid
   */
  isConfigurationValid(): boolean {
    const config = this.configuration;
    return !!(config.webhookUrl && config.webhookUrl.trim());
  }

  /**
   * Load configuration from localStorage (safe for SSR)
   */
  private loadConfiguration(): void {
    if (typeof localStorage === 'undefined') {
      // Running on server (SSR), skip localStorage
      return;
    }

    try {
      const stored = localStorage.getItem(AI_LOCAL_STORAGE_KEYS.CONFIG);
      if (stored) {
        const config = JSON.parse(stored) as AiConfiguration;
        config.isConfigured = !!(config.webhookUrl && config.webhookUrl.trim());
        this.configurationSubject.next(config);
      }
    } catch (error) {
      console.warn('Failed to load AI configuration from localStorage:', error);
      this.resetConfiguration();
    }
  }

  /**
   * Save configuration to localStorage (safe for SSR)
   */
  private saveConfiguration(config: AiConfiguration): void {
    if (typeof localStorage === 'undefined') {
      return;
    }
    try {
      localStorage.setItem(AI_LOCAL_STORAGE_KEYS.CONFIG, JSON.stringify(config));
    } catch (error) {
      console.error('Failed to save AI configuration to localStorage:', error);
    }
  }

  /**
   * Clear stored configuration (safe for SSR)
   */
  private clearStoredConfiguration(): void {
    if (typeof localStorage === 'undefined') {
      return;
    }
    try {
      localStorage.removeItem(AI_LOCAL_STORAGE_KEYS.CONFIG);
    } catch (error) {
      console.error('Failed to clear stored AI configuration:', error);
    }
  }

  /**
   * Get default configuration
   */
  private getDefaultConfiguration(): AiConfiguration {
    return {
      webhookUrl: 'https://apl-sandbox.taxgenie.online/webhook/asn-verification',
      authHeader: '',
      timeout: DEFAULT_TIMEOUT,
      isConfigured: true
    };
  }

  /**
   * Validate webhook URL format
   */
  validateWebhookUrl(url: string): boolean {
    if (!url || !url.trim()) {
      return false;
    }

    try {
      const urlObj = new URL(url);
      return urlObj.protocol === 'http:' || urlObj.protocol === 'https:';
    } catch {
      return false;
    }
  }

  /**
   * Validate timeout value
   */
  validateTimeout(timeout: number): boolean {
    return timeout >= 5 && timeout <= 120;
  }

  /**
   * Get configuration for display (with sensitive data masked)
   */
  getDisplayConfiguration(): Partial<AiConfiguration> {
    const config = this.configuration;
    return {
      webhookUrl: config.webhookUrl,
      authHeader: config.authHeader ? '***' : '',
      timeout: config.timeout,
      isConfigured: config.isConfigured
    };
  }

  /**
   * Test webhook connectivity (development only)
   */
  async testWebhookConnectivity(): Promise<void> {
    // Only available in development
    if (typeof window === 'undefined' || window.location.hostname !== 'localhost') {
      return;
    }

    const config = this.configuration;
    if (!config.webhookUrl) {
      console.log('❌ No webhook URL configured for testing');
      return;
    }

    console.log('🧪 Testing webhook connectivity:', config.webhookUrl);

    try {
      const response = await fetch(config.webhookUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          message: 'Connectivity test',
          userId: 'test-user',
          context: 'connectivity_test',
          timestamp: new Date().toISOString()
        })
      });

      const responseText = await response.text();
      const isHtml = responseText.includes('<!DOCTYPE html>');

      console.log('🧪 Test result:', {
        status: response.status,
        isHtml: isHtml,
        responsePreview: responseText.substring(0, 100)
      });
    } catch (error) {
      console.error('🚨 Test error:', error);
    }
  }
}
