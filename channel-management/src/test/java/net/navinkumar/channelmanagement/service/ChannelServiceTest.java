package net.navinkumar.channelmanagement.service;

import net.navinkumar.channelmanagement.model.Channel;
import net.navinkumar.channelmanagement.model.ChannelType;
import net.navinkumar.channelmanagement.repository.ChannelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private ChannelService channelService;

    private Channel testChannel;

    @BeforeEach
    void setUp() {
        testChannel = new Channel();
        testChannel.setId(1L);
        testChannel.setChannelCode("BOOKING_COM");
        testChannel.setChannelName("Booking.com");
        testChannel.setChannelType(ChannelType.OTA);
        testChannel.setActive(true);
        testChannel.setCommissionPercentage(15.0);
    }

    @Test
    void getActiveChannels_ShouldReturnActiveChannels() {
        // Given
        List<Channel> expectedChannels = Arrays.asList(testChannel);
        when(channelRepository.findByActiveTrue()).thenReturn(expectedChannels);

        // When
        List<Channel> actualChannels = channelService.getActiveChannels();

        // Then
        assertEquals(expectedChannels, actualChannels);
        verify(channelRepository).findByActiveTrue();
    }

    @Test
    void getChannelByCode_ShouldReturnChannel_WhenExists() {
        // Given
        when(channelRepository.findByChannelCode("BOOKING_COM")).thenReturn(Optional.of(testChannel));

        // When
        Optional<Channel> result = channelService.getChannelByCode("BOOKING_COM");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testChannel, result.get());
        verify(channelRepository).findByChannelCode("BOOKING_COM");
    }

    @Test
    void getChannelByCode_ShouldReturnEmpty_WhenNotExists() {
        // Given
        when(channelRepository.findByChannelCode("NON_EXISTENT")).thenReturn(Optional.empty());

        // When
        Optional<Channel> result = channelService.getChannelByCode("NON_EXISTENT");

        // Then
        assertFalse(result.isPresent());
        verify(channelRepository).findByChannelCode("NON_EXISTENT");
    }

    @Test
    void createChannel_ShouldSaveAndReturnChannel() {
        // Given
        when(channelRepository.save(any(Channel.class))).thenReturn(testChannel);

        // When
        Channel result = channelService.createChannel(testChannel);

        // Then
        assertEquals(testChannel, result);
        verify(channelRepository).save(testChannel);
    }

    @Test
    void getChannelsByType_ShouldReturnChannelsByType() {
        // Given
        List<Channel> expectedChannels = Arrays.asList(testChannel);
        when(channelRepository.findByChannelTypeAndActiveTrue(ChannelType.OTA)).thenReturn(expectedChannels);

        // When
        List<Channel> actualChannels = channelService.getChannelsByType(ChannelType.OTA);

        // Then
        assertEquals(expectedChannels, actualChannels);
        verify(channelRepository).findByChannelTypeAndActiveTrue(ChannelType.OTA);
    }
}