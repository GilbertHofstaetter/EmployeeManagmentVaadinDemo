package com.gho.OAuth2ResourceServerClient.ui;

import com.gho.OAuth2ResourceServerClient.ui.view.TestView;
import com.gho.OAuth2ResourceServerClient.ui.view.company.CompanyView;
import com.gho.OAuth2ResourceServerClient.ui.view.employee.EmployeeView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLink;

public class MainUI extends AppLayout implements AfterNavigationObserver {

    private final H1 pageTitle;

    private final RouterLink testView;

    private final RouterLink employeeView;

    private final RouterLink companyView;

    private final Anchor swagger;

    public MainUI() {
        // Navigation
        testView = createMenuLink(TestView.class, "Test", VaadinIcon.EDIT.create());
        swagger = createAnchorMenuLink("/swagger-ui.html", "Swagger-ui", VaadinIcon.ANCHOR.create());
        employeeView = createMenuLink(EmployeeView.class, "Employee", VaadinIcon.EDIT.create());
        companyView = createMenuLink(CompanyView.class, "Company", VaadinIcon.EDIT.create());


        final UnorderedList list = new UnorderedList(new ListItem(testView), new ListItem(swagger), new ListItem(employeeView), new ListItem(companyView));
        final Nav navigation = new Nav(list);
        addToDrawer(navigation);
        setPrimarySection(Section.NAVBAR);
        setDrawerOpened(true);

        // Header
        pageTitle = new H1("Home");
        //pageTitle.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");
        pageTitle.addClassName("menu-h1");
        pageTitle.getStyle().set("margin", "0");
        pageTitle.setSizeFull();

        Button logout = createMenuButton("Logout", VaadinIcon.SIGN_OUT.create());
        logout.addClickListener(e -> logout());
        logout.getElement().setAttribute("title", "Logout (Ctrl+L)");
        FlexLayout logoutButtonWrapper = new FlexLayout(logout);

        HorizontalLayout layout = new HorizontalLayout(new DrawerToggle(), pageTitle, logoutButtonWrapper);
        layout.setSizeFull();
        layout.setSpacing(true);
        layout.expand(logoutButtonWrapper);
        layout.setClassName("menu-header");

        final Header header = new Header(layout);
        header.setSizeFull();
        addToNavbar(header);
    }

    private RouterLink[] getRouterLinks() {
        return new RouterLink[]{testView, companyView, employeeView};
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        for (final RouterLink routerLink : getRouterLinks()) {
            if (routerLink.getHighlightCondition().shouldHighlight(routerLink, event)) {
                pageTitle.setText(((Span) routerLink.getChildren().toArray()[1]).getText());
            }
        }
    }

    private Button createMenuButton(String caption, Icon icon) {
        final Button routerButton = new Button(caption);
        routerButton.setClassName("menu-button");
        routerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        routerButton.setIcon(icon);
        icon.setSize("24px");
        return routerButton;
    }

    private RouterLink createMenuLink(Class<? extends Component> viewClass,
                                      String caption, Icon icon) {
        final RouterLink routerLink = new RouterLink(viewClass);
        routerLink.setClassName("menu-link");
        routerLink.add(icon);
        routerLink.add(new Span(caption));
        icon.setSize("24px");
        return routerLink;
    }

    private Anchor createAnchorMenuLink(String path,
                                        String caption, Icon icon) {
        final Anchor anchorLink = new Anchor(path, "");
        anchorLink.setClassName("menu-link");
        anchorLink.add(icon);
        anchorLink.add(new Span(caption));
        icon.setSize("24px");
        return anchorLink;
    }

    private void logout() {
        UI.getCurrent().getPage().setLocation("/logout");
    }
}
