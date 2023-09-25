package com.pedrovh.tortuga.discord.service.music.handler;

import com.pedrovh.tortuga.discord.music.GuildAudioManager;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.music.VoiceConnectionService;
import com.pedrovh.tortuga.discord.util.Constants;
import com.pedrovh.tortuga.discord.util.ResponseUtils;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

@Slf4j
public class ReplaceCommandAudioLoadResultHandler extends NextCommandAudioLoadResultHandler {

    private final Long position;

    public ReplaceCommandAudioLoadResultHandler(GuildAudioManager manager,
                                                VoiceConnectionService connectionService,
                                                ServerVoiceChannel voiceChannel,
                                                String identifier,
                                                MessageService messages,
                                                InteractionImmediateResponseBuilder responder,
                                                Long position) {
        super(manager, connectionService, voiceChannel, identifier, messages, responder);
        this.position = position;
    }

    @Override
    protected void handleTrackLoaded(AudioTrack track) {
        if(position == -1) {
            manager.getScheduler().nextTrack(track, false);
            responder.addEmbed(ResponseUtils.getPLayingEmbed(track))
                    .respond();
            return;
        }
        manager.getScheduler().replaceTrack(position.intValue(), track);
        responder.addEmbed(new EmbedBuilder()
                        .setTitle(messages.get(server.getIdAsString(), "command.music.replace.track.title", position+1, track.getInfo().title))
                        .setFooter(track.getInfo().author)
                        .setColor(Constants.GREEN))
                .respond();
    }

    @Override
    protected void handlePlaylistLoaded(AudioPlaylist playlist) {
        if(position == 0) {
            super.handlePlaylistLoaded(playlist);
            manager.getScheduler().nextTrack();
            return;
        }
        manager.getScheduler().replaceTrack(position.intValue(), playlist);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < playlist.getTracks().size(); i++)
            sb.append(i+1).append(". ").append(playlist.getTracks().get(i)).append("\n");

        responder.addEmbed(new EmbedBuilder()
                        .setTitle(messages.get(server.getIdAsString(), "command.music.replace.playlist.title", position+1, playlist.getName()))
                        .setDescription(sb.toString())
                        .setColor(Constants.GREEN))
                .respond();
    }

}