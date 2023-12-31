package com.pedrovh.tortuga.discord.service.playlist;

import com.pedrovh.tortuga.discord.dao.DAO;
import com.pedrovh.tortuga.discord.model.user.playlist.Playlist;
import com.pedrovh.tortuga.discord.model.user.playlist.Track;
import com.pedrovh.tortuga.discord.model.user.playlist.UserPlaylists;
import com.pedrovh.tortuga.discord.music.GuildAudioManager;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.music.MusicService;
import com.pedrovh.tortuga.discord.service.music.VoiceConnectionService;
import com.pedrovh.tortuga.discord.service.music.handler.AbstractCommandAudioLoadResultHandler;
import com.pedrovh.tortuga.discord.util.Constants;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRowBuilder;
import org.javacord.api.entity.message.component.ButtonBuilder;
import org.javacord.api.entity.message.component.ButtonStyle;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Singleton
public class UserPlaylistService {
    private final DAO<UserPlaylists, String> dao = new DAO<>(UserPlaylists.class);
    private final VoiceConnectionService connectionService;
    private final MusicService musicService;
    private final MessageService messages;

    public UserPlaylistService(VoiceConnectionService connectionService, MusicService musicService, MessageService messages) {
        this.connectionService = connectionService;
        this.musicService = musicService;
        this.messages = messages;
    }

