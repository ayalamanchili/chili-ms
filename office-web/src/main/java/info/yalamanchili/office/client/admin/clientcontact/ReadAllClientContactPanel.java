/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.admin.clientcontact;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import info.chili.gwt.callback.ALAsyncCallback;
import info.chili.gwt.utils.JSONUtils;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.Auth;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.gwt.ReadAllComposite;
import info.yalamanchili.office.client.gwt.TableRowOptionsWidget;
import info.yalamanchili.office.client.rpc.HttpService;
import info.yalamanchili.office.client.admin.client.TreeClientPanel;
import java.util.logging.Logger;

/**
 *
 * @author raghu
 */
public class ReadAllClientContactPanel extends ReadAllComposite {

    private static Logger logger = Logger.getLogger(ReadAllClientContactPanel.class.getName());
    public static ReadAllClientContactPanel instance;

    public ReadAllClientContactPanel(String parentId) {
        instance = this;
        this.parentId = parentId;
        initTable("ClientContact", OfficeWelcome.constants);
    }

    private ReadAllClientContactPanel() {
        instance = this;
        initTable("Clients", OfficeWelcome.constants);
    }

    @Override
    public void preFetchTable(int start) {
        HttpService.HttpServiceAsync.instance().doGet(getClientContactURL(start, OfficeWelcome.constants.tableSize()),
                OfficeWelcome.instance().getHeaders(), true, new ALAsyncCallback<String>() {
            @Override
            public void onResponse(String result) {
                logger.info(result);
                postFetchTable(result);
            }
        });
    }

    public String getClientContactURL(Integer start, String limit) {
        if (parentId != null) {
            return OfficeWelcome.constants.root_url() + "client/clientcontact/" + parentId + "/" + start.toString() + "/" + limit.toString();
        } else {
            return OfficeWelcome.constants.root_url() + "clientcontact/" + start.toString() + "/" + limit.toString();
        }
    }

    @Override
    public void createTableHeader() {
        table.setText(0, 0, getKeyValue("Table_Action"));
        table.setText(0, 1, getKeyValue("First Name"));
        table.setText(0, 2, getKeyValue("Middle Initial"));
        table.setText(0, 3, getKeyValue("Last Name"));
        table.setText(0, 4, getKeyValue("Email"));
        table.setText(0, 5, getKeyValue("Phone"));
        table.setText(0, 6, getKeyValue("Sex"));

    }

    @Override
    public void fillData(JSONArray entities) {
        for (int i = 1; i <= entities.size(); i++) {
            JSONObject entity = (JSONObject) entities.get(i - 1);
            addOptionsWidget(i, entity);
            table.setText(i, 1, JSONUtils.toString(entity, "firstName"));
            table.setText(i, 2, JSONUtils.toString(entity, "middleInitial"));
            table.setText(i, 3, JSONUtils.toString(entity, "lastName"));
            table.setText(i, 4, JSONUtils.toString(entity, "email"));
            table.setText(i, 5, JSONUtils.toString(entity, "phoneNumber"));
            table.setText(i, 6, JSONUtils.toString(entity, "sex"));
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

    @Override
    public void viewClicked(String entityId) {
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

    private String getDeleteURL(String entityId) {
         return OfficeWelcome.instance().constants.root_url() + "employee/delete/" + entityId;
    }

    @Override
    public void postDeleteSuccess() {
        new ResponseStatusWidget().show("Successfully deleted Client contact information");
        TabPanel.instance().timeandExpensePanel.entityPanel.clear();
        TabPanel.instance().timeandExpensePanel.entityPanel.add(new ReadAllClientContactPanel());
    }

    @Override
    public void updateClicked(String entityId) {
//        TabPanel.instance().timeandExpensePanel.sidePanelTop.clear();
//        TabPanel.instance().timeandExpensePanel.sidePanelTop.add(new TreeClientPanel(entityId));
        TabPanel.instance().timeandExpensePanel.entityPanel.clear();
        TabPanel.instance().timeandExpensePanel.entityPanel.add(new UpdateClientContactPanel(getEntity(entityId)));
    }
}
