/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.profile.immigration.travelhistroy;

import info.chili.gwt.crud.CreateComposite;
import info.chili.gwt.widgets.GenericPopup;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.profile.ProfileHome;

/**
 *
 * @author prasanthi.p
 */
public class CreateTraveHistoryPopUpPanel extends CreateTravelHistoryPanel {

    public CreateTraveHistoryPopUpPanel(CreateComposite.CreateCompositeType type) {
        super(type);
    }

    @Override
    protected String getURI() {
        return OfficeWelcome.constants.root_url() + "travelhistory/save/" + OfficeWelcome.instance().employeeId;
    }

    @Override
    protected void postCreateSuccess(String result) {
        new ResponseStatusWidget().show("Successfully Added Employee TravelHistroy");
        GenericPopup.instance().hide();
        ProfileHome.instance().refreshTravelHistroy();
    }
}
