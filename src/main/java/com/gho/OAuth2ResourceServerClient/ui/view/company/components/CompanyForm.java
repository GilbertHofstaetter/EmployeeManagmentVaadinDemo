package com.gho.OAuth2ResourceServerClient.ui.view.company.components;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CompanyForm extends FormLayout {
    private Company company;

    TextField id = new TextField();

    TextField name = new TextField();

    Binder<Company> binder = new BeanValidationBinder<>(Company.class);

    public CompanyForm() {
        add(id, name);
        id.setReadOnly(true);
        addClassName("contact-form");
        binder.bindInstanceFields(this);
    }

    public void setCompany(Company company) {
        this.company = company;
        binder.setBean(company);
    }

    public Binder<Company> getBinder() {
        return  binder;
    }
}
