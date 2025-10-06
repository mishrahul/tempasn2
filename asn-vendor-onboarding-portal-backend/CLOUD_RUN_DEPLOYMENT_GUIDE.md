# ASN Vendor Portal Backend - Google Cloud Run Deployment Guide

## 🎯 Overview

This guide covers deploying the ASN Vendor Portal Spring Boot backend to Google Cloud Run in project `noble-stratum-393405`.

### Current Setup
- **Frontend**: Running on Cloud Run (`https://vendor-onboarding-portal-34bfbmshvq-uc.a.run.app/`)
- **Database**: PostgreSQL on `34.93.203.197:5432`
- **Backend**: Ready to deploy to Google Cloud Run

## 🚀 Quick Deployment (One Command)

### Prerequisites
1. **Google Cloud CLI** installed and authenticated
2. **Docker** installed
3. **Access to project**: `noble-stratum-393405`

### Deploy Now
```bash
cd asn-vendor-onboarding-portal-backend
./deploy-cloudrun.sh
```

That's it! The script handles everything automatically.

## 📋 What the Script Does

1. **Validates Prerequisites** - Checks for gcloud, docker, authentication
2. **Enables APIs** - Enables Cloud Build, Cloud Run, Container Registry
3. **Builds Docker Image** - Multi-stage build optimized for Java 17
4. **Pushes to Registry** - Uploads to Google Container Registry
5. **Deploys to Cloud Run** - Configures service with all environment variables
6. **Tests Deployment** - Verifies health endpoint
7. **Provides Next Steps** - Shows service URL and testing instructions

## 🔧 Configuration Details

### Environment Variables Set
- `SERVER_PORT=8080`
- `LOG_LEVEL=INFO`
- `STORAGE_TYPE=local`
- `ALLOWED_ORIGINS=https://vendor-onboarding-portal-34bfbmshvq-uc.a.run.app`
- `DB_URL=jdbc:postgresql://34.93.203.197:5432/db_asn_vendor_onboarding_portal?currentSchema=sch_asn_vendor_onboarding_portal`
- `DB_USERNAME=postgres`
- `DB_PASSWORD=Xr9$wP7Z6#Lf@3Bn8*`
- `JWT_SECRET=1E223539-0D5B-4279-8F9A-B3B13BDC7A29-DA01E40D-6806-40FA-B280-44ACB4902845`
- `AES_ENCRYPTION_KEY=ASN-VENDOR-PORTAL-AES-KEY-32-CHARS`
- `DB_DDL_AUTO=validate`
- `DB_SHOW_SQL=false`
- `FILE_UPLOAD_LIMIT=10MB`
- `FILE_UPLOAD_REQUEST_LIMIT=50MB`
- `LOCAL_UPLOAD_DIR=./uploads`
- `EMAIL_ENABLED=false`
- `MAX_VENDORS_PER_OEM=1000`
- `DEFAULT_SESSION_TIMEOUT=3600`

### Resource Allocation
- **Memory**: 1GB
- **CPU**: 1 vCPU
- **Max Instances**: 10
- **Min Instances**: 0 (scales to zero)
- **Concurrency**: 80 requests per instance
- **Timeout**: 300 seconds
- **Region**: us-central1

## 🧪 Testing Your Deployment

After deployment, the script will provide a service URL. Test these endpoints:

### 1. Health Check
```bash
curl https://YOUR-SERVICE-URL/api/v1/actuator/health
```

Expected response:
```json
{"status":"UP"}
```

### 2. API Documentation
Visit: `https://YOUR-SERVICE-URL/api/v1/swagger-ui.html`

### 3. Test Database Connection
The health check will verify database connectivity automatically.

### 4. Test CORS
Your frontend at `https://vendor-onboarding-portal-34bfbmshvq-uc.a.run.app/` should be able to make API calls without CORS errors.

## 🔍 Monitoring and Logs

### View Logs
```bash
# View recent logs
gcloud run services logs read asn-vendor-portal-backend --region us-central1

# Follow logs in real-time
gcloud run services logs tail asn-vendor-portal-backend --region us-central1
```

### Monitor Performance
- Visit Google Cloud Console → Cloud Run → asn-vendor-portal-backend
- Check metrics for CPU, Memory, Request count, Response times

## 🚨 Troubleshooting

### Common Issues

#### 1. Authentication Error
```bash
gcloud auth login
gcloud config set project noble-stratum-393405
```

#### 2. Docker Build Fails
```bash
# Clean Maven cache and rebuild
mvn clean
docker system prune -f
./deploy-cloudrun.sh
```

#### 3. Database Connection Issues
- Verify database is accessible from Cloud Run
- Check if database credentials are correct in the script
- Ensure database allows connections from Google Cloud IPs

#### 4. CORS Issues
- Verify frontend URL is correct in ALLOWED_ORIGINS
- Check browser developer tools for CORS errors
- Ensure frontend is making requests to the correct backend URL

#### 5. Service Not Starting
```bash
# Check detailed logs
gcloud run services logs read asn-vendor-portal-backend --region us-central1 --limit 50

# Check service configuration
gcloud run services describe asn-vendor-portal-backend --region us-central1
```

## 🔄 Updates and Redeployment

### Update and Redeploy
```bash
# Make your code changes, then simply run:
./deploy-cloudrun.sh
```

The script will:
- Build a new image
- Deploy the updated version
- Automatically handle traffic migration

### Rollback if Needed
```bash
# List revisions
gcloud run revisions list --service asn-vendor-portal-backend --region us-central1

# Rollback to previous revision
gcloud run services update-traffic asn-vendor-portal-backend --to-revisions REVISION-NAME=100 --region us-central1
```

## 💰 Cost Optimization

### Current Configuration Cost Estimate
- **Compute**: ~$5-20/month (depending on usage)
- **Networking**: ~$1-3/month
- **Storage**: Minimal

### Cost Optimization Tips
1. **Scales to zero** when not in use (current setting)
2. **Monitor usage** and adjust max instances if needed
3. **Use appropriate memory/CPU** allocation based on performance needs

## 🔐 Security Notes

### Current Security Features
- **Non-root user** in container
- **Health checks** configured
- **CORS** properly configured
- **Environment variables** for sensitive data
- **HTTPS** automatically provided by Cloud Run

### Production Security Recommendations
1. **Use Google Secret Manager** for sensitive environment variables
2. **Enable VPC connector** if database needs private access
3. **Set up monitoring and alerting**
4. **Regular security updates** of base images

## 📞 Next Steps After Deployment

1. ✅ **Get the service URL** from deployment output
2. ✅ **Test all endpoints** using the provided URLs
3. ✅ **Update your frontend** configuration if needed
4. ✅ **Monitor the deployment** for any issues
5. ✅ **Set up alerts** for production monitoring

## 🎉 Success Indicators

Your deployment is successful when:
- ✅ Health check returns `{"status":"UP"}`
- ✅ Swagger UI loads at `/api/v1/swagger-ui.html`
- ✅ Frontend can make API calls without CORS errors
- ✅ Database operations work correctly
- ✅ No errors in Cloud Run logs

Your ASN Vendor Portal Backend is now running on Google Cloud Run and ready to serve your frontend!
