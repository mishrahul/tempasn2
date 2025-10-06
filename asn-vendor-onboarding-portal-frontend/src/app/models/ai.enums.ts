// AI Assistant Enums for TaxGenie Vendor Portal

export enum MessageSender {
  USER = 'user',
  AI = 'ai',
  ERROR = 'error'
}

export enum ConnectionStatusEnum {
  READY = 'Ready',
  DEMO_MODE = 'Demo Mode',
  CONNECTING = 'Connecting',
  ERROR = 'Error'
}

export enum ApiStatus {
  SUCCESS = 'success',
  ERROR = 'error'
}

export enum FormFieldType {
  TEXT = 'text',
  URL = 'url',
  NUMBER = 'number',
  PASSWORD = 'password'
}

export enum StatusCardType {
  CONNECTED = 'connected',
  DISCONNECTED = 'disconnected',
  LOADING = 'loading'
}

export enum NavigationTarget {
  BLANK = '_blank',
  SELF = '_self'
}

export enum LoadingState {
  IDLE = 'idle',
  LOADING = 'loading',
  SUCCESS = 'success',
  ERROR = 'error'
}

export enum ThemeMode {
  LIGHT = 'light',
  DARK = 'dark',
  AUTO = 'auto'
}

export enum AnimationType {
  SLIDE_IN_UP = 'slideInUp',
  SLIDE_IN_DOWN = 'slideInDown',
  FADE_IN = 'fadeIn',
  FADE_OUT = 'fadeOut',
  BOUNCE = 'bounce',
  PULSE = 'pulse'
}

export enum ComponentSize {
  SMALL = 'small',
  MEDIUM = 'medium',
  LARGE = 'large'
}

export enum ButtonVariant {
  PRIMARY = 'primary',
  SECONDARY = 'secondary',
  SUCCESS = 'success',
  WARNING = 'warning',
  ERROR = 'error',
  GHOST = 'ghost'
}

export enum ModalSize {
  SMALL = 'small',
  MEDIUM = 'medium',
  LARGE = 'large',
  EXTRA_LARGE = 'extra-large'
}

export enum DeviceType {
  MOBILE = 'mobile',
  TABLET = 'tablet',
  DESKTOP = 'desktop'
}

export enum LogLevel {
  DEBUG = 'debug',
  INFO = 'info',
  WARN = 'warn',
  ERROR = 'error'
}
