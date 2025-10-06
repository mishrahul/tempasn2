import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, delay, of } from 'rxjs';
import { v4 as uuidv4 } from 'uuid';
import { marked } from 'marked';
import { 
  AiMessage, 
  MessageSender, 
  AI_DEMO_RESPONSES, 
  AI_ANIMATION_DURATIONS 
} from '../models/ai.models';
import { AiApiService } from './ai-api.service';
import { AiConfigurationService } from './ai-configuration.service';

@Injectable({
  providedIn: 'root'
})
export class AiChatService {
  private messagesSubject = new BehaviorSubject<AiMessage[]>([]);
  private isTypingSubject = new BehaviorSubject<boolean>(false);
  private hasMessagesSubject = new BehaviorSubject<boolean>(false);

  public messages$ = this.messagesSubject.asObservable();
  public isTyping$ = this.isTypingSubject.asObservable();
  public hasMessages$ = this.hasMessagesSubject.asObservable();

  constructor(
    private apiService: AiApiService,
    private configService: AiConfigurationService
  ) {
    // Configure marked options
    marked.setOptions({
      breaks: true,
      gfm: true
    });
  }

  /**
   * Get current messages
   */
  get messages(): AiMessage[] {
    return this.messagesSubject.value;
  }

  /**
   * Get typing state
   */
  get isTyping(): boolean {
    return this.isTypingSubject.value;
  }

  /**
   * Add a new message
   */
  addMessage(sender: MessageSender, content: string): void {
    const message: AiMessage = {
      id: uuidv4(),
      sender,
      content,
      timestamp: new Date()
    };

    const currentMessages = this.messages;
    const updatedMessages = [...currentMessages, message];
    
    this.messagesSubject.next(updatedMessages);
    this.hasMessagesSubject.next(true);
  }

  /**
   * Send user message and get AI response
   */
  async sendMessage(content: string): Promise<void> {
    if (!content.trim() || this.isTyping) {
      return;
    }

    // Add user message
    this.addMessage(MessageSender.USER, content);

    // Show typing indicator
    this.setTyping(true);

    try {
      const config = this.configService.configuration;
      let response: string;

      console.log('🔍 DEBUG: config.isConfigured =', config.isConfigured);
      console.log('🔍 DEBUG: config.webhookUrl =', config.webhookUrl);
      console.log('🔍 DEBUG: Full config =', config);

      if (config.isConfigured && config.webhookUrl) {
        // Use real API
        console.log('🔗 Using PRODUCTION MODE - Sending to n8n workflow:', content.substring(0, 50) + '...');
        try {
          const apiResponse = await this.apiService.sendMessage(content, config).toPromise();
          response = this.apiService.extractResponseContent(apiResponse);
          console.log('✅ Received response from n8n:', response.substring(0, 100) + '...');
        } catch (apiError) {
          console.error('❌ API Error:', apiError);

          // Check if we should fallback to demo mode
          const errorMessage = apiError instanceof Error ? apiError.message : 'Unknown error';

          if (errorMessage.includes('Configuration error') ||
              errorMessage.includes('Network error') ||
              errorMessage.includes('CORS error') ||
              errorMessage.includes('endpoint not found')) {
            console.log('🎭 Falling back to DEMO MODE due to API error');
            await this.simulateTypingDelay();
            response = this.generateDemoResponse(content);

            // Add a note about the fallback
            response += '\n\n*Note: Currently running in demo mode due to configuration issues. Please check your AI service settings.*';
          } else {
            throw apiError; // Re-throw if it's not a configuration issue
          }
        }
      } else {
        // Use demo response
        console.log('🎭 Using DEMO MODE - Generating demo response for:', content.substring(0, 50) + '...');
        await this.simulateTypingDelay();
        response = this.generateDemoResponse(content);
      }

      // Add AI response
      this.addMessage(MessageSender.AI, response);

    } catch (error) {
      console.error('❌ Chat error:', error);
      const errorMessage = error instanceof Error ? error.message : 'Unknown error occurred';
      this.addMessage(MessageSender.ERROR, `Connection failed: ${errorMessage}. Please verify your configuration in settings.`);
    } finally {
      this.setTyping(false);
    }
  }

