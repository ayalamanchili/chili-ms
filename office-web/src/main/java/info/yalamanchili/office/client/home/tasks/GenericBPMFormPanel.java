/**
 * System Soft Technolgies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.home.tasks;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import info.chili.gwt.composite.BaseField;
import info.chili.gwt.fields.DataType;
import info.chili.gwt.fields.EnumField;
import info.chili.gwt.fields.StringField;
import info.chili.gwt.utils.JSONUtils;
import info.yalamanchili.office.client.OfficeWelcome;
import info.chili.gwt.crud.CreateComposite;
import info.chili.gwt.fields.BooleanField;
import info.chili.gwt.rpc.HttpService;
import java.util.logging.Logger;

/**
 *
 * @author ayalamanchili
 */
public abstract class GenericBPMFormPanel extends CreateComposite {

    private static Logger logger = Logger.getLogger(GenericBPMTaskFormPanel.class.getName());
    protected JSONArray formProperties;

    public GenericBPMFormPanel() {
        super(CreateComposite.CreateCompositeType.CREATE);
    }

    @Override
    protected JSONObject populateEntityFromFields() {
        JSONObject entity = new JSONObject();
        JSONArray vars = new JSONArray();
        int i = 0;
        for (String key : fields.keySet()) {
            JSONObject value = new JSONObject();
            value.put("id", new JSONString(key));
            //TODO currently support string and enum fields
            if (fields.get(key) instanceof StringField) {
                StringField stringField = (StringField) fields.get(key);
                value.put("value", new JSONString(stringField.getValue()));
            }
            if (fields.get(key) instanceof BooleanField) {
                BooleanField booleanField = (BooleanField) fields.get(key);
                value.put("value", new JSONString(booleanField.getValue().toString()));
            }
            if (fields.get(key) instanceof EnumField) {
                EnumField enumField = (EnumField) fields.get(key);
                if (enumField.getValue() != null) {
                    value.put("value", new JSONString(enumField.getValue()));
                }
            }
            vars.set(i, value);
            i++;
        }
        entity.put("entries", vars);
        return entity;
    }

    @Override
    protected void createButtonClicked() {
        HttpService.HttpServiceAsync.instance().doPut(getURI(), entity.toString(), OfficeWelcome.instance().getHeaders(), true,
                new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable arg0) {
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
    protected abstract void postCreateSuccess(String result);

    @Override
    protected void addListeners() {
    }

    @Override
    protected void configure() {
        setButtonText("Complete");
    }

    @Override
    protected void addWidgets() {
        if (formProperties == null) {
            return;
        }
        for (int i = 0; i < formProperties.size(); i++) {
            JSONObject formProperty = formProperties.get(i).isObject();
            boolean isRequired;
            if (JSONUtils.toString(formProperty, "required").equals("true")) {
                isRequired = true;
            } else {
                isRequired = false;
            }
            if (JSONUtils.toString(formProperty.get("type").isObject(), "name").equals("string")) {
                addField(JSONUtils.toString(formProperty, "id"), false, isRequired, DataType.STRING_FIELD);
            }
            if (JSONUtils.toString(formProperty.get("type").isObject(), "name").equals("boolean")) {
                addField(JSONUtils.toString(formProperty, "id"), false, isRequired, DataType.BOOLEAN_FIELD);
            }
            if (JSONUtils.toString(formProperty.get("type").isObject(), "name").equals("long")) {
                addField(JSONUtils.toString(formProperty, "id"), false, isRequired, DataType.INTEGER_FIELD);
            }
            if (JSONUtils.toString(formProperty.get("type").isObject(), "name").equals("date")) {
                addField(JSONUtils.toString(formProperty, "id"), false, isRequired, DataType.DATE_FIELD);
            }
            if (JSONUtils.toString(formProperty.get("type").isObject(), "name").equals("enum")) {
                JSONObject type = formProperty.get("type").isObject();
                JSONArray enumArray = JSONUtils.toJSONArray(type.get("values"));
                String[] enumVals = new String[enumArray.size()];
                for (int y = 0; y < enumArray.size(); y++) {
                    JSONObject enm = enumArray.get(y).isObject();
                    enumVals[y] = JSONUtils.toString(enm, "id");
                }
                addEnumField(JSONUtils.toString(formProperty, "id"), false, isRequired, enumVals);
            }
        }
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
        //TODO add task name and description;
    }

    @Override
    protected boolean processClientSideValidations(JSONObject entity) {
        boolean valid = true;
        for (int i = 0; i < formProperties.size(); i++) {
            JSONObject formProperty = formProperties.get(i).isObject();
            if (JSONUtils.toString(formProperty, "required").trim().length() > 0 && JSONUtils.toString(formProperty, "required").equals("true")) {
                BaseField field = fields.get(JSONUtils.toString(formProperty, "id"));
                if (field instanceof StringField) {
                    StringField stringField = (StringField) field;
                    if (stringField.getValue() == null || stringField.getValue().isEmpty()) {
                        stringField.setMessage("value is required");
                        valid = false;
                    }
                }
                if (field instanceof EnumField) {
                    EnumField enumField = (EnumField) field;
                    if (enumField.getValue() == null || enumField.getValue().trim().equalsIgnoreCase("SELECT")) {
                        enumField.setMessage("value is required");
                        valid = false;
                    }
                }
            }
        }
        return valid;
    }

    protected abstract String getURI();
}
