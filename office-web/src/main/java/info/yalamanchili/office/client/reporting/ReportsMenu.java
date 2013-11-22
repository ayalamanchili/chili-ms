/**
 * System Soft Technolgies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.reporting;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import info.yalamanchili.office.client.Auth;
import info.yalamanchili.office.client.Auth.ROLE;
import info.yalamanchili.office.client.TabPanel;
import info.yalamanchili.office.client.contracts.ContractsSidePanel;
import info.yalamanchili.office.client.contracts.ReadAllContractsPanel;

/**
 *
 * @author prasanthi.p
 */
public class ReportsMenu extends Composite {

    MenuBar reportsMenuBar = new MenuBar(false);

    public ReportsMenu() {
        initWidget(reportsMenuBar);
        configureReportsMenu();
    }

    protected void configureReportsMenu() {
        if (Auth.hasAnyOfRoles(ROLE.ROLE_ADMIN, ROLE.ROLE_EXPENSE, ROLE.ROLE_TIME, ROLE.ROLE_ACCOUNT_VIEW)) {
            reportsMenuBar.addItem("Contracts", ContractingMaintainenceCmd);
        }
        reportsMenuBar.addStyleName("entityMenuBar");
    }

    Command ContractingMaintainenceCmd = new Command() {
        @Override
        public void execute() {
            TabPanel.instance().getReportingPanel().entityPanel.clear();
            TabPanel.instance().getReportingPanel().sidePanelTop.clear();
            TabPanel.instance().getReportingPanel().entityPanel.add(new ReadAllContractsPanel());
            TabPanel.instance().getReportingPanel().sidePanelTop.add(new ContractsSidePanel());
        }
    };
}
