package com.pedrovh.tortuga.discord.util;

import lombok.experimental.UtilityClass;

import java.awt.*;

@UtilityClass
public class Constants {

    // colors
    public final Color GREEN = Color.decode("#008000");
    public final Color YELLOW = Color.decode("#DBF227");
    public final Color RED = Color.decode("#C70039");

    // queries
    public final String YOUTUBE_QUERY = "ytsearch: ";

    public final String EMOJI_SUCCESS = "✅";
    public final String EMOJI_WARNING = "⚠️";
    public final String EMOJI_ERROR = "❌";
    public final String EMOJI_PONG = "🏓";
    public final String EMOJI_SONG = "🎶";
    public final String EMOJI_INFO = "ℹ️";
    public final String EMOJI_LIVE = "🔴";

    // messages
    public final String TITLE_ERROR = EMOJI_ERROR + " Error!";

    // events
    public final String EVENT_PLAYLIST_REPLACE = "playlist-replace-";
    public final String EVENT_CANCEL = "button-cancel";

}
