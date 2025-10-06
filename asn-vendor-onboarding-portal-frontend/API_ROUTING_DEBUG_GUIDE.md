# API Routing Debug Guide

## Problem Summary
Production deployment returns HTML (Angular app's index.html) instead of JSON responses from API calls, while local development works correctly.

## Debug Features Added

### 1. Enhanced Console Logging
- **Environment Detection**: Logs production vs development mode
- **API Endpoint Resolution**: Shows which endpoint is being used
- **HTML Response Detection**: Automatically detects when HTML is returned instead of JSON
- **Webhook Connectivity Testing**: Direct webhook testing capabilities

### 2. Browser Console Debug Functions
Available globally in browser console after visiting the AI Specialist page:

```javascript
// Test webhook connectivity directly
debugApiRouting.testWebhook()

// Show environment information
debugApiRouting.showEnvironment()

// Show configuration details
debugApiRouting.showConfig()
```

## Step-by-Step Debugging Process

### Step 1: Deploy and Access Production
```bash
# Build production version
npm run build:prod

# Deploy to Firebase
firebase deploy

# Open production app in browser
```

### Step 2: Open Browser Console and Navigate
1. Open your production app
2. Navigate to "AI Specialist" page (this loads the debug functions)
3. Open browser Developer Tools (F12)
4. Go to Console tab

### Step 3: Run Environment Check
```javascript
debugApiRouting.showEnvironment()
```

**Expected Output (Production):**
```javascript
🌍 Environment Information: {
  production: true,
  apiUrl: "",
  corsMode: "direct",
  currentUrl: "https://your-domain.com/ai-specialist",
  hostname: "your-domain.com"
}
```

**If `production: false`** → Environment file replacement is not working

### Step 4: Check Configuration
```javascript
debugApiRouting.showConfig()
```

**Expected Output:**
```javascript
🔧 Configuration Information: {
  webhookUrl: "https://apl-sandbox.taxgenie.online/webhook/asn-verification",
  isConfigured: true,
  timeout: 30,
  hasAuthHeader: false
}
```

**If `webhookUrl` is empty** → Configuration not loaded properly

### Step 5: Test Webhook Connectivity
```javascript
debugApiRouting.testWebhook()
```

**Expected Output (Working):**
```javascript
🧪 Testing webhook connectivity: https://apl-sandbox.taxgenie.online/webhook/asn-verification
🧪 Webhook test result: {
  status: 200,
  statusText: "OK",
  isHtml: false,
  responsePreview: "{\"response\":\"...\"}",
  url: "https://apl-sandbox.taxgenie.online/webhook/asn-verification"
}
✅ Webhook test successful: Received non-HTML response
```

**Expected Output (Firebase Hosting Issue):**
```javascript
🧪 Testing webhook connectivity: https://apl-sandbox.taxgenie.online/webhook/asn-verification
🧪 Webhook test result: {
  status: 200,
  statusText: "OK",
  isHtml: true,
  responsePreview: "<!doctype html><html lang=\"en\" data-critters-container><head><meta charset=\"utf-8\"><title>TaxGenie Vendor Portal</title>...",
  url: "https://your-domain.com/webhook/asn-verification"
}
🚨 WEBHOOK TEST FAILED: Received HTML instead of JSON
This confirms Firebase hosting is intercepting the request
```

### Step 6: Test Actual API Call
1. Try sending a message through the AI chat interface
2. Monitor console for these logs:

**Working (Direct Webhook Call):**
```javascript
🚀 Sending message to AI endpoint: {
  environment: "PRODUCTION",
  endpoint: "https://apl-sandbox.taxgenie.online/webhook/asn-verification",
  webhookUrl: "https://apl-sandbox.taxgenie.online/webhook/asn-verification",
  isDirectWebhookCall: true
}
```

**Broken (Firebase Hosting Intercepting):**
```javascript
🚀 Sending message to AI endpoint: {
  environment: "PRODUCTION",
  endpoint: "https://apl-sandbox.taxgenie.online/webhook/asn-verification",
  webhookUrl: "https://apl-sandbox.taxgenie.online/webhook/asn-verification",
  isDirectWebhookCall: true
}
🚨 FIREBASE HOSTING ISSUE DETECTED!
API call returned HTML instead of JSON
Expected endpoint: https://apl-sandbox.taxgenie.online/webhook/asn-verification
Environment production: true
This means Firebase hosting is intercepting the API call
```

## Diagnosis Based on Results

### Scenario 1: Environment Not Production
**Symptoms:**
- `debugApiRouting.showEnvironment()` shows `production: false`
- API calls use `/api/chat` endpoint

**Solution:**
- Check Angular build configuration in `angular.json`
- Verify `fileReplacements` section exists for production
- Ensure `environment.prod.ts` has `production: true`

### Scenario 2: Configuration Not Loaded
**Symptoms:**
- `debugApiRouting.showConfig()` shows empty `webhookUrl`
- Environment is production but no webhook configured

**Solution:**
- Check AI configuration in app settings
- Re-enter webhook URL in configuration
- Clear browser localStorage and reconfigure

### Scenario 3: Firebase Hosting Intercepting (Most Likely)
**Symptoms:**
- Environment shows production correctly
- Configuration shows webhook URL
- `debugApiRouting.testWebhook()` returns HTML
- Console shows "FIREBASE HOSTING ISSUE DETECTED"

**Root Cause:**
Firebase hosting's catch-all rewrite rule is intercepting the webhook requests

**Solutions:**

#### Option A: Verify Direct Calls Are Working
The current implementation should make direct calls to the webhook URL, bypassing Firebase entirely. If this isn't working, there might be a CORS issue or network routing problem.

#### Option B: Use Subdomain for API
```javascript
// Update webhook URL to use subdomain
webhookUrl: "https://api.yourdomain.com/webhook/asn-verification"
```

#### Option C: Firebase Functions Proxy
Create a Firebase Function to proxy requests:
```javascript
// functions/index.js
exports.apiProxy = functions.https.onRequest((req, res) => {
  // Proxy to n8n webhook
});
```

#### Option D: Update Firebase Hosting Rules
Modify `firebase.json` to exclude API paths:
```json
{
  "hosting": {
    "rewrites": [
      {
        "source": "/api/**",
        "destination": "https://apl-sandbox.taxgenie.online/webhook/asn-verification"
      },
      {
        "source": "**",
        "destination": "/index.html"
      }
    ]
  }
}
```

## Quick Fix Verification

After implementing any solution:

1. Rebuild and redeploy
2. Run `debugApiRouting.testWebhook()` again
3. Look for `✅ Webhook test successful` message
4. Test actual AI chat functionality

## Console Log Reference

- 🚀 - API calls and routing
- 🔧 - Configuration loading
- 🌍 - Environment detection
- 🧪 - Connectivity tests
- 🚨 - Errors and issues
- ✅ - Successful operations
- ❌ - Failed operations

## Next Steps

Based on your debug results, we can implement the appropriate solution. The debug functions will help us identify exactly where the routing is failing and guide us to the correct fix.
