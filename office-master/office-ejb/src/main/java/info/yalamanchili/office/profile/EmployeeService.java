/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.profile;

import info.chili.commons.EntityQueryUtils;
import info.chili.dao.AbstractHandleEntityDao;
import info.chili.document.dao.SerializedEntityDao;
import info.chili.security.dao.CRoleDao;
import info.chili.security.domain.CRole;
import info.chili.security.domain.CUser;
import info.chili.service.jrs.exception.ServiceException;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.OfficeRoles.OfficeRole;
import info.yalamanchili.office.bpm.OfficeBPMIdentityService;
import info.yalamanchili.office.bpm.OfficeBPMService;
import info.yalamanchili.office.dao.expense.BankAccountDao;
import info.yalamanchili.office.dao.invite.InviteCodeDao;
import info.yalamanchili.office.dao.profile.EmployeeDao;
import info.yalamanchili.office.dao.profile.ext.DependentDao;
import info.yalamanchili.office.dao.profile.ext.EmployeeAdditionalDetailsDao;
import info.yalamanchili.office.dao.security.OfficeSecurityService;
import info.yalamanchili.office.dto.onboarding.InitiateOnBoardingDto;
import info.yalamanchili.office.dto.profile.EmployeeCreateDto;
import info.yalamanchili.office.dto.onboarding.OnBoardingEmployeeDto;
import info.yalamanchili.office.dto.security.User;
import info.yalamanchili.office.entity.Company;
import info.yalamanchili.office.entity.expense.BankAccount;
import info.yalamanchili.office.entity.profile.Address;
import info.yalamanchili.office.entity.profile.Email;
import info.yalamanchili.office.entity.profile.Employee;
import info.yalamanchili.office.entity.profile.EmployeeType;
import info.yalamanchili.office.entity.profile.Preferences;
import info.yalamanchili.office.entity.profile.ext.Dependent;
import info.yalamanchili.office.entity.profile.ext.EmployeeAdditionalDetails;
import info.yalamanchili.office.entity.profile.invite.InvitationType;
import info.yalamanchili.office.entity.profile.invite.InviteCode;
import info.yalamanchili.office.entity.profile.onboarding.EmployeeOnBoarding;
import info.yalamanchili.office.entity.profile.onboarding.OnBoardingStatus;
import info.yalamanchili.office.profile.invite.InviteCodeGeneratorService;
import info.yalamanchili.office.profile.notification.ProfileNotificationService;
import info.yalamanchili.office.security.SecurityUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.time.DateUtils;
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
        if (emp.getCompany() != null) {
            emp.setCompany(em.find(Company.class, employee.getCompany().getId()));
        }
        String employeeId = generateEmployeeId(employee.getFirstName(), employee.getLastName(), employee.getDateOfBirth());
        String generatepassword = generatepassword();
        String empType = emp.getEmployeeType().getName();
        if (empType.equals("Corporate Employee") || empType.equals("Employee")) {
            //Create CUser
            CUser user = mapper.map(employee, CUser.class);
            user.setPasswordHash(generatepassword);
            user.setUsername(employeeId);
            user.setEnabled(true);
            if (empType.equals("Corporate Employee")) {
                user.addRole(CRoleDao.instance().findRoleByName(OfficeRole.ROLE_CORPORATE_EMPLOYEE.name()));
            }
            user.addRole((CRole) EntityQueryUtils.findEntity(em, CRole.class, "rolename", OfficeRole.ROLE_USER.name()));
            user = OfficeSecurityService.instance().createCuser(user);
            emp.setUser(user);
        }

        //Create employee with basic information
        emp.setEmployeeId(employeeId);
        Preferences prefs = new Preferences();
        prefs.setEnableEmailNotifications(Boolean.TRUE);
        emp.setPreferences(prefs);

        //Create BPM User
        if (emp.getEmployeeType().getName().equalsIgnoreCase("Corporate Employee")) {
            OfficeBPMIdentityService.instance().createUser(employeeId);
            Map<String, Object> obj = new HashMap<>();
            obj.put("employee", emp);
            OfficeBPMService.instance().startProcess("new_corp_employee_process", obj);

        }

        Email email = new Email();
        email.setEmail(employee.getEmail());
        email.setPrimaryEmail(true);
        emp.addEmail(email);
        emp = EmployeeDao.instance().save(emp);
        emp = em.merge(emp);
        //create cert
        OfficeSecurityService.instance().createUserCert(emp, null, null);
        //Email notification
        if (empType.equals("Corporate Employee") || empType.equals("Employee")) {
            profileNotificationService.sendNewUserCreatedNotification(emp);
        }
        return emp.getId().toString();
    }

    public void initiateOnBoarding(InitiateOnBoardingDto dto) {
        InviteCode code = InviteCodeGeneratorService.instance().generate(InvitationType.CLIENT_ONBOARDING, dto.getEmail(), new Date(), DateUtils.addDays(new Date(), 7), false);
        SerializedEntityDao.instance().save(dto, code.getClass().getCanonicalName(), code.getId());
        EmployeeOnBoarding onboarding = new EmployeeOnBoarding();
        onboarding.setEmail(dto.getEmail());
        onboarding.setStartedBy(OfficeSecurityService.instance().getCurrentUserName());
        onboarding.setStartedDate(new Date());
        onboarding.setStatus(OnBoardingStatus.Pending_Initial_Document_Submission);
        em.merge(onboarding);
        InviteCodeGeneratorService.instance().sendInviteCodeEmail(code);
    }

    public String onBoardEmployee(OnBoardingEmployeeDto employee) {
        Employee emp = mapper.map(employee, Employee.class);
        InviteCode code = InviteCodeDao.instance().find(employee.getInviteCode().trim());
        InitiateOnBoardingDto initiateDto = (InitiateOnBoardingDto) SerializedEntityDao.instance().findAndConvert(code.getClass().getCanonicalName(), code.getId());
        emp.setEmployeeType(em.find(EmployeeType.class, initiateDto.getEmployeeType().getId()));
        if (initiateDto.getCompany() != null) {
            emp.setCompany(em.find(Company.class, initiateDto.getCompany().getId()));
        }
        emp.setStartDate(initiateDto.getStartDate());
        emp.setWorkStatus(employee.getWorkStatus());
        emp.setSsn(employee.getSsn());
        emp.setBranch(initiateDto.getBranch());
        String employeeId = generateEmployeeId(employee.getFirstName(), employee.getLastName(), employee.getDateOfBirth());
        String generatepass = generatepassword();
        String empType = emp.getEmployeeType().getName();
        if (empType.equals("Corporate Employee") || empType.equals("Employee")) {
            //Create CUser
            CUser user = mapper.map(employee, CUser.class);
            user.setPasswordHash(generatepass);
            user.setUsername(employeeId);
            user.setEnabled(true);
            if (empType.equals("Corporate Employee")) {
                user.addRole(CRoleDao.instance().findRoleByName(OfficeRole.ROLE_CORPORATE_EMPLOYEE.name()));
            }
            user.addRole((CRole) EntityQueryUtils.findEntity(em, CRole.class, "rolename", OfficeRole.ROLE_USER.name()));
            user = OfficeSecurityService.instance().createCuser(user);
            emp.setUser(user);
        }

        //Create employee with basic information
        emp.setEmployeeId(employeeId);
        Preferences prefs = new Preferences();
        prefs.setEnableEmailNotifications(Boolean.TRUE);
        emp.setPreferences(prefs);

        //Update Address for Employee
        Address address = new Address();
        address = employee.getAddress();
        emp.getAddresss().add(address);
        address.setContact(emp);

        //Update BankAccount Information for Employee
        BankAccount bankAccount = new BankAccount();
        bankAccount = employee.getBankAccount();
        BankAccountDao.instance().save(bankAccount, emp.getId(), emp.getClass().getCanonicalName());

        //Update Dependent Information for Employee
        Dependent dependent = new Dependent();
        dependent = employee.getDependent();
        DependentDao.instance().save(dependent, emp.getId(), emp.getClass().getCanonicalName());

        //Update Additional Information for Employee
        EmployeeAdditionalDetails employeeAdditionalDetails = new EmployeeAdditionalDetails();
        employeeAdditionalDetails = employee.getEmployeeAdditionalDetails();
        EmployeeAdditionalDetailsDao.instance().save(employeeAdditionalDetails, emp.getId(), emp.getClass().getCanonicalName());

        //Create BPM User
        if (emp.getEmployeeType().getName().equalsIgnoreCase("Corporate Employee")) {
            OfficeBPMIdentityService.instance().createUser(employeeId);
            Map<String, Object> obj = new HashMap<>();
            obj.put("employee", emp);
        }

//        //Start on boarding process
//        Map<String, Object> obj = new HashMap<>();
//        obj.put("entity", emp);
//        OfficeBPMService.instance().startProcess("on_boarding_employee_process", obj);
        Email email = new Email();
        email.setEmail(initiateDto.getEmail());
        email.setPrimaryEmail(true);
        emp.addEmail(email);

        emp = EmployeeDao.instance().save(emp);
        emp = em.merge(emp);
        //create cert
        OfficeSecurityService.instance().createUserCert(emp, null, null);
        return emp.getId().toString();
    }

    private String generateEmployeeId(String firstName, String lastName, Date dateofBirth) {
        String empId = firstName.toLowerCase().charAt(0) + lastName.toLowerCase();
        javax.persistence.Query findUserQuery = em.createQuery("from Employee where employeeId=:empIdParam");
        findUserQuery.setParameter("empIdParam", empId);
        if (findUserQuery.getResultList().size() > 0) {
            empId = empId + Integer.toString(dateofBirth.getDate());
        }
        if (empId.contains(" ")) {
            empId = empId.replace(" ", "_");
        }
        return empId;
    }

    public CUser changePassword(Long empId, User user) {
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

    public void deactivateUser(Long empId, EmployeeCreateDto dto) {
        Employee emp = getEmployee(empId);
        if (!emp.isActive()) {
            throw new ServiceException(ServiceException.StatusCode.INVALID_REQUEST, "SYSTEM", "invalid.request", "Employee already deactivated");
        }
        if (dto.getEndDate() == null) {
            throw new ServiceException(ServiceException.StatusCode.INVALID_REQUEST, "SYSTEM", "invalid.request", "End Date is required to deactivate the employee");
        }
        emp.setEndDate(dto.getEndDate());
        emp = em.merge(emp);
        OfficeBPMIdentityService.instance().deleteUser(emp.getEmployeeId());
        CUser user1 = emp.getUser();
        user1.setEnabled(false);
        Employee curUser = OfficeSecurityService.instance().getCurrentUser();
        profileNotificationService.sendEmployeeDeactivationNotification(curUser.getFirstName(), getEmployee(empId));
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
