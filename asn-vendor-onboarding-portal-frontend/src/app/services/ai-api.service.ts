import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, timeout, catchError } from 'rxjs';
import { AiApiRequest, AiApiResponse, AiConfiguration } from '../models/ai.models';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AiApiService {
  constructor(private http: HttpClient) {}

  /**
   * Send message to n8n webhook endpoint
   */
  sendMessage(
    message: string,
    config: AiConfiguration,
    userId: string = 'taxgenie-expert-user'
  ): Observable<AiApiResponse> {
    if (!config.webhookUrl) {
      return throwError(() => new Error('Webhook URL not configured'));
    }

    const headers = this.buildHeaders(config.authHeader);
    const request: AiApiRequest = {
      message,
      userId: `${userId}-${Date.now()}`,
      context: 'asn_implementation_expert',
      timestamp: new Date().toISOString()
    };

    // Determine the endpoint URL based on environment
    const endpoint = this.getApiEndpoint(config);

    // Log API calls in development only
    if (!environment.production) {
      console.log('🚀 Sending message to AI endpoint:', {
        environment: 'DEVELOPMENT',
        endpoint: endpoint,
        message: message.substring(0, 50) + '...'
      });
    }

    return this.http.post<AiApiResponse>(endpoint, request, { headers })
      .pipe(
        timeout(config.timeout * 1000),
        catchError((error) => {
          // Check if we received HTML instead of JSON (Firebase hosting issue)
          if (error.error && typeof error.error === 'string' && error.error.includes('<!DOCTYPE html>')) {
            // Log detailed error in development only
            if (!environment.production) {
              console.error('🚨 Firebase hosting is intercepting API calls');
              console.error('Expected endpoint:', endpoint);
            }
            return throwError(() => new Error('API configuration error. Please check your network connection and try again.'));
          }
          return this.handleError(error);
        })
      );
  }

  /**
   * Test connection to webhook endpoint
   */
  testConnection(config: AiConfiguration): Observable<AiApiResponse> {
    return this.sendMessage(
      'Premium connection test from TaxGenie ASN Expert AI',
      config,
      'test-user'
    );
  }

  /**
   * Get the appropriate API endpoint based on environment
   */
  private getApiEndpoint(config: AiConfiguration): string {
    if (environment.production) {
      // In production, make direct requests to the webhook URL
      return config.webhookUrl;
    } else {
      // In development, use the proxy endpoint
      return environment.apiUrl || '/api/chat';
    }
  }

  /**
   * Build HTTP headers based on auth configuration
   */
  private buildHeaders(authHeader: string): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (authHeader) {
      // Check if it's a key:value pair or a complete header
      if (authHeader.includes(':')) {
        const [key, value] = authHeader.split(':').map(s => s.trim());
        if (key && value) {
          headers = headers.set(key, value);
        }
      } else {
        // Assume it's an Authorization header
        headers = headers.set('Authorization', authHeader);
      }
    }

    return headers;
  }

  /**
   * Handle HTTP errors
   */
  private handleError = (error: any): Observable<never> => {
    let errorMessage = 'An unknown error occurred';

    if (error.name === 'TimeoutError') {
      errorMessage = `Request timed out after ${error.timeout || 'unknown'} seconds`;
    } else if (error instanceof HttpErrorResponse) {
      if (error.error instanceof ErrorEvent) {
        // Client-side error
        errorMessage = error.error.message;
      } else {
        // Server-side error
        const status = error.status;
        const statusText = error.statusText;

        // Special handling for different error scenarios
        if (status === 0) {
          if (environment.production) {
            errorMessage = 'Network error: Unable to connect to the AI service. Please check your internet connection and try again.';
          } else {
            errorMessage = 'CORS error: Unable to connect to webhook. This may be due to browser security restrictions when calling external APIs directly.';
          }
        } else if (status === 404 && environment.production) {
          // In production, 404 might indicate the webhook URL is incorrect
          errorMessage = 'AI service endpoint not found. Please verify your webhook URL configuration.';
        } else if (status === 200 && error.error && typeof error.error === 'string' && error.error.includes('<!DOCTYPE html>')) {
          // This is the specific case where we get HTML instead of JSON (Firebase hosting returning index.html)
          errorMessage = 'Configuration error: The AI service endpoint is not properly configured for production deployment.';
        } else {
          errorMessage = `HTTP ${status}: ${statusText}`;
          if (error.error?.message) {
            errorMessage += ` - ${error.error.message}`;
          }
        }
      }
    } else if (error.message) {
      errorMessage = error.message;
    }

    console.error('🚨 AI API Error Details:', {
      error,
      message: errorMessage,
      status: error.status,
      url: error.url,
      environment: environment.production ? 'PRODUCTION' : 'DEVELOPMENT'
    });

    return throwError(() => new Error(errorMessage));
  };

  /**
   * Extract response content from API response
   */
  extractResponseContent(data: any): string {
    if (typeof data === 'string') {
      return data;
    }

    if (data?.response) {
      return data.response;
    }

    if (data?.output) {
      return data.output;
    }

    if (data?.message) {
      return data.message;
    }

    // Fallback to JSON string
    return JSON.stringify(data, null, 2);
  }
}
