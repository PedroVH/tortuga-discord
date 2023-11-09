package com.pedrovh.tortuga.discord.service.music;

import com.pedrovh.tortuga.discord.music.GuildAudioManager;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.music.handler.DefaultAudioLoadResultHandler;
import com.pedrovh.tortuga.discord.service.music.handler.NextCommandAudioLoadResultHandler;
import com.pedrovh.tortuga.discord.service.music.handler.ReplaceCommandAudioLoadResultHandler;
import com.pedrovh.tortuga.discord.util.AudioTrackUtils;
import com.pedrovh.tortuga.discord.util.Constants;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;

@Slf4j
@Singleton
public class MusicService {

    private final VoiceConnectionService connectionService;
    private final MessageService messages;
    @Value("${bot.track.seek-time-position}")
    boolean seekPosition;

    public MusicService(VoiceConnectionService connectionService, MessageService messages) {
        this.connectionService = connectionService;
        this.messages = messages;
    }

    public void handle(final Message message) {
        final Server server = message.getServer().orElseThrow(RuntimeException::new);
        try{
            final User author = message.getUserAuthor().orElseThrow();

            loadAndPlay(message, author.getConnectedVoiceChannel(server).orElseThrow());
        } catch (NoSuchElementException e) {
            log.error("error handling music: {}", e.getMessage());
            new MessageBuilder().addEmbed(
                    new EmbedBuilder()
                            .setTitle(messages.get(server.getIdAsString(), "command.music.error.vc-required.title"))
                            .setDescription(messages.get(server.getIdAsString(), "command.music.error.vc-required.description"))
                            .setColor(Constants.RED))
                    .send(message.getChannel());
        }
        catch (Exception e) {
            log.error("error handling music: {}", e.getMessage());
            new MessageBuilder().addEmbed(
                    new EmbedBuilder()
                            .setTitle(messages.get(server.getIdAsString(), "command.music.loading.error.title"))
                            .setDescription(e.getMessage())
                            .setColor(Constants.RED))
                    .send(message.getChannel());
        }
    }

    public void skip(final ServerVoiceChannel voiceChannel, InteractionImmediateResponseBuilder response) {
        GuildAudioManager manager = connectionService.getGuildAudioManager(voiceChannel);
        manager.getScheduler().nextTrack();
        response
                .addEmbed(new EmbedBuilder()
                        .setTitle(messages.get(voiceChannel.getServer().getIdAsString(), "command.music.skip.title"))
                        .setColor(Constants.GREEN))
                .respond();
    }

    public void remove(ServerVoiceChannel voiceChannel, InteractionImmediateResponseBuilder response, int start, int end) {
        GuildAudioManager manager = connectionService.getGuildAudioManager(voiceChannel);
        List<AudioTrack> removed = manager.getScheduler().removeFromQueue(start, end);

        StringBuilder sb = new StringBuilder();
        int index = start + 1;
        for (AudioTrack track : removed) {
            sb.append(index++).append(". ").append(track.getInfo().title).append("\n");
        }

        response
                .addEmbed(new EmbedBuilder()
                        .setTitle(messages.get(voiceChannel.getServer().getIdAsString(), "command.music.remove.title"))
                        .setDescription(sb.toString())
                        .setColor(Constants.GREEN))
                .respond();
    }

    public void pause(final Server server, final InteractionImmediateResponseBuilder response) {
        Optional<GuildAudioManager> audioManager = connectionService.getGuildAudioManager(server.getIdAsString());
        if(audioManager.isPresent()) {
            GuildAudioManager manager = audioManager.get();
            boolean isPaused = !manager.getPlayer().isPaused();
            String paused = isPaused ?
                    messages.get(server.getIdAsString(), "command.music.pause.true.title") :
                    messages.get(server.getIdAsString(), "command.music.pause.false.title");
            manager.getPlayer().setPaused(isPaused);

            log.info("[{}] {}", server.getName(), paused);

            response.addEmbed(
                    new EmbedBuilder()
                            .setTitle(paused)
                            .setColor(Constants.GREEN))
                    .respond();
        } else {
            log.info("[{}] unable to pause: GuildAudioManager not found", server.getName());
            response.addEmbed(
                    new EmbedBuilder()
                            .setTitle(messages.get(server.getIdAsString(), "command.music.pause.warn.no-player"))
                            .setColor(Constants.YELLOW))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }
    }

    public void stop(Server server, InteractionImmediateResponseBuilder response) {
        Optional<GuildAudioManager> audioManager = connectionService.getGuildAudioManager(server.getIdAsString());

        if(audioManager.isPresent()) {
            GuildAudioManager manager = audioManager.get();
            manager.getPlayer().stopTrack();

            manager.getScheduler().clearQueue();
            log.info("[{}] stopped and queue cleared", server.getName());

            response
                    .addEmbed(
                            new EmbedBuilder()
                                    .setTitle(messages.get(server.getIdAsString(), "command.music.stop.title"))
                                    .setDescription(messages.get(server.getIdAsString(), "command.music.stop.description"))
                                    .setColor(Constants.GREEN))
                    .respond();
        } else {
            log.info("[{}] unable to stop: GuildAudioManager not found", server.getName());
            response.addEmbed(
                            new EmbedBuilder()
                                    .setTitle(messages.get(server.getIdAsString(), "command.music.stop.warn.no-player"))
                                    .setColor(Constants.YELLOW))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }
    }

