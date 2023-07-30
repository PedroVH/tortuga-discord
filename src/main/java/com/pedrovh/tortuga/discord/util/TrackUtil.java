package com.pedrovh.tortuga.discord.util;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.time.Duration;

@Slf4j
@UtilityClass
public class TrackUtil {

    public String formatTrackDuration(Long millis) {
        Duration duration = Duration.ofMillis(millis);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return hours > 0 ?
                String.format("%d:%02d:%02d", hours, minutes, seconds) :
                String.format("%02d:%02d", minutes, seconds);
    }

    public EmbedBuilder getPLayingEmbed(AudioTrack track) {
        return new EmbedBuilder()
                .setTitle(String.format(
                        "%s [%s] Playing %s",
                        Constants.EMOJI_SONG,
                        formatTrackDuration(track.getDuration()),
                        track.getInfo().title))
                .setFooter(track.getInfo().author)
                .setColor(Constants.GREEN);
    }

}
