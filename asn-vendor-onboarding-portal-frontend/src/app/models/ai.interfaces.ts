// AI Assistant Interfaces for TaxGenie Vendor Portal

export interface AiMessage {
  id: string;
  sender: 'user' | 'ai' | 'error';
  content: string;
  timestamp: Date;
  isTyping?: boolean;
}

export interface AiUser {
  id: string;
  name: string;
  email: string;
  plan: 'AI Pro' | 'Basic' | 'Enterprise';
  avatar?: string;
}

export interface AiConfiguration {
  webhookUrl: string;
  authHeader: string;
  timeout: number;
  isConfigured: boolean;
}

export interface AiApiRequest {
  message: string;
  userId: string;
  context: string;
  timestamp: string;
  webhookUrl?: string;
}

export interface AiApiResponse {
  response?: string;
  output?: string;
  message?: string;
  status: 'success' | 'error';
  metadata?: {
    confidence?: number;
    [key: string]: any;
  };
}

export interface NavigationItem {
  id: string;
  label: string;
  icon: string;
  active?: boolean;
  action?: () => void;
  href?: string;
  target?: '_blank' | '_self';
}

export interface NavigationSection {
  title: string;
  items: NavigationItem[];
}

export interface SuggestionCard {
  id: string;
  title: string;
  description: string;
  icon: string;
  message: string;
}

export interface AiConnectionStatus {
  isOnline: boolean;
  status: 'Ready' | 'Demo Mode' | 'Connecting' | 'Error';
  lastChecked?: Date;
}

export interface AiAppState {
  isTyping: boolean;
  hasMessages: boolean;
  sidebarCollapsed: boolean;
  settingsOpen: boolean;
  settingsModalOpen: boolean;
  connectionStatus: AiConnectionStatus;
}

export interface DropdownItem {
  id: string;
  label: string;
  icon: string;
  action?: () => void;
  divider?: boolean;
}

export interface ModalConfig {
  title: string;
  isOpen: boolean;
  showCloseButton?: boolean;
  backdropClose?: boolean;
}

export interface FormField {
  id: string;
  label: string;
  type: 'text' | 'url' | 'number' | 'password';
  value: string | number;
  placeholder?: string;
  helpText?: string;
  required?: boolean;
  min?: number;
  max?: number;
}

export interface StatusCard {
  type: 'connected' | 'disconnected' | 'loading';
  message: string;
  icon?: string;
}

export interface Environment {
  production: boolean;
  apiUrl?: string;
  defaultTimeout: number;
  enableAnalytics: boolean;
}
