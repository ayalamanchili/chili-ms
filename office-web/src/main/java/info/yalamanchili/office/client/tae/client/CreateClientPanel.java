/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.tae.client;

import info.yalamanchili.office.client.tae.project.ReadAllProjectsPanel;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import info.yalamanchili.gwt.fields.DataType;
import info.yalamanchili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.gwt.CreateComposite;
import info.yalamanchili.office.client.profile.emailtype.CreateEmailTypePanel;
import info.yalamanchili.office.client.profile.skill.SkillSidePanel;
import info.yalamanchili.office.client.rpc.HttpService;
import java.util.logging.Logger;

/**
 *
 * @author Prashanthi
 */
public class CreateClientPanel extends CreateComposite {

    private static Logger logger = Logger.getLogger(CreateClientPanel.class.getName());

    public CreateClientPanel(CreateComposite.CreateCompositeType type) {
        super(type);
        initCreateComposite("Client", OfficeWelcome.constants);
    }

    @Override
    protected JSONObject populateEntityFromFields() {
        JSONObject clnt = new JSONObject();

        assignEntityValueFromField("name", clnt);
        assignEntityValueFromField("description", clnt);
        logger.info(clnt.toString());
        return clnt;
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
        new ResponseStatusWidget().show("successfully Client created");
        TabPanel.instance().TimeandExpensePanel.sidePanelTop.clear();
        TabPanel.instance().TimeandExpensePanel.sidePanelTop.add(new ClientSidePanel());
        TabPanel.instance().TimeandExpensePanel.entityPanel.clear();
        TabPanel.instance().TimeandExpensePanel.entityPanel.add(new ReadAllClientsPanel());
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
        addField("description", false, true, DataType.STRING_FIELD);
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
    }

    @Override
    protected String getURI() {
        return OfficeWelcome.constants.root_url() + "client" ;
    }
}
