/**test
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ayalamanchili
 */
public class OfficeRoles {

    public enum OfficeRole {

        ROLE_USER,
        ROLE_CORPORATE_EMPLOYEE,
        ROLE_ADMIN,
        ROLE_HR,
        ROLE_RECRUITER,
        ROLE_TIME, // Contracts?
        ROLE_EXPENSE,// Accounts?
        ROLE_DRIVE,
        ROLE_RELATIONSHIP,
        ROLE_ACCOUNT_VIEW,
        //NEW _ROLES
        ROLE_HR_ADMINSTRATION,
        ROLE_H1B_IMMIGRATION,
        ROLE_GC_IMMIGRATION,
        ROLE_PAYROLL_AND_BENIFITS,
        ROLE_ACCOUNTS_RECEIVABLE,
        ROLE_ACCOUNTS_PAYABLE,
        ROLE_BILLING,
        ROLE_INVOICING,
        ROLE_SALES_AND_MARKETING,
        ROLE_CONTRACTS_ADMIN,
        ROLE_INFORMATION_TECHNOLOGY,
        ROLE_SYSTEM_AND_NETWORK_ADMIN,
        ROLE_CORPORATE_TIME_REPORTS,
        ROLE_CONSULTANT_TIME_REPORTS,
        ROLE_CONSULTANT_TIME_ADMIN,
        ROLE_IT_DEVELOPER,
        ROLE_CORPORATE_DATA,
        ROLE_PRB_EVALUATIONS_MANAGER,
        ROLE_HEALTH_INSURANCE_MANAGER,
        ROLE_BULK_IMPORT,
        ROLE_CHILI_ADMIN,
    }
    public final static Map<String, String> rolesMessages = new HashMap<String, String>();

    static {
        rolesMessages.put(OfficeRole.ROLE_USER.name(), "User");
        rolesMessages.put(OfficeRole.ROLE_CORPORATE_EMPLOYEE.name(), "Corporate Employee");
        rolesMessages.put(OfficeRole.ROLE_ADMIN.name(), "Admin");
        rolesMessages.put(OfficeRole.ROLE_HR.name(), "HR");
        rolesMessages.put(OfficeRole.ROLE_RECRUITER.name(), "Recruiter");
        rolesMessages.put(OfficeRole.ROLE_TIME.name(), "Time");
        rolesMessages.put(OfficeRole.ROLE_EXPENSE.name(), "Expense");
        rolesMessages.put(OfficeRole.ROLE_DRIVE.name(), "Drive");
        rolesMessages.put(OfficeRole.ROLE_RELATIONSHIP.name(), "Engagement");
        rolesMessages.put(OfficeRole.ROLE_ACCOUNT_VIEW.name(), "Account View");
        rolesMessages.put(OfficeRole.ROLE_HR_ADMINSTRATION.name(), "HR Administration");
        rolesMessages.put(OfficeRole.ROLE_H1B_IMMIGRATION.name(), "H1B Immigration");
        rolesMessages.put(OfficeRole.ROLE_GC_IMMIGRATION.name(), "GC Immigration");
        rolesMessages.put(OfficeRole.ROLE_PAYROLL_AND_BENIFITS.name(), "Payroll and Benefits");
        rolesMessages.put(OfficeRole.ROLE_ACCOUNTS_RECEIVABLE.name(), "Accounts Receivable");
        rolesMessages.put(OfficeRole.ROLE_ACCOUNTS_PAYABLE.name(), "Accounts Payable");
        rolesMessages.put(OfficeRole.ROLE_BILLING.name(), "Billing");
        rolesMessages.put(OfficeRole.ROLE_INVOICING.name(), "Invoicing");
        rolesMessages.put(OfficeRole.ROLE_SALES_AND_MARKETING.name(), "Sales and Marketing");
        rolesMessages.put(OfficeRole.ROLE_CONTRACTS_ADMIN.name(), "Contracts Administration");
        rolesMessages.put(OfficeRole.ROLE_INFORMATION_TECHNOLOGY.name(), "Information Technology");
        rolesMessages.put(OfficeRole.ROLE_SYSTEM_AND_NETWORK_ADMIN.name(), "System and Network Administration");
        rolesMessages.put(OfficeRole.ROLE_CORPORATE_TIME_REPORTS.name(), "Corporate Time Reports");
        rolesMessages.put(OfficeRole.ROLE_CONSULTANT_TIME_ADMIN.name(), "Consultant Time Admin");
        rolesMessages.put(OfficeRole.ROLE_CONSULTANT_TIME_REPORTS.name(), "Consultant Time Reports");
        rolesMessages.put(OfficeRole.ROLE_IT_DEVELOPER.name(), "IT Developer");
        rolesMessages.put(OfficeRole.ROLE_CORPORATE_DATA.name(), "Corporate Data");
        rolesMessages.put(OfficeRole.ROLE_PRB_EVALUATIONS_MANAGER.name(), "Probation Evalations Manager");
        rolesMessages.put(OfficeRole.ROLE_HEALTH_INSURANCE_MANAGER.name(), "Health Insurance Manager");
        rolesMessages.put(OfficeRole.ROLE_BULK_IMPORT.name(), "Bulk Import Manager");
        rolesMessages.put(OfficeRole.ROLE_CHILI_ADMIN.name(), "Chili Admin");
    }
}
