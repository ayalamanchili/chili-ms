/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.tae.clientlocation;

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
import info.yalamanchili.office.client.tae.client.TreeClientPanel;
import java.util.logging.Logger;

/**
 *
 * @author raghu
 */
public class ReadAllClientLocationsPanel extends ReadAllComposite {

    private static Logger logger = Logger.getLogger(ReadAllClientLocationsPanel.class.getName());
    public static ReadAllClientLocationsPanel instance;

    public ReadAllClientLocationsPanel(String parentId) {
        instance = this;
        this.parentId = parentId;
        initTable("ClientLocation", OfficeWelcome.constants);
    }

    ReadAllClientLocationsPanel() {
        instance = this;
        initTable("ClientLocation", OfficeWelcome.constants);
    }

    @Override
    public void preFetchTable(int start) {
        HttpService.HttpServiceAsync.instance().doGet(getClientLocURL(start, OfficeWelcome.constants.tableSize()),
                OfficeWelcome.instance().getHeaders(), true, new ALAsyncCallback<String>() {
            @Override
            public void onResponse(String result) {
                logger.info(result);
                postFetchTable(result);
            }
        });
    }

    public String getClientLocURL(Integer start, String limit) {
        if (parentId != null) {
            return OfficeWelcome.constants.root_url() + "client/clientlocation/" + parentId + "/" + start.toString() + "/" + limit.toString();
        } else {
            return OfficeWelcome.constants.root_url() + "clientlocation/" + start.toString() + "/" + limit.toString();
        }
    }

    @Override
    public void createTableHeader() {
        table.setText(0, 0, getKeyValue("Table_Action"));
        table.setText(0, 1, getKeyValue("Street 1"));
        table.setText(0, 2, getKeyValue("Street 2"));
        table.setText(0, 3, getKeyValue("City"));
        table.setText(0, 4, getKeyValue("State"));
        table.setText(0, 5, getKeyValue("Country"));
        table.setText(0, 6, getKeyValue("Zip"));
    }

    @Override
    public void fillData(JSONArray entities) {
        for (int i = 1; i <= entities.size(); i++) {
            JSONObject entity = (JSONObject) entities.get(i - 1);
            addOptionsWidget(i, entity);
//            table.setText(i, 1, JSONUtils.toString(entity.get("addressType"), "addressType"));
            table.setText(i, 1, JSONUtils.toString(entity, "street1"));
            table.setText(i, 2, JSONUtils.toString(entity, "street2"));
            table.setText(i, 3, JSONUtils.toString(entity, "city"));
            table.setText(i, 4, JSONUtils.toString(entity, "state"));
            table.setText(i, 5, JSONUtils.toString(entity, "country"));
            table.setText(i, 6, JSONUtils.toString(entity, "zip"));
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
         return OfficeWelcome.instance().constants.root_url() + "address/delete/" + entityId;
    }

    @Override
    public void postDeleteSuccess() {
        new ResponseStatusWidget().show("Successfully deleted Client location Information");
        TabPanel.instance().timeandExpensePanel.entityPanel.clear();
        TabPanel.instance().timeandExpensePanel.entityPanel.add(new ReadAllClientLocationsPanel());
    }

    @Override
    public void updateClicked(String entityId) {
//        TabPanel.instance().timeandExpensePanel.sidePanelTop.clear();
//        TabPanel.instance().timeandExpensePanel.sidePanelTop.add(new TreeClientPanel(entityId));
        TabPanel.instance().timeandExpensePanel.entityPanel.clear();
        TabPanel.instance().timeandExpensePanel.entityPanel.add(new UpdateClientLocationPanel(getEntity(entityId)));
    }
}
