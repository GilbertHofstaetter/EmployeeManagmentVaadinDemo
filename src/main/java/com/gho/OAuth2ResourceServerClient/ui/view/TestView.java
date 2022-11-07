package com.gho.OAuth2ResourceServerClient.ui.view;

import com.gho.OAuth2ResourceServerClient.ui.MainUI;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.security.PermitAll;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedInputStream;

@Route(value="test", layout = MainUI.class)
@Transactional(/*readOnly = true*/)
public class TestView extends Main {

    private FeederThread thread;

    public TestView() {
        VerticalLayout layout = new VerticalLayout();
        add(layout);

        Button create = new Button("Create", VaadinIcon.CHECK.create());
        create.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                add(new Label("Test"));
                //documentDialog.buildLayout();
                //https://blog.frankel.ch/guide-lazyinitializationexception/
                //var transaction = entityManager.getTransaction();
                //transaction.begin();

                //save();

                //transaction.commit();
            }
        });

        layout.add(create);


        IFrame iFrame = new IFrame("https://www.youtube.com/embed/dQw4w9WgXcQ");
        iFrame.setHeight("315px");
        iFrame.setWidth("560px");
        iFrame.setAllow("accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture");
        iFrame.getElement().setAttribute("allowfullscreen", true);
        iFrame.getElement().setAttribute("frameborder", "0");
        layout.add(iFrame);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        add(new Span("Waiting for updates"));

        // Start the data feed thread
        thread = new FeederThread(attachEvent.getUI(), this);
        thread.start();
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
        thread.interrupt();
        thread = null;
    }

    private static class FeederThread extends Thread {
        private final UI ui;
        private final TestView view;

        private int count = 0;

        public FeederThread(UI ui, TestView view) {
            this.ui = ui;
            this.view = view;
        }

        @Override
        public void run() {
            try {
                // Update the data for a while
                while (count < 10) {
                    // Sleep to emulate background work
                    Thread.sleep(500);
                    String message = "This is update " + count++;

                    ui.access(() -> view.add(new Span(message)));
                }

                // Inform that we're done
                ui.access(() -> {
                    view.add(new Span("Done updating"));
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
