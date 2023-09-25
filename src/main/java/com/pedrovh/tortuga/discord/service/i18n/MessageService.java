package com.pedrovh.tortuga.discord.service.i18n;

import io.micronaut.context.i18n.ResourceBundleMessageSource;
import jakarta.inject.Singleton;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class MessageService extends ResourceBundleMessageSource {

    private final Map<String, MessageContext> guildContexts = new ConcurrentHashMap<>();
    private final MessageContext defaultContext;

    public MessageService() {
        super("i18n.messages");
        defaultContext = ResourceBundle.getBundle("i18n.messages", Locale.getDefault()) != null ?
                MessageContext.of(Locale.getDefault()) : MessageContext.DEFAULT;
    }

    public String get(String code) {
        return get(null, code);
    }

    public String get(String code, Object... vars) {
        return get(null, code, vars);
    }

    public String get(String guildId, String code) {
        return super.getMessage(code, getContext(guildId), code);
    }

    public String get(String guildId, String code, Object... vars) {
        return super.getMessage(code, getContext(guildId).getLocale(), vars).orElse(code);
    }

    public MessageContext getContext(String guildId) {
        if(guildId == null) return defaultContext;
        return Optional.ofNullable(guildContexts.get(guildId)).orElse(defaultContext);
    }

    public void setLanguage(String guildId, String lang, String country) {
        Locale locale = country != null ? new Locale(lang, country) : new Locale(lang);
        guildContexts.put(guildId, MessageContext.of(locale));
    }

}
