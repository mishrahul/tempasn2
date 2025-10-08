#!/bin/bash

# Production Deployment Script with Cache Busting
# This script ensures clean deployment and helps avoid cache issues

echo "🚀 Starting production deployment..."

# Step 1: Clean previous builds
echo "🧹 Cleaning previous builds..."
rm -rf dist/
rm -rf .angular/cache/

# Step 2: Install dependencies (ensure latest versions)
echo "📦 Installing dependencies..."
npm ci

# Step 3: Build production version
echo "🔨 Building production version..."
npm run build:prod

# Step 4: Verify build output
echo "📋 Build verification..."
if [ ! -d "dist/taxgenie-vendor-portal" ]; then
    echo "❌ Build failed - dist directory not found"
    exit 1
fi

echo "✅ Build successful. Files generated:"
ls -la dist/taxgenie-vendor-portal/

# Step 5: Deploy to Firebase
echo "🚀 Deploying to Firebase..."
firebase deploy --project supplier-connect-app

# Step 6: Post-deployment verification
echo "✅ Deployment complete!"
echo ""
echo "🔍 Post-deployment checklist:"
echo "1. Clear browser cache on all devices"
echo "2. Test API routing functionality"
echo "3. Verify environment detection in production"
echo ""
echo "🧹 Cache clearing instructions:"
echo "- Chrome: Ctrl+Shift+R (hard refresh)"
echo "- DevTools: Application > Storage > Clear site data"
echo "- Mobile: Clear browser data in settings"
echo ""
echo "🌐 Production URL: https://supplier-connect-app.web.app"
