package com.gho.OAuth2ResourceServerClient.ui.view.company.components;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.obj.Picture;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import com.gho.OAuth2ResourceServerClient.ui.components.ImageNotification;
import com.gho.OAuth2ResourceServerClient.ui.view.employee.EmployeeEditorView;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.AllFinishedEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.server.StreamResource;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component("companyEmployeeForm")
@Scope("prototype")
public class EmployeeForm extends VerticalLayout implements ComponentEventListener {

    protected Company company;
    protected Grid<Employee> employeeGrid;

    private final EmployeeRepository employeeRepository;

    private final EmployeeAddDialog employeeAddDialog;

    public EmployeeForm(EmployeeRepository employeeRepository, EmployeeAddDialog employeeAddDialog) {
        this.employeeRepository = employeeRepository;
        this.employeeAddDialog = employeeAddDialog;
        this.employeeAddDialog.registerComponentEventListener(this);

        Button linkButton = new Button(
                VaadinIcon.LINK.create(), event -> {
            employeeAddDialog.open(company);
        });
        add(linkButton);
        this.employeeGrid = new Grid<>(Employee.class);
        employeeGrid.setSizeFull();
        employeeGrid.setColumns("id", "firstName", "lastName", "email", "birthDate");
        employeeGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        add(employeeGrid);
        setSizeFull();

        addImageColumn();
        addEditDeleteButton();
    }

    public void setCompany(Company company) {
        this.company = company;
        dataToUI();
    }

    protected void dataToUI() {
        employeeGrid.setItems(getEmployeeDataProvider(company.getId()));
    }

    private void addEditDeleteButton() {
        employeeGrid.addComponentColumn(employee -> {
            HorizontalLayout editCreateButtonLayout = new HorizontalLayout();
            editCreateButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

            Button editButton = new Button(
                    VaadinIcon.EDIT.create(), event -> {
                edit(employee);
            });
            editButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            Button deleteButton = new Button(
                    VaadinIcon.DEL.create(), event -> {
                delete(employee);
            });
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            editCreateButtonLayout.add(editButton, deleteButton);
            return editCreateButtonLayout;
        } );
    }

    private void delete(final Employee employee) {
        ConfirmDialog confirmDeleteDialog = new ConfirmDialog();
        confirmDeleteDialog.setHeader("Delete Employee");
        confirmDeleteDialog.setText("Are you sure?");
        confirmDeleteDialog.setCancelText("Cancel");
        confirmDeleteDialog.setCancelable(true);
        confirmDeleteDialog.setConfirmText("Delete");
        confirmDeleteDialog.addConfirmListener(listener -> {
            employee.setCompany(null);
            employeeRepository.save(employee);
            Notification.show("Deleted.", 5000, Notification.Position.TOP_END);
            dataToUI();
        });
        confirmDeleteDialog.open();
    }

    private void edit(Employee employee) {
        QueryParameters params = QueryParameters.simple(Collections.singletonMap("id", Long.toString(employee.getId())));
        UI.getCurrent().navigate(EmployeeEditorView.class, params);
    }

    @Override
    public void onComponentEvent(ComponentEvent componentEvent) {
        dataToUI();
    }

    private void addImageColumn() {
        employeeGrid.addComponentColumn(employee -> {
            //final StreamResource pdfResource = new StreamResource(() -> new ByteArrayInputStream(data), "report.pdf");
            final Image image = new Image();
            Picture picture = employee.getPicture();
            if (picture != null && picture.getPhoto() != null) {
                StreamResource resource = new StreamResource(picture.getFileName() != null ? picture.getFileName() : "", () -> new ByteArrayInputStream(picture.getPhoto()));
                image.setSrc(resource);
                image.setAlt(picture.getFileName());
                image.setHeight("30px");
                image.setWidth("30px");
            }
            image.getElement().addEventListener("mouseover", (domEvent) -> {
                ImageNotification imageNotification = new ImageNotification();
                imageNotification.open(employee.getPicture());

            });
            return image;
        }).setHeader("Photo");
    }

    DataProvider<Employee, Void> getEmployeeDataProvider(long id) {

        DataProvider<Employee, Void> dataProvider =
                DataProvider.fromCallbacks(

                        // First callback fetches items based on a query
                        query -> {
                            // The index of the first item to load
                            int offset = query.getOffset();

                            // The number of items to load
                            int limit = query.getLimit();

                            int page = offset / employeeGrid.getPageSize();

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
                            Page<Employee> employees = employeeRepository.findByCompanyId(id, sorted);

                            return employees.stream();
                        },
                        // Second callback fetches the total number of items currently in the Grid.
                        // The grid can then use it to properly adjust the scrollbars.
                        query -> employeeRepository.countByCompanyId(id));

        return dataProvider;
    }

}
