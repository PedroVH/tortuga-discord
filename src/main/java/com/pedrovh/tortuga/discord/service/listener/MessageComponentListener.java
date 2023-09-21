package com.pedrovh.tortuga.discord.service.listener;

import com.pedrovh.tortuga.discord.model.guild.GuildPreferences;
import com.pedrovh.tortuga.discord.service.guild.GuildPreferencesService;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.service.playlist.UserPlaylistService;
import com.pedrovh.tortuga.discord.util.Constants;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.SelectMenuOption;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.javacord.api.interaction.SelectMenuInteraction;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.util.Optional;

@Slf4j
@Singleton
public class MessageComponentListener implements MessageComponentCreateListener {

    private final UserPlaylistService userPlaylistService;
    private final GuildPreferencesService preferencesService;
    private final MessageService messages;

    public MessageComponentListener(UserPlaylistService userPlaylistService,
                                    GuildPreferencesService preferencesService,
                                    MessageService messages) {
        this.userPlaylistService = userPlaylistService;
        this.preferencesService = preferencesService;
        this.messages = messages;
    }

    @Override
    public void onComponentCreate(MessageComponentCreateEvent event) {
        MessageComponentInteraction messageComponentInteraction = event.getMessageComponentInteraction();
        String customId = messageComponentInteraction.getCustomId();
        Optional<Server> server = messageComponentInteraction.getServer();

        if (customId.equalsIgnoreCase(Constants.EVENT_CANCEL)) {
            if(messageComponentInteraction.getMessage() != null)
                messageComponentInteraction.getMessage().delete();
            else
                messageComponentInteraction.acknowledge();
        }
        if (server.isPresent() && customId.startsWith(Constants.EVENT_PLAYLIST_REPLACE))
            userPlaylistService.save(
                    event.getInteraction().getUser().getIdAsString(),
                    server.get(),
                    customId.replace(Constants.EVENT_PLAYLIST_REPLACE, ""),
                    messageComponentInteraction.createImmediateResponder(),
                    true);

        if(server.isPresent() && customId.equalsIgnoreCase(Constants.EVENT_LANGUAGE_MENU)) {
            // this custom id should only be used for select_menu
            SelectMenuInteraction menuInteraction = messageComponentInteraction.asSelectMenuInteraction().orElseThrow();

            Optional<GuildPreferences> optionalPref = preferencesService.findById(server.get().getIdAsString());
            GuildPreferences preferences = optionalPref.orElseGet(() -> new GuildPreferences(server.get().getIdAsString()));

            SelectMenuOption selected = menuInteraction.getChosenOptions().get(0);
            String value = selected.getValue();

            preferences.setLanguage(value);
            preferencesService.save(preferences);

            String[] locale = value.split("_");
            messages.setLanguage(server.get().getIdAsString(), locale[0], locale.length > 1 ? locale[1] : null);

            messageComponentInteraction.getMessage().delete();

            event.getInteraction().getChannel().ifPresent(channel -> new MessageBuilder().addEmbed(new EmbedBuilder()
                            .setTitle(messages.get(server.get().getIdAsString(), "command.language.changed.title", selected.getLabel()))
                            .setColor(Constants.GREEN))
                    .send(channel));
        }
    }

}