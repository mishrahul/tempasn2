import { inject, Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { LoginApiService } from 'src/app/services/login-service/login-api.service';
import { NotificationService } from 'src/app/services/notification.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  private loginApiService =  inject(LoginApiService)
  private notificationService =  inject(NotificationService)

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const access_token = sessionStorage.getItem('pxp_token');
    const reqOut = !!access_token 
      ? req.clone({
        setHeaders: {
          Authorization: `Bearer ${access_token}`
        }
      }): req;

    return next.handle(reqOut).pipe(
      catchError((err: any) => {
        if (err instanceof HttpErrorResponse) {
          if (err.status >= 400 && err.status < 500 && !reqOut.headers.has('Skip-Error-Handling')) {
            if (!!err.error?.message) {
              this.notificationService.error(err.error?.message);
            }

            if (err.status == 401) {
              this.loginApiService.logout();
            }
          }
        }

        //  We need to return the new empty response
        //  even when we don't have one (because of error in communication)
        return throwError(() => err);
      })
    );
  }
}
