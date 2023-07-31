package com.pedrovh.tortuga.discord.voice.music;

import com.pedrovh.tortuga.discord.guild.service.GuildPreferencesService;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import lombok.Getter;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;

@Getter
public class GuildAudioManager {

    private final AudioPlayer player;
    private final TrackScheduler scheduler;
    private final LavaplayerAudioSource source;
    private final Server server;

    private TextChannel defaultTextChannel;

    public GuildAudioManager(final DiscordApi api, final AudioPlayerManager manager, final Server server) {
        new GuildPreferencesService().findById(server.getIdAsString()).ifPresent(p -> {
            String id = p.getMusicChannelId();
            defaultTextChannel = api.getServerTextChannelById(id).orElse(null);
        });

        player = manager.createPlayer();
        scheduler = new TrackScheduler(player, server, defaultTextChannel);
        player.addListener(scheduler);
        source = new LavaplayerAudioSource(api, player);
        this.server = server;
    }

}
