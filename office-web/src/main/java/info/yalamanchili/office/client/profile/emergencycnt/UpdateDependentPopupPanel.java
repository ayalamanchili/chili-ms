/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.profile.emergencycnt;

import com.google.gwt.json.client.JSONObject;
import info.chili.gwt.widgets.GenericPopup;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.profile.ProfileHome;

/**
 *
 * @author radhika.mukkala
 */
public class UpdateDependentPopupPanel extends UpdateDependentPanel {

    public UpdateDependentPopupPanel(JSONObject entity) {
        super(entity);
    }

    @Override
    protected void postUpdateSuccess(String result) {
        new ResponseStatusWidget().show("Successfully Updated Dependent Information");
        GenericPopup.instance().hide();
        ProfileHome.instance().refreshDependentsPanel();
    }
}
