package com.pedrovh.tortuga.discord.service.command.slash.music;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.exception.QueueEmptyException;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.service.music.MusicService;
import jakarta.inject.Singleton;

@Singleton
public class Skip extends AbstractVoiceSlashCommand {

    private final MusicService service;

    public Skip(GuildPreferencesService preferencesService, MusicService service) {
        super(preferencesService);
        this.service = service;
    }

    @Override
    protected void handle() throws BotException {
        if(service.isQueueEmpty(server)) throw new QueueEmptyException();
        service.skip(event.getApi(), voiceChannel, response);
    }

}
