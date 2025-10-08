import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const twoFAGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.getRequire2FA()) {
    return true;
  } else {
    router.navigate(['/sign-in']); // or another fallback
    return false;
  }
};
