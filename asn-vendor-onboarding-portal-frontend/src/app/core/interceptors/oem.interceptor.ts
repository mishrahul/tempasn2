import { Injectable, inject } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class OemInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const selectedOEMId = sessionStorage.getItem('selectedOEMId');
    
    if (selectedOEMId && this.shouldAddOemId(req.url)) {
      const modifiedReq = req.clone({
        setHeaders: {
          'X-OEM-ID': selectedOEMId
        }
      });
      return next.handle(modifiedReq);
    }

    return next.handle(req);
  }

  private shouldAddOemId(url: string): boolean {
    // Add OEM ID to all API calls except auth endpoints
    const excludePatterns = ['/auth/', '/login', '/signup', '/verify'];
    return !excludePatterns.some(pattern => url.includes(pattern));
  }
}