package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Root;
import com.vaadin.ui.VerticalLayout;

public class Ticket2157 extends Application.LegacyApplication {

    @Override
    public void init() {
        Root w = new Root(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((AbstractOrderedLayout) w.getContent());
    }

    private void createUI(AbstractOrderedLayout layout) {
        VerticalLayout ol;
        Panel p;
        ComboBox cb;

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox without width");
        // p.setWidth("100px");
        cb = new ComboBox();
        // cb.setCaption("A combobox");
        // cb.setWidth("100%");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox without width with caption");
        // p.setWidth("100px");
        cb = new ComboBox();
        cb.setCaption("A combobox");
        // cb.setWidth("100px");
        p.addComponent(cb);
        layout.addComponent(p);

        //
        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 100px wide");
        // p.setWidth("100px");
        cb = new ComboBox();
        // cb.setCaption("A combobox");
        cb.setWidth("100px");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 100px wide with caption");
        // p.setWidth("100px");
        cb = new ComboBox();
        cb.setCaption("A combobox");
        cb.setWidth("100px");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 500px wide");
        // p.setWidth("500px");
        cb = new ComboBox();
        // cb.setCaption("A combobox");
        cb.setWidth("500px");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 500px wide with caption");
        // p.setWidth("500px");
        cb = new ComboBox();
        cb.setCaption("A combobox");
        cb.setWidth("500px");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 100% wide");
        p.setWidth("200px");
        ol.setWidth("100%");
        cb = new ComboBox();
        // cb.setCaption("A combobox");
        cb.setWidth("100%");
        p.addComponent(cb);
        layout.addComponent(p);

        ol = new VerticalLayout();
        p = new Panel(ol);
        p.setCaption("Combobox 100% wide with caption");
        p.setWidth("200px");
        ol.setWidth("100%");
        cb = new ComboBox();
        cb.setCaption("A combobox");
        cb.setWidth("100%");
        p.addComponent(cb);
        layout.addComponent(p);

    }
}