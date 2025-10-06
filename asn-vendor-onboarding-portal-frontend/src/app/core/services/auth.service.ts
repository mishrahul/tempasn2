import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';

interface LoginResponse {
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';

  constructor(private http: HttpClient) {}

  /**
   * Send login request to backend
   */
  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/login`, { username, password }).pipe(
      tap((res) => {
        sessionStorage.setItem(this.TOKEN_KEY, res.token);
      }),
      catchError((error) => {
        console.error('Login error:', error);
        return throwError(() => new Error('Login failed. Please try again.'));
      })
    );
  }

  /**
   * Log out user by clearing the token
   */
  logout(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
  }

  /**
   * Check if user is authenticated (token exists and not expired)
   */
  isAuthenticated(): boolean {
    const token = this.getToken();
    if (!token) return false;

    const payload = this.decodeToken(token);
    if (!payload || !payload.exp) return false;

    // Check expiration (exp is in seconds)
    return payload.exp * 1000 > Date.now();
  }

  /**
   * Get saved auth token
   */
  getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Decode JWT token payload
   */
  private decodeToken(token: string): any | null {
    try {
      const payload = token.split('.')[1];
      const decoded = atob(payload);
      return JSON.parse(decoded);
    } catch (e) {
      console.warn('Invalid token:', e);
      return null;
    }
  }
}
