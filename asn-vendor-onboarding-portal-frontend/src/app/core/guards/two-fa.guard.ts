import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { LoginApiService } from 'src/app/services/login-service/login-api.service';

export const twoFAGuard: CanActivateFn = () => {
  const loginApiService = inject(LoginApiService);
  const router = inject(Router);

  if (loginApiService.getRequire2FA()) {
    return true;
  } else {
    router.navigate(['/sign-in']); // or another fallback
    return false;
  }
};
