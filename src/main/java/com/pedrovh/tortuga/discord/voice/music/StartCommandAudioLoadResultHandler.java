package com.pedrovh.tortuga.discord.voice.music;

import com.pedrovh.tortuga.discord.util.Constants;
import com.pedrovh.tortuga.discord.util.TrackUtil;
import com.pedrovh.tortuga.discord.voice.VoiceConnectionService;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import java.util.List;

@Slf4j
public class StartCommandAudioLoadResultHandler extends AbstractAudioLoadResultHandler {

    private final InteractionImmediateResponseBuilder responder;

    public StartCommandAudioLoadResultHandler(GuildAudioManager manager,
                                              VoiceConnectionService connectionService,
                                              ServerVoiceChannel voiceChannel,
                                              String identifier,
                                              InteractionImmediateResponseBuilder responder) {
        super(manager, connectionService, voiceChannel, identifier);
        this.responder = responder;
    }

    @Override
    protected void handleTrackLoaded(AudioTrack track) {
        manager.getScheduler().nextTrack(track, false);

        responder.addEmbed(TrackUtil.getPLayingEmbed(track))
                .respond();
    }

    @Override
    protected void handlePlaylistLoaded(AudioPlaylist playlist) {
        final List<AudioTrack> tracks = manager.getScheduler().addPlaylistToQueueStart(playlist);

        final StringBuilder sb = new StringBuilder();
        tracks.forEach(track -> sb.append(track.getInfo().title).append("\n"));

        manager.getScheduler().nextTrack();

        responder.addEmbed(
                        new EmbedBuilder()
                                .setTitle(String.format("%s %s Tracks added to the start of the queue", Constants.EMOJI_SONG, tracks.size()))
                                .setDescription(sb.toString())
                                .setColor(Constants.GREEN))
                .respond();
    }

    @Override
    protected void respondNoMatches(EmbedBuilder embed) {
        responder.addEmbed(embed).respond();
    }

    @Override
    protected void respondLoadFailed(EmbedBuilder embed) {
        responder.addEmbed(embed).respond();
    }

}