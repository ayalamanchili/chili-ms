/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.client.time;

/**
 *
 * @author prasanthi.p
 */
public enum LeaveRequestAdminTimeCategory {

    PTO_USED,
    Unpaid,
    JuryDuty,
    Bereavement,
    Maternity,
    TDL_UNPIAD,
    TDL_PAID;

    public static String[] names() {
        LeaveRequestAdminTimeCategory[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }
        return names;
    }
}
