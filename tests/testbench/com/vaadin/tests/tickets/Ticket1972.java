package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;

public class Ticket1972 extends Application.LegacyApplication {

    @Override
    public void init() {
        Root w = new Root(getClass().getName());
        setMainWindow(w);
        setTheme("tests-ticket");
        GridLayout layout = new GridLayout(3, 3);
        layout.setStyleName("borders");
        layout.addComponent(new Label("1-1"));
        layout.space();
        layout.space();
        layout.addComponent(new Label("2-1"));
        layout.space();
        layout.space();
        layout.addComponent(new Label("3-1"));
        layout.space();
        layout.addComponent(new Label("3-3"));

        w.setContent(layout);
    }

}