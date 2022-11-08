package com.gho.OAuth2ResourceServerClient.ui.view.employee;

import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import com.gho.OAuth2ResourceServerClient.ui.MainUI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Route(value="employee", layout = MainUI.class)
public class EmployeeView extends Main {

    private EmployeeRepository employeeRepository;

    Grid<Employee> grid;

    TextField filter;

    public EmployeeView(EmployeeRepository employeeRepository) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        add(layout);

        this.employeeRepository = employeeRepository;
        this.grid = new Grid<>(Employee.class);
        grid.setSizeFull();
        grid.setColumns("id", "firstName", "lastName", "email", "birthDate");
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

//        grid.addComponentColumn(employee -> {
//            //final StreamResource pdfResource = new StreamResource(() -> new ByteArrayInputStream(data), "report.pdf");
//            final Image image = new Image();
//            Picture picture = employee.getPicture();
//            if (picture.getPhoto() != null) {
//                StreamResource resource = new StreamResource(picture.getFileName() != null ? picture.getFileName() : "", () -> new ByteArrayInputStream(picture.getPhoto()));
//                image.setSrc(resource);
//                image.setAlt(picture.getFileName());
//                image.setHeight("30px");
//                image.setWidth("30px");
//            }
//            image.getElement().addEventListener("mouseover", (domEvent) -> {
//                image.setHeight("400px");
//                image.setWidth("400px");
//                ImageNotification imageNotification = new ImageNotification();
//                imageNotification.open(employee.getPicture());
//
//            });
//            image.getElement().addEventListener("mouseleave", (domEvent) -> {
//                image.setHeight("30px");
//                image.setWidth("30px");
//            });
//            return image;
//        }).setAutoWidth(true).setFlexGrow(5).setHeader("Photo");

        this.filter = new TextField();
        HorizontalLayout actions = new HorizontalLayout(filter);
        layout.addAndExpand(actions, grid);
        filter.setPlaceholder("Filter by last name");

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> listEmployees(e.getValue()));

        listEmployees(null);
    }

    void listEmployees(String filterText) {
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
                        query ->  (int) employeeRepository.countByLastNameStartsWithIgnoreCase(filterText));

        return dataProvider;
    }
}
