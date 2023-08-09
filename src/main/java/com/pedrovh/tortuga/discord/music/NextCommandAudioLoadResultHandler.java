package com.pedrovh.tortuga.discord.music;

import com.pedrovh.tortuga.discord.util.AudioTrackUtils;
import com.pedrovh.tortuga.discord.util.Constants;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import java.util.List;

@Slf4j
public class NextCommandAudioLoadResultHandler extends AbstractAudioLoadResultHandler {

    protected final InteractionImmediateResponseBuilder responder;

    public NextCommandAudioLoadResultHandler(GuildAudioManager manager,
                                             VoiceConnectionService connectionService,
                                             ServerVoiceChannel channel,
                                             String identifier,
                                             InteractionImmediateResponseBuilder responder) {
        super(manager, connectionService, channel, identifier);
        this.responder = responder;
    }

    @Override
    protected void handleTrackLoaded(AudioTrack track) {
        manager.getScheduler().addAsNextInQueue(track);

        if(manager.getPlayer().getPlayingTrack() != null)
            responder.addEmbed(AudioTrackUtils.getAddedToPlaylistEmbed(track))
                    .respond();
        else
            responder.addEmbed(AudioTrackUtils.getPLayingEmbed(track))
                .respond();
    }

    @Override
    protected void handlePlaylistLoaded(AudioPlaylist playlist) {
        List<AudioTrack> tracks = AudioTrackUtils.getTracksAfterSelectedTrack(playlist);
        log.info("[{}] adding playlist {} to the start of the queue", server.getName(), playlist.getName());

        final StringBuilder sb = new StringBuilder();
        tracks.forEach(track -> sb.append(track.getInfo().title).append("\n"));

        manager.getScheduler().addAsNextInQueue(tracks.toArray(new AudioTrack[0]));

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
