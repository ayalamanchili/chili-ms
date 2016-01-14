/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.admin.vendor;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import info.chili.gwt.fields.DataType;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.chili.gwt.crud.UpdateComposite;
import info.chili.gwt.fields.BooleanField;
import info.chili.gwt.rpc.HttpService;
import info.chili.gwt.utils.Alignment;
import info.yalamanchili.office.client.profile.cllientinfo.InvoiceFrequency;
import java.util.logging.Logger;

/**
 *
 * @author Prashanthi
 */
public class UpdateVendorsPanel extends UpdateComposite {

    private static Logger logger = Logger.getLogger(UpdateVendorsPanel.class.getName());
    protected BooleanField submitForUpdateP = new BooleanField(OfficeWelcome.constants, "Select To Update CPD's Payment Terms", "Vendors", false, false, Alignment.HORIZONTAL);
    protected BooleanField submitForUpdateF = new BooleanField(OfficeWelcome.constants, "Select To Update CPD's Frequency", "Vendors", false, false, Alignment.HORIZONTAL);

    public UpdateVendorsPanel(JSONObject entity) {
        initUpdateComposite(entity, "Vendors", OfficeWelcome.constants);
    }

    @Override
    protected JSONObject populateEntityFromFields() {
        assignEntityValueFromField("name", entity);
        assignEntityValueFromField("description", entity);
        assignEntityValueFromField("vendorType", entity);
        assignEntityValueFromField("website", entity);
        assignEntityValueFromField("paymentTerms", entity);
        assignEntityValueFromField("vendorinvFrequency", entity);
        assignEntityValueFromField("vendorFees", entity);
        return entity;
    }

    @Override
    protected void updateButtonClicked() {
        HttpService.HttpServiceAsync.instance().doPut(getURI(), entity.toString(),
                OfficeWelcome.instance().getHeaders(), true, new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable arg0) {
                        handleErrorResponse(arg0);
                    }

                    @Override
                    public void onSuccess(String arg0) {
                        postUpdateSuccess(arg0);
                    }
                });
    }

    @Override
    public void populateFieldsFromEntity(JSONObject entity) {
        assignFieldValueFromEntity("name", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("description", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("vendorType", entity, DataType.ENUM_FIELD);
        assignFieldValueFromEntity("website", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("paymentTerms", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("vendorinvFrequency", entity, DataType.ENUM_FIELD);
        assignFieldValueFromEntity("vendorFees", entity, DataType.FLOAT_FIELD);
    }

    @Override
    protected void postUpdateSuccess(String result) {
        new ResponseStatusWidget().show("Successfully Updated Vendor Information");
        TabPanel.instance().adminPanel.sidePanelTop.clear();
        TabPanel.instance().adminPanel.sidePanelTop.add(new VendorsSidePanel());
        TabPanel.instance().adminPanel.entityPanel.clear();
        TabPanel.instance().adminPanel.entityPanel.add(new ReadAllVendorsPanel());
    }

    @Override
    protected void addListeners() {
        submitForUpdateF.getBox().addClickHandler(this);
        submitForUpdateP.getBox().addClickHandler(this);
    }

    @Override
    protected void configure() {
        submitForUpdateF.setValue(false);
        submitForUpdateP.setValue(false);
    }

    @Override
    protected void addWidgets() {
        addField("name", false, true, DataType.STRING_FIELD, Alignment.HORIZONTAL);
        addField("description", false, false, DataType.STRING_FIELD, Alignment.HORIZONTAL);
        addEnumField("vendorType", false, false, VendorType.names(), Alignment.HORIZONTAL);
        addField("vendorFees", false, false, DataType.FLOAT_FIELD, Alignment.HORIZONTAL);
        addField("website", false, false, DataType.STRING_FIELD, Alignment.HORIZONTAL);
        addField("paymentTerms", false, false, DataType.STRING_FIELD, Alignment.HORIZONTAL);
        addEnumField("vendorinvFrequency", false, false, InvoiceFrequency.names(), Alignment.HORIZONTAL);
        entityFieldsPanel.add(submitForUpdateP);
        entityFieldsPanel.add(submitForUpdateF);
        alignFields();
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
    }

    @Override
    protected String getURI() {
        if (submitForUpdateF.getValue() || submitForUpdateP.getValue()) {
            return OfficeWelcome.constants.root_url() + "vendor/updatecpd" + "?submitForUpdateF=" + submitForUpdateF.getValue() +  "&submitForUpdateP=" + submitForUpdateP.getValue();
        } else {
            return OfficeWelcome.constants.root_url() + "vendor";
        }
    }
}
