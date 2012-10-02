/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.tae.timesheetperiod;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import info.yalamanchili.gwt.fields.DataType;
import info.yalamanchili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.gwt.UpdateComposite;
import info.yalamanchili.office.client.rpc.HttpService;
/**
 *
 * @author bala
 */

public class UpdateTimeSheetPeriodPanel extends UpdateComposite {

     public UpdateTimeSheetPeriodPanel(JSONObject entity) {
        initUpdateComposite(entity, "TimeSheetPeriod", OfficeWelcome.constants);
    }
    @Override
    protected JSONObject populateEntityFromFields() {
        assignEntityValueFromField("name", entity);
        assignEntityValueFromField("startDate", entity);
        assignEntityValueFromField("endDate", entity);
        return entity;
    }

    @Override
    protected void updateButtonClicked() {
         HttpService.HttpServiceAsync.instance().doPut(getURI(), entity.toString(),
                OfficeWelcome.instance().getHeaders(), true, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable arg0) {
                handleErrorResponse(arg0);
            }

            @Override
            public void onSuccess(String arg0) {
                postUpdateSuccess(arg0);
            }
        });
    }

    @Override
    public void populateFieldsFromEntity(JSONObject entity) {
        assignFieldValueFromEntity("name", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("startDate", entity, DataType.DATE_FIELD);
        assignFieldValueFromEntity("endDate", entity, DataType.DATE_FIELD);
    }

    @Override
    protected void postUpdateSuccess(String result) {
        new ResponseStatusWidget().show("successfully updated Time Sheet Period");
        TabPanel.instance().TimeandExpensePanel.sidePanelTop.clear();
        TabPanel.instance().TimeandExpensePanel.sidePanelTop.add(new TimeSheetPeriodSidePanel());
        TabPanel.instance().TimeandExpensePanel.entityPanel.clear(); 
        TabPanel.instance().TimeandExpensePanel.entityPanel.add(new ReadAllTimeSheetPeriodsPanel());
    }

    @Override
    protected void addListeners() {
        
    }

    @Override
    protected void configure() {
       
    }

    @Override
    protected void addWidgets() {
        addField("name", false, true, DataType.STRING_FIELD);
        addField("startDate", false, true, DataType.DATE_FIELD);
        addField("endDate", false, false, DataType.DATE_FIELD);
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
       
    }

    @Override
    protected String getURI() {
         return OfficeWelcome.constants.root_url() + "timesheetperiod";
    }
    
}
