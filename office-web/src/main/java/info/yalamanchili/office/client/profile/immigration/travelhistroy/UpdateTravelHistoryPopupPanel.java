/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.profile.immigration.travelhistroy;

import com.google.gwt.json.client.JSONObject;
import info.chili.gwt.widgets.GenericPopup;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.profile.ProfileHome;

/**
 *
 * @author prasanthi.p
 */
public class UpdateTravelHistoryPopupPanel extends UpdateTravelHistoryPanel {

    public UpdateTravelHistoryPopupPanel(JSONObject entity) {
        super(entity);
    }

    @Override
    protected String getURI() {
        return OfficeWelcome.constants.root_url() + "travelhistroy/save/" + OfficeWelcome.instance().employeeId;
    }

    @Override
    protected void postUpdateSuccess(String result) {
        new ResponseStatusWidget().show("Successfully Updated Travel History Information");
        GenericPopup.instance().hide();
        ProfileHome.instance().refreshTravelHistroy();
    }
}
