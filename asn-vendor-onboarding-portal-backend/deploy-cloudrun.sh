#!/bin/bash

# Google Cloud Run Deployment Script for ASN Vendor Portal Backend
# This script handles the complete deployment process to Cloud Run

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ID="noble-stratum-393405"
REGION="asia-south1"
SERVICE_NAME="asn-vendor-portal-backend"
IMAGE_NAME="asia-south1-docker.pkg.dev/$PROJECT_ID/asn-backend/$SERVICE_NAME"

echo -e "${BLUE}📍 IMAGE_NAME: $IMAGE_NAME${NC}"
echo -e "${BLUE}🚀 Starting deployment to Google Cloud Run...${NC}"
echo -e "${BLUE}📋 Project: $PROJECT_ID${NC}"
echo -e "${BLUE}📍 Region: $REGION${NC}"
echo -e "${BLUE}🏷️  Service: $SERVICE_NAME${NC}"

# Check if required tools are installed
command -v gcloud >/dev/null 2>&1 || { echo -e "${RED}❌ gcloud CLI is required but not installed. Aborting.${NC}" >&2; exit 1; }
command -v docker >/dev/null 2>&1 || { echo -e "${RED}❌ Docker is required but not installed. Aborting.${NC}" >&2; exit 1; }

# Check if user is authenticated with gcloud
if ! gcloud auth list --filter=status:ACTIVE --format="value(account)" | grep -q .; then
    echo -e "${YELLOW}⚠️  Not authenticated with gcloud. Please run: gcloud auth login${NC}"
    exit 1
fi

# Set the project
echo -e "${BLUE}📋 Setting GCP project to: $PROJECT_ID${NC}"
gcloud config set project $PROJECT_ID

# Enable required APIs
echo -e "${BLUE}🔧 Enabling required Google Cloud APIs...${NC}"
gcloud services enable cloudbuild.googleapis.com
gcloud services enable run.googleapis.com
gcloud services enable artifactregistry.googleapis.com

# Build the Docker image for Cloud Run (amd64/linux)
echo -e "${BLUE}🔨 Building Docker image...${NC}"
docker build --platform linux/amd64 -t $IMAGE_NAME:latest .

# Push the image to Artifact Registry
echo -e "${BLUE}📤 Pushing image to Artifact Registry...${NC}"
docker push $IMAGE_NAME:latest

# Deploy to Cloud Run
echo -e "${BLUE}🚀 Deploying to Cloud Run...${NC}"
gcloud run deploy $SERVICE_NAME \
    --image $IMAGE_NAME:latest \
    --region $REGION \
    --platform managed \
    --allow-unauthenticated \
    --memory 1Gi \
    --cpu 1 \
    --max-instances 10 \
    --min-instances 0 \
    --concurrency 80 \
    --timeout 300 \
    --port 8080 \
    --set-env-vars "\
SERVER_PORT=8080,\
LOG_LEVEL=INFO,\
STORAGE_TYPE=local,\
ALLOWED_ORIGINS=https://vendor-onboarding-portal-34bfbmshvq-uc.a.run.app,\
DB_URL=jdbc:postgresql://34.93.203.197:5432/db_asn_vendor_onboarding_portal?currentSchema=sch_asn_vendor_onboarding_portal,\
DB_USERNAME=postgres,\
DB_PASSWORD=Xr9\$wP7Z6#Lf@3Bn8*,\
JWT_SECRET=1E223539-0D5B-4279-8F9A-B3B13BDC7A29-DA01E40D-6806-40FA-B280-44ACB4902845,\
AES_ENCRYPTION_KEY=ASN-VENDOR-PORTAL-AES-KEY-32-CHARS,\
DB_DDL_AUTO=validate,\
DB_SHOW_SQL=false,\
FILE_UPLOAD_LIMIT=10MB,\
FILE_UPLOAD_REQUEST_LIMIT=50MB,\
LOCAL_UPLOAD_DIR=./uploads,\
EMAIL_ENABLED=false,\
MAX_VENDORS_PER_OEM=1000,\
DEFAULT_SESSION_TIMEOUT=3600" \
    --quiet

# Get the service URL
SERVICE_URL=$(gcloud run services describe $SERVICE_NAME --region $REGION --format 'value(status.url)')

echo -e "${GREEN}✅ Deployment completed successfully!${NC}"
echo -e "${GREEN}🌐 Service URL: $SERVICE_URL${NC}"
echo -e "${GREEN}📊 Health Check: $SERVICE_URL/api/v1/actuator/health${NC}"
echo -e "${GREEN}📚 API Documentation: $SERVICE_URL/api/v1/swagger-ui.html${NC}"

echo ""
echo -e "${BLUE}🧪 Testing the deployment...${NC}"

# Wait a moment for the service to be ready
sleep 10

# Test health endpoint
echo -e "${BLUE}🔍 Testing health endpoint...${NC}"
if curl -f -s "$SERVICE_URL/api/v1/actuator/health" > /dev/null; then
    echo -e "${GREEN}✅ Health check passed!${NC}"
else
    echo -e "${YELLOW}⚠️  Health check failed. Service might still be starting up.${NC}"
fi

echo ""
echo -e "${YELLOW}📝 Next Steps:${NC}"
echo -e "${YELLOW}   1. Update your frontend to use: $SERVICE_URL${NC}"
echo -e "${YELLOW}   2. Test API endpoints thoroughly${NC}"
echo -e "${YELLOW}   3. Monitor logs: gcloud run services logs read $SERVICE_NAME --region $REGION${NC}"
echo -e "${YELLOW}   4. Check metrics in Google Cloud Console${NC}"

echo ""
echo -e "${GREEN}🎉 Your ASN Vendor Portal Backend is now live!${NC}"
