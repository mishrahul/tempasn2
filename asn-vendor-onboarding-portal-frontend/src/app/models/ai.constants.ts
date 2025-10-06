// AI Assistant Constants for TaxGenie Vendor Portal

import { NavigationSection, SuggestionCard, AiUser } from './ai.interfaces';

export const DEFAULT_AI_USER: AiUser = {
  id: 'taxgenie-user',
  name: 'expert@taxgenie.in',
  email: 'expert@taxgenie.in',
  plan: 'AI Pro'
};

export const DEFAULT_TIMEOUT = 30;

export const AI_NAVIGATION_SECTIONS: NavigationSection[] = [
  {
    title: 'Main',
    items: [
      {
        id: 'chat',
        label: 'ASN Expert Chat',
        icon: 'chat',
        active: true
      },
      {
        id: 'quick-setup',
        label: 'Quick Setup',
        icon: 'lightning'
      }
    ]
  },
  {
    title: 'Resources',
    items: [
      {
        id: 'book-meeting',
        label: 'Book Expert Meeting',
        icon: 'calendar',
        action: () => window.open('https://outlook.office365.com/owa/calendar/TMLASN21ExpertMeeting@taxgenie.in/bookings/', '_blank')
      },
      {
        id: 'documentation',
        label: 'Documentation',
        icon: 'document'
      },
      {
        id: 'support',
        label: 'Support Center',
        icon: 'support',
        href: 'https://taxgenie.atlassian.net/servicedesk/customer/portal/18',
        target: '_blank'
      }
    ]
  }
];

export const AI_SUGGESTION_CARDS: SuggestionCard[] = [
  {
    id: 'asn-innovation',
    title: '🚀 Discover ASN 2.1 Innovation',
    description: 'Explore the next-generation features and digital transformation capabilities',
    icon: '🚀',
    message: 'What is ASN 2.1 and how does it revolutionize supply chain management?'
  },
  {
    id: 'erp-integration',
    title: '🔗 Advanced ERP Integration',
    description: 'Professional setup guidance for SAP, Tally, Oracle, and custom systems',
    icon: '🔗',
    message: 'How can I seamlessly integrate my ERP system with ASN 2.1?'
  },
  {
    id: 'roi-analysis',
    title: '💎 Premium ROI Analysis',
    description: 'Comprehensive cost-benefit analysis and return on investment calculations',
    icon: '💎',
    message: 'What is the complete investment and ROI analysis for ASN 2.1?'
  },
  {
    id: 'implementation',
    title: '⚡ Strategic Implementation',
    description: 'Expert project planning, milestones, and accelerated deployment strategies',
    icon: '⚡',
    message: 'What is the strategic implementation timeline for ASN 2.1?'
  }
];

export const AI_API_ENDPOINTS = {
  DEFAULT_WEBHOOK: '',
  SUPPORT_URL: 'https://taxgenie.atlassian.net/servicedesk/customer/portal/18',
  BOOKING_URL: 'https://outlook.office365.com/owa/calendar/TMLASN21ExpertMeeting@taxgenie.in/bookings/',
  WEBSITE_URL: 'https://www.taxgenie.in'
};

export const AI_LOCAL_STORAGE_KEYS = {
  CONFIG: 'taxgenie-ai-premium-config',
  USER_PREFERENCES: 'taxgenie-user-preferences',
  CHAT_HISTORY: 'taxgenie-chat-history'
};

export const AI_ANIMATION_DURATIONS = {
  FAST: 150,
  NORMAL: 200,
  SLOW: 300,
  TYPING_DELAY: 1800
};

export const AI_BREAKPOINTS = {
  MOBILE: 480,
  TABLET: 768,
  DESKTOP: 1024,
  LARGE: 1280
};

