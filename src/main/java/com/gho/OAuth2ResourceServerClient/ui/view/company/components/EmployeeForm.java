package com.gho.OAuth2ResourceServerClient.ui.view.company.components;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import com.gho.OAuth2ResourceServerClient.ui.view.employee.EmployeeEditorView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.QueryParameters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component("companyEmployeeForm")
public class EmployeeForm extends VerticalLayout {

    protected Company company;
    protected Grid<Employee> employeeGrid;

    private final EmployeeRepository employeeRepository;

    public EmployeeForm(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        this.employeeGrid = new Grid<>(Employee.class);
        employeeGrid.setSizeFull();
        employeeGrid.setColumns("id", "firstName", "lastName", "email", "birthDate");
        employeeGrid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        add(employeeGrid);
        setSizeFull();

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

    private void delete(Employee employee) {
        employee.setCompany(null);
        employee = employeeRepository.save(employee);
        dataToUI();
    }

    private void edit(Employee employee) {
        QueryParameters params = QueryParameters.simple(Collections.singletonMap("id", Long.toString(employee.getId())));
        UI.getCurrent().navigate(EmployeeEditorView.class, params);
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
