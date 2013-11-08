/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.profile.cllientinfo;

/**
 *
 * @author anuyalamanchili
 */
public enum InvoiceFrequency {

    WEEKLY,
    WEEKLY_ENDING_FRIDAY,
    WEEKLY_ENDING_SUNDAY,
    WEEKLY_MON_TO_SUN,
    BI_WEEKLY,
    MONTHLY,
    MONTHLY_LAST_FRIDAY,
    MONTHLY_LAST_SATURDAY,
    MONTHLY_FIRST_SUNDAY,
    SEMI_MONTHLY,
    NOT_REQUIRED;

    public static String[] names() {
        InvoiceFrequency[] invoiceFrequencies = values();
        String[] names = new String[invoiceFrequencies.length];

        for (int i = 0; i < invoiceFrequencies.length; i++) {
            names[i] = invoiceFrequencies[i].name();
        }

        return names;
    }
}
