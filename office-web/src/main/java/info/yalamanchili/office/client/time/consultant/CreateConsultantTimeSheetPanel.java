/**
 * System Soft Technolgies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.time.consultant;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import info.chili.gwt.crud.CreateComposite;
import info.chili.gwt.fields.DataType;
import info.chili.gwt.fields.DateField;
import info.chili.gwt.rpc.HttpService;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.profile.employee.SelectConsultantEmployeeWidget;
import info.yalamanchili.office.client.time.TimeSheetCategory;
import info.yalamanchili.office.client.time.TimeSheetStatus;
import java.util.logging.Logger;

/**
 *
 * @author ayalamanchili
 */
public class CreateConsultantTimeSheetPanel extends CreateComposite {

    private static Logger logger = Logger.getLogger(CreateConsultantTimeSheetPanel.class.getName());
    SelectConsultantEmployeeWidget employeeF = new SelectConsultantEmployeeWidget("Employee", false, false);

    public CreateConsultantTimeSheetPanel(CreateComposite.CreateCompositeType type) {
        super(type);
        initCreateComposite("EmployeeTimeSheet", OfficeWelcome.constants);
    }

    @Override
    protected JSONObject populateEntityFromFields() {
        JSONObject entity = new JSONObject();
        assignEntityValueFromField("employee", entity);
        assignEntityValueFromField("category", entity);
        assignEntityValueFromField("startDate", entity);
        assignEntityValueFromField("endDate", entity);
        assignEntityValueFromField("status", entity);
        assignEntityValueFromField("hours", entity);
        assignEntityValueFromField("notes", entity);
        return entity;
    }

    @Override
    protected void createButtonClicked() {
        HttpService.HttpServiceAsync.instance().doPut(getURI(), entity.toString(), OfficeWelcome.instance().getHeaders(), true,
                new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable arg0) {
                handleErrorResponse(arg0);
            }

            @Override
            public void onSuccess(String arg0) {
                postCreateSuccess(arg0);
            }
        });
    }

    @Override
    protected void addButtonClicked() {
    }

    @Override
    protected void postCreateSuccess(String result) {
        new ResponseStatusWidget().show("Corporate Timesheet Successfully Created");
        TabPanel.instance().timePanel.entityPanel.clear();
        TabPanel.instance().getTimePanel().entityPanel.clear();
        TabPanel.instance().getTimePanel().entityPanel.add(new ReadAllConsultantTimeSheetsPanel(employeeF.getSelectedObjectId()));
    }

    @Override
    protected void addListeners() {
    }

    @Override
    protected void configure() {
    }

    @Override
    protected void addWidgets() {
        addDropDown("employee", employeeF);
        addEnumField("category", false, true, TimeSheetCategory.names());
        addEnumField("status", false, false, TimeSheetStatus.names());
        addField("startDate", false, true, DataType.DATE_FIELD);
        addField("endDate", false, true, DataType.DATE_FIELD);
        addField("hours", false, true, DataType.FLOAT_FIELD);
        addField("notes", false, false, DataType.TEXT_AREA_FIELD);
    }

    @Override
    protected boolean processClientSideValidations(JSONObject entity) {
        DateField startDateF = (DateField) fields.get("startDate");
        DateField endDateF = (DateField) fields.get("endDate");
        if (startDateF.getDate() != null && endDateF.getDate() != null && startDateF.getDate().after(endDateF.getDate())) {
            endDateF.setMessage("End Date must be equal to or after Start Date");
            return false;
        }
        return true;
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
    }

    @Override
    protected String getURI() {
        return OfficeWelcome.constants.root_url() + "consultant-timesheet";
    }

    @Override
    protected boolean showDocumentationLink() {
        return true;
    }

    @Override
    protected String getDocumentationLink() {
        return OfficeWelcome.instance().getOfficeClientConfig().getPortalDocumentationSiteUrl() + "timesheets/employee-leave-request.html";
    }
}
