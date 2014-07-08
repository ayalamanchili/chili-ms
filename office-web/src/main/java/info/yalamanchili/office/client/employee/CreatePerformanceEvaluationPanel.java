/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.employee;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import info.chili.gwt.crud.CreateComposite;
import info.chili.gwt.fields.DataType;
import info.chili.gwt.rpc.HttpService;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.profile.employee.SelectEmployeeWidget;
import java.util.logging.Logger;

/**
 *
 * @author prasanthi.p
 */
public class CreatePerformanceEvaluationPanel extends CreateComposite {

    private static Logger logger = Logger.getLogger(CreatePerformanceEvaluationPanel.class.getName());
    SelectEmployeeWidget selectEmployeeWidgetF = new SelectEmployeeWidget("Employee", false, true);

    public CreatePerformanceEvaluationPanel(CreateComposite.CreateCompositeType type) {
        super(type);
        initCreateComposite("PerformanceEvaluation", OfficeWelcome.constants);
    }

    @Override
    protected JSONObject populateEntityFromFields() {
        JSONObject entity = new JSONObject();
        assignEntityValueFromField("evaluationDate", entity);
        assignEntityValueFromField("evaluationPeriodStartDate", entity);
        assignEntityValueFromField("evaluationPeriodEndDate", entity);
        assignEntityValueFromField("type", entity);
        assignEntityValueFromField("rating", entity);
        assignEntityValueFromField("keyAccomplishments", entity);
        assignEntityValueFromField("areasNeedImprovement", entity);
        assignEntityValueFromField("managersComments", entity);
        assignEntityValueFromField("employeeComments", entity);
        assignEntityValueFromField("ceoComments", entity);
        assignEntityValueFromField("employee", entity);
        logger.info("ddd" + entity);
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
        new ResponseStatusWidget().show("PerformanceEvaluation Successfully Created");
        TabPanel.instance().myOfficePanel.entityPanel.clear();
        TabPanel.instance().myOfficePanel.entityPanel.add(new ReadAllPerformanceEvaluationPanel());
    }

    @Override
    protected void addListeners() {
    }

    @Override
    protected void configure() {
    }

    @Override
    protected void addWidgets() {
        addField("evaluationDate", false, true, DataType.DATE_FIELD);
        addField("evaluationPeriodStartDate", false, true, DataType.DATE_FIELD);
        addField("evaluationPeriodEndDate", false, true, DataType.DATE_FIELD);
        addEnumField("type", false, false, EvaluationFrequencyType.names());
        addField("rating", false, true, DataType.IMAGE_FIELD);
        addField("keyAccomplishments", false, true, DataType.STRING_FIELD);
        addField("areasNeedImprovement", false, true, DataType.STRING_FIELD);
        addField("managersComments", false, false, DataType.STRING_FIELD);
        addField("employeeComments", false, true, DataType.STRING_FIELD);
        addField("ceoComments", false, true, DataType.STRING_FIELD);
        addDropDown("employee", selectEmployeeWidgetF);
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
    }

    @Override
    protected String getURI() {
        return OfficeWelcome.constants.root_url() + "performance-evaluation";
    }
}
