package com.pedrovh.tortuga.discord.music;

import com.pedrovh.tortuga.discord.util.Constants;
import com.pedrovh.tortuga.discord.util.TrackUtil;
import com.pedrovh.tortuga.discord.voice.VoiceConnectionService;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;

@Slf4j
@Singleton
public class MusicService {

    private final VoiceConnectionService connectionService;

    public MusicService(VoiceConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    public void handle(final DiscordApi api, final Message message) {
        final MessageBuilder response = new MessageBuilder();
        try{
            final Server guild = message.getServer().orElseThrow(RuntimeException::new);
            final User author = message.getUserAuthor().orElseThrow();

            String identifier = getIdentifier(message.getContent());

            loadAndPlay(api, identifier, message, author.getConnectedVoiceChannel(guild).orElseThrow(), response);
        } catch (NoSuchElementException e) {
            log.warn("error handling music: {}", e.getMessage());
            response.addEmbed(
                    new EmbedBuilder()
                            .setTitle(Constants.TITLE_ERROR)
                            .setDescription("You have to be in a voice channel!")
                            .setColor(Constants.RED))
                    .send(message.getChannel());
        }
        catch (Exception e) {
            log.warn("error handling music: {}", e.getMessage());
            response.addEmbed(
                    new EmbedBuilder()
                            .setTitle(Constants.TITLE_ERROR)
                            .setDescription(e.getMessage())
                            .setColor(Constants.RED))
                    .send(message.getChannel());
        }
    }

    public void next(final DiscordApi api, final ServerVoiceChannel voiceChannel, InteractionImmediateResponseBuilder response) {
        GuildAudioManager manager = connectionService.getGuildAudioManager(api, voiceChannel);
        manager.getScheduler().nextTrack();
        response
                .addEmbed(new EmbedBuilder()
                        .setTitle(String.format("%s Skipped", Constants.EMOJI_SUCCESS))
                        .setColor(Constants.GREEN))
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

    public void pause(final Server guild, final InteractionImmediateResponseBuilder response) {
        Optional<GuildAudioManager> audioManager = connectionService.getGuildAudioManager(guild.getIdAsString());
        if(audioManager.isPresent()) {
            GuildAudioManager manager = audioManager.get();
            boolean isPaused = !manager.getPlayer().isPaused();
            String paused = isPaused?"Paused":"Unpaused";
            manager.getPlayer().setPaused(isPaused);

            log.info("[{}] {}", guild.getName(), paused);

            response.addEmbed(
                    new EmbedBuilder()
                            .setTitle(String.format("%s %s!", Constants.EMOJI_SUCCESS, paused))
                            .setColor(Constants.GREEN))
                    .respond();
        } else {
            log.info("[{}] unable to pause: GuildAudioManager not found", guild.getName());
            response.addEmbed(
                    new EmbedBuilder()
                            .setTitle(String.format("%s There's nothing to pause!", Constants.EMOJI_ERROR))
                            .setColor(Constants.YELLOW))
                    .respond();
        }
    }

    public void loop(final DiscordApi api, final ServerVoiceChannel voiceChannel, final InteractionImmediateResponseBuilder response) {
        GuildAudioManager manager = connectionService.getGuildAudioManager(api, voiceChannel);
        boolean loop = manager.getScheduler().toggleLoop();
        String enabled = loop?"enabled":"disabled";
        log.info("[{}] looping {} for this server", voiceChannel.getServer().getName(), enabled);
        response.addEmbed(
                new EmbedBuilder()
                        .setTitle(String.format("%s Looping %s!", Constants.EMOJI_SUCCESS, enabled))
                        .setColor(Constants.GREEN))
                .respond();
    }

    public void queue(Server guild, final InteractionImmediateResponseBuilder response) {
        Optional<GuildAudioManager> audioManager = connectionService.getGuildAudioManager(guild.getIdAsString());
        if(audioManager.isPresent()) {
            GuildAudioManager manager = audioManager.get();
            Queue<AudioTrack> queue = manager.getScheduler().getQueue();
            StringBuilder sb = new StringBuilder();

            AudioTrack currentTrack = manager.getPlayer().getPlayingTrack();

            if(currentTrack == null) {
                response.addEmbed(
                                new EmbedBuilder()
                                        .setTitle(String.format("%s Queue is empty!", Constants.EMOJI_SUCCESS))
                                        .setColor(Constants.GREEN))
                        .respond();
                return;
            }
            sb.append(". ").append(currentTrack.getInfo().title);

            long totalTimeMs = currentTrack.getDuration() - currentTrack.getPosition();

            int count = 1;
            for(AudioTrack track : queue) {
                sb.append(count++).append(". ").append(track.getInfo().title).append("\n");
                totalTimeMs += track.getDuration();
            }

            String totalTime = TrackUtil.formatTrackDuration(totalTimeMs);

            response.addEmbed(
                            new EmbedBuilder()
                                    .setTitle(String.format("%s Current queue", Constants.EMOJI_SUCCESS))
                                    .setDescription(sb.toString())
                                    .setColor(Constants.GREEN)
                                    .setFooter(String.format("Time left: %s", totalTime))
                                    .setTimestamp(Instant.now().plus(totalTimeMs, ChronoUnit.MILLIS)))
                    .respond();
        } else {
            log.info("[{}] unable to display queue: GuildAudioManager not found", guild.getName());
            response.addEmbed(
                            new EmbedBuilder()
                                    .setTitle(String.format("%s There's no queue!", Constants.EMOJI_ERROR))
                                    .setColor(Constants.YELLOW))
                    .respond();
        }
    }

    private String getIdentifier(String content) {
        try {
            new URL(content);
        } catch (MalformedURLException ex) {
            log.debug("{} is youtube search...", content);
            return Constants.YOUTUBE_QUERY.concat(content);
        }
        return content;
    }

    private void loadAndPlay(final DiscordApi api, final String identifier, final Message message, final ServerVoiceChannel channel, final MessageBuilder response) {
        final String guild = channel.getServer().getName();
        final GuildAudioManager manager = connectionService.getGuildAudioManager(api, channel);

        connectionService.getPlayerManager().loadItemOrdered(manager, identifier, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                connectionService.getAudioConnection(channel, manager.getSource());
                manager.getScheduler().queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if(playlist.isSearchResult()) {
                    trackLoaded(playlist.getTracks().get(0));
                    return;
                }

                connectionService.getAudioConnection(channel, manager.getSource());

                List<AudioTrack> tracks = manager.getScheduler().queuePlaylist(playlist);
                StringBuilder sb = new StringBuilder();
                tracks.forEach(track -> sb.append(track.getInfo().title).append("\n"));

                response.addEmbed(
                        new EmbedBuilder()
                                .setTitle(String.format("%s %s Tracks added to the queue", Constants.EMOJI_SONG, tracks.size()))
                                .setDescription(sb.toString())
                                .setColor(Constants.GREEN))
                        .send(message.getChannel());
            }

            @Override
            public void noMatches() {
                log.warn("[{}] no matches found for {}", guild, identifier);
                response.addEmbed(
                        new EmbedBuilder()
                                .setTitle(String.format("️%s No matches found...", Constants.EMOJI_ERROR))
                                .setColor(Constants.YELLOW))
                        .send(message.getChannel());
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                log.warn("[{}] error loading item: {}", guild, exception.getMessage());
                response.addEmbed(
                        new EmbedBuilder()
                                .setTitle(Constants.TITLE_ERROR)
                                .setDescription(exception.getMessage())
                                .setColor(Constants.RED))
                        .send(message.getChannel());
            }
        });
    }

