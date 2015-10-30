/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
package info.yalamanchili.office.client.profile.cllientinfo;

import info.chili.gwt.composite.ALComposite;
import info.chili.gwt.widgets.ClickableLink;
import info.yalamanchili.office.client.Auth;
import info.yalamanchili.office.client.TabPanel;
import info.chili.gwt.crud.CreateComposite.CreateCompositeType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.VerticalPanel;
import info.chili.gwt.callback.ALAsyncCallback;
import info.chili.gwt.rpc.HttpService;
import info.chili.gwt.utils.JSONUtils;
import info.yalamanchili.office.client.Auth.ROLE;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.profile.employee.TreeEmployeePanel;
import info.yalamanchili.office.client.project.offboarding.CreateProjectOffBoardingPanel;

public class ClientInfoOptionsPanel extends ALComposite implements ClickHandler {

    protected VerticalPanel panel = new VerticalPanel();
    protected ClickableLink addClientInfoLink = new ClickableLink("Add Client Information");
    protected ClickableLink viewBISInfoLink = new ClickableLink("View BIS Information");
    protected ClickableLink submitProjectEndDetails = new ClickableLink("Submit Project End Details");

    public ClientInfoOptionsPanel() {
        init(panel);
    }

    @Override
    protected void addListeners() {
        addClientInfoLink.addClickHandler(this);
        submitProjectEndDetails.addClickHandler(this);
        viewBISInfoLink.addClickHandler(this);
    }

    @Override
    protected void configure() {
        // TODO move this to common css
        panel.setSpacing(5);
    }

    @Override
    protected void addWidgets() {
        if (Auth.hasAnyOfRoles(ROLE.ROLE_ADMIN, ROLE.ROLE_RELATIONSHIP, ROLE.ROLE_TIME, ROLE.ROLE_RECRUITER, ROLE.ROLE_HR)) {
            panel.add(addClientInfoLink);
        }
        if (Auth.hasAnyOfRoles(Auth.ROLE.ROLE_BIS_VIEW)) {
            panel.add(viewBISInfoLink);
        }
        panel.add(submitProjectEndDetails);
    }

    @Override
    public void onClick(ClickEvent arg0) {
        if (arg0.getSource().equals(addClientInfoLink)) {
            TabPanel.instance().myOfficePanel.entityPanel.clear();
            TabPanel.instance().myOfficePanel.entityPanel.add(new CreateClientInfoPanel(CreateCompositeType.ADD));
        }
        if (arg0.getSource().equals(submitProjectEndDetails)) {
            TabPanel.instance().myOfficePanel.entityPanel.clear();
            TabPanel.instance().myOfficePanel.entityPanel.add(new CreateProjectOffBoardingPanel(CreateCompositeType.ADD));
        }
         if (arg0.getSource().equals(viewBISInfoLink)) {
            HttpService.HttpServiceAsync.instance().doGet(getBISInfoUrl(), OfficeWelcome.instance().getHeaders(), true,
                    new ALAsyncCallback<String>() {
                        @Override
                        public void onResponse(String arg0) {
                            TabPanel.instance().myOfficePanel.entityPanel.clear();
                            TabPanel.instance().myOfficePanel.entityPanel.add(new ReadAllBISClientInformationPanel(JSONUtils.toJSONArray(JSONParser.parseLenient(arg0).isObject().get("result"))));
                        }
                    });
        }
         
    }
    protected String getBISInfoUrl() {
        return OfficeWelcome.constants.root_url() + "clientinformation/bis-info/" + TreeEmployeePanel.instance().getEntityId();
    }
}
