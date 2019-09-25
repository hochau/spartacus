package com.baoxue.spartacus.controller.sse;

import lombok.Data;


/**
 *
 * @Author C
 * @Date 2019/8/24 13:53
 **/
@Data
public class MessageBody<T> {
    private long timestamp;

    private String from;

    private String to;

    private T payload;
}
