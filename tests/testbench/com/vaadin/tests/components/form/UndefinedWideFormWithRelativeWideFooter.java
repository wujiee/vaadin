package com.vaadin.tests.components.form;

import com.vaadin.Application;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class UndefinedWideFormWithRelativeWideFooter extends
        Application.LegacyApplication {

    @Override
    public void init() {

        Root w = new Root("Test");
        setMainWindow(w);

        final Form f = new Form();
        w.addComponent(f);
        f.setSizeUndefined();
        f.getLayout().setSizeUndefined();

        f.setCaption("Test form with a really long caption");
        f.addField("foo", new TextField("Foo"));
        f.addField("bar", new TextField("A bit longer field caption"));

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");
        Button b = new Button("right aligned");
        hl.addComponent(b);
        hl.setComponentAlignment(b, Alignment.TOP_RIGHT);
        f.setFooter(hl);
    }
}