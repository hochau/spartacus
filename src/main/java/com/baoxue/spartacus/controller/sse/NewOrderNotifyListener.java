package com.baoxue.spartacus.controller.sse;

import org.springframework.context.ApplicationListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

/**
 *
 * @Author C
 * @Date 2019/8/24 13:56
 **/
@Component
public class NewOrderNotifyListener implements ApplicationListener<NewOrderNotifyEvent> {

    @Override
    public void onApplicationEvent(NewOrderNotifyEvent event) {
        Optional.of(event.getSseEmitter()).ifPresent(sseEmitter -> {
            try {
                sseEmitter.send(event.getMessageBody(), MediaType.APPLICATION_JSON_UTF8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
