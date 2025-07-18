package net.navinkumar.channelmanagement.service;

import net.navinkumar.channelmanagement.model.Channel;
import net.navinkumar.channelmanagement.model.ChannelType;
import net.navinkumar.channelmanagement.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing booking channels
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService {
    
    private final ChannelRepository channelRepository;
    
    /**
     * Get all active channels
     */
    public List<Channel> getActiveChannels() {
        return channelRepository.findByActiveTrue();
    }
    
    /**
     * Get channel by code
     */
    public Optional<Channel> getChannelByCode(String channelCode) {
        return channelRepository.findByChannelCode(channelCode);
    }
    
    /**
     * Get channels by type
     */
    public List<Channel> getChannelsByType(ChannelType channelType) {
        return channelRepository.findByChannelTypeAndActiveTrue(channelType);
    }
    
    /**
     * Create new channel
     */
    public Channel createChannel(Channel channel) {
        log.info("Creating new channel: {}", channel.getChannelCode());
        return channelRepository.save(channel);
    }
    
    /**
     * Update existing channel
     */
    public Channel updateChannel(Long channelId, Channel channelUpdate) {
        return channelRepository.findById(channelId)
            .map(existingChannel -> {
                existingChannel.setChannelName(channelUpdate.getChannelName());
                existingChannel.setChannelType(channelUpdate.getChannelType());
                existingChannel.setActive(channelUpdate.getActive());
                existingChannel.setApiEndpoint(channelUpdate.getApiEndpoint());
                existingChannel.setUsername(channelUpdate.getUsername());
                existingChannel.setPassword(channelUpdate.getPassword());
                existingChannel.setApiKey(channelUpdate.getApiKey());
                existingChannel.setConfiguration(channelUpdate.getConfiguration());
                existingChannel.setCommissionPercentage(channelUpdate.getCommissionPercentage());
                log.info("Updating channel: {}", existingChannel.getChannelCode());
                return channelRepository.save(existingChannel);
            })
            .orElseThrow(() -> new RuntimeException("Channel not found with id: " + channelId));
    }
    
    /**
     * Activate/Deactivate channel
     */
    public Channel toggleChannelStatus(Long channelId, boolean active) {
        return channelRepository.findById(channelId)
            .map(channel -> {
                channel.setActive(active);
                log.info("Channel {} status changed to: {}", channel.getChannelCode(), active);
                return channelRepository.save(channel);
            })
            .orElseThrow(() -> new RuntimeException("Channel not found with id: " + channelId));
    }
}