package com.pedrovh.tortuga.discord.model.guild;

import com.pedrovh.tortuga.discord.util.Constants;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = GuildPreferences.COLLECTION, schemaVersion = "1.1")
public class GuildPreferences implements Serializable {

    public static final String COLLECTION = "GUILD_PREFERENCES";
    @Id
    private String guildId;
    private String musicChannelId;
    private String language;

    public GuildPreferences(String guildId) {
        this.guildId = guildId;
    }

    public String getLanguage() {
        if(language == null)
            return Constants.LANGUAGE_DEFAULT;
        return language;
    }
}
