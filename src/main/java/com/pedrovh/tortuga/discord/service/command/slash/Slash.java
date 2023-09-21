package com.pedrovh.tortuga.discord.service.command.slash;

import com.pedrovh.tortuga.discord.service.command.Help;
import com.pedrovh.tortuga.discord.service.command.slash.bot.Language;
import com.pedrovh.tortuga.discord.service.command.slash.channel.Channel;
import com.pedrovh.tortuga.discord.service.command.slash.health.Ping;
import com.pedrovh.tortuga.discord.service.command.slash.messages.Clear;
import com.pedrovh.tortuga.discord.service.command.slash.music.*;
import com.pedrovh.tortuga.discord.service.command.slash.music.playlist.Playlist;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.EnumSet;
import java.util.List;

public enum Slash {

    COMMAND_LANGUAGE(
            "language",
            "Sets the language the bot should use",
            Language.class,
            EnumSet.of(PermissionType.ADMINISTRATOR)),

    COMMAND_CHANNEL(
            "channel",
            "A command dedicated to channels",
            Channel.class,
            EnumSet.of(PermissionType.ADMINISTRATOR),
            SlashCommandOption.createBooleanOption(
                    Slash.OPTION_MUSIC,
                    "Whether this channel should be used for music or not",
                    true)),
    COMMAND_CLEAR(
            "clear",
            "Deletes latest messages from the text channel",
            Clear.class,
            SlashCommandOption.createLongOption(
                    Slash.OPTION_NUMBER,
                    "number of messages to delete",
                    true,
                    1L,
                    100L)),
    COMMAND_LEAVE(
            "leave",
            "Leaves the voice channel",
            Leave.class),
    COMMAND_LOOP(
            "loop",
            "Toggles loop for current track",
            Loop.class),
    COMMAND_HELP(
            "help",
            "Helpful message about how to use me",
            Help.class),
    COMMAND_NEXT(
            "next",
            "Adds a song or playlist as next in the queue",
            Next.class,
            SlashCommandOption.createStringOption(
                    Slash.OPTION_QUERY,
                    "The track url, playlist url or search query",
                    true)
    ),
    COMMAND_PAUSE(
            "pause",
            "Pauses/Unpauses current track",
            Pause.class),
    COMMAND_PING(
            "ping",
            "Check on me!",
            Ping.class),
    COMMAND_PLAYLIST(
            "playlist",
            "Command related to saved playlists",
            Playlist.class,
            SlashCommandOption.createSubcommand(
                    Slash.OPTION_SAVE,
                    "Saves the current playlist",
                    List.of(SlashCommandOption.createStringOption(Slash.OPTION_NAME, "The name of the playlist", true))
            ),
            SlashCommandOption.createSubcommand(
                    Slash.OPTION_LOAD,
                    "Loads the saved playlist",
                    List.of(SlashCommandOption.createStringOption(Slash.OPTION_NAME, "The name of the playlist", true))
            ),
            SlashCommandOption.createSubcommand(
                    Slash.OPTION_DELETE,
                    "Deletes a playlist",
                    List.of(SlashCommandOption.createStringOption(Slash.OPTION_NAME, "The name of the playlist", true))
            ),
            SlashCommandOption.createSubcommand(
                    Slash.OPTION_LIST,
                    "Lists saved playlists",
                    List.of(SlashCommandOption.createStringOption(Slash.OPTION_NAME, "The name of the playlist to list the tracks", false))
            )
    ),
    COMMAND_QUEUE(
            "queue",
            "Lists current tracks in queue",
            Queue.class),
    COMMAND_REMOVE(
            "remove",
            "Removes tracks from the queue",
            Remove.class,
            SlashCommandOption.createLongOption(
                    Slash.OPTION_END,
                    "The end track position to delete until (inclusive)",
                    true,
                    1L,
                    500L),
            SlashCommandOption.createLongOption(
                    Slash.OPTION_START,
                    "The start track position to start deleting from (inclusive)",
                    false,
                    1L,
                    499L)),
    COMMAND_SKIP(
            "skip",
            "Skip current track",
            Skip.class),
    COMMAND_REPLACE(
            "replace",
            "Replaces a track from the queue",
            Replace.class,
            SlashCommandOption.createLongOption(
                    Slash.OPTION_TRACK,
                    "The track position in the queue",
                    true,
                    0L,
                    500L),
            SlashCommandOption.createStringOption(
                    Slash.OPTION_QUERY,
                    "The track url, playlist url or search query",
                    true)),
    COMMAND_STOP(
            "stop",
            "Stops the track and clears the queue",
            Stop.class);

    public static final String OPTION_MUSIC = "music";
    public static final String OPTION_NUMBER = "number";
    public static final String OPTION_QUERY = "query";
    public static final String OPTION_START = "start";
    public static final String OPTION_END = "end";
    public static final String OPTION_SAVE = "save";
    public static final String OPTION_LOAD = "load";
    public static final String OPTION_DELETE = "delete";
    public static final String OPTION_LIST = "list";
    public static final String OPTION_NAME = "name";
    public static final String OPTION_TRACK = "track";

    public final String name;
    public final String description;
    public final Class<? extends SlashCommand> handler;
    public final EnumSet<PermissionType> permissions;
    public final List<SlashCommandOption> options;

    Slash(String name, String description, Class<? extends SlashCommand> handler) {
        this.name = name;
        this.description = description;
        this.handler = handler;
        this.permissions = EnumSet.of(PermissionType.SEND_MESSAGES);
        this.options = null;
    }

    Slash(String name, String description, Class<? extends SlashCommand> handler, EnumSet<PermissionType> permissions) {
        this.name = name;
        this.description = description;
        this.handler = handler;
        this.permissions = permissions;
        this.options = null;
    }

    Slash(String name, String description, Class<? extends SlashCommand> handler, SlashCommandOption... options) {
        this.name = name;
        this.description = description;
        this.handler = handler;
        this.permissions = EnumSet.of(PermissionType.SEND_MESSAGES);
        this.options = List.of(options);
    }

    Slash(String name, String description, Class<? extends SlashCommand> handler, EnumSet<PermissionType> permissions, SlashCommandOption... options) {
        this.name = name;
        this.description = description;
        this.handler = handler;
        this.permissions = permissions;
        this.options = List.of(options);
    }

    public SlashCommandBuilder build() {
        return new SlashCommandBuilder()
                .setName(name)
                .setDescription(description)
                .setDefaultEnabledForPermissions(permissions)
                .setOptions(options);
    }

}
