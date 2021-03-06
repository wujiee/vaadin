/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vaadin.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Lightweight profiling tool that can be used to collect profiling data with
 * zero overhead unless enabled. To enable profiling, add
 * <code>&lt;set-property name="vaadin.profiler" value="true" /&gt;</code> to
 * your .gwt.xml file.
 * 
 * @author Vaadin Ltd
 * @since 7.0.0
 */
public class Profiler {
    /**
     * Class to include using deferred binding to enable the profiling.
     * 
     * @author Vaadin Ltd
     * @since 7.0.0
     */
    public static class EnabledProfiler extends Profiler {
        @Override
        protected boolean isImplEnabled() {
            return true;
        }
    }

    private static final String evtGroup = "VaadinProfiler";

    private static final class GwtStatsEvent extends JavaScriptObject {
        protected GwtStatsEvent() {
            // JSO constructor
        }

        private native String getEvtGroup()
        /*-{
            return this.evtGroup;
        }-*/;

        private native double getMillis()
        /*-{
            return this.millis;
        }-*/;

        private native String getSubSystem()
        /*-{
            return this.subSystem;
        }-*/;

        private native String getType()
        /*-{
            return this.type;
        }-*/;

        private native String getModuleName()
        /*-{
            return this.moduleName;
        }-*/;

        public final String getEventName() {
            String group = getEvtGroup();
            if (evtGroup.equals(group)) {
                return getSubSystem();
            } else {
                return group + "." + getSubSystem();
            }
        }
    }

    private static class Node {

        private final String name;
        private final LinkedHashMap<String, Node> children = new LinkedHashMap<String, Node>();
        private double time = 0;
        private int count = 0;

        public Node(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        private Node accessChild(String name, double time) {
            Node child = children.get(name);
            if (child == null) {
                child = new Node(name);
                children.put(name, child);
            }
            child.time -= time;
            child.count++;
            return child;
        }

        public double getTimeSpent() {
            return time;
        }

        public int getCount() {
            return count;
        }

        public double getOwnTime() {
            double time = getTimeSpent();
            for (Node node : children.values()) {
                time -= node.getTimeSpent();
            }
            return time;
        }

        public Widget buildTree() {
            String message = getStringRepresentation("");

            if (getName() == null || !children.isEmpty()) {
                SimpleTree tree = new SimpleTree(message);
                for (Node node : children.values()) {
                    Widget child = node.buildTree();
                    tree.add(child);
                }
                return tree;
            } else {
                return new Label(message);
            }
        }

        public void buildRecursiveString(StringBuilder builder, String prefix) {
            if (getName() != null) {
                String msg = getStringRepresentation(prefix);
                builder.append(msg + '\n');
            }
            String childPrefix = prefix + "*";
            for (Node node : children.values()) {
                node.buildRecursiveString(builder, childPrefix);
            }
        }

        @Override
        public String toString() {
            return getStringRepresentation("");
        }

        private String getStringRepresentation(String prefix) {
            if (getName() == null) {
                return "";
            }
            String msg = prefix + " " + getName() + " in " + getTimeSpent()
                    + " ms.";
            if (getCount() > 1) {
                msg += " Invoked "
                        + getCount()
                        + " times ("
                        + roundToSignificantFigures(getTimeSpent() / getCount())
                        + " ms per time).";
            }
            if (!children.isEmpty()) {
                double ownTime = getOwnTime();
                msg += " " + ownTime + " ms spent in own code";
                if (getCount() > 1) {
                    msg += " ("
                            + roundToSignificantFigures(ownTime / getCount())
                            + " ms per time)";
                }
                msg += '.';
            }
            return msg;
        }

        public static double roundToSignificantFigures(double num) {
            // Number of significant digits
            int n = 3;
            if (num == 0) {
                return 0;
            }

            final double d = Math.ceil(Math.log10(num < 0 ? -num : num));
            final int power = n - (int) d;

            final double magnitude = Math.pow(10, power);
            final long shifted = Math.round(num * magnitude);
            return shifted / magnitude;
        }

        public void sumUpTotals(Map<String, Node> totals) {
            String name = getName();
            if (name != null) {
                Node totalNode = totals.get(name);
                if (totalNode == null) {
                    totalNode = new Node(name);
                    totals.put(name, totalNode);
                }

                totalNode.time += getOwnTime();
                totalNode.count += getCount();
            }
            for (Node node : children.values()) {
                node.sumUpTotals(totals);
            }
        }
    }

    /**
     * Checks whether the profiling gathering is enabled.
     * 
     * @return <code>true</code> if the profiling is enabled, else
     *         <code>false</code>
     */
    public static boolean isEnabled() {
        // This will be fully inlined by the compiler
        Profiler create = GWT.create(Profiler.class);
        return create.isImplEnabled();
    }

