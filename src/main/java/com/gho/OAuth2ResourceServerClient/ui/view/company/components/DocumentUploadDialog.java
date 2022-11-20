package com.gho.OAuth2ResourceServerClient.ui.view.company.components;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.obj.Document;
import com.gho.OAuth2ResourceServerClient.obj.Picture;
import com.gho.OAuth2ResourceServerClient.repository.DocumentRepository;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.upload.AllFinishedEvent;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;

public class DocumentUploadDialog extends Dialog {

    private Company company;

    private MultiFileMemoryBuffer memoryBuffer;

    private Upload singleFileUpload;

    private final DocumentRepository documentRepository;

    public DocumentUploadDialog(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
        memoryBuffer = new MultiFileMemoryBuffer();
        singleFileUpload = new Upload(memoryBuffer);
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
        document.setCompany(company);
        document.setFileName(event.getFileName());
        try {
            document.setDocument(IOUtils.toByteArray(fileData));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        document = documentRepository.save(document);
        close();
    }


    public void open(Company company) {
        this.company = company;
        open();
    }
}
