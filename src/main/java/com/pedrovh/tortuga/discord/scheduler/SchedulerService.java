package com.pedrovh.tortuga.discord.scheduler;

import com.pedrovh.tortuga.discord.music.service.VoiceConnectionService;
import com.pedrovh.tortuga.discord.music.GuildAudioManager;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Singleton
public class SchedulerService {

    @Value("${bot.disconnect.after}")
    private Long disconnectAfter;
    private final VoiceConnectionService service;

    public SchedulerService(VoiceConnectionService service) {
        this.service = service;
    }

    @Scheduled(fixedDelay = "1m", initialDelay = "1m")
    void executeEveryMinute() {
        log.trace("looking for inactive voice connections...");

        for (GuildAudioManager value : service.getAudioManagers().values()) {
            Optional<Instant> endOfQueue = value.getScheduler().getLatestEndOfQueue();
            if(endOfQueue.isEmpty())
                continue;

            Duration duration = Duration.between(endOfQueue.get(), Instant.now());
            if(duration.compareTo(Duration.ofMinutes(disconnectAfter)) < 0)
                continue;

            Server server = value.getServer();
            Optional<ServerVoiceChannel> voiceChannel = server.getConnectedVoiceChannel(server.getApi().getYourself());
            if(voiceChannel.isEmpty())
                continue;

            service.leaveVoiceChannel(voiceChannel.get());
            log.info("[{}] leaving voice channel for inactivity", server.getName());
        }
    }

}
