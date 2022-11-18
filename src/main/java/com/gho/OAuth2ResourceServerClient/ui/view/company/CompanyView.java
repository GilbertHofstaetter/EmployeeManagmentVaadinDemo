package com.gho.OAuth2ResourceServerClient.ui.view.company;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.obj.Picture;
import com.gho.OAuth2ResourceServerClient.repository.CompanyRepository;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import com.gho.OAuth2ResourceServerClient.service.CompanyService;
import com.gho.OAuth2ResourceServerClient.ui.MainUI;
import com.gho.OAuth2ResourceServerClient.ui.view.employee.EmployeeEditorView;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route(value = "company", layout = MainUI.class)
public class CompanyView extends Main {

    Grid<Company> grid;
    TextField filter;
    private final CompanyRepository companyRepository;

    private final CompanyService companyService;

    private final SessionFactory sessionFactory;

    public CompanyView(CompanyRepository companyRepository, CompanyService companyService, SessionFactory sessionFactory) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        add(layout);

        this.companyService = companyService;
        this.companyRepository = companyRepository;
        this.sessionFactory = sessionFactory;
        this.grid = new Grid<>(Company.class);
        grid.setSizeFull();
        grid.setColumns("id", "name");
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        addCountEmployeesColumn();
        addCountDocumentsColumn();
        addEditDeleteButton();

        this.filter = new TextField();
        HorizontalLayout actions = new HorizontalLayout(filter);
        layout.addAndExpand(actions, grid);
        filter.setPlaceholder("Filter by name");

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> dataToUI(e.getValue()));

        dataToUI(null);
    }

    void dataToUI(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            filterText = "";
        }
        grid.setItems(getCompanyDataProvider(filterText));
    }

    private void addCountEmployeesColumn() {
        grid.addComponentColumn(company -> {
            Label employeeCounter = new Label("");
            Session session = sessionFactory.openSession();
            session.update(company);
            employeeCounter.setText(Integer.toString(company.getEmployees().size()));
            session.close();
            return employeeCounter;
        }).setHeader("Employee counter");
    }

    private void addCountDocumentsColumn() {
        grid.addComponentColumn(company -> {
            Label documentsCounter = new Label("");
            Session session = sessionFactory.openSession();
            session.update(company);
            documentsCounter.setText(Integer.toString(company.getDocuments().size()));
            session.close();
            return documentsCounter;
        }).setHeader("Documents counter");
    }

    private void addEditDeleteButton() {
        grid.addComponentColumn(company -> {
            HorizontalLayout editCreateButtonLayout = new HorizontalLayout();
            editCreateButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

            Button editButton = new Button(
                    VaadinIcon.EDIT.create(), event -> {
                edit(company);
            });
            editButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            Button deleteButton = new Button(
                    VaadinIcon.DEL.create(), event -> {
                delete(company);
            });
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            editCreateButtonLayout.add(editButton, deleteButton);
            return editCreateButtonLayout;
        });
    }

    private void delete(Company company) {
        try {
            ConfirmDialog confirmDeleteDialog = new ConfirmDialog();
            confirmDeleteDialog.setHeader("Delete Employee");
            confirmDeleteDialog.setText("Are you sure?");
            confirmDeleteDialog.setCancelText("Cancel");
            confirmDeleteDialog.setCancelable(true);
            confirmDeleteDialog.setConfirmText("Delete");
            confirmDeleteDialog.addConfirmListener(listener -> {
                companyService.delete(company);
                Notification.show("Deleted.", 5000, Notification.Position.TOP_END);
                dataToUI(filter.getValue());
            });
            confirmDeleteDialog.open();
        } catch (Exception e) {
            Notification.show("Deletion not possible.", 5000, Notification.Position.TOP_END);
        }
    }

    private void edit(Company company) {
        QueryParameters params = QueryParameters.simple(Collections.singletonMap("id", Long.toString(company.getId())));
        UI.getCurrent().navigate(CompanyEditorView.class, params);
    }


    DataProvider<Company, Void> getCompanyDataProvider(String filterText) {

        DataProvider<Company, Void> dataProvider =
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
                            Page<Company> companies = companyRepository.findByNameStartsWithIgnoreCase(filterText, sorted);

                            return companies.stream();
                        },
                        // Second callback fetches the total number of items currently in the Grid.
                        // The grid can then use it to properly adjust the scrollbars.
                        query -> companyRepository.countByNameStartsWithIgnoreCase(filterText));

        return dataProvider;
    }
}