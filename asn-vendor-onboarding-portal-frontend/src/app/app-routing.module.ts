import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardComponent } from './components/pages/dashboard/dashboard.component';
import { PlansComponent } from './components/pages/plans/plans.component';
import { SettingsComponent } from './components/pages/settings/settings.component';
import { OnboardingComponent } from './components/pages/onboarding/onboarding.component';
import { SelfDeploymentComponent } from './components/pages/self-deployment/self-deployment.component';
import { AiSpecialistComponent } from './components/pages/ai-specialist/ai-specialist.component';
import { SignInFormComponent } from './auth/sign-in-form/sign-in-form.component';
import { SignUpFormComponent } from './auth/sign-up-form/sign-up-form.component';
import { TwoFactorFormComponent } from './auth/two-factor-form/two-factor-form.component';
import { ChooseOemPortalComponent } from './components/pages/choose-oem-portal/choose-oem-portal.component';
import { authGuard } from './core/guards/auth.guard';
import { SelfDeploymentFormComponent } from './components/pages/self-deployment-form/self-deployment-form.component';
import { LandingRedirectComponent } from './components/pages/landing-redirect/landing-redirect.component';
import { guestGuard } from './core/guards/guest.guard';
import { twoFAGuard } from './core/guards/two-fa.guard';

const routes: Routes = [
  { path: '', 
    component: LandingRedirectComponent,

  },
  { path: 'sign-in', 
    component: SignInFormComponent,
    canActivate: [guestGuard]
  },
  { path: 'sign-up', 
    component: SignUpFormComponent,
    canActivate: [guestGuard]
  },
  { path: '2fa', 
    component: TwoFactorFormComponent,
    canActivate: [guestGuard, twoFAGuard]
  },
  { path: 'oem-portals', 
    component: ChooseOemPortalComponent, 
    canActivate: [authGuard],
    data: { requireSelectedOEM: false }
  },
  { path: 'dashboard', 
    component: DashboardComponent, 
    canActivate: [authGuard],
    data: { requireSelectedOEM: true } 
  },
  { path: 'onboarding', 
    component: OnboardingComponent, 
    canActivate: [authGuard],
    data: { requireSelectedOEM: true } 
  },
  { path: 'plans', 
    component: PlansComponent, 
    canActivate: [authGuard],
    data: { requireSelectedOEM: true } 
  },
  { path: 'self-deployment', 
    component: SelfDeploymentComponent, 
    canActivate: [authGuard],
    data: { requireSelectedOEM: true } 
  },
  { path: 'ai-specialist', 
    component: AiSpecialistComponent, 
    canActivate: [authGuard],
    data: { requireSelectedOEM: true } 
  },
  { path: 'settings', 
    component: SettingsComponent, 
    canActivate: [authGuard],
    data: { requireSelectedOEM: true } 
  },
  { path: 'onboarding/self-deployment-setup', 
    component: SelfDeploymentFormComponent, 
    canActivate: [authGuard],
    data: { requireSelectedOEM: true } 
  },

  { path: '**', redirectTo: '/dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    enableTracing: false, // Set to true for debugging
    scrollPositionRestoration: 'top'
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
