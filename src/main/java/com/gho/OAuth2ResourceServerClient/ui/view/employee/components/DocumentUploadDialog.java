package com.gho.OAuth2ResourceServerClient.ui.view.employee.components;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.obj.Document;
import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.repository.DocumentRepository;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.upload.AllFinishedEvent;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

public class DocumentUploadDialog extends Dialog {

    private Employee employee;

    private MultiFileMemoryBuffer memoryBuffer;

    private Upload singleFileUpload;

    private final DocumentRepository documentRepository;

    public DocumentUploadDialog(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
        memoryBuffer = new MultiFileMemoryBuffer();
        singleFileUpload = new Upload(memoryBuffer);
        singleFileUpload.addFileRejectedListener(event -> {
            String errorMessage = event.getErrorMessage();

            Notification notification = Notification.show(errorMessage, 5000,
                    Notification.Position.MIDDLE);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        });
        add(singleFileUpload);
        singleFileUpload.addSucceededListener(event -> upload(event));

        Button closeButton = new Button("Close", e -> close());
        getFooter().add(closeButton);
    }

    public void registerActionListener(ComponentEventListener<AllFinishedEvent> listener) {
        singleFileUpload.addAllFinishedListener(listener);
    }

    void upload(SucceededEvent event) {
        String fileName = event.getFileName();
        InputStream fileData = memoryBuffer.getInputStream(fileName);
        long contentLength = event.getContentLength();
        String mimeType = event.getMIMEType();

        Document document = new Document();
        document.setEmployee(employee);
        document.setFileName(event.getFileName());
        try {
            document.setDocument(IOUtils.toByteArray(fileData));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        document = documentRepository.save(document);
        close();
    }


    public void open(Employee employee) {
        this.employee = employee;
        open();
    }
}
