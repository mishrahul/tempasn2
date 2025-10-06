import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Layout Components
import { HeaderComponent } from './components/layout/header/header.component';
import { SidebarComponent } from './components/layout/sidebar/sidebar.component';

// Page Components
import { DashboardComponent } from './components/pages/dashboard/dashboard.component';
import { PlansComponent } from './components/pages/plans/plans.component';
import { SettingsComponent } from './components/pages/settings/settings.component';
import { OnboardingComponent } from './components/pages/onboarding/onboarding.component';
import { SelfDeploymentComponent } from './components/pages/self-deployment/self-deployment.component';
import { AiSpecialistComponent } from './components/pages/ai-specialist/ai-specialist.component';

// Shared Components
import { StatCardComponent } from './components/shared/stat-card/stat-card.component';
import { ProgressSectionComponent } from './components/shared/progress-section/progress-section.component';
import { PlanCardComponent } from './components/shared/plan-card/plan-card.component';
import { AlertComponent } from './components/shared/alert/alert.component';
import { AiChatComponent } from './components/shared/ai-chat/ai-chat.component';

// Services
import { NotificationService } from './services/notification.service';
import { UserService } from './services/user.service';
import { PlanService } from './services/plan.service';
import { OnboardingService } from './services/onboarding.service';

// AI Services
import { AiChatService } from './services/ai-chat.service';
import { AiApiService } from './services/ai-api.service';
import { AiConfigurationService } from './services/ai-configuration.service';
import { AiStateService } from './services/ai-state.service';
import { AiUtilityService } from './services/ai-utility.service';
import { ToastrModule } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { AuthInterceptor } from './core/interceptors/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    SidebarComponent,
    DashboardComponent,
    PlansComponent,
    SettingsComponent,
    OnboardingComponent,
    SelfDeploymentComponent,
    AiSpecialistComponent,
    StatCardComponent,
    ProgressSectionComponent,
    PlanCardComponent,
    AlertComponent,
    AiChatComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    ToastrModule.forRoot(),
    CommonModule
  ],
  providers: [
    NotificationService,
    UserService,
    PlanService,
    OnboardingService,
    AiChatService,
    AiApiService,
    AiConfigurationService,
    AiStateService,
    AiUtilityService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