    /**
     * Enters a named block. There should always be a matching invocation of
     * {@link #leave(String)} when leaving the block. Calls to this method will
     * be removed by the compiler unless profiling is enabled.
     * 
     * @param name
     *            the name of the entered block
     */
    public static void enter(String name) {
        if (isEnabled()) {
            logGwtEvent(name, "begin");
        }
    }

    /**
     * Leaves a named block. There should always be a matching invocation of
     * {@link #enter(String)} when entering the block. Calls to this method will
     * be removed by the compiler unless profiling is enabled.
     * 
     * @param name
     *            the name of the left block
     */
    public static void leave(String name) {
        if (isEnabled()) {
            logGwtEvent(name, "end");
        }
    }

    private static native final void logGwtEvent(String name, String type)
    /*-{
        $wnd.__gwtStatsEvent({
            evtGroup: @com.vaadin.client.Profiler::evtGroup,
            moduleName: @com.google.gwt.core.client.GWT::getModuleName()(),
            millis: (new Date).getTime(),
            sessionId: undefined,
            subSystem: name,
            type: type
        });
    }-*/;

    /**
     * Resets the collected profiler data. Calls to this method will be removed
     * by the compiler unless profiling is enabled.
     */
    public static void reset() {
        if (isEnabled()) {
            /*
             * Old implementations might call reset for initialization, so
             * ensure it is initialized here as well. Initialization has no side
             * effects if already done.
             */
            initialize();

            clearEventsList();
        }
    }

    /**
     * Initializes the profiler. This should be done before calling any other
     * function in this class. Failing to do so might cause undesired behavior.
     * This method has no side effects if the initialization has already been
     * done.
     * <p>
     * Please note that this method should be called even if the profiler is not
     * enabled because it will then remove a logger function that might have
     * been included in the HTML page and that would leak memory unless removed.
     * </p>
     * 
     * @since 7.0.2
     */
    public static void initialize() {
        if (isEnabled()) {
            ensureLogger();
        } else {
            ensureNoLogger();
        }
    }

    /**
     * Outputs the gathered profiling data to the debug console.
     */
    public static void logTimings() {
        if (!isEnabled()) {
            VConsole.log("Profiler is not enabled, no data has been collected.");
            return;
        }

        LinkedList<Node> stack = new LinkedList<Node>();
        Node rootNode = new Node(null);
        stack.add(rootNode);
        JsArray<GwtStatsEvent> gwtStatsEvents = getGwtStatsEvents();
        if (gwtStatsEvents.length() == 0) {
            VConsole.log("No profiling events recorded, this might happen if another __gwtStatsEvent handler is installed.");
            return;
        }

        for (int i = 0; i < gwtStatsEvents.length(); i++) {
            GwtStatsEvent gwtStatsEvent = gwtStatsEvents.get(i);
            String eventName = gwtStatsEvent.getEventName();
            String type = gwtStatsEvent.getType();
            boolean isBeginEvent = "begin".equals(type);

            Node stackTop = stack.getLast();
            boolean inEvent = eventName.equals(stackTop.getName())
                    && !isBeginEvent;

            if (!inEvent && stack.size() >= 2
                    && eventName.equals(stack.get(stack.size() - 2).name)
                    && !isBeginEvent) {
                // back out of sub event
                stackTop.time += gwtStatsEvent.getMillis();
                stack.removeLast();
                stackTop = stack.getLast();

                inEvent = true;
            }

            if (type.equals("end")) {
                if (!inEvent) {
                    VConsole.error("Got end event for " + eventName
                            + " but is currently in " + stackTop.getName());
                    return;
                }
                Node previousStackTop = stack.removeLast();
                previousStackTop.time += gwtStatsEvent.getMillis();
            } else {
                if (!inEvent) {
                    stackTop = stackTop.accessChild(eventName,
                            gwtStatsEvent.getMillis());
                    stack.add(stackTop);
                }
                if (!isBeginEvent) {
                    // Create sub event
                    stack.add(stackTop.accessChild(eventName + "." + type,
                            gwtStatsEvent.getMillis()));
                }
            }
        }

        if (stack.size() != 1) {
            VConsole.log("Not all nodes are left, the last node is "
                    + stack.getLast().getName());
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        rootNode.buildRecursiveString(stringBuilder, "");
        Console implementation = VConsole.getImplementation();
        if (implementation instanceof VDebugConsole) {
            VDebugConsole console = (VDebugConsole) implementation;

            SimpleTree tree = (SimpleTree) stack.getFirst().buildTree();
            tree.setText("Profiler data");

            console.showTree(tree, stringBuilder.toString());
        } else {
            VConsole.log(stringBuilder.toString());
        }

        Map<String, Node> totals = new HashMap<String, Node>();
        rootNode.sumUpTotals(totals);

        ArrayList<Node> totalList = new ArrayList<Node>(totals.values());
        Collections.sort(totalList, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return (int) (o2.getTimeSpent() - o1.getTimeSpent());
            }
        });

