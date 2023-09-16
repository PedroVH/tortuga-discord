package com.pedrovh.tortuga.discord.playlist.service;

import com.pedrovh.tortuga.discord.dao.DAO;
import com.pedrovh.tortuga.discord.guild.model.GuildPlaylists;
import com.pedrovh.tortuga.discord.music.GuildAudioManager;
import com.pedrovh.tortuga.discord.music.handler.AbstractCommandAudioLoadResultHandler;
import com.pedrovh.tortuga.discord.music.service.MusicService;
import com.pedrovh.tortuga.discord.music.service.VoiceConnectionService;
import com.pedrovh.tortuga.discord.playlist.model.Playlist;
import com.pedrovh.tortuga.discord.util.Constants;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.channel.ServerVoiceChannel;
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
public class PlaylistService {

    private final DAO<GuildPlaylists, String> dao = new DAO<>(GuildPlaylists.class);
    private final VoiceConnectionService connectionService;
    private final MusicService musicService;

    public PlaylistService(VoiceConnectionService connectionService, MusicService musicService) {
        this.connectionService = connectionService;
        this.musicService = musicService;
    }

    public void save(Server server, String name, final InteractionImmediateResponseBuilder response, boolean replace) {
        GuildPlaylists guildPlaylists = get(server).orElse(null);
        if(guildPlaylists == null) {
            log.info("[{}] creating new guildPlaylists in database", server.getName());
            guildPlaylists = new GuildPlaylists();
            guildPlaylists.setGuildId(server.getIdAsString());
            guildPlaylists.setPlaylists(new ArrayList<>());
            dao.insert(guildPlaylists);
        }
        // if already exists, asks if you want to replace it
        if(!replace && get(server, name).isPresent()) {
            response.addEmbed(new EmbedBuilder()
                            .setTitle(String.format("%s There's already a saved playlist called %s. Do you wish to replace it?", Constants.EMOJI_INFO, name)))
                    .addComponents(
                            new ActionRowBuilder().addComponents(
                                new ButtonBuilder()
                                        .setCustomId(Constants.EVENT_PLAYLIST_REPLACE + name)
                                        .setLabel("Yes!")
                                        .setStyle(ButtonStyle.SUCCESS)
                                        .setEmoji(Constants.EMOJI_SUCCESS)
                                        .build(),
                                new ButtonBuilder()
                                        .setCustomId(Constants.EVENT_CANCEL)
                                        .setLabel("No!")
                                        .setStyle(ButtonStyle.DANGER)
                                        .setEmoji(Constants.EMOJI_ERROR)
                                        .build())
                                    .build())
                    .respond();
            return;
        }

        Optional<GuildAudioManager> audioManager = connectionService.getGuildAudioManager(server.getIdAsString());
        if(audioManager.isPresent()) {
            GuildAudioManager manager = audioManager.get();
            AudioTrack currentTrack = manager.getPlayer().getPlayingTrack();
            List<String> queue = new ArrayList<>();
            queue.add(currentTrack.getInfo().uri);
            queue.addAll(manager.getScheduler().getQueue().stream().map(a -> a.getInfo().uri).toList());

            List<Playlist> playlists = new ArrayList<>(guildPlaylists.getPlaylists().stream().filter(p -> !p.getName().equalsIgnoreCase(name)).toList());
            playlists.add(new Playlist(name, queue));

            guildPlaylists.setPlaylists(playlists);

            dao.save(guildPlaylists);

            log.info("[{}] updated playlist {}", server.getName(), name);
            response.addEmbed(
                    new EmbedBuilder()
                            .setTitle(String.format("%s Saved playlist %s", Constants.EMOJI_SUCCESS, name))
                            .setColor(Constants.GREEN))
                    .respond();
        } else {
            log.info("[{}] unable to save playlist: GuildAudioManager not found", server.getName());
            response.addEmbed(
                            new EmbedBuilder()
                                    .setTitle(String.format("%s There's no queue!", Constants.EMOJI_WARNING))
                                    .setColor(Constants.YELLOW))
                    .respond();
        }
    }

