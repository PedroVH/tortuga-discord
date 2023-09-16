package com.pedrovh.tortuga.discord.music;

import com.pedrovh.tortuga.discord.util.AudioTrackUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.server.Server;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Getter
public class TrackScheduler extends AudioEventAdapter {

    private final BlockingQueue<AudioTrack> queue;
    private final AudioPlayer player;
    private final Server server;
    private final TextChannel textChannel;

    private boolean loop;
    private Instant latestEndOfQueue;

    public TrackScheduler(AudioPlayer player, Server server, TextChannel textChannel) {
        this.player = player;
        this.server = server;
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
        queue(track, true);
    }

    public void queue(AudioTrack track, boolean notify) {
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            log.info("[{}] adding {} to queue: {}", server.getName(), track.getInfo().title, queue.offer(track));
            if(notify)
                textChannel.sendMessage(AudioTrackUtils.getAddedToPlaylistEmbed(track));
        } else {
            latestEndOfQueue = null;
            log.info("[{}] playing {}", server.getName(), track.getInfo().title);
            if(notify)
                textChannel.sendMessage(AudioTrackUtils.getPLayingEmbed(track));
        }
    }

    public List<AudioTrack> removeFromQueue(final int initPos, final int endPos) throws IndexOutOfBoundsException {
        log.info("[{}] removing {} to {} from queue", server.getName(), initPos, endPos);

        List<AudioTrack> queueList = new ArrayList<>(queue);
        List<AudioTrack> removed = new ArrayList<>(queueList.subList(initPos, endPos));

        queue.removeAll(removed);
        return removed;
    }

    public void clearQueue() {
        queue.clear();
    }

    public List<AudioTrack> queuePlaylist(AudioPlaylist playlist) {
        List<AudioTrack> tracks = AudioTrackUtils.getTracksAfterSelectedTrack(playlist);

        log.info("[{}] adding playlist {} to queue", server.getName(), playlist.getName());
        tracks.forEach(queue::offer);

        if(player.getPlayingTrack() == null)
            nextTrack();

        return tracks;
    }

    public void addAsNextInQueue(AudioTrack... tracks) {
        List<AudioTrack> queueList = new ArrayList<>();
        queueList.addAll(Arrays.stream(tracks).toList());
        queueList.addAll(queue);

        queue.clear();
        queue.addAll(queueList);
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
        if(track == null) {
            log.info("[{}] reached the end of the queue", server.getName());
            latestEndOfQueue = Instant.now();
        } else {
            log.info("[{}] playing {}", server.getName(), track.getInfo().title);
            latestEndOfQueue = null;
            if(notify)
                textChannel.sendMessage(AudioTrackUtils.getPLayingEmbed(track));
        }
    }

    public boolean toggleLoop() {
        loop = !loop;
        return loop;
    }

    public Optional<Instant> getLatestEndOfQueue() {
        return Optional.ofNullable(latestEndOfQueue);
    }

}