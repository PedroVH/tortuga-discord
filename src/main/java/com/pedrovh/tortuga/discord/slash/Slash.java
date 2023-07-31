package com.pedrovh.tortuga.discord.slash;

import com.pedrovh.tortuga.discord.slash.command.Help;
import com.pedrovh.tortuga.discord.slash.command.channel.Channel;
import com.pedrovh.tortuga.discord.slash.command.health.Ping;
import com.pedrovh.tortuga.discord.slash.command.messages.Clear;
import com.pedrovh.tortuga.discord.slash.command.voice.*;
import org.javacord.api.interaction.SlashCommandBuilder;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.List;

public enum Slash {

    COMMAND_CHANNEL(
            "channel",
            "A command dedicated to channels",
            Channel.class,
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
    COMMAND_PAUSE(
            "pause",
            "Pauses/Unpauses current track",
            Pause.class),
    COMMAND_PING(
            "ping",
            "Check on me!",
            Ping.class),
    COMMAND_QUEUE(
            "queue",
            "Lists current tracks in queue",
            Queue.class),
    COMMAND_SKIP(
            "skip",
            "Skip current track",
            Skip.class),
    COMMAND_START(
            "start",
            "Adds a track or playlist to the start of the queue",
            Start.class,
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

    public final String name;
    public final String description;
    public final Class<? extends SlashCommand> handler;
    public final List<SlashCommandOption> options;

    Slash(String name, String description, Class<? extends SlashCommand> handler) {
        this.name = name;
        this.description = description;
        this.handler = handler;
        this.options = null;
    }

    Slash(String name, String description, Class<? extends SlashCommand> handler, SlashCommandOption... options) {
        this.name = name;
        this.description = description;
        this.handler = handler;
        this.options = List.of(options);
    }

    public SlashCommandBuilder build() {
        return new SlashCommandBuilder()
                .setName(name)
                .setDescription(description)
                .setOptions(options);
    }

}
