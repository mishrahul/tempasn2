# TaxGenie Vendor Portal - Angular Application

A production-ready Angular 17 application for TaxGenie's Tata Motors ASN 2.1 vendor portal. This comprehensive vendor management system provides a modern, responsive interface for managing vendor onboarding, plans, and ASN 2.1 compliance.

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Building for Production](#building-for-production)
- [Deployment](#deployment)
- [Project Structure](#project-structure)
- [Available Scripts](#available-scripts)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## 🚀 Project Overview

The TaxGenie Vendor Portal is designed specifically for Tata Motors' ASN 2.1 implementation, providing vendors with:

- **Comprehensive Onboarding**: Step-by-step guidance for ASN 2.1 compliance
- **Plan Management**: Multiple subscription tiers (Basic, Professional, Enterprise)
- **Self-Deployment Support**: Tools and resources for independent implementation
- **AI Specialist Integration**: Advanced AI assistant for ASN 2.1 queries
- **Real-time Notifications**: System-wide notification management
- **Responsive Design**: Mobile-first approach with professional UI/UX

## ✨ Features

- **Modern Angular 17+ Architecture**: Latest Angular features and best practices
- **Responsive Design**: Mobile-first approach with professional styling
- **Component-Based Architecture**: Reusable, maintainable components
- **Service-Based Data Management**: Centralized data handling with RxJS
- **Reactive Forms**: Advanced form validation and user input handling
- **Routing with Lazy Loading**: Optimized performance with code splitting
- **Material Design Integration**: Angular Material components
- **SCSS Styling**: Professional custom styling with CSS variables
- **Notification System**: Toast notifications and alerts
- **Firebase Integration**: Ready for Firebase hosting deployment
- **Production-Ready Build**: Optimized builds with proper bundling

## 📋 Prerequisites

Before setting up the project, ensure you have the following installed on your system:

### Required Software

1. **Node.js** (version 18.x or higher)
   - Download from: https://nodejs.org/
   - Verify installation: `node --version`

2. **npm** (comes with Node.js) or **yarn**
   - Verify npm: `npm --version`
   - Or install yarn: `npm install -g yarn`

3. **Angular CLI** (version 17.x or higher)
   ```bash
   npm install -g @angular/cli@17
   ```
   - Verify installation: `ng version`

4. **Git** (for version control)
   - Download from: https://git-scm.com/
   - Verify installation: `git --version`

### Optional Tools

- **Firebase CLI** (for deployment)
  ```bash
  npm install -g firebase-tools
  ```

- **VS Code** (recommended IDE)
  - Download from: https://code.visualstudio.com/
  - Recommended extensions:
    - Angular Language Service
    - Angular Snippets
    - TypeScript Hero
    - SCSS IntelliSense

## 🛠️ Installation

Follow these steps to set up the project locally:

### 1. Clone the Repository

```bash
git clone <repository-url>
cd taxgenie-vendor-portal
```

### 2. Install Dependencies

Using npm:
```bash
npm install
```

Or using yarn:
```bash
yarn install
```

This will install all required dependencies including:
- Angular 17+ framework and CLI tools
- Angular Material for UI components
- RxJS for reactive programming
- TypeScript for type safety
- Development tools (ESLint, Karma, Jasmine)

### 3. Verify Installation

Check that all dependencies are installed correctly:
```bash
ng version
```

You should see output showing Angular CLI and core packages versions.

## ⚙️ Configuration

### Environment Setup

The application is configured to work out of the box for development. However, you may need to configure certain aspects:

### 1. Firebase Configuration (Optional)

If you plan to deploy to Firebase:

1. **Install Firebase CLI**:
   ```bash
   npm install -g firebase-tools
   ```

2. **Login to Firebase**:
   ```bash
   firebase login
   ```

3. **Initialize Firebase** (if not already done):
   ```bash
   firebase init
   ```

4. **Update Firebase Project ID**:
   - Edit `.firebaserc` and update the project ID to match your Firebase project

### 2. API Configuration

The application currently uses mock data services. To connect to real APIs:

1. **Create environment files** (if needed):
   ```bash
   mkdir src/environments
   ```

2. **Add environment configuration**:
   ```typescript
   // src/environments/environment.ts
   export const environment = {
     production: false,
     apiUrl: 'http://localhost:3000/api',
     firebaseConfig: {
       // Your Firebase config
     }
   };
   ```

### 3. Styling Customization

The application uses CSS custom properties for theming. You can customize colors in `src/styles.scss`:

```scss
:root {
  --taxgenie-primary: #1f4e79;
  --taxgenie-secondary: #2563eb;
  --taxgenie-accent: #dc2626;
  // Add your custom colors
}
```

## 🚀 Running the Application

### Development Server

Start the development server:

```bash
npm start
```

Or using Angular CLI directly:
```bash
ng serve
```

The application will be available at:
- **URL**: http://localhost:4200
- **Auto-reload**: Enabled (changes will automatically refresh the browser)

### Development Server Options

```bash
# Run on a specific port
ng serve --port 4300

# Run with specific host (for network access)
ng serve --host 0.0.0.0

# Run with production configuration
ng serve --configuration production

# Open browser automatically
ng serve --open
```

### Accessing the Application

1. Open your browser and navigate to `http://localhost:4200`
2. You should see the TaxGenie Vendor Portal login/dashboard
3. The application includes:
   - **Dashboard**: Overview of vendor status and progress
   - **Plans**: Subscription management (Basic, Professional, Enterprise)
   - **Settings**: Configuration and profile management
   - **Onboarding**: Step-by-step ASN 2.1 setup guide

## 🏗️ Building for Production

### Production Build

Create an optimized production build:

```bash
npm run build:prod
```

Or using Angular CLI:
```bash
ng build --configuration production
```

This will:
- Create optimized bundles in the `dist/taxgenie-vendor-portal/` directory
- Enable ahead-of-time (AOT) compilation
- Minify and compress all assets
- Apply tree-shaking to remove unused code
- Generate source maps for debugging

### Build Configurations

The project includes multiple build configurations:

```bash
# Development build (larger, with source maps)
ng build --configuration development

# Production build (optimized, minified)
ng build --configuration production

# Custom build with specific options
ng build --output-path=custom-dist --base-href=/custom-path/
```

### Build Output

After building, you'll find the following in `dist/taxgenie-vendor-portal/`:
- `index.html` - Main HTML file
- `main.[hash].js` - Application code
- `polyfills.[hash].js` - Browser compatibility code
- `runtime.[hash].js` - Angular runtime
- `styles.[hash].css` - Compiled styles
- `assets/` - Static assets (images, fonts, etc.)

## 🚀 Deployment

### Firebase Hosting (Recommended)

The project is pre-configured for Firebase hosting:

1. **Build the project**:
   ```bash
   npm run build:prod
   ```

2. **Deploy to Firebase**:
   ```bash
   firebase deploy
   ```

3. **Access your deployed app**:
   - Your app will be available at: `https://your-project-id.web.app`

### Alternative Deployment Options

#### 1. Static File Hosting (Netlify, Vercel, etc.)

1. Build the project: `npm run build:prod`
2. Upload the contents of `dist/taxgenie-vendor-portal/` to your hosting provider
3. Configure redirects for Angular routing:
   ```
   /*    /index.html   200
   ```

#### 2. Apache/Nginx Server

1. Build the project: `npm run build:prod`
2. Copy `dist/taxgenie-vendor-portal/` to your web server directory
3. Configure server for Angular routing:

**Apache (.htaccess)**:
```apache
RewriteEngine On
RewriteCond %{REQUEST_FILENAME} !-f
RewriteCond %{REQUEST_FILENAME} !-d
RewriteRule . /index.html [L]
```

**Nginx**:
```nginx
location / {
  try_files $uri $uri/ /index.html;
}
```

#### 3. Docker Deployment

Create a `Dockerfile`:
```dockerfile
FROM nginx:alpine
COPY dist/taxgenie-vendor-portal /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

Build and run:
```bash
docker build -t taxgenie-vendor-portal .
docker run -p 80:80 taxgenie-vendor-portal
```

## 📁 Project Structure

```
taxgenie-vendor-portal/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── layout/
│   │   │   │   ├── header/           # Main navigation header
│   │   │   │   └── sidebar/          # Side navigation menu
│   │   │   ├── pages/
│   │   │   │   ├── dashboard/        # Main dashboard view
│   │   │   │   ├── plans/            # Subscription plans management
│   │   │   │   ├── settings/         # User settings and configuration
│   │   │   │   ├── onboarding/       # ASN 2.1 onboarding flow
│   │   │   │   ├── self-deployment/  # Self-deployment resources
│   │   │   │   └── ai-specialist/    # AI assistant interface
│   │   │   └── shared/
│   │   │       ├── stat-card/        # Reusable statistics cards
│   │   │       ├── progress-section/ # Progress tracking components
│   │   │       ├── plan-card/        # Plan display cards
│   │   │       ├── notification/     # Toast notifications
│   │   │       └── alert/            # Alert messages
│   │   ├── models/
│   │   │   ├── user.model.ts         # User data interface
│   │   │   ├── plan.model.ts         # Subscription plan interface
│   │   │   ├── notification.model.ts # Notification interface
│   │   │   └── onboarding.model.ts   # Onboarding data interface
│   │   ├── services/
│   │   │   ├── user.service.ts       # User management service
│   │   │   ├── plan.service.ts       # Plan management service
│   │   │   ├── notification.service.ts # Notification service
│   │   │   └── onboarding.service.ts # Onboarding workflow service
│   │   ├── app-routing.module.ts     # Application routing configuration
│   │   ├── app.component.ts          # Root application component
│   │   ├── app.component.scss        # Root component styles
│   │   └── app.module.ts             # Main application module
│   ├── assets/                       # Static assets (images, icons, etc.)
│   ├── styles.scss                   # Global application styles
│   ├── index.html                    # Main HTML template
│   └── main.ts                       # Application bootstrap
├── dist/                             # Built application (generated)
├── node_modules/                     # Dependencies (generated)
├── angular.json                      # Angular CLI configuration
├── package.json                      # Project dependencies and scripts
├── tsconfig.json                     # TypeScript configuration
├── tsconfig.app.json                 # App-specific TypeScript config
├── firebase.json                     # Firebase hosting configuration
├── .firebaserc                       # Firebase project configuration
└── README.md                         # This file
```

### Key Components

- **AppComponent**: Root component with main layout structure
- **HeaderComponent**: Top navigation with branding and user menu
- **SidebarComponent**: Side navigation menu
- **DashboardComponent**: Main dashboard with statistics and progress
- **PlansComponent**: Subscription plan management and upgrades
- **SettingsComponent**: User profile and application settings
- **OnboardingComponent**: Step-by-step ASN 2.1 setup guide
- **NotificationComponent**: System-wide notification display

### Services Architecture

- **UserService**: Manages user authentication and profile data
- **PlanService**: Handles subscription plans and upgrades
- **NotificationService**: Manages toast notifications and alerts
- **OnboardingService**: Controls the onboarding workflow

## 📜 Available Scripts

The following npm scripts are available:

```bash
# Development
npm start                 # Start development server (ng serve)
npm run ng                # Run Angular CLI commands

# Building
npm run build             # Build for production (ng build)
npm run build:prod        # Build with production configuration

# Testing
npm test                  # Run unit tests (ng test)
npm run lint              # Run ESLint for code quality
npm run e2e               # Run end-to-end tests

# Utilities
npm run ng -- <command>   # Run any Angular CLI command
```

### Angular CLI Commands

You can also use Angular CLI directly:

```bash
# Generate new components
ng generate component components/shared/my-component

# Generate services
ng generate service services/my-service

# Generate modules
ng generate module modules/my-module

# Run tests
ng test --watch=false --browsers=ChromeHeadless

# Analyze bundle size
ng build --stats-json
npx webpack-bundle-analyzer dist/taxgenie-vendor-portal/stats.json
```

## 🔧 Troubleshooting

### Common Issues and Solutions

#### 1. Node.js Version Issues

**Problem**: Angular CLI or npm commands fail with version errors.

**Solution**:
```bash
# Check Node.js version (should be 18.x or higher)
node --version

# Update Node.js if needed
# Download from https://nodejs.org/ or use nvm:
nvm install 18
nvm use 18
```

#### 2. Angular CLI Not Found

**Problem**: `ng` command not recognized.

**Solution**:
```bash
# Install Angular CLI globally
npm install -g @angular/cli@17

# Or use npx to run without global installation
npx @angular/cli@17 serve
```

#### 3. Port Already in Use

**Problem**: Development server fails to start on port 4200.

**Solution**:
```bash
# Use a different port
ng serve --port 4300

# Or kill the process using port 4200
# On Windows:
netstat -ano | findstr :4200
taskkill /PID <PID> /F

# On macOS/Linux:
lsof -ti:4200 | xargs kill -9
```

#### 4. Memory Issues During Build

**Problem**: Build fails with "JavaScript heap out of memory" error.

**Solution**:
```bash
# Increase Node.js memory limit
export NODE_OPTIONS="--max-old-space-size=8192"
npm run build:prod

# Or on Windows:
set NODE_OPTIONS=--max-old-space-size=8192
npm run build:prod
```

#### 5. Firebase Deployment Issues

**Problem**: Firebase deployment fails or shows wrong project.

**Solution**:
```bash
# Check current project
firebase projects:list

# Switch to correct project
firebase use your-project-id

# Re-login if needed
firebase logout
firebase login

# Deploy with specific project
firebase deploy --project your-project-id
```

#### 6. Styling Issues

**Problem**: Styles not loading or appearing incorrectly.

**Solution**:
```bash
# Clear Angular cache
rm -rf .angular/cache
# Or on Windows:
rmdir /s .angular\cache

# Restart development server
npm start
```

#### 7. TypeScript Compilation Errors

**Problem**: TypeScript errors preventing compilation.

**Solution**:
```bash
# Check TypeScript version compatibility
npm list typescript

# Update TypeScript if needed
npm install typescript@~5.2.0 --save-dev

# Clear node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

### Performance Optimization

#### 1. Bundle Size Analysis

```bash
# Generate stats file
ng build --stats-json

# Analyze bundle (install webpack-bundle-analyzer first)
npm install -g webpack-bundle-analyzer
webpack-bundle-analyzer dist/taxgenie-vendor-portal/stats.json
```

#### 2. Lazy Loading

The application supports lazy loading. To add lazy-loaded modules:

```typescript
// In app-routing.module.ts
{
  path: 'feature',
  loadChildren: () => import('./feature/feature.module').then(m => m.FeatureModule)
}
```

#### 3. Production Optimizations

The production build includes:
- **Tree Shaking**: Removes unused code
- **Minification**: Compresses JavaScript and CSS
- **AOT Compilation**: Ahead-of-time template compilation
- **Bundle Splitting**: Separates vendor and application code

### Getting Help

If you encounter issues not covered here:

1. **Check Angular Documentation**: https://angular.io/docs
2. **Angular CLI Documentation**: https://angular.io/cli
3. **Stack Overflow**: Search for Angular-specific questions
4. **GitHub Issues**: Check the Angular CLI GitHub repository
5. **Community Forums**: Angular Discord, Reddit r/Angular

### Development Best Practices

1. **Code Style**: Follow Angular style guide
2. **Testing**: Write unit tests for components and services
3. **Performance**: Use OnPush change detection where possible
4. **Accessibility**: Follow WCAG guidelines
5. **Security**: Sanitize user inputs and use HTTPS

## 🤝 Contributing

### Development Workflow

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/new-feature`
3. **Make your changes**
4. **Run tests**: `npm test`
5. **Run linting**: `npm run lint`
6. **Commit changes**: `git commit -m "Add new feature"`
7. **Push to branch**: `git push origin feature/new-feature`
8. **Create Pull Request**

### Code Standards

- Follow Angular style guide
- Use TypeScript strict mode
- Write unit tests for new features
- Update documentation for API changes
- Use semantic commit messages

### Testing

```bash
# Run all tests
npm test

# Run tests with coverage
ng test --code-coverage

# Run e2e tests
npm run e2e
```

---

## 📄 License

This project is proprietary software developed for TaxGenie's Tata Motors ASN 2.1 vendor portal implementation.

## 📞 Support

For technical support or questions about this implementation:

- **Email**: support@taxgenie.com
- **Documentation**: Internal TaxGenie documentation portal
- **Training**: ASN 2.1 implementation training materials included

---

**Built with ❤️ by the TaxGenie Team**

*Last updated: August 2025*

```
src/
├── app/
│   ├── components/
│   │   ├── layout/
│   │   │   ├── header/
│   │   │   └── sidebar/
│   │   ├── pages/
│   │   │   ├── dashboard/
│   │   │   ├── plans/
│   │   │   ├── settings/
│   │   │   ├── onboarding/
│   │   │   ├── self-deployment/
│   │   │   └── ai-specialist/
│   │   └── shared/
│   │       ├── stat-card/
│   │       ├── progress-section/
│   │       ├── plan-card/
│   │       ├── notification/
│   │       └── alert/
│   ├── services/
│   │   ├── notification.service.ts
│   │   ├── user.service.ts
│   │   ├── plan.service.ts
│   │   └── onboarding.service.ts
│   ├── models/
│   │   ├── user.model.ts
│   │   ├── plan.model.ts
│   │   ├── onboarding.model.ts
│   │   └── notification.model.ts
│   ├── app-routing.module.ts
│   ├── app.module.ts
│   └── app.component.ts
├── assets/
├── styles.scss
├── main.ts
└── index.html
```

## Installation & Setup

1. Install dependencies:
   ```bash
   npm install
   ```

2. Start development server:
   ```bash
   npm start
   ```

3. Build for production:
   ```bash
   npm run build:prod
   ```

## Key Components

### Layout Components
- **HeaderComponent**: Top navigation with branding and user menu
- **SidebarComponent**: Left navigation menu with route links

### Page Components
- **DashboardComponent**: Main dashboard with stats and progress
- **PlansComponent**: Subscription plans management
- **SettingsComponent**: Account and company settings
- **OnboardingComponent**: ASN 2.1 onboarding flow
- **SelfDeploymentComponent**: Self-deployment resources
- **AiSpecialistComponent**: AI assistant interface

### Shared Components
- **StatCardComponent**: Reusable stat display cards
- **ProgressSectionComponent**: Progress tracking display
- **PlanCardComponent**: Subscription plan cards
- **NotificationComponent**: Toast notification system
- **AlertComponent**: Alert messages

## Services

### Core Services
- **UserService**: User data management
- **PlanService**: Subscription plan operations
- **OnboardingService**: Onboarding progress tracking
- **NotificationService**: Global notification system

## Styling

The application uses SCSS with CSS custom properties for theming:
- Consistent color scheme with TaxGenie branding
- Responsive grid layouts
- Smooth animations and transitions
- Modern card-based UI design

## Production Considerations

- Lazy loading for route modules
- Optimized bundle sizes
- Progressive Web App features ready
- SEO-friendly meta tags
- Accessibility compliance
- Error handling and loading states
- Form validation

## Browser Support

- Modern browsers (Chrome, Firefox, Safari, Edge)
- IE11+ with polyfills
- Mobile browsers (iOS Safari, Chrome Mobile)

## License

Proprietary - TaxGenie Solutions