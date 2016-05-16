/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.admin.subcntrlocation;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import info.chili.gwt.crud.UpdateComposite;
import info.chili.gwt.data.CanadaStatesFactory;
import info.chili.gwt.data.CountryFactory;
import info.chili.gwt.data.IndiaStatesFactory;
import info.chili.gwt.data.USAStatesFactory;
import info.chili.gwt.fields.DataType;
import info.chili.gwt.fields.EnumField;
import info.chili.gwt.fields.StringField;
import info.chili.gwt.rpc.HttpService;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.admin.subcontractor.TreeSubcontractorPanel;
import info.yalamanchili.office.client.profile.address.AddressByZipService;
import java.util.logging.Logger;

/**
 *
 * @author anuyalamanchili
 */
public class UpdateSubcontractorLocationPanel extends UpdateComposite implements ChangeHandler {
    
    private static Logger logger = Logger.getLogger(UpdateSubcontractorLocationPanel.class.getName());

    public UpdateSubcontractorLocationPanel(JSONObject entity) {
        initUpdateComposite(entity, "SubcontractorLocation", OfficeWelcome.constants);
    }
    EnumField statesF;
    EnumField countriesF;
    StringField zipField;
    StringField cityField;        

    @Override
    protected JSONObject populateEntityFromFields() {
        assignEntityValueFromField("street1", entity);
        assignEntityValueFromField("street2", entity);
        assignEntityValueFromField("zip", entity);        
        assignEntityValueFromField("city", entity);
        assignEntityValueFromField("state", entity);
        assignEntityValueFromField("country", entity);

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
        assignFieldValueFromEntity("street1", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("street2", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("zip", entity, DataType.STRING_FIELD);        
        assignFieldValueFromEntity("city", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("state", entity, DataType.ENUM_FIELD);
        assignFieldValueFromEntity("country", entity, DataType.ENUM_FIELD);
    }

    @Override
    protected void postUpdateSuccess(String result) {
        new ResponseStatusWidget().show("Successfully Updated Subcontractor location");
        TabPanel.instance().adminPanel.entityPanel.clear();
        TabPanel.instance().adminPanel.entityPanel.add(new ReadAllSubcontractorLocationsPanel(TreeSubcontractorPanel.instance().getEntityId()));
    }

    @Override
    protected void addListeners() {
        if (countriesF != null) {
            countriesF.listBox.addChangeHandler(this);
        }
         zipField.getTextbox().addChangeHandler(this);        
    }

    @Override
    protected void configure() {
    }

    @Override
    protected void addWidgets() {
        addField("street1", false, true, DataType.STRING_FIELD);
        addField("street2", false, false, DataType.STRING_FIELD);
        addField("zip", false, false, DataType.STRING_FIELD);        
        addField("city", false, true, DataType.STRING_FIELD);
        JSONValue service = entity.get("country");
        addEnumField("country", false, true, CountryFactory.getCountries().toArray(new String[0]));
        switch (service.isString().stringValue()) {
            case "USA":
                addEnumField("state", false, true, USAStatesFactory.getStates().toArray(new String[0]));
                break;
            case "INDIA":
                addEnumField("state", false, true, IndiaStatesFactory.getStates().toArray(new String[0]));
                break;
            case "CANADA":
                addEnumField("state", false, true, CanadaStatesFactory.getStates().toArray(new String[0]));
                break;
        }
        addField("state", false, true, DataType.ENUM_FIELD);
        countriesF = (EnumField) fields.get("country");
        statesF = (EnumField) fields.get("state");
        zipField = (StringField) fields.get("zip");
        cityField = (StringField) fields.get("city");         
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
    }

    @Override
    protected String getURI() {
        return OfficeWelcome.constants.root_url() + "address";
    }

    @Override
    public void onChange(ChangeEvent event) {
        switch (countriesF.getValue()) {
            case "USA":
                statesF.setValues(USAStatesFactory.getStates().toArray(new String[0]));
                break;
            case "INDIA":
                statesF.setValues(IndiaStatesFactory.getStates().toArray(new String[0]));
                break;
            case "CANADA":
                statesF.setValues(CanadaStatesFactory.getStates().toArray(new String[0]));
                break;
        }
        
        if (event.getSource().equals(zipField.getTextbox())) {
            AddressByZipService.getZipInformationService(zipField.getValue(), cityField, statesF, countriesF);
        }    
    }
 
}
