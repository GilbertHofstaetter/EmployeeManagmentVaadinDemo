package com.gho.OAuth2ResourceServerClient.conf;

import com.gho.OAuth2ResourceServerClient.controller.PictureController;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Component
@Log4j2
public class RemoveTempFilesInterceptor implements HandlerInterceptor {

    Queue<PictureController.FileDownloadEvent> events = new LinkedList<>();

    //https://reflectoring.io/spring-boot-application-events-explained/
    //@Async
    @EventListener//(condition = "#event.name eq 'reflectoring'")
    void handleConditionalListener(PictureController.FileDownloadEvent event) {
        events.add(event);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (request.getRequestURI().equals("/api/picture/download")) {
            PictureController.FileDownloadEvent event = events.poll();
            //delete something
            log.info(event.getName() + "was deleted");
        }
    }
}
