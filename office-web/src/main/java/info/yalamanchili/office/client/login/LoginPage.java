/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import info.yalamanchili.gwt.widgets.ResponseStatusWidget;
import info.yalamanchili.office.client.OfficeWelcome;
import info.yalamanchili.office.client.rpc.HttpService;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * @author yphanikumar
 */
public class LoginPage extends Composite {

    private static Logger logger = Logger.getLogger(LoginPage.class.getName());
    private static LoginPage.LoginPageUiBinder uiBinder = GWT.create(LoginPage.LoginPageUiBinder.class);
    private static LoginPage instance;

    public static LoginPage instance() {
        return instance;
    }
    @UiField
    Image loginLogo;
    @UiField
    Label usernameL;
    @UiField
    TextBox usernameTb;
    @UiField
    Label passwordL;
    @UiField
    PasswordTextBox passwordTb;
    @UiField
    Button loginB;

    @UiHandler("loginB")
    void handleLogin(ClickEvent e) {
        loginClicked();
    }

    interface LoginPageUiBinder extends UiBinder<Widget, LoginPage> {
    }

    public LoginPage() {
        initWidget(uiBinder.createAndBindUi(this));
        loginB.addStyleName("loginB");
    }

    protected void loginClicked() {
        JSONObject user = new JSONObject();
        user.put("username", new JSONString(usernameTb.getText()));
        user.put("passwordHash", new JSONString(passwordTb.getText()));
        Map<String, String> headers = OfficeWelcome.instance().getHeaders();
        headers.put("username", usernameTb.getText());
        headers.put("password", passwordTb.getText());
        HttpService.HttpServiceAsync.instance().doPut(getLoginURL(), user.toString(), headers, true, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable arg0) {
                new ResponseStatusWidget().show("login failed");
            }

            @Override
            public void onSuccess(String userString) {
                if (userString != null && userString.trim().length() > 0) {
                    OfficeWelcome.instance().username = usernameTb.getText();
                    OfficeWelcome.instance().password = passwordTb.getText();
                    JSONObject user = (JSONObject) JSONParser.parseLenient(userString);
                    OfficeWelcome.instance().onMainModuleLoad(user);
                } else {
                    new ResponseStatusWidget().show("login failed");
                }
            }
        });
    }

    protected void setAutoLogout() {
        // TODO
    }

    protected String getLoginURL() {
        return OfficeWelcome.constants.root_url() + "admin/login";
    }
}