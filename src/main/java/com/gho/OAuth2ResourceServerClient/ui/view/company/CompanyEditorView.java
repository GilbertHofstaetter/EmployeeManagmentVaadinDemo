package com.gho.OAuth2ResourceServerClient.ui.view.company;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.repository.CompanyRepository;
import com.gho.OAuth2ResourceServerClient.ui.MainUI;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route(value="companyEditor", layout = MainUI.class)
public class CompanyEditorView extends Main implements HasUrlParameter<String> {

    private final CompanyRepository companyRepository;

    private Company company;

    private CompanyForm companyForm;

    public CompanyEditorView(CompanyRepository companyRepository) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        add(layout);

        this.companyRepository = companyRepository;

        MenuBar menuBar = new MenuBar();
        layout.add(menuBar);
        menuBar.addItem("New", (event) -> {
            create();
        });

        menuBar.addItem("Save", (event) -> {
            save();
        });

        companyForm = new CompanyForm();
        layout.add(companyForm);

    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String s) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        Map<String, List<String>> parametersMap = queryParameters.getParameters();

        try {
            Long id = Long.valueOf((String) parametersMap.get("id").toArray()[0]);
            load(id);
        } catch (Exception e) {}
    }

    protected void load(Long id) {
        Optional<Company> companyOpt = companyRepository.findById(id);
        if (!companyOpt.isEmpty()) {
            company = companyOpt.get();
            dataToUi();
        }
    }

    protected void create() {
        company = new Company();
        dataToUi();
    }

    protected void save() {
        if (companyForm.getBinder().validate().isOk()) {
            company = companyRepository.save(company);
            dataToUi();
        }
    }

    protected void dataToUi() {
        companyForm.setCompany(company);
       // employeeGrid.setItems(employeeRepository.findByCompanyId(company.getId()));
    }

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
}
