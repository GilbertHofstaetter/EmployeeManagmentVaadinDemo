package com.gho.OAuth2ResourceServerClient.ui.view.employee;

import com.gho.OAuth2ResourceServerClient.obj.Company;
import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import com.gho.OAuth2ResourceServerClient.repository.PictureRepository;
import com.gho.OAuth2ResourceServerClient.ui.MainUI;
import com.gho.OAuth2ResourceServerClient.ui.view.employee.components.EmployeeForm;
import com.gho.OAuth2ResourceServerClient.ui.view.employee.components.PictureForm;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.AllFinishedEvent;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route(value="employeeEditor", layout = MainUI.class)
public class EmployeeEditorView extends Main implements HasUrlParameter<String>, ComponentEventListener<AllFinishedEvent> {

    private Employee employee;
    private final EmployeeRepository employeeRepository;

    private final EmployeeForm employeeForm;

    private final PictureForm pictureForm;

    public EmployeeEditorView(EmployeeRepository employeeRepository, EmployeeForm employeeForm, PictureRepository pictureRepository) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        add(layout);

        this.employeeRepository = employeeRepository;
        this.employeeForm = employeeForm;
        this.pictureForm = new PictureForm(pictureRepository);
        pictureForm.registerActionListener(this);

        MenuBar menuBar = new MenuBar();
        layout.add(menuBar);
        menuBar.addItem("New", (event) -> {
            create();
        });

        menuBar.addItem("Save", (event) -> {
            save();
        });

        layout.add(pictureForm);
        layout.add(employeeForm);
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
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (!employeeOpt.isEmpty()) {
            employee = employeeOpt.get();
            dataToUi();
        }
    }

    protected void create() {
        employee = new Employee();
        dataToUi();
    }

    protected void save() {
        if (employeeForm.getBinder().validate().isOk()) {
            employee = employeeRepository.save(employee);
            dataToUi();
        }
    }
    protected void dataToUi() {
       employeeForm.dataToUI(employee);
       pictureForm.dataToUI(employee);
    }
    
    @Override
    public void onComponentEvent(AllFinishedEvent allFinishedEvent) {
        save();
    }
}
