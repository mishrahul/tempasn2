import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { AiChatService } from '../../../services/ai-chat.service';
import { AiStateService } from '../../../services/ai-state.service';
import { AiConfigurationService } from '../../../services/ai-configuration.service';
import { 
  AiMessage, 
  AI_SUGGESTION_CARDS, 
  SuggestionCard,
  AiConnectionStatus 
} from '../../../models/ai.models';

@Component({
  selector: 'app-ai-chat',
  templateUrl: './ai-chat.component.html',
  styleUrl: './ai-chat.component.scss'
})
export class AiChatComponent implements OnInit, OnDestroy {
  currentMessage = '';
  messages: AiMessage[] = [];
  isTyping = false;
  isLoading = false;
  hasMessages = false;
  isProductionMode = false;

  suggestionCards: SuggestionCard[] = AI_SUGGESTION_CARDS;

  private subscriptions: Subscription[] = [];

  constructor(
    private chatService: AiChatService,
    private stateService: AiStateService,
    private configService: AiConfigurationService
  ) {}

  ngOnInit(): void {
    // Subscribe to chat service observables
    this.subscriptions.push(
      this.chatService.messages$.subscribe(messages => {
        this.messages = messages;
      }),

      this.chatService.isTyping$.subscribe(isTyping => {
        this.isTyping = isTyping;
        this.isLoading = isTyping;
      }),

      this.chatService.hasMessages$.subscribe(hasMessages => {
        this.hasMessages = hasMessages;
      }),

      // Subscribe to configuration changes to update production mode status
      this.configService.configuration$.subscribe(config => {
        this.isProductionMode = config.isConfigured && !!config.webhookUrl;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  async sendMessage(): Promise<void> {
    if (!this.currentMessage.trim() || this.isTyping) {
      return;
    }

    const message = this.currentMessage.trim();
    this.currentMessage = '';

    // Use ChatService to handle the message
    await this.chatService.sendMessage(message);
  }

  handleKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  sendSuggestion(message: string): void {
    this.currentMessage = message;
    this.sendMessage();
  }

  trackByMessageId(index: number, message: AiMessage): string {
    return message.id;
  }

  formatMessage(content: string): string {
    return this.chatService.formatMessageContent(content);
  }

  formatTime(timestamp: Date): string {
    return new Intl.DateTimeFormat('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true
    }).format(timestamp);
  }
}
