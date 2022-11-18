package com.gho.OAuth2ResourceServerClient.ui.view.employee;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.obj.Picture;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import com.gho.OAuth2ResourceServerClient.repository.PictureRepository;
import com.gho.OAuth2ResourceServerClient.service.EmployeeService;
import com.gho.OAuth2ResourceServerClient.ui.MainUI;
import com.gho.OAuth2ResourceServerClient.ui.components.ImageNotification;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route(value = "employee", layout = MainUI.class)
public class EmployeeView extends Main {

    Grid<Employee> grid;
    TextField filter;
    private final EmployeeRepository employeeRepository;

    private final EmployeeService employeeService;

    private final SessionFactory sessionFactory;

    public EmployeeView(EmployeeRepository employeeRepository, EmployeeService employeeService, SessionFactory sessionFactory) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        add(layout);

        this.employeeRepository = employeeRepository;
        this.employeeService = employeeService;
        this.sessionFactory = sessionFactory;
        this.grid = new Grid<>(Employee.class);
        grid.setSizeFull();
        grid.setColumns("id", "firstName", "lastName", "email", "birthDate");
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        addCompanyColumn();
        addCountDocumentsColumn();
        addImageColumn();
        addEditDeleteButton();

        this.filter = new TextField();
        HorizontalLayout actions = new HorizontalLayout(filter);
        layout.addAndExpand(actions, grid);
        filter.setPlaceholder("Filter by last name");

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> dataToUI(e.getValue()));

        dataToUI(null);
    }

    private void addImageColumn() {
        grid.addComponentColumn(employee -> {
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

    private void addCompanyColumn() {
        grid.addComponentColumn(employee -> {
            Label companyLabel = new Label("");
            Company company = employee.getCompany();
            if (company != null) {
                companyLabel.setText(company.getName());
            }
            return companyLabel;
        }).setHeader("Company");
    }

    private void addCountDocumentsColumn() {
        grid.addComponentColumn(employee -> {
            Label documentsCounter = new Label("");
            Session session = sessionFactory.openSession();
            session.update(employee);
            documentsCounter.setText(Integer.toString(employee.getDocuments().size()));
            session.close();
            return documentsCounter;
        }).setHeader("Documents counter");
    }

    private void addEditDeleteButton() {
        grid.addComponentColumn(employee -> {
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

    private void delete(Employee employee) {
        try {
            ConfirmDialog confirmDeleteDialog = new ConfirmDialog();
            confirmDeleteDialog.setHeader("Delete Employee");
            confirmDeleteDialog.setText("Are you sure?");
            confirmDeleteDialog.setCancelText("Cancel");
            confirmDeleteDialog.setCancelable(true);
            confirmDeleteDialog.setConfirmText("Delete");
            confirmDeleteDialog.addConfirmListener(listener -> {
                employeeService.delete(employee);
                Notification.show("Deleted.", 5000, Notification.Position.TOP_END);
                dataToUI(filter.getValue());
            });
            confirmDeleteDialog.open();
        } catch (Exception e) {
            Notification.show("Deletion not possible.", 5000, Notification.Position.TOP_END);
        }
    }

    private void edit(Employee employee) {
        QueryParameters params = QueryParameters.simple(Collections.singletonMap("id", Long.toString(employee.getId())));
        UI.getCurrent().navigate(EmployeeEditorView.class, params);
    }

    void dataToUI(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            filterText = "";
        }
        grid.setItems(getEmployeeDataProvider(filterText));
    }

    DataProvider<Employee, Void> getEmployeeDataProvider(String filterText) {

        DataProvider<Employee, Void> dataProvider =
                DataProvider.fromCallbacks(

                        // First callback fetches items based on a query
                        query -> {
                            // The index of the first item to load
                            int offset = query.getOffset();

                            // The number of items to load
                            int limit = query.getLimit();

                            int page = offset / grid.getPageSize();

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
                            Page<Employee> employees = employeeRepository.findByLastNameStartsWithIgnoreCase(filterText, sorted);

                            return employees.stream();
                        },
                        // Second callback fetches the total number of items currently in the Grid.
                        // The grid can then use it to properly adjust the scrollbars.
                        query -> employeeRepository.countByLastNameStartsWithIgnoreCase(filterText));

        return dataProvider;
    }
}