    public void start(final DiscordApi api, final ServerVoiceChannel voiceChannel, final String query, InteractionImmediateResponseBuilder responder) {
        Server guild = voiceChannel.getServer();
        GuildAudioManager manager = connectionService.getGuildAudioManager(api, voiceChannel);
        String identifier = getIdentifier(query);

        connectionService.getPlayerManager().loadItemOrdered(manager, identifier, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                connectionService.getAudioConnection(voiceChannel, manager.getSource());
                manager.getScheduler().nextTrack(track, false);

                responder.addEmbed(TrackUtil.getPLayingEmbed(track))
                        .respond();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if(playlist.isSearchResult()) {
                    trackLoaded(playlist.getTracks().get(0));
                    return;
                }

                connectionService.getAudioConnection(voiceChannel, manager.getSource());

                List<AudioTrack> tracks = manager.getScheduler().queuePlaylist(playlist);
                StringBuilder sb = new StringBuilder();
                tracks.forEach(track -> sb.append(track.getInfo().title).append("\n"));

                manager.getScheduler().addToStart(tracks);
                manager.getScheduler().nextTrack();

                responder.addEmbed(
                                new EmbedBuilder()
                                        .setTitle(String.format("%s %s Tracks added to the start of the queue", Constants.EMOJI_SONG, tracks.size()))
                                        .setDescription(sb.toString())
                                        .setColor(Constants.GREEN))
                        .respond();
            }

            @Override
            public void noMatches() {
                log.warn("[{}] no matches found for {}", guild, identifier);
                responder.addEmbed(
                                new EmbedBuilder()
                                        .setTitle(String.format("️%s No matches found...", Constants.EMOJI_ERROR))
                                        .setColor(Constants.YELLOW))
                        .respond();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                log.warn("[{}] error loading item: {}", guild, exception.getMessage());
                responder.addEmbed(
                                new EmbedBuilder()
                                        .setTitle(Constants.TITLE_ERROR)
                                        .setDescription(exception.getMessage())
                                        .setColor(Constants.RED))
                        .respond();
            }
        });
    }

}
