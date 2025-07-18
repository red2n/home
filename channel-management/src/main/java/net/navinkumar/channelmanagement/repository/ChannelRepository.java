package net.navinkumar.channelmanagement.repository;

import net.navinkumar.channelmanagement.model.Channel;
import net.navinkumar.channelmanagement.model.ChannelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Channel entity
 */
@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    
    Optional<Channel> findByChannelCode(String channelCode);
    
    List<Channel> findByChannelType(ChannelType channelType);
    
    List<Channel> findByActiveTrue();
    
    List<Channel> findByChannelTypeAndActiveTrue(ChannelType channelType);
}