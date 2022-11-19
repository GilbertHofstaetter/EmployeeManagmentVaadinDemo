package com.gho.OAuth2ResourceServerClient.ui.view.company;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.repository.CompanyRepository;
import com.gho.OAuth2ResourceServerClient.ui.MainUI;
import com.gho.OAuth2ResourceServerClient.ui.view.company.components.CompanyForm;
import com.gho.OAuth2ResourceServerClient.ui.view.company.components.DocumentForm;
import com.gho.OAuth2ResourceServerClient.ui.view.company.components.EmployeeForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route(value="companyEditor", layout = MainUI.class)
public class CompanyEditorView extends Main implements HasUrlParameter<String> {

    private final CompanyRepository companyRepository;

    private Company company;

    private final CompanyForm companyForm;

    private final EmployeeForm employeeForm;

    private final DocumentForm documentForm;

    protected Map<Tab, Component> tabSelector = new HashMap<>();

    public CompanyEditorView(CompanyRepository companyRepository, CompanyForm companyForm, EmployeeForm companyEmployeeForm) {
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

        this.companyForm = companyForm;
        this.employeeForm = companyEmployeeForm;
        this.documentForm = new DocumentForm();

        addTabs(layout);
        tabSelector.values().forEach(component -> component.setVisible(false));

        layout.add(companyForm);
        layout.add(employeeForm);
        layout.add(documentForm);
        companyForm.setVisible(true);
    }

    protected void addTabs(VerticalLayout layout) {
        Tab general = new Tab(VaadinIcon.USER.create(), new Span("General"));
        tabSelector.put(general, companyForm);
        Tab employees = new Tab(VaadinIcon.COG.create(), new Span("Employees"));
        tabSelector.put(employees, employeeForm);
        Tab documents = new Tab(VaadinIcon.BELL.create(),new Span("Documents"));
        tabSelector.put(documents, documentForm);

        for (Tab tab : new Tab[] { general, employees, documents }) {
            tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        }

        Tabs tabs = new Tabs(general, employees, documents);

        layout.add(tabs);
        tabs.addSelectedChangeListener(listener -> {
            Tab selected = listener.getSelectedTab();
            tabSelector.values().forEach(component -> component.setVisible(false));
            tabSelector.get(selected).setVisible(true);
        });
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
        employeeForm.setCompany(company);

    }


}
