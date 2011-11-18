package com.vaadin.tests.components.window;

import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.tests.util.Log;
import com.vaadin.tests.util.LoremIpsum;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.ResizeEvent;
import com.vaadin.ui.Window.ResizeListener;

public class LazyWindowResize extends AbstractTestCase {

    private Root mainWindow;
    private Window subWindow;
    private Button lazyMode;
    private Log log = new Log(5);
    private CheckBox resizeListenerCheckBox;

    protected ResizeListener resizeListener = new ResizeListener() {

        public void windowResized(ResizeEvent e) {
            throw new RuntimeException("Out of order...");
            // if (e.getWindow() == mainWindow) {
            // log.log("Main window resized");
            // } else {
            // log.log("Sub window resized");
            // }

        }
    };
    private CheckBox immediateCheckBox;;;

    @Override
    protected String getDescription() {
        return "Check or uncheck the checkbox to use lazy or eager resize events. Lazy mode uses a small delay before recalculating layout sizes and can be used to speed up resizes in slow UIs.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6715;
    }

    @Override
    public void init() {
        mainWindow = new Root("Resize test");
        setMainWindow(mainWindow);
        subWindow = new Window("Sub window");
        subWindow.setHeight("50%");
        subWindow.setWidth("50%");
        subWindow.center();
        subWindow.addComponent(new Label(LoremIpsum.get(1000)));
        getMainWindow().addWindow(subWindow);

        lazyMode = new CheckBox("Lazy resize");
        lazyMode.setImmediate(true);
        lazyMode.addListener(new ClickListener() {

            public void buttonClick(ClickEvent event) {
                setLazy(lazyMode.booleanValue());
            }
        });

        resizeListenerCheckBox = new CheckBox("Resize listener");
        resizeListenerCheckBox.setImmediate(true);
        resizeListenerCheckBox.addListener(new ClickListener() {

            public void buttonClick(ClickEvent event) {
                if (resizeListenerCheckBox.booleanValue()) {
                    subWindow.addListener(resizeListener);
                    mainWindow.addListener(resizeListener);
                } else {
                    subWindow.removeListener(resizeListener);
                    mainWindow.removeListener(resizeListener);
                }

            }

        });
        immediateCheckBox = new CheckBox("Windows immediate");
        immediateCheckBox.setImmediate(true);
        immediateCheckBox.addListener(new ClickListener() {

            public void buttonClick(ClickEvent event) {
                mainWindow.setImmediate(immediateCheckBox.booleanValue());
                subWindow.setImmediate(immediateCheckBox.booleanValue());

            }

        });
        mainWindow.addComponent(lazyMode);
        mainWindow.addComponent(resizeListenerCheckBox);
        mainWindow.addComponent(immediateCheckBox);
        mainWindow.addComponent(log);
        mainWindow.addComponent(new Label("<br/><br/>", Label.CONTENT_XHTML));
        mainWindow.addComponent(new Label(LoremIpsum.get(10000)));

        setLazy(false);
    }

    private void setLazy(boolean b) {
        mainWindow.setResizeLazy(b);
        subWindow.setResizeLazy(b);
    }

}