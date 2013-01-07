/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.admin.client;

import com.google.gwt.json.client.JSONObject;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.gwt.TreePanelComposite;
import info.yalamanchili.office.client.admin.clientcontact.ClientContactOptionsPanel;
import info.yalamanchili.office.client.admin.clientcontact.ReadAllClientContactPanel;
import info.yalamanchili.office.client.admin.clientlocation.ClientLocationOptionsPanel;
import info.yalamanchili.office.client.admin.clientlocation.ReadAllClientLocationsPanel;
import info.yalamanchili.office.client.admin.project.ProjectOptionsPanel;
import info.yalamanchili.office.client.admin.project.ReadAllProjectsPanel;
import java.util.logging.Logger;

/**
 *
 * @author raghu
 */
public class TreeClientPanel extends TreePanelComposite {

    private static TreeClientPanel instance;

    public static TreeClientPanel instance() {
        return instance;
    }
    private static Logger logger = Logger.getLogger(TreeClientPanel.class.getName());
    protected static final String PROJECT_NODE = "project";
    protected static final String CLIENTLOCATION_NODE = "clientlocation";
    protected static final String CONTACTS_NODE = "contacts";

    public TreeClientPanel(String entityId) {
        super(entityId);
        instance = this;
        init("Client", OfficeWelcome.constants);
    }

    @Override
    protected void addListeners() {
    }

    @Override
    protected void configure() {
    }

    @Override
    protected void addWidgets() {
        addFirstChildLink("Project", PROJECT_NODE);
        addFirstChildLink("Locations", CLIENTLOCATION_NODE);
        addFirstChildLink("Contacts", CONTACTS_NODE);
    }

    @Override
    public void treeNodeSelected(String entityNodeKey) {
        if (PROJECT_NODE.equals(entityNodeKey)) {
            TabPanel.instance().timeandExpensePanel.entityPanel.clear();
            TabPanel.instance().timeandExpensePanel.entityPanel.add(new ReadAllProjectsPanel(entityId));
            TabPanel.instance().timeandExpensePanel.entityPanel.add(new ProjectOptionsPanel());
        }
        if (CLIENTLOCATION_NODE.equals(entityNodeKey)) {
            TabPanel.instance().timeandExpensePanel.entityPanel.clear();
            TabPanel.instance().timeandExpensePanel.entityPanel.add(new ReadAllClientLocationsPanel(entityId));
            TabPanel.instance().timeandExpensePanel.entityPanel.add(new ClientLocationOptionsPanel());
        }
        if (CONTACTS_NODE.equals(entityNodeKey)) {
            TabPanel.instance().timeandExpensePanel.entityPanel.clear();
            TabPanel.instance().timeandExpensePanel.entityPanel.add(new ReadAllClientContactPanel(entityId));
            TabPanel.instance().timeandExpensePanel.entityPanel.add(new ClientContactOptionsPanel());
        }
    }

    @Override
    public void loadEntity() {
    }

    @Override
    public void showEntity() {
    }
}
