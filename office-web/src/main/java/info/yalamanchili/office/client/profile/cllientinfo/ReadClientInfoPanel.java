/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.profile.cllientinfo;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import info.chili.gwt.callback.ALAsyncCallback;
import info.chili.gwt.fields.DataType;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.gwt.ReadComposite;
import info.yalamanchili.office.client.rpc.HttpService;
import info.yalamanchili.office.client.admin.client.SelectClientWidget;
import info.yalamanchili.office.client.admin.clientcontact.SelectClientContactWidget;
import info.yalamanchili.office.client.admin.clientlocation.SelectClientLocationWidget;
import info.yalamanchili.office.client.admin.vendor.SelectVendorWidget;
import info.yalamanchili.office.client.admin.vendorcontact.SelectVendorContactWidget;
import info.yalamanchili.office.client.admin.vendorlocation.SelectVendorLocationsWidget;
import java.util.logging.Logger;

/**
 *
 * @author Prashanthi
 */
public class ReadClientInfoPanel extends ReadComposite {

    private static ReadClientInfoPanel instance;
    private static Logger logger = Logger.getLogger(ReadClientInfoPanel.class.getName());

    public ReadClientInfoPanel(JSONObject entity) {
        instance = this;
        initReadComposite(entity, "ClientInfo", OfficeWelcome.constants);
    }

    @Override
    public void populateFieldsFromEntity(JSONObject entity) {
        assignFieldValueFromEntity("consultantJobTitle", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("client", entity, null);
        assignFieldValueFromEntity("clientContact", entity, null);
        assignFieldValueFromEntity("clientLocation", entity, null);
        assignFieldValueFromEntity("vendor", entity, null);
        assignFieldValueFromEntity("vendorContact", entity, null);
        assignFieldValueFromEntity("vendorLocation", entity, null);
        assignFieldValueFromEntity("ciPrimary", entity, DataType.BOOLEAN_FIELD);
        assignFieldValueFromEntity("startDate", entity, DataType.DATE_FIELD);
        assignFieldValueFromEntity("endDate", entity, DataType.DATE_FIELD);

    }

    @Override
    protected void addListeners() {
    }

    @Override
    protected void configure() {
    }

    @Override
    protected void addWidgets() {
        addField("consultantJobTitle", false, false, DataType.STRING_FIELD);
        addDropDown("client", new SelectClientWidget(false, false));
        addDropDown("clientContact", new SelectClientContactWidget(false, false));
        addDropDown("clientLocation", new SelectClientLocationWidget(false, false));
        addDropDown("vendor", new SelectVendorWidget(false, false));
        addDropDown("vendorContact", new SelectVendorContactWidget(false, false));
        addDropDown("vendorLocation", new SelectVendorLocationsWidget(false, false));
        addField("ciPrimary", false, false, DataType.BOOLEAN_FIELD);
        addField("startDate", false, false, DataType.DATE_FIELD);
        addField("endDate", false, false, DataType.DATE_FIELD);
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
    }

    @Override
    protected String getURI() {
        return OfficeWelcome.constants.root_url() + "clientinformation/" + entityId;
    }

    @Override
    public void loadEntity(String entityId) {
        HttpService.HttpServiceAsync.instance().doGet(getURI(), OfficeWelcome.instance().getHeaders(), true,
                new ALAsyncCallback<String>() {
                    @Override
                    public void onResponse(String response) {
                        logger.info("read ec6 response" + response);
                        entity = (JSONObject) JSONParser.parseLenient(response);
                        populateFieldsFromEntity(entity);
                    }
                });
    }
}
