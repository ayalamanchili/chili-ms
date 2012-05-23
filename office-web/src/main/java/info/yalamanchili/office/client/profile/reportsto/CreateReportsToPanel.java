package info.yalamanchili.office.client.profile.reportsto;

import info.yalamanchili.gwt.fields.DataType;
import info.yalamanchili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.gwt.CreateComposite;
import info.yalamanchili.office.client.profile.employee.TreeEmployeePanel;
import info.yalamanchili.office.client.rpc.HttpService.HttpServiceAsync;

import java.util.logging.Logger;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CreateReportsToPanel extends CreateComposite {
	private static Logger logger = Logger.getLogger(CreateReportsToPanel.class.getName());

	public CreateReportsToPanel(CreateCompositeType type) {
		super(type);
		initCreateComposite("ReportsTo", OfficeWelcome.constants);
	}

	@Override
	protected JSONObject populateEntityFromFields() {
		JSONObject contact = new JSONObject();
		assignEntityValueFromField("firstName", contact);
		assignEntityValueFromField("middleInitial", contact);
		assignEntityValueFromField("lastName", contact);
		assignEntityValueFromField("sex", contact);

		JSONObject reportsTo = new JSONObject();
		assignEntityValueFromField("reportsToRole", reportsTo);
		reportsTo.put("contact", contact);
		return reportsTo;
	}

	@Override
	protected void createButtonClicked() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addButtonClicked() {
		HttpServiceAsync.instance().doPut(getURI(), entity.toString(), OfficeWelcome.instance().getHeaders(), false,
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable arg0) {
						logger.info(arg0.getMessage());
						handleErrorResponse(arg0);
					}

					@Override
					public void onSuccess(String arg0) {
						new ResponseStatusWidget().show("successfully added Reports To");
						TabPanel.instance().myOfficePanel.clear();
						TabPanel.instance().myOfficePanel.entityPanel.add(new ReadAllReportsToPanel(TreeEmployeePanel
								.instance().getEntityId()));

					}

				});

	}

	@Override
	protected void addListeners() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void configure() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addWidgets() {
		addField("reportsToRole", false, true, DataType.STRING_FIELD);
		addField("firstName", false, true, DataType.STRING_FIELD);
		addField("middleInitial", false, true, DataType.STRING_FIELD);
		addField("lastName", false, true, DataType.STRING_FIELD);
		String[] strs = { "MALE", "FEMALE" };
		addEnumField("sex", false, true, strs);
	}

	@Override
	protected void addWidgetsBeforeCaptionPanel() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getURI() {
		return OfficeWelcome.constants.root_url() + "employee/reportsto/" + TreeEmployeePanel.instance().getEntityId();
	}
}
