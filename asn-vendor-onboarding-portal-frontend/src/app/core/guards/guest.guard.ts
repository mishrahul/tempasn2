import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { hasSelectedOEM, isAuthenticated } from 'src/app/components/shared/utils/auth-utils';

export const guestGuard: CanActivateFn = () => {
  const router = inject(Router);

  if (isAuthenticated()) {
    // If logged in, but OEM not selected, then redirect to the OEM page
    if (!hasSelectedOEM()) {
      router.navigate(['/oem-portals']);
      return false;
    }
    // Otherwise, redirect to the dashboard
    router.navigate(['/dashboard']);
    return false;
  }

  return true; // Allow access if NOT logged in
};
