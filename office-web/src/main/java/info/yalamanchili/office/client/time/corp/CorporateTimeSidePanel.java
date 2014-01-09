/**
 * System Soft Technolgies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.time.corp;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import info.chili.gwt.composite.ALComposite;
import info.chili.gwt.crud.CreateComposite;
import info.chili.gwt.widgets.ClickableLink;
import info.yalamanchili.office.client.Auth;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.profile.employee.SelectCorpEmployeeWidget;
import info.yalamanchili.office.client.profile.employee.SelectEmployeeWidget;
import info.yalamanchili.office.client.tae.timesheet.ReadAllEmployeeTimeSheets;
import java.util.logging.Logger;

/**
 *
 * @author anuyalamanchili
 */
public class CorporateTimeSidePanel extends ALComposite implements ClickHandler {

    private static Logger logger = Logger.getLogger(CorporateTimeSidePanel.class.getName());
    public FlowPanel timeSheetsidepanel = new FlowPanel();
    ClickableLink createtimeSheetlink = new ClickableLink("Enter TimeSheet");

    //Timesheets for employee
    CaptionPanel timesheetsForEmpCaptionPanel = new CaptionPanel();
    FlowPanel timesheetsForEmpPanel = new FlowPanel();
    SelectCorpEmployeeWidget empWidget = new SelectCorpEmployeeWidget(false, false);
    Button showTimeSheetsForEmpB = new Button("View");

    public CorporateTimeSidePanel() {
        init(timeSheetsidepanel);
    }

    @Override
    protected void addListeners() {
        createtimeSheetlink.addClickHandler(this);
        showTimeSheetsForEmpB.addClickHandler(this);
    }

    @Override
    protected void configure() {
        timesheetsForEmpCaptionPanel.setCaptionHTML("Time Summary");
    }

    @Override
    protected void addWidgets() {
        if (Auth.isAdmin() || Auth.hasContractsRole()) {
            timeSheetsidepanel.add(createtimeSheetlink);
            //employee
            timesheetsForEmpPanel.add(empWidget);
            timesheetsForEmpPanel.add(showTimeSheetsForEmpB);
            timesheetsForEmpCaptionPanel.setContentWidget(timesheetsForEmpPanel);
            timeSheetsidepanel.add(timesheetsForEmpCaptionPanel);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource().equals(createtimeSheetlink)) {
            TabPanel.instance().timePanel.entityPanel.clear();
            TabPanel.instance().timePanel.entityPanel.add(new CreateCorporateTimeSheetPanel(CreateComposite.CreateCompositeType.CREATE));
        }
        if (empWidget.getSelectedObject() != null && event.getSource().equals(showTimeSheetsForEmpB)) {
            TabPanel.instance().getTimePanel().entityPanel.clear();
            TabPanel.instance().getTimePanel().entityPanel.add(new CorporateTimeSummaryPanel(empWidget.getSelectedObjectId()));
            TabPanel.instance().getTimePanel().entityPanel.add(new ReadAllCorporateTimeSheetPanel(empWidget.getSelectedObjectId()));
        }
    }
}
