package com.pedrovh.tortuga.discord.listener;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.event.audio.AudioSourceFinishedEvent;
import org.javacord.api.listener.audio.AudioSourceFinishedListener;


@Slf4j
@Singleton
public class AudioFinishedListener implements AudioSourceFinishedListener {


    @Override
    public void onAudioSourceFinished(AudioSourceFinishedEvent event) {

    }
}
