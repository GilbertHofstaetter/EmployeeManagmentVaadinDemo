package com.gho.OAuth2ResourceServerClient.ui.components;

import com.gho.OAuth2ResourceServerClient.obj.Picture;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.StreamResource;

import java.io.ByteArrayInputStream;

public class ImageNotification extends Notification {

    final Image image;

    public ImageNotification() {
        super();
        image = new Image();
        image.getElement().addEventListener("mouseleave", (domEvent) -> {
            close();
        });
        add(image);
        setPosition(Position.MIDDLE);
    }

    public void open(Picture picture) {
        if (picture.getPhoto() != null) {
            StreamResource resource = new StreamResource(picture.getFileName() != null ? picture.getFileName() : "", () -> new ByteArrayInputStream(picture.getPhoto()));
            image.setSrc(resource);
            image.setAlt(picture.getFileName());
            image.setHeight("400px");
            image.setWidth("400px");
        }
        open();
    }


}
