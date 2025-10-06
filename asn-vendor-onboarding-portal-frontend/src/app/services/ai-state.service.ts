import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AiAppState, AiConnectionStatus } from '../models/ai.models';

@Injectable({
  providedIn: 'root'
})
export class AiStateService {
  private initialState: AiAppState = {
    isTyping: false,
    hasMessages: false,
    sidebarCollapsed: false,
    settingsOpen: false,
    settingsModalOpen: false,
    connectionStatus: {
      isOnline: false,
      status: 'Demo Mode'
    }
  };

  private stateSubject = new BehaviorSubject<AiAppState>(this.initialState);
  public state$ = this.stateSubject.asObservable();

  constructor() {}

  /**
   * Get current state
   */
  get state(): AiAppState {
    return this.stateSubject.value;
  }

  /**
   * Update state
   */
  updateState(updates: Partial<AiAppState>): void {
    const currentState = this.state;
    const newState = { ...currentState, ...updates };
    this.stateSubject.next(newState);
  }

  /**
   * Toggle sidebar collapsed state
   */
  toggleSidebar(): void {
    this.updateState({
      sidebarCollapsed: !this.state.sidebarCollapsed
    });
  }

  /**
   * Toggle settings dropdown
   */
  toggleSettings(): void {
    this.updateState({
      settingsOpen: !this.state.settingsOpen
    });
  }

  /**
   * Toggle settings modal
   */
  toggleSettingsModal(): void {
    this.updateState({
      settingsModalOpen: !this.state.settingsModalOpen,
      settingsOpen: false // Close dropdown when modal opens
    });
  }

  /**
   * Set typing state
   */
  setTyping(isTyping: boolean): void {
    this.updateState({ isTyping });
  }

  /**
   * Set messages state
   */
  setHasMessages(hasMessages: boolean): void {
    this.updateState({ hasMessages });
  }

  /**
   * Set connection status to ready
   */
  setConnectionReady(): void {
    this.updateState({
      connectionStatus: {
        isOnline: true,
        status: 'Ready'
      }
    });
  }

  /**
   * Set connection status to demo mode
   */
  setConnectionDemo(): void {
    this.updateState({
      connectionStatus: {
        isOnline: false,
        status: 'Demo Mode'
      }
    });
  }

  /**
   * Set connection status to connecting
   */
  setConnectionConnecting(): void {
    this.updateState({
      connectionStatus: {
        isOnline: false,
        status: 'Connecting'
      }
    });
  }

  /**
   * Set connection status to error
   */
  setConnectionError(): void {
    this.updateState({
      connectionStatus: {
        isOnline: false,
        status: 'Error'
      }
    });
  }

  /**
   * Reset state to initial values
   */
  resetState(): void {
    this.stateSubject.next(this.initialState);
  }

  /**
   * Close all dropdowns and modals
   */
  closeAll(): void {
    this.updateState({
      settingsOpen: false,
      settingsModalOpen: false
    });
  }
}
