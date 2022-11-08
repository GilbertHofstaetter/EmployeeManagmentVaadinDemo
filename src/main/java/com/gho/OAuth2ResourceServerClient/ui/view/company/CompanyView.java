package com.gho.OAuth2ResourceServerClient.ui.view.company;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.repository.CompanyRepository;
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

@Route(value = "company", layout = MainUI.class)
public class CompanyView extends Main {

    Grid<Company> grid;
    TextField filter;
    private final CompanyRepository companyRepository;

    public CompanyView(CompanyRepository companyRepository) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        add(layout);

        this.companyRepository = companyRepository;
        this.grid = new Grid<>(Company.class);
        grid.setSizeFull();
        grid.setColumns("id", "name");
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        this.filter = new TextField();
        HorizontalLayout actions = new HorizontalLayout(filter);
        layout.addAndExpand(actions, grid);
        filter.setPlaceholder("Filter by last name");

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> listCompanies(e.getValue()));

        listCompanies(null);
    }

    void listCompanies(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            filterText = "";
        }
        grid.setItems(getCompanyDataProvider(filterText));
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