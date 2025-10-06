import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { hasSelectedOEM } from 'src/app/components/shared/utils/auth-utils';

function getUser() {
  return {
    token: sessionStorage.getItem('pxp_token'),
    roles: JSON.parse(sessionStorage.getItem('roles') || '[]') as string[]
  };
}

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const router = inject(Router);
  const user = getUser();

  if (!user.token) {
    router.navigate(['/sign-in']);
    return false;
  }

  
  // Check if selectedOEM is required on this route (optional, you can add data property to routes)
  const requireSelectedOEM = route.data['requireSelectedOEM'] ?? false;

  if (requireSelectedOEM && !hasSelectedOEM()) {
    // Redirect to choose OEM page if selectedOEM not present
    router.navigate(['/oem-portals']);
    return false;
  }


  const requiredRoles = route.data['roles'] as string[] | undefined;

  if (requiredRoles && requiredRoles.length > 0) {
    const hasRole = user.roles.some(role => requiredRoles.includes(role));
    if (!hasRole) {
      router.navigate(['/unauthorized']);
      return false;
    }
  }

  return true;
};
