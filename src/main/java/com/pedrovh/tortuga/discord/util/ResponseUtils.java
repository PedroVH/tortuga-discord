package com.pedrovh.tortuga.discord.util;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;

@Slf4j
@UtilityClass
public class ResponseUtils {

    public EmbedBuilder getPLayingEmbed(AudioTrack track) {
        String duration = track.getInfo().isStream ? Constants.EMOJI_LIVE + "Live" : AudioTrackUtils.formatTimeDuration(track.getDuration() - track.getPosition());
        return new EmbedBuilder()
                .setTitle(String.format(
                        "%s [%s] %s",
                        Constants.EMOJI_SONG,
                        duration,
                        track.getInfo().title))
                .setFooter(track.getInfo().author)
                .setColor(Constants.GREEN);
    }

    public EmbedBuilder getAddedToPlaylistEmbed(AudioTrack track) {
        return new EmbedBuilder()
                .setTitle(String.format("%s %s %s", Constants.EMOJI_SONG, Constants.EMOJI_LIST, track.getInfo().title))
                .setDescription(track.getInfo().author)
                .setColor(Constants.GREEN);
    }

}
