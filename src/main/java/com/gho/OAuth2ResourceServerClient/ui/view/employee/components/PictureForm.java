package com.gho.OAuth2ResourceServerClient.ui.view.employee.components;

import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.obj.Picture;
import com.gho.OAuth2ResourceServerClient.repository.PictureRepository;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.AllFinishedEvent;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.io.IOUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//@Component
public class PictureForm extends HorizontalLayout {

    private Employee employee;
    MemoryBuffer memoryBuffer;
    Upload singleFileUpload;

    Image image = new Image();

    private final PictureRepository pictureRepository;

    public PictureForm(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
        memoryBuffer = new MemoryBuffer();
        singleFileUpload = new Upload(memoryBuffer);
        add(image, singleFileUpload);
        singleFileUpload.addSucceededListener(event -> upload(event));
    }

    public void dataToUI(Employee employee) {
        this.employee = employee;
        image.setSrc("");
        image.setAlt("");
        singleFileUpload.clearFileList();
        if (employee.getId() != 0) {
            setVisible(true);
            Picture picture = employee.getPicture();
            if (picture != null && picture.getPhoto() != null) {
                StreamResource resource = new StreamResource(picture.getFileName() != null ? picture.getFileName() : "", () -> new ByteArrayInputStream(picture.getPhoto()));
                image.setSrc(resource);
                image.setAlt(picture.getFileName());
                image.setHeight("150px");
                image.setWidth("150px");
            }
        }
        else
            setVisible(false);
    }

    void upload(SucceededEvent event) {
        InputStream fileData = memoryBuffer.getInputStream();
        String fileName = event.getFileName();
        long contentLength = event.getContentLength();
        String mimeType = event.getMIMEType();

        Picture picture = employee.getPicture();
        if (picture == null)
            picture = new Picture();
        picture.setFileName(event.getFileName());
        try {
            picture.setPhoto(IOUtils.toByteArray(fileData));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        picture = pictureRepository.save(picture);
        employee.setPicture(picture);
        dataToUI(employee);
    }

    public void registerActionListener(ComponentEventListener<AllFinishedEvent> listener) {
        singleFileUpload.addAllFinishedListener(listener);
    }
}
