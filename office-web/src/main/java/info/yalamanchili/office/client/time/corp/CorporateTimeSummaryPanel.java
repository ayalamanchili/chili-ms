/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.time.corp;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.HTML;
import info.chili.gwt.callback.ALAsyncCallback;
import info.chili.gwt.crud.ReadComposite;
import info.chili.gwt.fields.DataType;
import info.chili.gwt.rpc.HttpService;
import info.chili.gwt.utils.Alignment;
import info.yalamanchili.office.client.OfficeWelcome;
import java.util.logging.Logger;

/**
 *
 * @author anuyalamanchili
 */
public class CorporateTimeSummaryPanel extends ReadComposite {

    protected String employeeId;
    private static CorporateTimeSummaryPanel instance;
    private static Logger logger = Logger.getLogger(CorporateTimeSummaryPanel.class.getName());
    
    protected static HTML leaveRequestPolicy = new HTML("<!doctype html>\n"
            + "<html>\n"
            + "<head>\n"
            + "	<title></title>\n"
            + "</head>\n"
            + "<body>\n"
            + "	<li><a href=\"\\\\\\https://apps.sstech.us/site/office/timesheets/corp-approval-process.html\\\\\\\">Paid Time Off Leaves(PTO)</a>\n"
            + "	<hr /></li>\n"
            + "</ul>\n"
            + "</body>\n"
            + "</html>");

    public static CorporateTimeSummaryPanel instance() {
        return instance;
    }

    public CorporateTimeSummaryPanel() {
        initReadComposite("CorporateTimeSummary", OfficeWelcome.constants);
    }

    public CorporateTimeSummaryPanel(String employeeId) {
        this.employeeId = employeeId;
        initReadComposite("CorporateTimeSummary", OfficeWelcome.constants);
    }

    @Override
    public void loadEntity(String entityId) {
        HttpService.HttpServiceAsync.instance().doGet(getURI(), OfficeWelcome.instance().getHeaders(), true,
                new ALAsyncCallback<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.isEmpty() && JSONParser.parseLenient(response).isObject() != null) {
                    entity = (JSONObject) JSONParser.parseLenient(response);
                    populateFieldsFromEntity(entity);
                }
            }
        });
    }

    @Override
    public void populateFieldsFromEntity(JSONObject entity) {
        assignFieldValueFromEntity("employee", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("startDate", entity, DataType.DATE_FIELD);
        assignFieldValueFromEntity("availablePTOHours", entity, DataType.FLOAT_FIELD);
//        assignFieldValueFromEntity("availableVacationHours", entity, DataType.FLOAT_FIELD);
//        assignFieldValueFromEntity("usedUnpaidHours", entity, DataType.FLOAT_FIELD);
    }

    @Override
    protected void addListeners() {
    }

    @Override
    protected void configure() {
    }

    @Override
    protected void addWidgets() {
        addField("employee", true, false, DataType.STRING_FIELD, Alignment.HORIZONTAL);
        addField("startDate", true, false, DataType.DATE_FIELD, Alignment.HORIZONTAL);
        addField("availablePTOHours", true, false, DataType.FLOAT_FIELD, Alignment.HORIZONTAL);
        entityFieldsPanel.add(leaveRequestPolicy);
//        addField("availableVacationHours", true, false, DataType.FLOAT_FIELD, Alignment.HORIZONTAL);
//        addField("usedUnpaidHours", true, false, DataType.FLOAT_FIELD, Alignment.HORIZONTAL);
        alignFields();
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
    }

    @Override
    protected String getURI() {
        if (employeeId != null) {
            return OfficeWelcome.constants.root_url() + "corporate-timesheet/summary/" + employeeId;
        } else {
            return OfficeWelcome.constants.root_url() + "corporate-timesheet/summary";
        }
    }
}
