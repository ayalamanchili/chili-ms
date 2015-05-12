/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.expense;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import info.chili.gwt.crud.CreateComposite;
import info.chili.gwt.fields.DataType;
import info.chili.gwt.rpc.HttpService;
import info.chili.gwt.utils.Alignment;
import info.chili.gwt.widgets.ClickableLink;
import info.chili.gwt.widgets.GenericPopup;
import info.chili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.expenseitem.CreateExpenseItemPanel;
import info.yalamanchili.office.client.profile.phone.PhoneOptionsPanel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author benerji.v
 */
public class CreateImmigrationCheckRequisitionPanel extends CreateComposite implements ClickHandler{
    
    private static Logger logger = Logger.getLogger(CreateImmigrationCheckRequisitionPanel.class.getName());
    protected ClickableLink addItemL = new ClickableLink("Add Expense Item");
    protected List<CreateImmigrationCheckRequisitionPanel> expenseItemPanels = new ArrayList<CreateImmigrationCheckRequisitionPanel>();
    RadioButton passCheckInfo = new RadioButton("payment", "Add Check Information");
    RadioButton passBankAcctInfo = new RadioButton("payment", "Add Bank Account Information");
    RadioButton useCurrentPayrollInfo = new RadioButton("payment", "Use Current Payroll Information");
    HTML tac = new HTML("<h6> I " + OfficeWelcome.instance().getCurrentUserName() + " hereby certify that I am solely responsible \n"
            + "for repayment of the above Immigration requistion amount, to System Soft Technologies,as \n"
            + "per agreed terms & conditions or on-demand. </h6>");
    
    public CreateImmigrationCheckRequisitionPanel() {
        super(CreateCompositeType.CREATE);
        initCreateComposite("ImmigrationCheckRequisition", OfficeWelcome.constants);
    }

    @Override
    protected JSONObject populateEntityFromFields() {
        JSONObject entity = new JSONObject();
        assignEntityValueFromField("requestedDate", entity);
        assignEntityValueFromField("neededByDate", entity);
        assignEntityValueFromField("amount", entity);
        assignEntityValueFromField("mailingAddress", entity);
        assignEntityValueFromField("caseType", entity);
        assignEntityValueFromField("purpose", entity);
        entity.put("company", new JSONObject());
        entity.put("employee", new JSONObject());
        return entity;
        
    }

    @Override
    protected void createButtonClicked() {
        HttpService.HttpServiceAsync.instance().doPut(getURI(), entity.toString(), OfficeWelcome.instance().getHeaders(), true,
                new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable arg0) {
                        logger.info(arg0.getMessage());
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
    public void onClick(ClickEvent event) {
        if (event.getSource().equals(addItemL)) {
            CreateImmigrationCheckRequisitionPanel panel = new CreateImmigrationCheckRequisitionPanel();
            expenseItemPanels.add(panel);
            entityFieldsPanel.add(panel);
        }
        super.onClick(event);
    }

    @Override
    protected void postCreateSuccess(String result) {
        new ResponseStatusWidget().show("Request Submited, please wait for email notification within 48 hours for Email confirmation");
        TabPanel.instance().expensePanel.entityPanel.clear();
        TabPanel.instance().expensePanel.entityPanel.add(new ReadAllImmigrationCheckRequisitionPanel());
        GenericPopup.instance().hide();
    }

    @Override
    protected void addListeners() {
        passBankAcctInfo.addClickHandler(this);
        passCheckInfo.addClickHandler(this);
        useCurrentPayrollInfo.addClickHandler(this);
    }

    @Override
    protected void configure() {
        setButtonText("Submit");
    }

    @Override
    protected void addWidgets() {
        entityFieldsPanel.add(getLineSeperatorTag("ImmigrationCheck Requisition Information"));
        addField("requestedDate", false, true, DataType.TEXT_AREA_FIELD, Alignment.HORIZONTAL);
        addField("neededByDate", false, true, DataType.CURRENCY_FIELD, Alignment.HORIZONTAL);
        addField("amount", false, true, DataType.DATE_FIELD, Alignment.HORIZONTAL);
        addField("mailingAddress", false, true, DataType.DATE_FIELD, Alignment.HORIZONTAL);
        addField("caseType", false, true, DataType.DATE_FIELD, Alignment.HORIZONTAL);
        addField("purpose", false, true, DataType.DATE_FIELD, Alignment.HORIZONTAL);
        entityFieldsPanel.add(passBankAcctInfo);
        entityFieldsPanel.add(passCheckInfo);
        entityFieldsPanel.add(useCurrentPayrollInfo);
        entityFieldsPanel.add(tac);
        entityFieldsPanel.add(addItemL);
        alignFields();
    }

    @Override
    protected void addWidgetsBeforeCaptionPanel() {
        
    }

    @Override
    protected String getURI() {
        return OfficeWelcome.constants.root_url() + "ImmigrationCheckRequisition/submit-ImmigrationCheck-requisition-request";
    }
}
