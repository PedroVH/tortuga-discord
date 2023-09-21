package com.pedrovh.tortuga.discord.service.command.slash.bot;

import com.pedrovh.tortuga.discord.exception.BotException;
import com.pedrovh.tortuga.discord.service.command.slash.AbstractSlashServerCommand;
import com.pedrovh.tortuga.discord.service.i18n.MessageService;
import com.pedrovh.tortuga.discord.util.Constants;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.SelectMenu;
import org.javacord.api.entity.message.component.SelectMenuOption;

import java.util.List;

@Slf4j
@Singleton
public class Language extends AbstractSlashServerCommand {

    protected Language(MessageService messages) {
        super(messages);
    }

    @Override
    protected void handle() throws BotException {
        response
                .addComponents(ActionRow.of(
                        SelectMenu.createStringMenu(
                                Constants.EVENT_LANGUAGE_MENU,
                                "",
                                1,
                                1,
                                List.of(
                                        SelectMenuOption.create("English", Constants.LANGUAGE_DEFAULT),
                                        SelectMenuOption.create("Português Brasileiro", Constants.LANGUAGE_PT_BR)
                                )
                        )
                ))
                .respond();
    }

}
