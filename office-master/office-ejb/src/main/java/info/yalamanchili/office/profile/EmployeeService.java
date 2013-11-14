/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.profile;

import info.chili.commons.EntityQueryUtils;
import info.chili.security.domain.CRole;
import info.chili.security.domain.CUser;
import info.chili.service.jrs.exception.ServiceException;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.OfficeRoles;
import info.yalamanchili.office.bpm.OfficeBPMIdentityService;
import info.yalamanchili.office.dao.profile.EmployeeDao;
import info.yalamanchili.office.dao.security.SecurityService;
import info.yalamanchili.office.dto.profile.EmployeeCreateDto;
import info.yalamanchili.office.dto.security.User;
import info.yalamanchili.office.entity.profile.Email;
import info.yalamanchili.office.entity.profile.Employee;
import info.yalamanchili.office.entity.profile.EmployeeType;
import info.yalamanchili.office.entity.profile.Preferences;
import info.yalamanchili.office.profile.notification.ProfileNotificationService;
import info.yalamanchili.office.security.SecurityUtils;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author raghu
 */
@Component
@Scope("request")
public class EmployeeService {

    private final static Logger logger = Logger.getLogger(EmployeeService.class.getName());
    //TODO remove extended
    @PersistenceContext
    protected EntityManager em;
    @Autowired
    protected ProfileNotificationService profileNotificationService;

    @Autowired
    protected Mapper mapper;

    public String createUser(EmployeeCreateDto employee) {
        Employee emp = mapper.map(employee, Employee.class);
        emp.setEmployeeType(em.find(EmployeeType.class, emp.getEmployeeType().getId()));
        String employeeId = generateEmployeeId(employee);
        if (!emp.getEmployeeType().getName().equals("SUB_CONTRACTOR")) {
            //Create CUser
            CUser user = mapper.map(employee, CUser.class);
            user.setPasswordHash(SecurityUtils.encodePassword(user.getPasswordHash(), null));
            user.setUsername(employeeId);
            user.setEnabled(true);
            user.addRole((CRole) EntityQueryUtils.findEntity(em, CRole.class, "rolename", OfficeRoles.ROLE_USER));
            user = SecurityService.instance().createCuser(user);
            emp.setUser(user);
        }

        //Create employee with basic information
        emp.setEmployeeId(employeeId);
        Preferences prefs = new Preferences();
        prefs.setEnableEmailNotifications(Boolean.TRUE);
        emp.setPreferences(prefs);

        //Create BPM User
        if (emp.getEmployeeType().getName().equalsIgnoreCase("CORPORATE_EMPLOYEE")) {
            OfficeBPMIdentityService.instance().createUser(employeeId);
        }
        Email email = new Email();
        email.setEmail(employee.getEmail());
        email.setPrimaryEmail(true);
        emp.addEmail(email);
        emp = EmployeeDao.instance().save(emp);
        em.merge(emp);
        //Email notification
        profileNotificationService.sendNewUserCreatedNotification(emp);
        return emp.getId().toString();
    }

    private String generateEmployeeId(EmployeeCreateDto emp) {
        String empId = emp.getFirstName().toLowerCase().charAt(0) + emp.getLastName().toLowerCase();
        javax.persistence.Query findUserQuery = em.createQuery("from Employee where employeeId=:empIdParam");
        findUserQuery.setParameter("empIdParam", empId);
        if (findUserQuery.getResultList().size() > 0) {
            empId = empId + Integer.toString(emp.getDateOfBirth().getDate());
        }
        if (empId.contains(" ")) {
            empId = empId.replace(" ", "_");
        }
        return empId;
    }

    public CUser changePassword(Long empId, User user) {
        //TODO check existing password
        CUser user1 = getEmployee(empId).getUser();
        String oldpswd = SecurityUtils.encodePassword(user.getOldPassword(), null);
        if (oldpswd.equals(user1.getPasswordHash())) {
            user1.setPasswordHash(SecurityUtils.encodePassword(user.getNewPassword(), null));
            return em.merge(user1);
        } else {
            throw new ServiceException(ServiceException.StatusCode.INVALID_REQUEST, "SYSTEM", "invalid.password", "Old Password Doesn't Match");
        }

    }

    public CUser resetPassword(Long empId, User user) {
        CUser user1 = getEmployee(empId).getUser();
        user1.setPasswordHash(SecurityUtils.encodePassword(user.getNewPassword(), null));
        profileNotificationService.sendResetPasswordNotification(getEmployee(empId), user.getNewPassword());
        return em.merge(user1);

    }

    public void deactivateUser(Long empId) {
        CUser user1 = getEmployee(empId).getUser();
        user1.setEnabled(false);
        em.merge(user1);

    }

    public String generatepassword() {
        final int PASSWORD_LENGTH = 6;
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < PASSWORD_LENGTH; x++) {
            sb.append((char) ((int) (Math.random() * 26) + 97));
        }
        return sb.toString();
    }

    private Employee getEmployee(Long empId) {
        Employee employee = em.find(Employee.class, empId);
        if (employee == null) {
            logger.warning("employee not found" + employee.getEmployeeId());
            return null;
        } else {
            return employee;
        }
    }

    public static EmployeeService instance() {
        return SpringContext.getBean(EmployeeService.class);
    }
}
