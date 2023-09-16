package com.pedrovh.tortuga.discord.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.guild.service.GuildPreferencesService;
import com.pedrovh.tortuga.discord.music.service.MusicService;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class Loop extends AbstractVoiceSlashCommand {

    private final MusicService service;

    public Loop(GuildPreferencesService preferencesService, MusicService service) {
        super(preferencesService);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        service.loop(api, voiceChannel, response);
    }

}
