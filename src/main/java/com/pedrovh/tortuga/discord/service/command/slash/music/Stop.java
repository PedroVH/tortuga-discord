package com.pedrovh.tortuga.discord.service.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.service.music.MusicService;
import jakarta.inject.Singleton;

@Singleton
public class Stop extends AbstractVoiceSlashCommand {

    private final MusicService service;

    public Stop(GuildPreferencesService preferencesService, MessageService messages, MusicService service) {
        super(preferencesService, messages);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        service.stop(server, response);
    }

}
