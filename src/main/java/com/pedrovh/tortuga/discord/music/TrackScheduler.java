package com.pedrovh.tortuga.discord.music;

import com.pedrovh.tortuga.discord.util.Constants;
import com.pedrovh.tortuga.discord.util.TrackUtil;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Getter
public class TrackScheduler extends AudioEventAdapter {

    private BlockingQueue<AudioTrack> queue;
    private final AudioPlayer player;
    private final String guildName;
    private final TextChannel textChannel;

    private boolean loop;

    public TrackScheduler(AudioPlayer player, String guildName, TextChannel textChannel) {
        this.player = player;
        this.guildName = guildName;
        this.textChannel = textChannel;
        this.queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Only start the next track if the end reason is suitable for it (FINISHED or LOAD_FAILED)
        if (endReason.mayStartNext) {
            if(loop)
                nextTrack(track, false);
            else
                nextTrack();
        }
    }

    /**
     * Add the next track to queue or play right away if nothing is in the queue.
     *
     * @param track  The track to play or add to queue.
     */
    public void queue(AudioTrack track) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            log.info("[{}] adding {} to queue: {}", guildName, track.getInfo().title, queue.offer(track));
            textChannel.sendMessage(
                            new EmbedBuilder()
                                    .setTitle(String.format("%s %s added to the queue", Constants.EMOJI_SONG, track.getInfo().title))
                                    .setDescription(track.getInfo().author)
                                    .setColor(Constants.GREEN));
        } else {
            log.info("[{}] playing {}", guildName, track.getInfo().title);
            textChannel.sendMessage(
                    new EmbedBuilder()
                            .setTitle(String.format(
                                    "%s [%s] Playing %s",
                                    Constants.EMOJI_SONG,
                                    TrackUtil.formatTrackDuration(track.getDuration()),
                                    track.getInfo().title))
                            .setFooter(track.getInfo().author)
                            .setColor(Constants.GREEN));
        }
    }

    public List<AudioTrack> queuePlaylist(AudioPlaylist playlist) {
        List<AudioTrack> adding = new ArrayList<>();
        List<AudioTrack> tracks = playlist.getTracks();
        AudioTrack selectedTrack = playlist.getSelectedTrack();
        log.info("[{}] adding playlist {} to queue", guildName, playlist.getName());

        final int index = selectedTrack != null ? tracks.indexOf(selectedTrack) : 0;
        for (int i = index; i < tracks.size(); i++) {
            AudioTrack track = tracks.get(i);
            adding.add(track);
            queue.offer(track);
        }
        if(player.getPlayingTrack() == null)
            nextTrack();

        return adding;
    }

    /**
     * Start the next track, stopping the current one if it is playing.
     */
    public void nextTrack() {
        nextTrack(queue.poll());
    }

    public void nextTrack(AudioTrack track) {
        nextTrack(track, true);
    }

    public void nextTrack(AudioTrack track, boolean notify) {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        player.startTrack(track, false);
        if(track == null)
            log.info("[{}] reached the end of the queue", guildName);
        else {
            log.info("[{}] playing {}", guildName, track.getInfo().title);
            if(notify)
                textChannel.sendMessage(TrackUtil.getPLayingEmbed(track));
        }
    }

    public void addToStart(List<AudioTrack> tracks) {
        tracks.addAll(queue);
        queue = new LinkedBlockingQueue<>(tracks);
    }

    public boolean toggleLoop() {
        loop = !loop;
        return loop;
    }

}