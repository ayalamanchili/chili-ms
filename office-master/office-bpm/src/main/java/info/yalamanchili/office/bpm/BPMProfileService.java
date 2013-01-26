/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.bpm;

import info.chili.spring.SpringContext;
import info.yalamanchili.office.OfficeRoles;
import info.yalamanchili.office.dao.security.SecurityService;
import info.yalamanchili.office.email.Email;
import info.yalamanchili.office.entity.profile.Employee;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *
 * @author anuyalamanchili
 */
@Component("bpmProfileService")
public class BPMProfileService {

    private final static Logger logger = Logger.getLogger(BPMProfileService.class.getName());
    @Autowired
    protected OfficeBPMService officeBPMService;

    @Async
    public void startAddressUpdatedProcess(Employee emp) {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("employee", emp);
        officeBPMService.startProcess("address_update_process", vars);
    }

    @PostConstruct
    public void registerProcesses() {
        OfficeBPMService.instance().registerProcess("address_update_process", "info/yalamanchili/office/address_update_process.bpmn20.xml");
    }

    public static BPMProfileService instance() {
        return (BPMProfileService) SpringContext.getBean("bpmProfileService");
    }
}
