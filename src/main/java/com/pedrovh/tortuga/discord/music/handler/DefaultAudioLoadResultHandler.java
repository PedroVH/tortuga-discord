package com.pedrovh.tortuga.discord.music.handler;

import com.pedrovh.tortuga.discord.music.GuildAudioManager;
import com.pedrovh.tortuga.discord.music.service.VoiceConnectionService;
import com.pedrovh.tortuga.discord.util.Constants;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.List;

@Slf4j
public class DefaultAudioLoadResultHandler extends AbstractAudioLoadResultHandler {

    private final Message message;

    public DefaultAudioLoadResultHandler(GuildAudioManager manager,
                                         VoiceConnectionService connectionService,
                                         ServerVoiceChannel voiceChannel,
                                         String identifier,
                                         Message message) {
        super(manager, connectionService, voiceChannel, identifier);

        this.message = message;
    }

    @Override
    protected void handleTrackLoaded(AudioTrack track) {
        manager.getScheduler().queue(track);
    }

    @Override
    protected void handlePlaylistLoaded(AudioPlaylist playlist) {
        List<AudioTrack> tracks = manager.getScheduler().queuePlaylist(playlist);
        StringBuilder sb = new StringBuilder();
        tracks.forEach(track -> sb.append(track.getInfo().title).append("\n"));

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(String.format("%s %s Tacks added to the queue", Constants.EMOJI_SONG, tracks.size()))
                .setDescription(sb.toString())
                .setColor(Constants.GREEN);

        new MessageBuilder()
                .addEmbed(embed)
                .send(message.getChannel());
    }

    @Override
    protected void respondNoMatches(EmbedBuilder embed) {
        new MessageBuilder().addEmbed(embed).send(message.getChannel());
    }

    @Override
    protected void respondLoadFailed(EmbedBuilder embed) {
        new MessageBuilder().addEmbed(embed).send(message.getChannel());
    }
}