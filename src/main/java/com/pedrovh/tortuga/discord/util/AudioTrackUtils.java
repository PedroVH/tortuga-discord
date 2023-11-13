package com.pedrovh.tortuga.discord.util;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@UtilityClass
public class AudioTrackUtils {

    public String formatTrackDuration(Long millis) {
        Duration duration = Duration.ofMillis(millis);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return hours > 0 ?
                String.format("%d:%02d:%02d", hours, minutes, seconds) :
                String.format("%02d:%02d", minutes, seconds);
    }

    public static List<AudioTrack> getTracksAfterSelectedTrack(AudioPlaylist playlist) {
        List<AudioTrack> filtered = new ArrayList<>();
        List<AudioTrack> tracks = playlist.getTracks();
        AudioTrack selectedTrack = playlist.getSelectedTrack();

        final int index = selectedTrack != null ? tracks.indexOf(selectedTrack) : 0;
        for (int i = index; i < tracks.size(); i++) {
            AudioTrack track = tracks.get(i);
            filtered.add(track);
        }
        return filtered;
    }

}
