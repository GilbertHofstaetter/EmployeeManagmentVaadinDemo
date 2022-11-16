package com.gho.OAuth2ResourceServerClient.ui.view.employee;

import com.gho.OAuth2ResourceServerClient.obj.Employee;
import com.gho.OAuth2ResourceServerClient.repository.EmployeeRepository;
import com.gho.OAuth2ResourceServerClient.ui.MainUI;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route(value="employeeEditor", layout = MainUI.class)
public class EmployeeEditorView extends Main implements HasUrlParameter<String> {

    private Employee employee;
    private final EmployeeRepository employeeRepository;

    public EmployeeEditorView(EmployeeRepository employeeRepository) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        add(layout);

        this.employeeRepository = employeeRepository;
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

    private void load(Long id) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (!employeeOpt.isEmpty()) {
            employee = employeeOpt.get();
            dataToUi();
        }
    }
    private void dataToUi() {
       // employeeForm.setEmployee(employee);
       // pictureForm.dataToUi();
    }
}
