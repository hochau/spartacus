//package com.baoxue.blog.controller;
//
//import com.baoxue.blog.globals.Globals;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
///**
// *
// * @Author C
// * @Date 2019/8/24 13:56
// **/
//@Controller
//@RequestMapping("/sse")
//public class SSEController {
//
//    @GetMapping(path = "/event/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    @CrossOrigin
//    public SseEmitter handle(@PathVariable String id) {
//        SseEmitter sseEmitter = new SseEmitter();
//        Globals.sseEmitters.put(id, sseEmitter);
//        sseEmitter.onTimeout(() -> {Globals.sseEmitters.remove(id);});
//        sseEmitter.onCompletion(() -> {Globals.sseEmitters.remove(id);});
//        return sseEmitter;
//    }
//
//}
