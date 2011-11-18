package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

public class Ticket1444 extends Application.LegacyApplication {

    @Override
    public void init() {

        final Root mainWin = new Root("Test app to break layout fuction in IE7");
        setMainWindow(mainWin);

        VerticalLayout ol = new VerticalLayout();
        ol.setHeight("250px");
        ol.setWidth("500px");

        Label red = new Label(
                "<div style='background:red;width:100%;height:100%;'>??</div>",
                Label.CONTENT_XHTML);
        red.setSizeFull();

        ol.addComponent(red);
        mainWin.addComponent(ol);

    }
}