    public void load(String name, final ServerVoiceChannel channel, final InteractionImmediateResponseBuilder response) {
        final Server server = channel.getServer();
        final Playlist playlist = getPlaylist(server, name, response);

        final GuildAudioManager manager = connectionService.getGuildAudioManager(channel);
        for(String url : playlist.getUrls()) {
            connectionService.getPlayerManager()
                    .loadItemOrdered(
                            manager,
                            musicService.getIdentifier(url),
                            new AbstractCommandAudioLoadResultHandler(manager, connectionService, channel, url, response) {
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
        response.addEmbed(new EmbedBuilder()
                        .setTitle(String.format("%s Playlist %s loaded", Constants.EMOJI_SUCCESS, name))
                        .setColor(Constants.GREEN))
                .respond();
    }

    public void list(Server server, String name, final InteractionImmediateResponseBuilder response) {
        final Playlist playlist = getPlaylist(server, name, response);
        final StringBuilder sb = new StringBuilder();

        appendTrackNames(sb, playlist);

        response.addEmbed(new EmbedBuilder()
                .setTitle(String.format("%s Playlist %s", Constants.EMOJI_SUCCESS, name))
                .setDescription(sb.toString())
                .setColor(Constants.GREEN))
                .respond();
    }

    public void list(Server server, final InteractionImmediateResponseBuilder response) {
        final GuildPlaylists guildPlaylists = getPlaylists(server, response);
        final StringBuilder sb = new StringBuilder();

        for(Playlist playlist : guildPlaylists.getPlaylists()) {
            sb.append("**").append(playlist.getName()).append("**\n");
            appendTrackNames(sb, playlist);
        }

        response.addEmbed(new EmbedBuilder()
                .setTitle(String.format("%s Saved playlists", Constants.EMOJI_SUCCESS))
                .setDescription(sb.toString())
                .setColor(Constants.GREEN))
                .respond();
    }

    private void appendTrackNames(StringBuilder sb, Playlist playlist) {
        for(String url : playlist.getUrls()) {
            connectionService.getPlayerManager().loadItemSync(musicService.getIdentifier(url), new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    sb.append(track.getInfo().title).append("\n");
                }
                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for(AudioTrack track : playlist.getTracks())
                        sb.append(track.getInfo().title).append("\n");
                }
                @Override
                public void noMatches() {
                }
                @Override
                public void loadFailed(FriendlyException exception) {
                }
            });
        }
    }

    private Optional<GuildPlaylists> get(Server server) {
        return Optional.ofNullable(dao.findById(server.getIdAsString()));
    }

    private Optional<Playlist> get(Server server, String name) {
        GuildPlaylists guildPlaylists = get(server).orElse(null);
        if(guildPlaylists == null) return Optional.empty();

        return guildPlaylists.getPlaylists().stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
    }

    private GuildPlaylists getPlaylists(Server server, final InteractionImmediateResponseBuilder response) {
        final GuildPlaylists guildPlaylists = get(server).orElse(null);

        if(guildPlaylists == null) {
            log.warn("[{}] there's no saved playlist", server.getName());
            response
                    .addEmbed(new EmbedBuilder()
                            .setTitle(String.format("%s No saved playlist!", Constants.EMOJI_WARNING))
                            .setDescription("Use '/playlist save %NAME%' to save the current queue as a playlist.")
                            .setColor(Constants.YELLOW)
                    )
                    .respond();
        }
        return guildPlaylists;
    }

    private Playlist getPlaylist(Server server, String name, final InteractionImmediateResponseBuilder response) {
        final Playlist playlist = get(server, name).orElse(null);

        if(playlist == null) {
            log.warn("[{}] playlist {} not found", server.getName(), name);
            response
                    .addEmbed(new EmbedBuilder()
                            .setTitle(String.format("%s Playlist not found", Constants.EMOJI_WARNING))
                            .setDescription("Use '/playlist list' to list all saved playlists.")
                            .setColor(Constants.YELLOW)
                    )
                    .respond();
        }
        return playlist;
    }
}
