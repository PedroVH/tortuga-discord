package com.pedrovh.tortuga.discord.voice.music;

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
import org.javacord.api.entity.server.Server;

import java.time.Instant;
import java.util.ArrayList;
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
        // Calling startTrack with the noInterrupt set to true will start the track only if nothing is currently playing. If
        // something is playing, it returns false and does nothing. In that case the player was already playing so this
        // track goes to the queue instead.
        if (!player.startTrack(track, true)) {
            log.info("[{}] adding {} to queue: {}", server.getName(), track.getInfo().title, queue.offer(track));
            textChannel.sendMessage(
                            new EmbedBuilder()
                                    .setTitle(String.format("%s %s added to the queue", Constants.EMOJI_SONG, track.getInfo().title))
                                    .setDescription(track.getInfo().author)
                                    .setColor(Constants.GREEN));
        } else {
            log.info("[{}] playing {}", server.getName(), track.getInfo().title);
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
        List<AudioTrack> tracks = getPlaylistAfterSelectedTrack(playlist);

        log.info("[{}] adding playlist {} to queue", server.getName(), playlist.getName());
        tracks.forEach(queue::offer);

        if(player.getPlayingTrack() == null)
            nextTrack();

        return tracks;
    }

    public List<AudioTrack> addPlaylistToQueueStart(AudioPlaylist playlist) {
        List<AudioTrack> tracks = getPlaylistAfterSelectedTrack(playlist);
        log.info("[{}] adding playlist {} to the start of the queue", server.getName(), playlist.getName());

        List<AudioTrack> queueList = new ArrayList<>();
        queueList.addAll(tracks);
        queueList.addAll(queue);

        queue.clear();
        queue.addAll(queueList);

        return tracks;
    }

    private List<AudioTrack> getPlaylistAfterSelectedTrack(AudioPlaylist playlist) {
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

    public void clearQueue() {
        queue.clear();
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
            if(notify)
                textChannel.sendMessage(TrackUtil.getPLayingEmbed(track));
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