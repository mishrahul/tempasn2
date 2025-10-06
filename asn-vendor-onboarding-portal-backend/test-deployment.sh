#!/bin/bash

# Test Script for ASN Vendor Portal Backend Deployment
# This script tests the deployed Cloud Run service

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ID="noble-stratum-393405"
REGION="us-central1"
SERVICE_NAME="asn-vendor-portal-backend"

echo -e "${BLUE}🧪 Testing ASN Vendor Portal Backend Deployment...${NC}"

# Get the service URL
echo -e "${BLUE}🔍 Getting service URL...${NC}"
SERVICE_URL=$(gcloud run services describe $SERVICE_NAME --region $REGION --format 'value(status.url)' 2>/dev/null)

if [ -z "$SERVICE_URL" ]; then
    echo -e "${RED}❌ Service not found. Make sure it's deployed first.${NC}"
    exit 1
fi

echo -e "${GREEN}🌐 Service URL: $SERVICE_URL${NC}"

# Test 1: Health Check
echo -e "${BLUE}🔍 Testing health endpoint...${NC}"
HEALTH_RESPONSE=$(curl -s -w "%{http_code}" "$SERVICE_URL/api/v1/actuator/health")
HTTP_CODE="${HEALTH_RESPONSE: -3}"
RESPONSE_BODY="${HEALTH_RESPONSE%???}"

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ Health check passed!${NC}"
    echo -e "${GREEN}   Response: $RESPONSE_BODY${NC}"
else
    echo -e "${RED}❌ Health check failed!${NC}"
    echo -e "${RED}   HTTP Code: $HTTP_CODE${NC}"
    echo -e "${RED}   Response: $RESPONSE_BODY${NC}"
fi

# Test 2: API Documentation
echo -e "${BLUE}🔍 Testing Swagger UI endpoint...${NC}"
SWAGGER_RESPONSE=$(curl -s -w "%{http_code}" "$SERVICE_URL/api/v1/swagger-ui.html")
SWAGGER_HTTP_CODE="${SWAGGER_RESPONSE: -3}"

if [ "$SWAGGER_HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ Swagger UI accessible!${NC}"
    echo -e "${GREEN}   URL: $SERVICE_URL/api/v1/swagger-ui.html${NC}"
else
    echo -e "${YELLOW}⚠️  Swagger UI check failed (HTTP: $SWAGGER_HTTP_CODE)${NC}"
fi

# Test 3: API Docs JSON
echo -e "${BLUE}🔍 Testing OpenAPI JSON endpoint...${NC}"
API_DOCS_RESPONSE=$(curl -s -w "%{http_code}" "$SERVICE_URL/api/v1/api-docs")
API_DOCS_HTTP_CODE="${API_DOCS_RESPONSE: -3}"

if [ "$API_DOCS_HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ OpenAPI JSON accessible!${NC}"
else
    echo -e "${YELLOW}⚠️  OpenAPI JSON check failed (HTTP: $API_DOCS_HTTP_CODE)${NC}"
fi

# Test 4: CORS Headers (simulate frontend request)
echo -e "${BLUE}🔍 Testing CORS configuration...${NC}"
CORS_RESPONSE=$(curl -s -H "Origin: https://vendor-onboarding-portal-34bfbmshvq-uc.a.run.app" \
                     -H "Access-Control-Request-Method: GET" \
                     -H "Access-Control-Request-Headers: Content-Type" \
                     -X OPTIONS \
                     -w "%{http_code}" \
                     "$SERVICE_URL/api/v1/actuator/health")
CORS_HTTP_CODE="${CORS_RESPONSE: -3}"

if [ "$CORS_HTTP_CODE" = "200" ] || [ "$CORS_HTTP_CODE" = "204" ]; then
    echo -e "${GREEN}✅ CORS configuration working!${NC}"
else
    echo -e "${YELLOW}⚠️  CORS preflight check returned HTTP: $CORS_HTTP_CODE${NC}"
fi

# Test 5: Check service configuration
echo -e "${BLUE}🔍 Checking service configuration...${NC}"
echo -e "${BLUE}   Memory: $(gcloud run services describe $SERVICE_NAME --region $REGION --format 'value(spec.template.spec.containers[0].resources.limits.memory)')${NC}"
echo -e "${BLUE}   CPU: $(gcloud run services describe $SERVICE_NAME --region $REGION --format 'value(spec.template.spec.containers[0].resources.limits.cpu)')${NC}"
echo -e "${BLUE}   Max Instances: $(gcloud run services describe $SERVICE_NAME --region $REGION --format 'value(spec.template.metadata.annotations.run\.googleapis\.com/execution-environment)')${NC}"

# Test 6: Check recent logs for errors
echo -e "${BLUE}🔍 Checking recent logs for errors...${NC}"
ERROR_COUNT=$(gcloud run services logs read $SERVICE_NAME --region $REGION --limit 50 --format="value(textPayload)" 2>/dev/null | grep -i "error\|exception\|failed" | wc -l)

if [ "$ERROR_COUNT" -eq 0 ]; then
    echo -e "${GREEN}✅ No recent errors in logs!${NC}"
else
    echo -e "${YELLOW}⚠️  Found $ERROR_COUNT potential errors in recent logs${NC}"
    echo -e "${YELLOW}   Check logs with: gcloud run services logs read $SERVICE_NAME --region $REGION${NC}"
fi

# Summary
echo ""
echo -e "${BLUE}📊 Test Summary:${NC}"
echo -e "${GREEN}🌐 Service URL: $SERVICE_URL${NC}"
echo -e "${GREEN}📚 API Documentation: $SERVICE_URL/api/v1/swagger-ui.html${NC}"
echo -e "${GREEN}📋 OpenAPI Spec: $SERVICE_URL/api/v1/api-docs${NC}"
echo -e "${GREEN}❤️  Health Check: $SERVICE_URL/api/v1/actuator/health${NC}"

echo ""
echo -e "${YELLOW}📝 Next Steps:${NC}"
echo -e "${YELLOW}   1. Update your frontend to use: $SERVICE_URL${NC}"
echo -e "${YELLOW}   2. Test your specific API endpoints${NC}"
echo -e "${YELLOW}   3. Monitor performance in Google Cloud Console${NC}"

echo ""
echo -e "${GREEN}🎉 Testing completed!${NC}"
