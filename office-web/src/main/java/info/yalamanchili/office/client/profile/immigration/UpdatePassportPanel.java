/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.profile.immigration;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import info.chili.gwt.crud.UpdateComposite;
import info.chili.gwt.data.CountryFactory;
import info.chili.gwt.fields.DataType;
import info.chili.gwt.rpc.HttpService;
import info.chili.gwt.utils.Alignment;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.profile.employee.TreeEmployeePanel;
import java.util.logging.Logger;

/**
 *
 * @author Madhu.Badiginchala
 */
public class UpdatePassportPanel extends UpdateComposite {
    
    private static Logger logger = Logger.getLogger(UpdatePassportPanel.class.getName());

    public UpdatePassportPanel(JSONObject entity) {
        
        initUpdateComposite(entity, "Passport", OfficeWelcome.constants2);
    }

    @Override
    protected JSONObject populateEntityFromFields() {
        assignEntityValueFromField("passportNumber", entity);
        assignEntityValueFromField("doYouHoldAValidPassport", entity);
        assignEntityValueFromField("passportIssuedDate", entity);
        assignEntityValueFromField("passportExpiryDate", entity);
        assignEntityValueFromField("passportExpirationAlertIndicator", entity);
        assignEntityValueFromField("passportCountryOfIssuance", entity);
        assignEntityValueFromField("dateOfBirth", entity);
        assignEntityValueFromField("stateOfBirth", entity);
        assignEntityValueFromField("placeOfBirth", entity);
        assignEntityValueFromField("countryOfBirth", entity);
        assignEntityValueFromField("nationalityIs", entity);
        assignEntityValueFromField("countryOfNationality", entity);
        assignEntityValueFromField("passportStateOfIssuance", entity);
        assignEntityValueFromField("identificationMarks", entity);
        assignEntityValueFromField("haveYouEverLostPassport", entity);
        assignEntityValueFromField("travelDocumentNumber", entity);
        assignEntityValueFromField("commentS", entity);
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
        assignFieldValueFromEntity("passportNumber", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("doYouHoldAValidPassport", entity, DataType.BOOLEAN_FIELD);
        assignFieldValueFromEntity("passportIssuedDate", entity, DataType.DATE_FIELD);
        assignFieldValueFromEntity("passportExpiryDate", entity, DataType.DATE_FIELD);
        assignFieldValueFromEntity("passportExpirationAlertIndicator", entity, DataType.BOOLEAN_FIELD);
        assignFieldValueFromEntity("passportCountryOfIssuance", entity, DataType.ENUM_FIELD);
        assignFieldValueFromEntity("dateOfBirth", entity, DataType.DATE_FIELD);
        assignFieldValueFromEntity("stateOfBirth", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("placeOfBirth", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("countryOfBirth", entity, DataType.ENUM_FIELD);
        assignFieldValueFromEntity("nationalityIs", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("countryOfNationality", entity, DataType.ENUM_FIELD);
        assignFieldValueFromEntity("passportStateOfIssuance", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("identificationMarks", entity, DataType.TEXT_AREA_FIELD);
        assignFieldValueFromEntity("haveYouEverLostPassport", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("travelDocumentNumber", entity, DataType.STRING_FIELD);
        assignFieldValueFromEntity("commentS", entity, DataType.TEXT_AREA_FIELD);
    }

    @Override
    protected void postUpdateSuccess(String result) {
        new ResponseStatusWidget().show("Successfully Updated Passport");
        TabPanel.instance().myOfficePanel.entityPanel.clear();
        TabPanel.instance().myOfficePanel.entityPanel.add(new ReadAllPassportsPanel(TreeEmployeePanel.instance().getEntityId()));
    }

    @Override
    protected void addListeners() {
    }

    @Override
    protected void configure() {
    }

    @Override
    protected void addWidgets() {
        addField("passportNumber", false, true, DataType.STRING_FIELD, Alignment.HORIZONTAL);
        addField("doYouHoldAValidPassport", false, true, DataType.BOOLEAN_FIELD, Alignment.HORIZONTAL);
        addField("passportIssuedDate", false, true, DataType.DATE_FIELD, Alignment.HORIZONTAL);
        addField("passportExpiryDate", false, true, DataType.DATE_FIELD, Alignment.HORIZONTAL);
        addField("passportExpirationAlertIndicator", false, false, DataType.BOOLEAN_FIELD, Alignment.HORIZONTAL);
        addEnumField("passportCountryOfIssuance", false, true, CountryFactory.getCountries().toArray(new String[0]), Alignment.HORIZONTAL);
        addField("dateOfBirth", false, true, DataType.DATE_FIELD, Alignment.HORIZONTAL);
        addField("stateOfBirth", false, false, DataType.STRING_FIELD, Alignment.HORIZONTAL);
        addField("placeOfBirth", false, false, DataType.STRING_FIELD, Alignment.HORIZONTAL);
        addEnumField("countryOfBirth", false, true, CountryFactory.getCountries().toArray(new String[0]), Alignment.HORIZONTAL);
        addField("nationalityIs", false, false, DataType.STRING_FIELD, Alignment.HORIZONTAL);
        addEnumField("countryOfNationality", false, false, CountryFactory.getCountries().toArray(new String[0]), Alignment.HORIZONTAL);
        addField("passportStateOfIssuance", false, false, DataType.STRING_FIELD, Alignment.HORIZONTAL);
        addField("identificationMarks", false, false, DataType.TEXT_AREA_FIELD, Alignment.HORIZONTAL);
        addField("haveYouEverLostPassport", false, false, DataType.STRING_FIELD, Alignment.HORIZONTAL);
        addField("travelDocumentNumber", false, false, DataType.STRING_FIELD, Alignment.HORIZONTAL);
        addField("commentS", false, false, DataType.TEXT_AREA_FIELD, Alignment.HORIZONTAL);
        alignFields();
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
    }

    @Override
    protected String getURI() {
        return OfficeWelcome.constants.root_url() + "passport/save/" + TreeEmployeePanel.instance().getEntityId();
    }

}
