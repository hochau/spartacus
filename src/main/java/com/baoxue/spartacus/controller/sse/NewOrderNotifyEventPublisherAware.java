package com.baoxue.spartacus.controller.sse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 *
 * @Author C
 * @Date 2019/8/24 13:56
 **/
@Component
public class NewOrderNotifyEventPublisherAware implements ApplicationEventPublisherAware {
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 推送事件，listener会监听这个推送事件
     *
     * @param newOrderNotifyEvent
     */
    public void publish(NewOrderNotifyEvent newOrderNotifyEvent) {
        applicationEventPublisher.publishEvent(newOrderNotifyEvent);
    }

}
