package com.pedrovh.tortuga.discord.music;

import com.pedrovh.tortuga.discord.util.AudioTrackUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

@Slf4j
public class StartCommandAudioLoadResultHandler extends NextCommandAudioLoadResultHandler {

    public StartCommandAudioLoadResultHandler(GuildAudioManager manager,
                                              VoiceConnectionService connectionService,
                                              ServerVoiceChannel voiceChannel,
                                              String identifier,
                                              InteractionImmediateResponseBuilder responder) {
        super(manager, connectionService, voiceChannel, identifier, responder);
    }

    @Override
    protected void handleTrackLoaded(AudioTrack track) {
        manager.getScheduler().nextTrack(track, false);

        responder.addEmbed(AudioTrackUtils.getPLayingEmbed(track))
                .respond();
    }

    @Override
    protected void handlePlaylistLoaded(AudioPlaylist playlist) {
        super.handlePlaylistLoaded(playlist);
        manager.getScheduler().nextTrack();
    }

}