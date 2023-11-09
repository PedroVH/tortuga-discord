package com.pedrovh.tortuga.discord.service.music.handler;

import com.pedrovh.tortuga.discord.music.GuildAudioManager;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
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
    protected final MessageService messages;
    protected final Server server;
    protected final long atPosition;

    public AbstractAudioLoadResultHandler(GuildAudioManager manager,
                                          VoiceConnectionService connectionService,
                                          ServerVoiceChannel voiceChannel,
                                          String identifier,
                                          MessageService messages,
                                          long atPosition) {
        this.manager = manager;
        this.connectionService = connectionService;
        this.voiceChannel = voiceChannel;
        this.identifier = identifier;
        this.messages = messages;
        this.server = voiceChannel.getServer();
        this.atPosition = atPosition;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        connectionService.createAudioConnection(voiceChannel, manager.getSource());
        track.setPosition(atPosition);
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
                .setTitle(messages.get(server.getIdAsString(), "command.music.loading.warn.no-matches.title"))
                .setColor(Constants.YELLOW);

        respondNoMatches(embed);
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        log.warn("[{}] error loading item: {}", server.getName(), exception.getMessage());
        // load
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(messages.get(server.getIdAsString(), "command.music.loading.error.failed.title"))
                .setDescription(exception.getMessage())
                .setColor(Constants.RED);

        respondLoadFailed(embed);
    }

    protected abstract void respondNoMatches(EmbedBuilder embed);
    protected abstract void respondLoadFailed(EmbedBuilder embed);

}