    public void loop(final ServerVoiceChannel voiceChannel, final InteractionImmediateResponseBuilder response) {
        GuildAudioManager manager = connectionService.getGuildAudioManager(voiceChannel);
        boolean loop = manager.getScheduler().toggleLoop();
        log.info("[{}] looping {}", voiceChannel.getServer().getName(), loop);
        response.addEmbed(
                new EmbedBuilder()
                        .setTitle(messages.get(voiceChannel.getServer().getIdAsString(), String.format("command.music.loop.%b.title", loop)))
                        .setColor(Constants.GREEN))
                .respond();
    }

    public void queue(Server server, final InteractionImmediateResponseBuilder response) {
        Optional<GuildAudioManager> audioManager = connectionService.getGuildAudioManager(server.getIdAsString());
        if(audioManager.isPresent()) {
            GuildAudioManager manager = audioManager.get();
            Queue<AudioTrack> queue = manager.getScheduler().getQueue();
            StringBuilder sb = new StringBuilder();

            AudioTrack currentTrack = manager.getPlayer().getPlayingTrack();
            sb.append(Constants.EMOJI_SONG).append(" ").append(currentTrack.getInfo().title).append("\n");

            long totalTimeMs = currentTrack.getDuration() - currentTrack.getPosition();

            int count = 1;
            for(AudioTrack track : queue) {
                sb.append(count++).append(". ").append(track.getInfo().title).append("\n");
                totalTimeMs += track.getDuration();
            }

            String totalTime = AudioTrackUtils.formatTimeDuration(totalTimeMs);

            response.addEmbed(
                            new EmbedBuilder()
                                    .setTitle(messages.get(server.getIdAsString(), "command.music.queue.title"))
                                    .setDescription(sb.toString())
                                    .setColor(Constants.GREEN)
                                    .setFooter(messages.get(server.getIdAsString(), "command.music.queue.footer", totalTime))
                                    .setTimestamp(Instant.now().plus(totalTimeMs, ChronoUnit.MILLIS)))
                    .respond();
        } else {
            log.info("[{}] unable to list queue: GuildAudioManager not found", server.getName());
            response.addEmbed(
                            new EmbedBuilder()
                                    .setTitle(messages.get(server.getIdAsString(), "command.music.queue.warn.no-player"))
                                    .setColor(Constants.YELLOW))
                    .respond();
        }
    }

    public void loadAndPlay(final Message message, final ServerVoiceChannel channel) {
        final GuildAudioManager manager = connectionService.getGuildAudioManager(channel);
        final String identifier = getIdentifier(message.getContent());

        connectionService.getPlayerManager()
                .loadItemOrdered(
                        manager,
                        identifier,
                        new DefaultAudioLoadResultHandler(manager, connectionService, channel, identifier, messages, message, getTimePosition(identifier)));
    }

    public void replace(final ServerVoiceChannel channel, final long pos, final String query, InteractionImmediateResponseBuilder responder) {
        final GuildAudioManager manager = connectionService.getGuildAudioManager(channel);
        final String identifier = getIdentifier(query);

        connectionService.getPlayerManager()
                .loadItemOrdered(
                        manager,
                        identifier,
                        new ReplaceCommandAudioLoadResultHandler(manager, connectionService, channel, identifier, messages, responder, pos, getTimePosition(identifier)));
    }

    public void next(final ServerVoiceChannel channel, final String query, InteractionImmediateResponseBuilder responder) {
        final GuildAudioManager manager = connectionService.getGuildAudioManager(channel);
        final String identifier = getIdentifier(query);

        connectionService.getPlayerManager()
                .loadItemOrdered(
                        manager,
                        identifier,
                        new NextCommandAudioLoadResultHandler(manager, connectionService, channel, identifier, messages, responder, getTimePosition(identifier)));
    }

    public boolean isQueueEmpty(Server server) {
        return connectionService.getGuildAudioManager(server.getIdAsString())
                .filter(manager -> manager.getPlayer().getPlayingTrack() == null && manager.getScheduler().getQueue().isEmpty())
                .isPresent();
    }

    public String getIdentifier(String content) {
        try {
            new URL(content);
        } catch (MalformedURLException ex) {
            log.debug("{} is youtube search...", content);
            return Constants.YOUTUBE_QUERY.concat(content);
        }
        return content;
    }

    public long getTimePosition(String identifier) {
        try {
            if(seekPosition && identifier.matches("t=[0-9]*s?")) {
                String[] split = identifier.split("t=");
                if(split.length > 0)
                    return Long.parseLong(split[split.length - 1].replace("s", "")) * 1000;
            }
        } catch (Exception ex) {
            log.error("Error reading duration from URL", ex);
        }
        return 0;
    }
}
