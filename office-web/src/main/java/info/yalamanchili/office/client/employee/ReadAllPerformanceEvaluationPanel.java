/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.employee;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import info.chili.gwt.callback.ALAsyncCallback;
import info.chili.gwt.crud.CRUDReadAllComposite;
import info.chili.gwt.crud.TableRowOptionsWidget;
import info.chili.gwt.date.DateUtils;
import info.chili.gwt.rpc.HttpService;
import info.chili.gwt.utils.JSONUtils;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.Auth;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.expense.ReadExpensePanel;
import info.yalamanchili.office.client.expense.UpdateExpensePanel;
import java.util.logging.Logger;

/**
 *
 * @author prasanthi.p
 */
public class ReadAllPerformanceEvaluationPanel extends CRUDReadAllComposite {

    private static Logger logger = Logger.getLogger(ReadAllPerformanceEvaluationPanel.class.getName());
    public static ReadAllPerformanceEvaluationPanel instance;

    public ReadAllPerformanceEvaluationPanel() {
        instance = this;
        initTable("PerformanceEvaluation", OfficeWelcome.constants);
    }

    @Override
    public void viewClicked(String entityId) {
        TabPanel.instance().expensePanel.entityPanel.clear();
//        TabPanel.instance().expensePanel.entityPanel.add(new ReadPerformanceEvaluationPanel(getEntity(entityId)));
    }

    @Override
    public void deleteClicked(String entityId) {
        HttpService.HttpServiceAsync.instance().doPut(getDeleteURL(entityId), null, OfficeWelcome.instance().getHeaders(), true,
                new ALAsyncCallback<String>() {
            @Override
            public void onResponse(String arg0) {
                postDeleteSuccess();
            }
        });
    }

    @Override
    public void postDeleteSuccess() {
        new ResponseStatusWidget().show("Successfully Deleted PerformanceEvaluation Information");
        TabPanel.instance().myOfficePanel.entityPanel.clear();
        TabPanel.instance().myOfficePanel.entityPanel.add(new ReadAllPerformanceEvaluationPanel());
    }

    @Override
    public void updateClicked(String entityId) {
        TabPanel.instance().myOfficePanel.entityPanel.clear();
//        TabPanel.instance().myOfficePanel.entityPanel.add(new UpdatePerformanceEvaluationPanel(getEntity(entityId)));
    }

    @Override
    public void preFetchTable(int start) {
        HttpService.HttpServiceAsync.instance().doGet(getEvalURL(start, OfficeWelcome.constants.tableSize()), OfficeWelcome.instance().getHeaders(),
                false, new ALAsyncCallback<String>() {
            @Override
            public void onResponse(String result) {
                logger.info(result);
                postFetchTable(result);
            }
        });
    }

    @Override
    public void createTableHeader() {
        table.setText(0, 0, getKeyValue("Table_Action"));
        table.setText(0, 1, getKeyValue("EvaluationDate"));
        table.setText(0, 2, getKeyValue("EvaluationPeriodStartDate"));
        table.setText(0, 3, getKeyValue("EvaluationPeriodEndDate"));
        table.setText(0, 4, getKeyValue("Type"));
        table.setText(0, 5, getKeyValue("Rating"));
//        table.setText(0, 6, getKeyValue("KeyAccomplishments"));
//        table.setText(0, 7, getKeyValue("AreasNeedImprovement"));
//        table.setText(0, 8, getKeyValue("ManagersComments"));
//        table.setText(0, 9, getKeyValue("EmployeeComments"));
//        table.setText(0, 10, getKeyValue("CeoComments"));
//        table.setText(0, 11, getKeyValue("Employee"));
    }

    @Override
    public void fillData(JSONArray entities) {
        for (int i = 1; i <= entities.size(); i++) {
            JSONObject entity = (JSONObject) entities.get(i - 1);
            addOptionsWidget(i, entity);
            table.setText(i, 1, DateUtils.getFormatedDate(JSONUtils.toString(entity, "evaluationDate"), DateTimeFormat.PredefinedFormat.DATE_LONG));
            table.setText(i, 2, DateUtils.getFormatedDate(JSONUtils.toString(entity, "evaluationPeriodStartDate"), DateTimeFormat.PredefinedFormat.DATE_LONG));
            table.setText(i, 3, DateUtils.getFormatedDate(JSONUtils.toString(entity, "evaluationPeriodEndDate"), DateTimeFormat.PredefinedFormat.DATE_LONG));
            table.setText(i, 4, JSONUtils.toString(entity.get("type"), "name"));
            table.setText(i, 5, JSONUtils.toString(entity, "rating"));
//            table.setText(i, 6, JSONUtils.toString(entity, "keyAccomplishments"));
//            table.setText(i, 7, JSONUtils.toString(entity, "areasNeedImprovement"));
//            table.setText(i, 8, JSONUtils.toString(entity, "managersComments"));
//            table.setText(i, 9, JSONUtils.toString(entity, "employeeComments"));
//            table.setText(i, 10, JSONUtils.toString(entity, "ceoComments"));
//            table.setText(i, 11, JSONUtils.toString(entity.get("employee"), "firstName"));
        }
    }

    @Override
    protected void addOptionsWidget(int row, JSONObject entity) {
        if (Auth.isAdmin() || Auth.isHR()) {
            createOptionsWidget(TableRowOptionsWidget.OptionsType.READ_UPDATE_DELETE, row, JSONUtils.toString(entity, "id"));
        } else {
            createOptionsWidget(TableRowOptionsWidget.OptionsType.READ, row, JSONUtils.toString(entity, "id"));
        }
    }

    private String getDeleteURL(String entityId) {
        return OfficeWelcome.instance().constants.root_url() + "performance-evaluation/delete/" + entityId;
    }

    private String getEvalURL(Integer start, String limit) {
        return OfficeWelcome.constants.root_url() + "performance-evaluation/" + start.toString() + "/"
                + limit.toString();
    }
}
