package com.pedrovh.tortuga.discord.voice;

import com.pedrovh.tortuga.discord.music.GuildAudioManager;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioConnection;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Singleton
public class VoiceConnectionService {

    @Value("${google.user}")
    private String googleUser;
    @Value("${google.password}")
    private String googlePassword;
    @Getter
    private AudioPlayerManager playerManager;
    @Getter
    private final ConcurrentHashMap<String, GuildAudioManager> audioManagers = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<String, AudioConnection> connections = new ConcurrentHashMap<>();

    @PostConstruct
    public void configure() {
        playerManager = new DefaultAudioPlayerManager();

        playerManager.getConfiguration().setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);

        playerManager.registerSourceManager(new YoutubeAudioSourceManager(true, googleUser, googlePassword));
        playerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new VimeoAudioSourceManager());
        playerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        playerManager.registerSourceManager(new BeamAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());
    }

    public void leaveVoiceChannel(ServerVoiceChannel voiceChannel) {
        voiceChannel.disconnect().join();
    }

    public AudioConnection getAudioConnection(ServerVoiceChannel channel, AudioSource source) {
        connections.computeIfAbsent(channel.getServer().getIdAsString(), (f) -> {
            log.info("[{}] connecting to voice channel {}", channel.getServer().getName(), channel.getName());
            return channel.connect().join();
        });
        AudioConnection connection = connections.get(channel.getServer().getIdAsString());

        if(connection.getAudioSource().isEmpty())
            connection.setAudioSource(source);

        return connection;
    }

    public GuildAudioManager getGuildAudioManager(DiscordApi api, ServerVoiceChannel channel) {
        String guildId = channel.getServer().getIdAsString();
        if(audioManagers.containsKey(guildId))
            return audioManagers.get(guildId);

        GuildAudioManager audioManager = new GuildAudioManager(api, playerManager, channel.getServer());
        audioManagers.put(guildId, audioManager);
        return audioManager;
    }

    public Optional<GuildAudioManager> getGuildAudioManager(String guildId) {
        return Optional.of(audioManagers.get(guildId));
    }

}
