/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.home.tasks;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import info.chili.gwt.composite.ALComposite;
import info.chili.gwt.widgets.ClickableLink;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;

/**
 *
 * @author ayalamanchili
 */
public class TasksStackPanelWidget extends ALComposite implements ClickHandler {

    protected ScrollPanel panel = new ScrollPanel();
    protected FlowPanel mainPanel = new FlowPanel();
    protected ClickableLink myTasksL = new ClickableLink("My Tasks");
    protected ClickableLink completedTasksL = new ClickableLink("Completed Tasks");

    public TasksStackPanelWidget() {
        init(panel);
    }

    @Override
    protected void addListeners() {
        myTasksL.addClickHandler(this);
        completedTasksL.addClickHandler(this);
    }

    @Override
    protected void configure() {
    }

    @Override
    protected void addWidgets() {
        mainPanel.add(myTasksL);
        mainPanel.add(completedTasksL);
        panel.add(mainPanel);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource().equals(myTasksL)) {
            TabPanel.instance().getHomePanel().entityPanel.clear();
            String myTasksUrl = OfficeWelcome.constants.root_url() + "bpm/tasks/currentuser/";
            TabPanel.instance().getHomePanel().entityPanel.add(new ReadAllTasks(myTasksUrl));
        }
        if (event.getSource().equals(completedTasksL)) {
            TabPanel.instance().getHomePanel().entityPanel.clear();
            String completedTasksUrl = OfficeWelcome.constants.root_url() + "bpm/history/tasks/";
            TabPanel.instance().getHomePanel().entityPanel.add(new ReadAllTasks(completedTasksUrl));
        }
    }
}