export const AI_DEMO_RESPONSES = {
  'asn 2.1': `**🚀 ASN 2.1: Revolutionary Digital Supply Chain Intelligence**

Welcome to the future of supply chain automation with TaxGenie's cutting-edge ASN 2.1 platform.

**💎 Next-Generation Capabilities:**
• **Intelligent Digital Invoice Processing**: AI-powered document recognition and automated validation
• **Advanced GST Compliance Engine**: Real-time regulatory compliance with 99.99% accuracy
• **Lightning-Fast Processing**: Sub-30-second invoice processing with enterprise-grade reliability
• **Military-Grade Security**: End-to-end encryption, digital signatures, and blockchain verification
• **Predictive Analytics**: AI-driven insights for supply chain optimization

**🎯 Transformative Business Impact:**
• **Operational Excellence**: 95% reduction in manual processing errors
• **Cost Optimization**: Average 40-60% reduction in administrative overhead
• **Compliance Assurance**: Automated regulatory adherence across all jurisdictions
• **Scalability**: Seamlessly handle 10x transaction volume growth
• **Environmental Impact**: 100% paperless operations supporting sustainability goals

**⚡ Advanced Integration Ecosystem:**
Seamlessly connects with 500+ ERP systems, cloud platforms, and enterprise applications through our intelligent API orchestration layer.

**🌐 Expert Implementation**: Visit www.taxgenie.in for white-glove implementation services with dedicated solution architects.`,

  'erp integration': `**🏗️ Enterprise ERP Integration Excellence with TaxGenie**

Experience seamless integration across the entire enterprise software ecosystem.

**🎯 Comprehensive Platform Support:**
• **SAP Ecosystem**: Complete integration (S/4HANA, ECC, Business One, SuccessFactors, Ariba)
• **Oracle Suite**: End-to-end connectivity (Cloud ERP, NetSuite, JD Edwards, PeopleSoft)
• **Microsoft Dynamics**: Full-stack integration (365, Business Central, NAV, GP)
• **Tally Solutions**: Native connectivity (Prime, ERP 9, latest versions)
• **Legacy & Custom Systems**: AI-powered API translation and legacy modernization

**⚡ TaxGenie Implementation Excellence Framework:**

**Phase 1: Strategic Discovery & Architecture (2-3 days)**
• Comprehensive enterprise architecture assessment
• AI-powered integration complexity analysis
• Security, compliance, and performance optimization planning
• Custom integration blueprint development

**Phase 2: Intelligent Connector Deployment (3-4 days)**
• Zero-downtime installation with automated rollback capabilities
• Real-time data synchronization and validation
• Advanced field mapping with ML-powered data transformation
• Comprehensive security hardening and penetration testing

**💰 Enterprise Investment Framework:**
• **Strategic Setup**: ₹15,000 (enterprise-grade implementation)
• **Premium Partnership**: ₹25,000/year (white-glove support & optimization)
• **Custom Enterprise Solutions**: ₹12,000/day (specialized requirements)

**🌐 Schedule Enterprise Consultation**: Contact our solution architects at www.taxgenie.in for your personalized integration roadmap.`,

  'pricing': `**💎 TaxGenie ASN 2.1 - Premium Enterprise Investment Portfolio**

**🎯 Comprehensive Professional Service Ecosystem:**

**🏗️ Enterprise Setup Excellence: ₹15,000**
• Dedicated solution architect assignment and project management
• Advanced TaxGenie connector deployment with custom optimizations
• Enterprise-grade security implementation and hardening
• Comprehensive system integration with performance optimization
• Premium user training with interactive learning modules
• White-glove go-live support with 24/7 monitoring

**⚡ Annual Enterprise Partnership: ₹25,000/year**
• Priority access to latest AI features and innovations
• Dedicated enterprise support with <2 hour response SLAs
• Proactive system monitoring with predictive maintenance
• Advanced compliance automation with regulatory updates
• Enterprise-grade disaster recovery and business continuity
• Quarterly optimization reviews with performance enhancements

**🚀 Custom Enterprise Solutions: ₹12,000/day**
• Advanced enterprise architecture consulting and design
• Custom API development for complex legacy system integration
• Specialized compliance modules for industry-specific requirements
• Advanced analytics and business intelligence implementations
• Multi-tenant and white-label solution development

**📊 Enterprise ROI Analysis:**
• **Year 1 Total Investment**: ₹40,000 (setup + annual enterprise partnership)
• **Ongoing Annual Investment**: ₹25,000/year premium partnership
• **Payback Period**: 2-4 months through operational efficiency gains

**🌐 Schedule Executive Consultation**: Contact our enterprise team at www.taxgenie.in or +91-9551651651 for personalized ROI analysis and implementation planning.`,

  'timeline': `**⚡ TaxGenie ASN 2.1 - Strategic Enterprise Implementation Timeline**

**🚨 MISSION-CRITICAL COMPLIANCE DEADLINE: September 30, 2025**

**📅 TaxGenie Enterprise Implementation Methodology (4-6 weeks):**

**Week 1-2: Strategic Foundation & Enterprise Assessment**
• Comprehensive stakeholder alignment and executive briefings
• Advanced enterprise architecture assessment and dependency mapping
• Technical infrastructure evaluation and capacity planning
• Security compliance review and penetration testing planning
• Dedicated solution architect assignment and project kickoff

**Week 2-3: Enterprise-Grade Deployment & Integration**
• Professional TaxGenie connector deployment with zero-downtime strategy
• Advanced enterprise security implementation and hardening
• Sophisticated field mapping and data transformation configuration
• Comprehensive integration testing with synthetic data validation
• Performance optimization and load testing (up to 1M transactions/hour)

**Week 3-4: Advanced Validation & User Excellence**
• Extensive end-to-end testing with automated quality assurance
• Comprehensive user acceptance testing with interactive simulations
• Executive training programs and change management workshops
• Advanced compliance validation and regulatory testing
• Disaster recovery testing and business continuity verification

**Week 4-5: Production Excellence & Strategic Optimization**
• Blue-green production deployment with comprehensive monitoring
• Real-time performance optimization with machine learning insights
• Advanced analytics implementation and dashboard configuration
• 24/7 premium support activation with dedicated account management
• Post-implementation optimization and continuous improvement planning

**⚡ Accelerated Enterprise Track (3-4 weeks):**
Available for Fortune 500 organizations with dedicated enterprise resources and priority implementation scheduling.

**⏰ Executive Recommendation:**
Immediate strategic initiative launch ensures comfortable compliance achievement with ample optimization time and competitive advantage realization.

**🌐 Begin Your Digital Transformation**: Contact our enterprise team immediately at www.taxgenie.in to secure your priority implementation slot and ensure deadline compliance with strategic advantage.`
};
