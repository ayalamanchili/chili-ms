/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.ejb.hr;

import info.chili.spring.SpringContext;
import info.yalamanchili.office.OfficeRoles;
import info.yalamanchili.office.bpm.OfficeBPMService;
import info.yalamanchili.office.dao.profile.EmployeeDao;
import info.yalamanchili.office.dto.prospect.ProspectDto;
import info.yalamanchili.office.email.MailUtils;
import info.yalamanchili.office.entity.hr.Prospect;
import info.yalamanchili.office.entity.hr.ProspectStatus;
import info.yalamanchili.office.entity.profile.Employee;
import info.yalamanchili.office.jms.MessagingService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.dozer.Mapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Madhu.Badiginchala
 */
@Repository
@Scope("prototype")
public class ProspectProcessBean {

    @PersistenceContext
    protected EntityManager em;

    public String startNewProspectProcess(Prospect prospect, Employee currentUser) {
        Map<String, Object> vars = new HashMap<>();
        String assigned = "";
        vars.put("prospect", prospect);
        vars.put("entityId", prospect.getId());
        if (prospect.getManager() != null) {
            vars.put("approvalManager", EmployeeDao.instance().findById(prospect.getManager()).getEmployeeId());
            vars.put("caseManager", EmployeeDao.instance().findById(prospect.getManager()).getFirstName());
        }
        if (prospect.getAssigned() != null) {
            assigned = EmployeeDao.instance().findById(prospect.getAssigned()).getFirstName();
        }
        vars.put("assignedTo", assigned);
        vars.put("currentEmployee", currentUser);
        return OfficeBPMService.instance().startProcess("prospect_process", vars);
    }

    public static ProspectProcessBean instance() {
        return SpringContext.getBean(ProspectProcessBean.class);
    }

    public void notifyCaseManager(Prospect entity, List<Long> employees) {
        info.chili.email.Email email = new info.chili.email.Email();
        if (entity.getStatus().equals(ProspectStatus.IN_PROGRESS) && entity.getManager() != null) {
            email.addTo(EmployeeDao.instance().findById(entity.getManager()).getPrimaryEmail().getEmail());
        } else if (entity.getStatus().equals(ProspectStatus.IN_PROGRESS) && entity.getManager() == null) {
            email.addTo(EmployeeDao.instance().findById(entity.getAssigned()).getPrimaryEmail().getEmail());
        } else if (entity.getStatus().equals(ProspectStatus.RECRUITING) || entity.getStatus().equals(ProspectStatus.BENCH)) {
            email.addTo(EmployeeDao.instance().findById(entity.getAssigned()).getPrimaryEmail().getEmail());
        }
        if (employees != null && employees.size() > 0) {
            for (Long empId : employees) {
                email.addTo(EmployeeDao.instance().findById(empId).getPrimaryEmail().getEmail());
            }
        }
        email.setHtml(Boolean.TRUE);
        email.setSubject("New Prospect " + entity.getContact().getFirstName() + " " + entity.getContact().getLastName() + " Has Created");
        String messageText = entity.describe() + "\n Navigate To MyOffice --> Prospect --> Search -> Click on update to change the status with appropriate comments";
        email.setBody(messageText);
        MessagingService.instance().sendEmail(email);
    }

    public void sendIPProspectsNotification(List<Prospect> inProgressProspects, int inprogressCount, boolean moreThanThreeDays) {
        info.chili.email.Email email = new info.chili.email.Email();
        Mapper mapper = SpringContext.getBean(Mapper.class);
        List<ProspectDto> prospectDtos = new ArrayList();
        email.addTos(MailUtils.instance().getEmailsAddressesForRoles(OfficeRoles.OfficeRole.ROLE_PROSPECTS_MANAGER.name()));
        email.setHtml(Boolean.TRUE);
        String status = inProgressProspects.get(0).getStatus().name();
        for (Prospect prospect : inProgressProspects) {
            if (prospect.getStatus().equals(ProspectStatus.RECRUITING)) {
                email.addTo(EmployeeDao.instance().findById(prospect.getAssigned()).getPrimaryEmail().getEmail());
            }
            prospectDtos.add(ProspectDto.map(mapper, prospect));
        }
        if (moreThanThreeDays == true) {
            email.setSubject(inprogressCount + " " + status + " Prospects status not changed from more than 3 days\n");
        } else {
            email.setSubject(inprogressCount + " " + status + " Prospects status not changed from more than 2 days\n");
        }
        email.setBody("Prospect Status : " + status + " \n Count: " + inprogressCount);
        HashMap<String, Object> emailContext = new HashMap();
        emailContext.put("prospects", prospectDtos);
        email.setContext(emailContext);
        email.setTemplateName("prospects_template.html");
        MessagingService.instance().sendEmail(email);
    }

    public void benchProspectsWeeklyNotification(Map<Prospect, String> benchProspects) {
        info.chili.email.Email email = new info.chili.email.Email();
        Mapper mapper = SpringContext.getBean(Mapper.class);
        List<ProspectDto> prospectDtos = new ArrayList();
        email.addTos(MailUtils.instance().getEmailsAddressesForRoles(OfficeRoles.OfficeRole.ROLE_PROSPECTS_MANAGER.name()));
        email.setHtml(Boolean.TRUE);
        for (Prospect prospect : benchProspects.keySet()) {
            ProspectDto dto = ProspectDto.map(mapper, prospect);
            dto.setBenchDate(benchProspects.get(prospect));
            prospectDtos.add(dto);
        }
        email.setSubject(benchProspects.size() + " Bench Prospects Weekly Notification\n");
        HashMap<String, Object> emailContext = new HashMap();
        emailContext.put("prospects", prospectDtos);
        email.setContext(emailContext);
        email.setTemplateName("prospects_template.html");
        MessagingService.instance().sendEmail(email);
    }
}
