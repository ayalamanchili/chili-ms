/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.tae.timesheet;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import info.yalamanchili.gwt.fields.DataType;
import info.yalamanchili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.gwt.CreateComposite;
import info.yalamanchili.office.client.profile.employee.SelectEmployeeWidget;
import info.yalamanchili.office.client.rpc.HttpService;
import info.yalamanchili.office.client.tae.sow.SelectSOWWidget;
import java.util.logging.Logger;


/**
 *
 * @author raghu
 */
public class CreateTimesheetPanel extends CreateComposite {

     private static Logger logger = Logger.getLogger(info.yalamanchili.office.client.tae.timesheet.CreateTimesheetPanel.class.getName());
    public CreateTimesheetPanel(CreateComposite.CreateCompositeType type) {
        super(type);
        initCreateComposite("Timesheet", OfficeWelcome.constants);
    }
    @Override
    protected JSONObject populateEntityFromFields() {
         JSONObject ts = new JSONObject();
        assignEntityValueFromField("paidRate", ts);
        assignEntityValueFromField("billedRate", ts);
        assignEntityValueFromField("mondayPaidHours", ts);
        assignEntityValueFromField("mondayBilledHours", ts);
        assignEntityValueFromField("tuesdayPaidHours", ts);
        assignEntityValueFromField("tuesdayBilledHours", ts);
        assignEntityValueFromField("wednesdayPaidHours", ts);
        assignEntityValueFromField("wednesdayBilledHours", ts);
        assignEntityValueFromField("thursdayPaidHours", ts);
        assignEntityValueFromField("thursdayBilledHours", ts);
        assignEntityValueFromField("fridayPaidHours", ts);
        assignEntityValueFromField("fridayBilledHours", ts);
        assignEntityValueFromField("saturdayPaidHours", ts);
        assignEntityValueFromField("saturdayBilledHours", ts);
        assignEntityValueFromField("sundayPaidHours", ts);
        assignEntityValueFromField("sundayBilledHours", ts);
        assignEntityValueFromField("notes", ts);
        ts.put("timeSheetStatus",new JSONString("Approved") );
        ts.put("timeSheetCategory",new JSONString("Regular"));
        return ts;
    }

    @Override
    protected void createButtonClicked() {
         HttpService.HttpServiceAsync.instance().doPut(getURI(), entity.toString(), OfficeWelcome.instance().getHeaders(), true,
                new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable arg0) {
                        logger.info(arg0.getMessage());
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
       new ResponseStatusWidget().show("Timesheet Successfully created");
       TabPanel.instance().TimeandExpensePanel.entityPanel.clear();
        TabPanel.instance().TimeandExpensePanel.entityPanel.add(new ReadAllTimesheetPanel());
    }

    @Override
    protected void addListeners() {
        
    }

    @Override
    protected void configure() {
        
    }

    @Override
    protected void addWidgets() {
         addField("paidRate", false, true, DataType.CURRENCY_FIELD);
         addField("billedRate", false, true, DataType.CURRENCY_FIELD);
         addField("mondayPaidHours", false, true, DataType.FLOAT_FIELD);
         addField("mondayBilledHours", false, true, DataType.FLOAT_FIELD);
         addField("tuesdayPaidHours", false, true, DataType.FLOAT_FIELD);
         addField("tuesdayBilledHours", false, true, DataType.FLOAT_FIELD);
         addField("wednesdayPaidHours", false, true, DataType.FLOAT_FIELD);
         addField("wednesdayBilledHours", false, true, DataType.FLOAT_FIELD);
         addField("thursdayPaidHours", false, true, DataType.FLOAT_FIELD);
         addField("thursdayBilledHours", false, true, DataType.FLOAT_FIELD);
         addField("fridayPaidHours", false, true, DataType.FLOAT_FIELD);
         addField("fridayBilledHours", false, true, DataType.FLOAT_FIELD);
         addField("saturdayPaidHours", false, true, DataType.FLOAT_FIELD);
         addField("saturdayBilledHours", false, true, DataType.FLOAT_FIELD);
         addField("sundayPaidHours", false, true, DataType.FLOAT_FIELD);
         addField("sundayBilledHours", false, true, DataType.FLOAT_FIELD);
         addField("notes", false, true, DataType.STRING_FIELD);
         addDropDown("StatementofWork", new SelectSOWWidget(false, false));
         addDropDown("Employee", new SelectEmployeeWidget(false, false));
//         addDropDown("project", new SelectProjectWidget(false, false));
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
//         addField("name", false, true, DataType.STRING_FIELD);
//        addField("description", false, false, DataType.STRING_FIELD);
    }

    @Override
    protected String getURI() {
        return OfficeWelcome.constants.root_url() + "timesheet" ;
    }
    
}
