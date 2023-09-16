package com.pedrovh.tortuga.discord.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.guild.service.GuildPreferencesService;
import com.pedrovh.tortuga.discord.music.service.MusicService;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class Queue extends AbstractVoiceSlashCommand {

    private final MusicService service;

    public Queue(GuildPreferencesService preferencesService, MusicService service) {
        super(preferencesService);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        service.queue(server, response);
    }
}
