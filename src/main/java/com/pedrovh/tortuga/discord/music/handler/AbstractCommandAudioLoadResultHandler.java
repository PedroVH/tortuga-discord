package com.pedrovh.tortuga.discord.music.handler;

import com.pedrovh.tortuga.discord.music.GuildAudioManager;
import com.pedrovh.tortuga.discord.music.service.VoiceConnectionService;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionImmediateResponseBuilder;

public abstract class AbstractCommandAudioLoadResultHandler extends AbstractAudioLoadResultHandler {

    protected final InteractionImmediateResponseBuilder responder;

    public AbstractCommandAudioLoadResultHandler(GuildAudioManager manager,
                                                 VoiceConnectionService connectionService,
                                                 ServerVoiceChannel voiceChannel,
                                                 String identifier,
                                                 InteractionImmediateResponseBuilder responder) {
        super(manager, connectionService, voiceChannel, identifier);
        this.responder = responder;
    }

    @Override
    protected void respondNoMatches(EmbedBuilder embed) {
        responder.addEmbed(embed).respond();
    }

    @Override
    protected void respondLoadFailed(EmbedBuilder embed) {
        responder.addEmbed(embed).respond();
    }

}