    public void save(String userId, Server server, String name, final InteractionImmediateResponseBuilder response, boolean replace) {
        UserPlaylists userPlaylists = get(userId);
        if(userPlaylists == null) {
            log.info("[{}] creating new guildPlaylists in database", server.getName());
            userPlaylists = new UserPlaylists();
            userPlaylists.setUserId(userId);
            userPlaylists.setPlaylists(new ArrayList<>());
            dao.insert(userPlaylists);
        }
        // if already exists, asks if you want to replace it
        if(!replace && get(userId, name).isPresent()) {
            response.addEmbed(new EmbedBuilder()
                            .setTitle(messages.get("command.music.playlist.save.replace.title", name)))
                    .addComponents(
                            new ActionRowBuilder().addComponents(
                                            new ButtonBuilder()
                                                    .setCustomId(Constants.EVENT_PLAYLIST_REPLACE + name)
                                                    .setLabel(messages.get(server.getIdAsString(), "command.music.playlist.save.replace.button.yes"))
                                                    .setStyle(ButtonStyle.SUCCESS)
                                                    .setEmoji(Constants.EMOJI_SUCCESS)
                                                    .build(),
                                            new ButtonBuilder()
                                                    .setCustomId(Constants.EVENT_CANCEL)
                                                    .setLabel(messages.get(server.getIdAsString(), "command.music.playlist.save.replace.button.no"))
                                                    .setStyle(ButtonStyle.DANGER)
                                                    .setEmoji(Constants.EMOJI_ERROR)
                                                    .build())
                                    .build())
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        Optional<GuildAudioManager> audioManager = connectionService.getGuildAudioManager(server.getIdAsString());
        if(audioManager.isPresent()) {
            GuildAudioManager manager = audioManager.get();
            AudioTrack currentTrack = manager.getPlayer().getPlayingTrack();
            List<Track> queue = new ArrayList<>();
            queue.add(new Track(currentTrack.getInfo()));
            queue.addAll(manager.getScheduler().getQueue().stream().map(a -> new Track(a.getInfo())).toList());

            List<Playlist> playlists = new ArrayList<>(userPlaylists.getPlaylists().stream().filter(p -> !p.getName().equalsIgnoreCase(name)).toList());
            playlists.add(new Playlist(name, queue));

            userPlaylists.setPlaylists(playlists);

            dao.save(userPlaylists);

            log.info("[{}] updated playlist {}", server.getName(), name);
            response.addEmbed(
                            new EmbedBuilder()
                                    .setTitle(messages.get(server.getIdAsString(), "command.music.playlist.save.title", name))
                                    .setColor(Constants.GREEN))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        } else {
            log.info("[{}] unable to save playlist: GuildAudioManager not found", server.getName());
            response.addEmbed(
                            new EmbedBuilder()
                                    .setTitle(messages.get(server.getIdAsString(), "command.music.playlist.save.warn.no-queue"))
                                    .setColor(Constants.YELLOW))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        }
    }

    public void load(final String userId, final ServerVoiceChannel channel, String name, final InteractionImmediateResponseBuilder response) {
        final Server server = channel.getServer();
        final Optional<Playlist> optionalPlaylist = get(userId, name);
        if(optionalPlaylist.isEmpty()) {
            playlistNotFound(server.getName(), name, response);
            return;
        }
        final Playlist playlist = optionalPlaylist.get();
        final GuildAudioManager manager = connectionService.getGuildAudioManager(channel);
        for(Track track : playlist.getTracks()) {
            connectionService.getPlayerManager()
                    .loadItemOrdered(
                            manager,
                            musicService.getIdentifier(track.getUrl()),
                            new AbstractCommandAudioLoadResultHandler(manager, connectionService, channel, track.getUrl(), messages, response) {
                                @Override
                                protected void handleTrackLoaded(AudioTrack track) {
                                    manager.getScheduler().queue(track, false);
                                }
                                @Override
                                protected void handlePlaylistLoaded(AudioPlaylist playlist) {
                                    manager.getScheduler().queuePlaylist(playlist);
                                }
                            });
        }
        StringBuilder sb = new StringBuilder();
        appendTrackNames(sb, playlist);

        response.addEmbed(new EmbedBuilder()
                        .setTitle(messages.get(server.getIdAsString(), "command.music.playlist.load.title", name))
                        .setDescription(sb.toString())
                        .setColor(Constants.GREEN))
                .respond();
    }

    public void delete(String userId, Server server, String name, final InteractionImmediateResponseBuilder response) {
        final UserPlaylists userPlaylists = get(userId);
        if(userPlaylists == null) {
            noSavedPlaylist(server, response);
            return;
        }
        final List<Playlist> playlists = new ArrayList<>(userPlaylists.getPlaylists());

        if(playlists.removeIf(p -> p.getName().equalsIgnoreCase(name))) {
            response.addEmbed(new EmbedBuilder()
                            .setTitle(messages.get(server.getIdAsString(), "command.music.playlist.delete.title", name))
                            .setColor(Constants.GREEN))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
        } else
            playlistNotFound(server.getName(), name, response);
    }

    public void list(String userId, Server server, String name, final InteractionImmediateResponseBuilder response) {
        final Optional<Playlist> playlist = get(userId, name);
        if(playlist.isEmpty()) {
            playlistNotFound(server.getName(), name, response);
            return;
        }
        final StringBuilder sb = new StringBuilder();
        appendTrackNames(sb, playlist.get());

        response.addEmbed(new EmbedBuilder()
                        .setTitle(messages.get(server.getIdAsString(), "command.music.playlist.list.title", name))
                        .setDescription(sb.toString())
                        .setColor(Constants.GREEN))
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

    public void list(String userId, Server server, final InteractionImmediateResponseBuilder response) {
        final UserPlaylists userPlaylists = get(userId);
        if(userPlaylists == null) {
            noSavedPlaylist(server, response);
            return;
        }
        final StringBuilder sb = new StringBuilder();

        for(Playlist playlist : userPlaylists.getPlaylists()) {
            sb.append("**").append(playlist.getName()).append("**\n");
            appendTrackNames(sb, playlist);
        }

        response.addEmbed(new EmbedBuilder()
                        .setTitle(messages.get(server.getIdAsString(), "command.music.playlist.list.all.title"))
                        .setDescription(sb.toString())
                        .setColor(Constants.GREEN))
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

    private UserPlaylists get(String userId) {
        return dao.findById(userId);
    }

    private Optional<Playlist> get(String userId, String name) {
        UserPlaylists userPlaylists = get(userId);
        if(userPlaylists == null)
            return Optional.empty();
        return userPlaylists.getPlaylists().stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
    }

    private void appendTrackNames(StringBuilder sb, Playlist playlist) {
        int i = 1;
        for(Track track : playlist.getTracks()) {
            sb.append(i).append(". ").append(track.getName()).append("\n");
        }
    }

    private void noSavedPlaylist(Server server, final InteractionImmediateResponseBuilder response) {
        log.warn("[{}] there's no saved playlist", server.getName());
        response
                .addEmbed(new EmbedBuilder()
                        .setTitle(messages.get(server.getIdAsString(), "command.music.playlist.warn.never-saved.title"))
                        .setDescription(messages.get(server.getIdAsString(), "command.music.playlist.warn.never-saved.description"))
                        .setColor(Constants.YELLOW))
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

    private void playlistNotFound(final String server, final String name, final InteractionImmediateResponseBuilder response) {
        log.warn("[{}] playlist {} not found", server, name);
        response
                .addEmbed(new EmbedBuilder()
                        .setTitle(messages.get(server, "command.music.playlist.warn.not-found.title"))
                        .setDescription(messages.get(server, "command.music.playlist.warn.not-found.description"))
                        .setColor(Constants.YELLOW))
                .setFlags(MessageFlag.EPHEMERAL)
                .respond();
    }

}
