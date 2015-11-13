/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.expensereports;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import info.chili.gwt.callback.ALAsyncCallback;
import info.chili.gwt.rpc.HttpService;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.gwt.SearchComposite;
import info.yalamanchili.office.client.profile.employee.SelectEmployeeWidget;
import java.util.logging.Logger;

/**
 *
 * @author radhika.mukkala
 */
class SearchExpenseReportsPanel extends SearchComposite {

    private static Logger logger = Logger.getLogger(SearchExpenseReportsPanel.class.getName());
    SelectEmployeeWidget employee= new SelectEmployeeWidget("Employee", false,false);

    public SearchExpenseReportsPanel() {
        init("Expense Reports Search", "ExpenseReports", OfficeWelcome.constants);
    }

    @Override
    protected void onOpenAdvancedSearch() {
        super.onOpenAdvancedSearch();
        TabPanel.instance().myOfficePanel.sidePanelTop.setHeight("100%");
    }

    @Override
    protected void populateSearchSuggestBox() {

    }

    @Override
    protected void populateAdvancedSuggestBoxes() {
    }

    @Override
    protected void addListeners() {
    }

    @Override
    protected void configure() {
    }

    @Override
    protected void addWidgets() {
        addDropDown("employee", employee);
        addEnumField("status", false, false, ExpenseReportStatus.names());
    }

    @Override
    protected JSONObject populateEntityFromFields() {
        JSONObject entity = new JSONObject();
        assignEntityValueFromField("employee", entity, null);
        assignEntityValueFromField("status", entity);
        logger.info(entity.toString());
        return entity;
    }

    @Override
    protected void search(String searchText) {
    }

    @Override
    protected void search(JSONObject entity) {
        HttpService.HttpServiceAsync.instance().doPut(getSearchURI(0, 10), entity.toString(),
                OfficeWelcome.instance().getHeaders(), true, new ALAsyncCallback<String>() {
                    @Override
                    public void onResponse(String result) {
                        processSearchResult(result);
                    }
                });
    }

    @Override
    protected void postSearchSuccess(JSONArray result) {
        logger.info("result :" + result.toString());
        TabPanel.instance().expensePanel.entityPanel.clear();
        TabPanel.instance().getExpensePanel().entityPanel.add(new ReadAllExpenseReportsPanel(result));
    }

    @Override
    protected String getSearchURI(String searchText, Integer start, Integer limit) {
        return URL.encode(OfficeWelcome.constants.root_url() + "expensereport/search/" + searchText + "/" + start.toString() + "/"
                + limit.toString());
    }

    @Override
    protected String getSearchURI(Integer start, Integer limit) {
        return URL.encode(OfficeWelcome.constants.root_url() + "expensereport/search-expensereport/" + start.toString() + "/"
                + limit.toString());
    }

    @Override
    protected boolean disableRegularSearch() {
        return true;
    }
}
