package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Root;
import com.vaadin.ui.TabSheet;

public class Ticket1519 extends Application.LegacyApplication {

    @Override
    public void init() {

        final Root mainWin = new Root("Test app to #1519");
        setMainWindow(mainWin);

        setTheme("tests-tickets");
        TabSheet ts = new TabSheet();

        ts.addTab(new CustomLayout("Ticket1519_News"), "News", null);
        ts.addTab(new CustomLayout("Ticket1519_Support"), "Support", null);

        mainWin.addComponent(ts);

    }
}