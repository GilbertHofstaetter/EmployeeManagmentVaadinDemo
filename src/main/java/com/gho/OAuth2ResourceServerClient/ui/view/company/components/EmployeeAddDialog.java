package com.gho.OAuth2ResourceServerClient.ui.view.company.components;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.obj.Picture;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import com.gho.OAuth2ResourceServerClient.ui.components.ImageNotification;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.AllFinishedEvent;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class EmployeeAddDialog extends Dialog {

    protected Grid<Employee> grid;

    protected TextField filter;

    protected Company company;

    private final EmployeeRepository employeeRepository;

    protected List<ComponentEventListener> componentEventListeners = new ArrayList();

    public EmployeeAddDialog(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        add(layout);

        this.grid = new Grid<>(Employee.class);
        grid.setSizeFull();
        grid.setColumns("id", "firstName", "lastName", "email", "birthDate");
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        addCompanyColumn();
        addImageColumn();
        addLinkEmployeeButton();

        this.filter = new TextField();
        HorizontalLayout actions = new HorizontalLayout(filter);
        layout.addAndExpand(actions, grid);
        filter.setPlaceholder("Filter by last name");

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> dataToUI(e.getValue()));

        Button closeButton = new Button("Close", e -> close());
        getFooter().add(closeButton);

        dataToUI(null);
    }

    public void open(Company company) {
        this.company = company;
        open();
    }

    private void addLinkEmployeeButton() {
        grid.addComponentColumn(employee -> {
            HorizontalLayout editCreateButtonLayout = new HorizontalLayout();
            editCreateButtonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

            Button editButton = new Button(
                    VaadinIcon.LINK.create(), event -> {
                linkEmployee(employee);
            });

            editCreateButtonLayout.add(editButton);
            return editCreateButtonLayout;
        } );
    }

    protected void linkEmployee(Employee employee) {
        employee.setCompany(company);
        employee = employeeRepository.save(employee);
        dataToUI(filter.getValue());
        componentEventListeners.forEach(componentEventListener -> {
            componentEventListener.onComponentEvent(new ComponentEvent(this, true));
        });
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

    void dataToUI(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            filterText = "";
        }
        grid.setItems(getEmployeeDataProvider(filterText));
    }

    public void registerComponentEventListener(ComponentEventListener componentEventListener) {
        componentEventListeners.add(componentEventListener);
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
