package com.pedrovh.tortuga.discord.service.music.handler;

import com.pedrovh.tortuga.discord.music.GuildAudioManager;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.music.VoiceConnectionService;
import com.pedrovh.tortuga.discord.util.AudioTrackUtils;
import com.pedrovh.tortuga.discord.util.Constants;
import com.pedrovh.tortuga.discord.util.ResponseUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import java.util.List;

@Slf4j
public class NextCommandAudioLoadResultHandler extends AbstractCommandAudioLoadResultHandler {

    public NextCommandAudioLoadResultHandler(GuildAudioManager manager,
                                             VoiceConnectionService connectionService,
                                             ServerVoiceChannel channel,
                                             String identifier,
                                             MessageService messages,
                                             InteractionImmediateResponseBuilder responder) {
        super(manager, connectionService, channel, identifier, messages, responder);
    }

    @Override
    protected void handleTrackLoaded(AudioTrack track) {
        manager.getScheduler().addAsNextInQueue(track);

        if (manager.getPlayer().getPlayingTrack() != null)
            responder.addEmbed(ResponseUtils.getAddedToPlaylistEmbed(track))
                    .respond();
        else
            responder.addEmbed(ResponseUtils.getPLayingEmbed(track))
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
                                .setTitle(messages.get(server.getIdAsString(), "command.music.next.playlist.title", tracks.size()))
                                .setDescription(sb.toString())
                                .setColor(Constants.GREEN))
                .respond();
    }
}