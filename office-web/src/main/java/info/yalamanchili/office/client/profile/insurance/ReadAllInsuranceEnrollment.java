/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.profile.insurance;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import info.chili.gwt.callback.ALAsyncCallback;
import info.chili.gwt.crud.CRUDReadAllComposite;
import info.chili.gwt.crud.CreateComposite;
import info.chili.gwt.crud.TableRowOptionsWidget;
import info.chili.gwt.rpc.HttpService;
import info.chili.gwt.utils.JSONUtils;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.profile.phonetype.CreatePhoneTypePanel;
import java.util.logging.Logger;

/**
 *
 * @author prasanthi.p
 */
public class ReadAllInsuranceEnrollment extends CRUDReadAllComposite {

    private static Logger logger = Logger.getLogger(ReadAllInsuranceEnrollment.class.getName());
    public static ReadAllInsuranceEnrollment instance;

    public ReadAllInsuranceEnrollment() {
        instance = this;
        initTable("InsuranceEnrollment", OfficeWelcome.constants);
    }

    public ReadAllInsuranceEnrollment(JSONArray array) {
        instance = this;
        initTable("InsuranceEnrollment", array, OfficeWelcome.constants);
    }

    @Override
    public void preFetchTable(int start) {
        HttpService.HttpServiceAsync.instance().doGet(getReadAllPhoneTypeURL(start, OfficeWelcome.constants.tableSize()), OfficeWelcome.instance().getHeaders(), true,
                new ALAsyncCallback<String>() {
                    @Override
                    public void onResponse(String result) {
                        postFetchTable(result);
                    }
                });

    }

    public String getReadAllPhoneTypeURL(Integer start, String limit) {
        return OfficeWelcome.constants.root_url() + "insurance-enrollment/" + start.toString() + "/" + limit.toString();
    }

    @Override
    public void createTableHeader() {
        table.setText(0, 0, getKeyValue("Action"));
        table.setText(0, 1, getKeyValue("Year"));
        table.setText(0, 2, getKeyValue("InsuranceType"));
        table.setText(0, 3, getKeyValue("Enrolled"));

    }

    @Override
    public void fillData(JSONArray entities) {
        for (int i = 1; i <= entities.size(); i++) {
            JSONObject entity = (JSONObject) entities.get(i - 1);
            addOptionsWidget(i, entity);
            logger.info("insurance entity"+entity);
            table.setText(i, 1, JSONUtils.toString(entity, "year"));
            table.setText(i, 2, JSONUtils.toString(entity, "type"));
            table.setText(i, 3, JSONUtils.toString(entity, "enrolled"));
        }

    }

    @Override
    protected void addOptionsWidget(int row, JSONObject entity) {
        createOptionsWidget(TableRowOptionsWidget.OptionsType.READ_UPDATE_DELETE, row, JSONUtils.toString(entity, "id"));
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

    protected String getDeleteURL(String entityId) {
        return OfficeWelcome.instance().constants.root_url() + "insurance-enrollment/delete/" + entityId;
    }

    @Override
    public void postDeleteSuccess() {
        new ResponseStatusWidget().show("Successfully Deleted Insurance Enrollment Information");
        TabPanel.instance().profilePanel.entityPanel.clear();
        TabPanel.instance().profilePanel.entityPanel.add(new ReadAllInsuranceEnrollment());
    }

    @Override
    public void updateClicked(String entityId) {
        TabPanel.instance().profilePanel.entityPanel.clear();
//        TabPanel.instance().profilePanel.entityPanel.add(new UpdatePhoneTypePanel(getEntity(entityId)));
    }

    @Override
    protected void createButtonClicked() {
        TabPanel.instance().profilePanel.entityPanel.clear();
        TabPanel.instance().profilePanel.entityPanel.add(new CreatePhoneTypePanel(CreateComposite.CreateCompositeType.CREATE));
    }
}
