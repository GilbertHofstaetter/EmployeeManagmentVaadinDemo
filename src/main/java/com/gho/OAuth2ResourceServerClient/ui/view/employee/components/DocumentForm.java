package com.gho.OAuth2ResourceServerClient.ui.view.employee.components;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.obj.Document;
import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.repository.DocumentRepository;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.AllFinishedEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Component("employeeDocumentForm")
public class DocumentForm extends VerticalLayout implements ComponentEventListener<AllFinishedEvent> {

    protected Employee employee;

    protected TextField filter;

    protected Grid<Document> documentGrid;

    private final DocumentRepository documentRepository;

    public DocumentForm(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
        setSizeFull();

        Button addDocumentButton = new Button(
                VaadinIcon.UPLOAD.create(), event -> {
            DocumentUploadDialog documentUploadDialog = new DocumentUploadDialog(documentRepository);
            documentUploadDialog.registerActionListener(this);
            documentUploadDialog.open(employee);
        });
        add(addDocumentButton);

        this.documentGrid = new Grid<>(Document.class);
        documentGrid.setSizeFull();
        documentGrid.setColumns("id", "fileName");
        documentGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        addOpenDocumentButton();

        this.filter = new TextField();
        HorizontalLayout actions = new HorizontalLayout(filter);
        addAndExpand(actions, documentGrid);
        filter.setPlaceholder("Filter by fileName %");

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> dataToUI(e.getValue()));
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        dataToUI(null);
    }

    void dataToUI(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            filterText = "%";
        }
        documentGrid.setItems(getDocumentDataProvider(employee.getId(), "%" + filterText + "%"));
    }

    private void addOpenDocumentButton() {
        documentGrid.addComponentColumn(document -> {
            HorizontalLayout editCreateButtonLayout = new HorizontalLayout();
            editCreateButtonLayout.setJustifyContentMode(JustifyContentMode.END);

            Button editButton = new Button(
                    VaadinIcon.DOWNLOAD.create(), event -> {
                openDocument(document);
            });
            editButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            Button deleteButton = new Button(
                    VaadinIcon.DEL.create(), event -> {
                delete(document);
            });
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            editCreateButtonLayout.add(editButton, deleteButton);
            return editCreateButtonLayout;
        } );
    }

    protected void delete(Document document) {
        ConfirmDialog confirmDeleteDialog = new ConfirmDialog();
        confirmDeleteDialog.setHeader("Delete Document");
        confirmDeleteDialog.setText("Are you sure?");
        confirmDeleteDialog.setCancelText("Cancel");
        confirmDeleteDialog.setCancelable(true);
        confirmDeleteDialog.setConfirmText("Delete");
        confirmDeleteDialog.addConfirmListener(listener -> {
            documentRepository.delete(document);
            Notification.show("Deleted.", 5000, Notification.Position.TOP_END);
            dataToUI(null);
        });
        confirmDeleteDialog.open();
    }

    protected void openDocument(Document document) {
        StreamResource resource = new StreamResource(document.getFileName() != null ? document.getFileName() : "", () -> new ByteArrayInputStream(document.getDocument()));
        StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
        UI.getCurrent().getPage().open(registration.getResourceUri().toString(), document.getFileName());
    }

    @Override
    public void onComponentEvent(AllFinishedEvent allFinishedEvent) {
        dataToUI(null);
    }

    DataProvider<Document, Void> getDocumentDataProvider(long id, String filterText) {

        DataProvider<Document, Void> dataProvider =
                DataProvider.fromCallbacks(

                        // First callback fetches items based on a query
                        query -> {
                            // The index of the first item to load
                            int offset = query.getOffset();

                            // The number of items to load
                            int limit = query.getLimit();

                            int page = offset / documentGrid.getPageSize();

                            Pageable sorted =
                                    PageRequest.of(page, limit, Sort.by("id"));

                            List<Sort.Order> jpaOrders = new ArrayList<Sort.Order>();
                            List<QuerySortOrder> sortOrdersList = query.getSortOrders();
                            for (QuerySortOrder orders : sortOrdersList) {
                                Sort.Order order;
                                if (orders.getDirection() == SortDirection.ASCENDING)
                                    order = Sort.Order.asc(orders.getSorted());
                                else
                                    order = Sort.Order.desc(orders.getSorted());
                                jpaOrders.add(order);
                            }
                            if (!jpaOrders.isEmpty())
                                sorted = PageRequest.of(page, limit, Sort.by(jpaOrders));
                            Page<Document> documents = documentRepository.findByEmployeeIdAndFileNameLike(id, filterText, sorted);

                            return documents.stream();
                        },
                        // Second callback fetches the total number of items currently in the Grid.
                        // The grid can then use it to properly adjust the scrollbars.
                        query -> documentRepository.countByEmployeeIdAndFileNameLike(id, filterText));

        return dataProvider;
    }

}
