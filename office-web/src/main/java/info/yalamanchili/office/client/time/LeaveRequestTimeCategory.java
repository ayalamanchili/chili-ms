/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.time;

/**
 *
 * @author ayalamanchili
 */
public enum LeaveRequestTimeCategory {

    Vacation_Spent,
    PTO_Spent,
    Unpaid,
    JuryDuty,
    Bereavement,
    Maternity;

    public static String[] names() {
        LeaveRequestTimeCategory[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }
        return names;
    }
}
