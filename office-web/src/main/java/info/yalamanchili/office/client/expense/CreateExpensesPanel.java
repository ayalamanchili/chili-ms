/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.expense;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import info.chili.gwt.fields.DataType;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.expensecategory.SelectExpenseCategoryWidget;
import info.yalamanchili.office.client.gwt.CreateComposite;
import info.yalamanchili.office.client.profile.employee.SelectEmployeeWidget;
import info.yalamanchili.office.client.rpc.HttpService;
import java.util.logging.Logger;

/**
 *
 * @author raghu
 */
public class CreateExpensesPanel extends CreateComposite {
    private static Logger logger = Logger.getLogger(CreateExpensesPanel.class.getName());
   
    public CreateExpensesPanel(CreateComposite.CreateCompositeType type)
    {
        super(type);
        initCreateComposite("Expenses", OfficeWelcome.constants);
    }

    @Override
    protected JSONObject populateEntityFromFields() {
        JSONObject entity = new JSONObject();
        assignEntityValueFromField("employee", entity);
        assignEntityValueFromField("name", entity);
        assignEntityValueFromField("description", entity);
        assignEntityValueFromField("amount", entity);
        assignEntityValueFromField("expenseDate", entity);
        assignEntityValueFromField("category", entity);
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
        new ResponseStatusWidget().show("Expense Successfully Created");
        TabPanel.instance().getExpensePanel().entityPanel.clear();
        TabPanel.instance().getExpensePanel().entityPanel.add(new ReadAllExpensesPanel());
    }

    @Override
    protected void addListeners() {
        
    }

    @Override
    protected void configure() {
        
    }

    @Override
    protected void addWidgets() {
        addDropDown("employee", new SelectEmployeeWidget(false, true));
        addField("name", false, true, DataType.STRING_FIELD);
        addField("amount", false, false, DataType.CURRENCY_FIELD);
        addDropDown("category", new SelectExpenseCategoryWidget(false, true));
        addField("expenseDate", false, true, DataType.DATE_FIELD);
        addField("description", false, true, DataType.TEXT_AREA_FIELD);
       
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
        
    }

    @Override
    protected String getURI() {
         return OfficeWelcome.constants.root_url() + "expense";
    }
}