  /**
   * Clear all messages
   */
  clearMessages(): void {
    this.messagesSubject.next([]);
    this.hasMessagesSubject.next(false);
  }

  /**
   * Set typing state
   */
  private setTyping(isTyping: boolean): void {
    this.isTypingSubject.next(isTyping);
  }

  /**
   * Simulate typing delay for demo mode
   */
  private simulateTypingDelay(): Promise<void> {
    const delay = AI_ANIMATION_DURATIONS.TYPING_DELAY + Math.random() * 2500;
    return new Promise(resolve => setTimeout(resolve, delay));
  }

  /**
   * Generate demo response based on message content
   */
  private generateDemoResponse(message: string): string {
    const lowerMessage = message.toLowerCase();
    
    // Check for specific keywords
    for (const [key, response] of Object.entries(AI_DEMO_RESPONSES)) {
      if (lowerMessage.includes(key)) {
        return response;
      }
    }

    // Default response
    return `**✨ TaxGenie ASN Expert AI - Premium Intelligence at Your Service!**

Thank you for your inquiry: "${message}"

**🎯 Comprehensive ASN 2.1 Expertise Portfolio:**

• **🚀 Advanced ASN 2.1 Intelligence**: Deep technical analysis, strategic implementation, and competitive advantage realization
• **🏗️ Enterprise ERP Mastery**: Premium integration services for Fortune 500 systems and complex architectures
• **🔄 Digital Innovation Leadership**: Advanced automation workflows, AI-powered processing, and intelligent optimization
• **⚡ Strategic Implementation Excellence**: Executive-level project management, accelerated timelines, and guaranteed success
• **💎 Investment & ROI Optimization**: Comprehensive financial analysis, strategic value realization, and enterprise cost modeling
• **🛡️ Advanced Technical Excellence**: Premium troubleshooting, predictive maintenance, and proactive optimization

**💡 Premium Consultation Recommendations:**
• "How can ASN 2.1 transform our enterprise operations?"
• "What's our optimal enterprise integration strategy?"
• "How do we achieve rapid ROI with strategic implementation?"
• "What advanced features can accelerate our digital transformation?"

**🌐 Executive Services**: Visit www.taxgenie.in for premium consultation, dedicated solution architects, and white-glove implementation services.

*Currently showcasing premium capabilities in demonstration mode. Configure your enterprise n8n webhook for live AI responses powered by your custom knowledge base and advanced integrations.*`;
  }

  /**
   * Format message content using marked
   */
  formatMessageContent(content: string): string {
    try {
      // Normalize bullets and headers: ensure each starts on its own line  
      const normalized = content
        .replace(/•\s*/g, '\n- ')      // turn inline "•" into proper markdown list items
        .replace(/✅\s*/g, '\n- ✅ ')  // same for checkmarks
        .replace(/## /g, '\n## ')     // ensure headers start on new lines
        .replace(/\n{3,}/g, '\n\n');  // collapse multiple line breaks

      // Parse markdown synchronously
      const result = marked.parse(normalized) as string;
      return result;
      
    } catch (error) {
      console.warn('Markdown parsing failed, using fallback:', error);
      
      // Fallback formatting if marked fails
      return content
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\*(.*?)\*/g, '<em>$1</em>')
        .replace(/\n/g, '<br>')
        .replace(/•\s*/g, '<br>• ')
        .replace(/✅\s*/g, '<br>✅ ');
    }
  }

  /**
   * Get message by ID
   */
  getMessageById(id: string): AiMessage | undefined {
    return this.messages.find(message => message.id === id);
  }

  /**
   * Update message content
   */
  updateMessage(id: string, content: string): void {
    const messages = this.messages;
    const messageIndex = messages.findIndex(m => m.id === id);
    
    if (messageIndex !== -1) {
      const updatedMessages = [...messages];
      updatedMessages[messageIndex] = {
        ...updatedMessages[messageIndex],
        content
      };
      this.messagesSubject.next(updatedMessages);
    }
  }

  /**
   * Delete message by ID
   */
  deleteMessage(id: string): void {
    const messages = this.messages.filter(m => m.id !== id);
    this.messagesSubject.next(messages);
    this.hasMessagesSubject.next(messages.length > 0);
  }
}
