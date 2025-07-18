package net.navinkumar.channelmanagement.controller;

import net.navinkumar.channelmanagement.model.Channel;
import net.navinkumar.channelmanagement.model.ChannelType;
import net.navinkumar.channelmanagement.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Channel Management operations
 */
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {
    
    private final ChannelService channelService;
    
    /**
     * Get all active channels
     */
    @GetMapping
    public ResponseEntity<List<Channel>> getActiveChannels() {
        List<Channel> channels = channelService.getActiveChannels();
        return ResponseEntity.ok(channels);
    }
    
    /**
     * Get channel by code
     */
    @GetMapping("/{channelCode}")
    public ResponseEntity<Channel> getChannelByCode(@PathVariable String channelCode) {
        return channelService.getChannelByCode(channelCode)
            .map(channel -> ResponseEntity.ok(channel))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get channels by type
     */
    @GetMapping("/type/{channelType}")
    public ResponseEntity<List<Channel>> getChannelsByType(@PathVariable ChannelType channelType) {
        List<Channel> channels = channelService.getChannelsByType(channelType);
        return ResponseEntity.ok(channels);
    }
    
    /**
     * Create new channel
     */
    @PostMapping
    public ResponseEntity<Channel> createChannel(@RequestBody Channel channel) {
        Channel createdChannel = channelService.createChannel(channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel);
    }
    
    /**
     * Update existing channel
     */
    @PutMapping("/{channelId}")
    public ResponseEntity<Channel> updateChannel(@PathVariable Long channelId, @RequestBody Channel channel) {
        try {
            Channel updatedChannel = channelService.updateChannel(channelId, channel);
            return ResponseEntity.ok(updatedChannel);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Toggle channel status (activate/deactivate)
     */
    @PatchMapping("/{channelId}/status")
    public ResponseEntity<Channel> toggleChannelStatus(@PathVariable Long channelId, @RequestParam boolean active) {
        try {
            Channel updatedChannel = channelService.toggleChannelStatus(channelId, active);
            return ResponseEntity.ok(updatedChannel);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}