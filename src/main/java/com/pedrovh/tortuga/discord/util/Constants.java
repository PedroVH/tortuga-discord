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
    public final String EMOJI_WARNING = "\u26A0\uFE0F";
    public final String EMOJI_ERROR = "❌";
    public final String EMOJI_PONG = "\uD83C\uDFD3";
    public final String EMOJI_SONG = "\uD83C\uDFB6";
    public final String EMOJI_PLUS = "➕";
    public final String EMOJI_LIST = "\uD83D\uDCCB";
    public final String EMOJI_LOOP = "\uD83D\uDD01";
    public final String EMOJI_INFO = "\u2139\uFE0F";
    public final String EMOJI_LIVE = "🔴";

    // events
    public final String EVENT_PLAYLIST_REPLACE = "playlist-replace-";
    public final String EVENT_CANCEL = "button-cancel";
    public final String EVENT_LANGUAGE_MENU = "language-menu";

    // langauges
    public final String LANGUAGE_DEFAULT = "default";
    public final String LANGUAGE_PT_BR = "pt_BR";

}