        double total = 0;
        double top20total = 0;
        for (int i = 0; i < totalList.size(); i++) {
            Node node = totalList.get(i);
            double timeSpent = node.getTimeSpent();
            total += timeSpent;
            if (i < 20) {
                top20total += timeSpent;
            }
        }

        VConsole.log("Largest individual contributors using " + top20total
                + " ms out of " + total + " ms");
        for (int i = 0; i < 20 && i < totalList.size(); i++) {
            Node node = totalList.get(i);
            double timeSpent = node.getTimeSpent();
            total += timeSpent;
            VConsole.log(" * " + node.getName() + ": " + timeSpent + " ms in "
                    + node.getCount() + " invokations.");
        }

    }

    /**
     * Overridden in {@link EnabledProfiler} to make {@link #isEnabled()} return
     * true if GWT.create returns that class.
     * 
     * @return <code>true</code> if the profiling is enabled, else
     *         <code>false</code>
     */
    protected boolean isImplEnabled() {
        return false;
    }

    /**
     * Outputs the time passed since various events recored in
     * performance.timing if supported by the browser.
     */
    public static void logBootstrapTimings() {
        if (isEnabled()) {
            double now = Duration.currentTimeMillis();

            StringBuilder stringBuilder = new StringBuilder(
                    "Time since window.performance.timing events");
            SimpleTree tree = new SimpleTree(stringBuilder.toString());

            String[] keys = new String[] { "navigationStart",
                    "unloadEventStart", "unloadEventEnd", "redirectStart",
                    "redirectEnd", "fetchStart", "domainLookupStart",
                    "domainLookupEnd", "connectStart", "connectEnd",
                    "requestStart", "responseStart", "responseEnd",
                    "domLoading", "domInteractive",
                    "domContentLoadedEventStart", "domContentLoadedEventEnd",
                    "domComplete", "loadEventStart", "loadEventEnd" };

            for (String key : keys) {
                double value = getPerformanceTiming(key);
                if (value == 0) {
                    // Ignore missing value
                    continue;
                }
                String text = key + ": " + (now - value);
                tree.add(new Label(text));
                stringBuilder.append("\n * ");
                stringBuilder.append(text);
            }

            if (tree.getWidgetCount() == 0) {
                VConsole.log("Bootstrap timings not supported, please ensure your browser supports performance.timing");
                return;
            }

            Console implementation = VConsole.getImplementation();
            if (implementation instanceof VDebugConsole) {
                VDebugConsole console = (VDebugConsole) implementation;
                console.showTree(tree, stringBuilder.toString());
            } else {
                VConsole.log(stringBuilder.toString());
            }
        }
    }

    private static final native double getPerformanceTiming(String name)
    /*-{
        if ($wnd.performance && $wnd.performance.timing && $wnd.performance.timing[name]) {
            return $wnd.performance.timing[name];
        } else {
            return 0;
        }
    }-*/;

    private static native JsArray<GwtStatsEvent> getGwtStatsEvents()
    /*-{
        return $wnd.vaadin.gwtStatsEvents || [];
    }-*/;

    /**
     * Add logger if it's not already there, also initializing the event array
     * if needed.
     */
    private static native void ensureLogger()
    /*-{
        if (typeof $wnd.__gwtStatsEvent != 'function') {
            if (typeof $wnd.vaadin.gwtStatsEvents != 'object') {
                $wnd.vaadin.gwtStatsEvents = [];
            }  
            $wnd.__gwtStatsEvent = function(event) {
                $wnd.vaadin.gwtStatsEvents.push(event);
                return true;
            }
        }
    }-*/;

    /**
     * Remove logger function and event array if it seems like the function has
     * been added by us.
     */
    private static native void ensureNoLogger()
    /*-{
        if (typeof $wnd.vaadin.gwtStatsEvents == 'object') {
            delete $wnd.vaadin.gwtStatsEvents;
            if (typeof $wnd.__gwtStatsEvent == 'function') {
                $wnd.__gwtStatsEvent = function(){};
            }
        }  
    }-*/;

    private static native JsArray<GwtStatsEvent> clearEventsList()
    /*-{
        $wnd.vaadin.gwtStatsEvents = [];
    }-*/;

}
