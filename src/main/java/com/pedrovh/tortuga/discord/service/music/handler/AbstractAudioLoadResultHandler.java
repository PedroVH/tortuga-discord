package com.pedrovh.tortuga.discord.service.music.handler;

import com.pedrovh.tortuga.discord.music.GuildAudioManager;
import com.pedrovh.tortuga.discord.service.music.VoiceConnectionService;
import com.pedrovh.tortuga.discord.util.Constants;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;

@Slf4j
public abstract class AbstractAudioLoadResultHandler implements AudioLoadResultHandler {

    protected final GuildAudioManager manager;
    protected final VoiceConnectionService connectionService;
    protected final ServerVoiceChannel voiceChannel;
    protected final String identifier;
    protected final Server server;

    public AbstractAudioLoadResultHandler(GuildAudioManager manager,
                                         VoiceConnectionService connectionService,
                                         ServerVoiceChannel voiceChannel,
                                         String identifier) {
        this.manager = manager;
        this.connectionService = connectionService;
        this.voiceChannel = voiceChannel;
        this.identifier = identifier;

        this.server = voiceChannel.getServer();
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        connectionService.createAudioConnection(voiceChannel, manager.getSource());
        handleTrackLoaded(track);
    }

    protected abstract void handleTrackLoaded(AudioTrack track);

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        // start
        if(playlist.isSearchResult()) {
            trackLoaded(playlist.getTracks().get(0));
            return;
        }

        connectionService.createAudioConnection(voiceChannel, manager.getSource());

        handlePlaylistLoaded(playlist);
    }

    protected abstract void handlePlaylistLoaded(AudioPlaylist playlist);

    @Override
    public void noMatches() {
        // start
        log.warn("[{}] no matches found for {}", server, identifier);
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(String.format("️%s No matches found...", Constants.EMOJI_WARNING))
                .setColor(Constants.YELLOW);

        respondNoMatches(embed);
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        log.warn("[{}] error loading item: {}", server.getName(), exception.getMessage());
        // load
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(Constants.TITLE_ERROR)
                .setDescription(exception.getMessage())
                .setColor(Constants.RED);

        respondLoadFailed(embed);
    }

    protected abstract void respondNoMatches(EmbedBuilder embed);
    protected abstract void respondLoadFailed(EmbedBuilder embed);

}
