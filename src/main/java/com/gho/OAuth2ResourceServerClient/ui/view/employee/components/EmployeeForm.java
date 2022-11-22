package com.gho.OAuth2ResourceServerClient.ui.view.employee.components;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.repository.CompanyRepository;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class EmployeeForm extends FormLayout {

    TextField id = new TextField("id");
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    TextField email = new TextField("Email");
    DatePicker birthDate = new DatePicker("Birthdate");
    ComboBox<Company> company = new ComboBox<Company>("Company");
    Binder<Employee> binder = new BeanValidationBinder<>(Employee.class);
    CompanyRepository companyRepository;
    private Employee employee;

    public EmployeeForm(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
        company.setItems(getCompanyDataProvider());
        add(id, firstName, lastName, birthDate, email, company);
        id.setReadOnly(true);
        addClassName("contact-form");
        binder.bindInstanceFields(this);
    }

    public void dataToUI(Employee employee) {
        this.employee = employee;
        binder.setBean(employee);
    }

    public Binder<Employee> getBinder() {
        return binder;
    }

    DataProvider<Company, String> getCompanyDataProvider() {
        DataProvider<Company, String> dataProvider =

                DataProvider.fromFilteringCallbacks(

                        // First callback fetches items based on a query
                        query -> {
                            // The index of the first item to load
                            int offset = query.getOffset();

                            // The number of items to load
                            int limit = query.getLimit();

                            int page = offset / company.getPageSize();

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
                            Page<Company> companies = companyRepository.findByNameStartsWithIgnoreCase(query.getFilter().get(), sorted);
//                            .out.println("Total elements" + companies.getTotalElements());
//                            System.out.println("Total pages" + companies.getTotalPages());

                            return companies.stream();
                        },
                        // Second callback fetches the total number of items currently in the Grid.
                        // The grid can then use it to properly adjust the scrollbars.
                        query -> companyRepository.countByNameStartsWithIgnoreCase(query.getFilter().get()));

        return dataProvider;

    }
}
