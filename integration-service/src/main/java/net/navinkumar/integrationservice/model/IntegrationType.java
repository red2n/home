package net.navinkumar.integrationservice.model;

/**
 * Types of external system integrations
 */
public enum IntegrationType {
    PAYMENT_GATEWAY,    // Payment processing systems (Stripe, PayPal, etc.)
    ACCOUNTING,         // Accounting systems (QuickBooks, SAP, etc.)
    CRM,               // Customer Relationship Management systems
    EMAIL,             // Email service providers (SendGrid, Mailchimp, etc.)
    SMS,               // SMS service providers (Twilio, etc.)
    REVENUE_MANAGEMENT, // Revenue management systems
    POS,               // Point of Sale systems
    LOYALTY,           // Loyalty program systems
    ANALYTICS,         // Analytics platforms (Google Analytics, etc.)
    INVENTORY,         // External inventory management systems
    REPORTING,         // Business intelligence and reporting tools
    AUTHENTICATION,    // SSO and authentication providers
    BACKUP,            // Backup and archival systems
    MONITORING,        // System monitoring and alerting
    OTHER              // Custom or miscellaneous integrations
}