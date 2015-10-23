/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.contracts;

/**
 *
 * @author Madhu.Badiginchala
 */
public enum ClientInformationStatus {

    PENDING_ACCOUNTS_VERIFICATION,
    PENDING_HR_VERIFICATION,
    COMPLETED;

    public static String[] names() {
        ClientInformationStatus[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }
        return names;
    }

}
