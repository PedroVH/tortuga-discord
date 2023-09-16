package com.pedrovh.tortuga.discord.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.guild.service.GuildPreferencesService;
import com.pedrovh.tortuga.discord.music.service.MusicService;
import jakarta.inject.Singleton;

@Singleton
public class Stop extends AbstractVoiceSlashCommand {

    private final MusicService service;

    public Stop(GuildPreferencesService preferencesService, MusicService service) {
        super(preferencesService);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        service.stop(server, response);
    }

}
