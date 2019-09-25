package com.baoxue.spartacus.controller.sse;

import lombok.Data;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 *
 * @Author C
 * @Date 2019/8/24 13:55
 **/
@Data
public class NewOrderNotifyEvent extends ApplicationEvent {

    private String eventId;

    private SseEmitter sseEmitter;

    private MessageBody messageBody;


    private NewOrderNotifyEvent(Object source) {
        super(source);
    }

    public NewOrderNotifyEvent(Object source, String eventId, SseEmitter sseEmitter, MessageBody messageBody) {
        this(source);
        this.eventId = eventId;
        this.sseEmitter = sseEmitter;
        this.messageBody = messageBody;
    }

}
