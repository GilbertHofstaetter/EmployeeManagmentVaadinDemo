package com.gho.OAuth2ResourceServerClient.ui.view;

import com.gho.OAuth2ResourceServerClient.ui.MainUI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;

import javax.servlet.http.HttpServletResponse;

/**
 * View shown when trying to navigate to a view that does not exist using
 */
@ParentLayout(MainUI.class)
public class ErrorView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    private final Span explanation;

    public ErrorView() {
        H1 header = new H1("The view could not be found.");
        add(header);

        explanation = new Span();
        add(explanation);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        explanation.setText("Could not navigate to '"
                + event.getLocation().getPath() + "'.");
        return HttpServletResponse.SC_NOT_FOUND;
    }
}
