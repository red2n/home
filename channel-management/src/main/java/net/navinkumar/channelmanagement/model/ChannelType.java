package net.navinkumar.channelmanagement.model;

/**
 * Types of booking channels
 */
public enum ChannelType {
    OTA,           // Online Travel Agency (Booking.com, Expedia, etc.)
    GDS,           // Global Distribution System
    CHANNEL_MANAGER, // Channel Manager (SiteMinder, etc.)
    DIRECT,        // Direct booking
    CORPORATE,     // Corporate booking
    WHOLESALER,    // Wholesaler booking
    METASEARCH,    // Metasearch engines (Google, TripAdvisor)
    SOCIAL         // Social media platforms
}