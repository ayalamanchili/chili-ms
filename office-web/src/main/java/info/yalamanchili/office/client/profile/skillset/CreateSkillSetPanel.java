/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.profile.skillset;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import info.yalamanchili.gwt.fields.DataType;
import info.yalamanchili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.gwt.CreateComposite;
import info.yalamanchili.office.client.gwt.FileUploadPanel;
import info.yalamanchili.office.client.profile.employee.TreeEmployeePanel;
import info.yalamanchili.office.client.rpc.HttpService;
import java.util.logging.Logger;

/**
 *
 * @author raghu
 */
public class CreateSkillSetPanel extends CreateComposite {

    protected String employeeId;
    private static Logger logger = Logger.getLogger(CreateSkillSetPanel.class.getName());
    FileUploadPanel resumeUploadPanel = new FileUploadPanel(OfficeWelcome.constants, "SkillSet", "resumeUrl", "SkillSet/resumeUrl");

    public CreateSkillSetPanel(String employeeId) {
        super(CreateCompositeType.CREATE);
        this.employeeId = employeeId;
        initCreateComposite("SkillSet", OfficeWelcome.constants);
    }

    @Override
    protected void addWidgets() {
        entityDisplayWidget.add(resumeUploadPanel);
    }

    @Override
    protected void addListeners() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void configure() {
        // TODO Auto-generated method stub
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
                        uploadResume(arg0);
                    }
                });
    }

    @Override
    protected JSONObject populateEntityFromFields() {
        JSONObject skillSet = new JSONObject();
        assignEntityValueFromField("lastUpdated", skillSet);
        skillSet.put("resumeUrl", resumeUploadPanel.getFileName());
        return skillSet;
    }

    @Override
    protected void addButtonClicked() {
    }

    protected void uploadResume(String entityId) {
        resumeUploadPanel.upload(entityId.trim());
    }

    @Override
    protected void postCreateSuccess(String result) {
        new ResponseStatusWidget().show("successfully created skllset");
//        TabPanel.instance().myOfficePanel.entityPanel.clear();
//        TabPanel.instance().myOfficePanel.entityPanel.add(new ReadSkillSetPanel(result));
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
        // TODO Auto-generated method stub
    }

    @Override
    protected String getURI() {
        return OfficeWelcome.constants.root_url() + "employee/skillset/" + employeeId;
    }
}
