/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
package info.yalamanchili.office.jrs;

import info.chili.jpa.validation.Validate;
import info.chili.security.dao.CRoleDao;
import info.chili.security.domain.CRole;
import info.chili.security.domain.CUser;
import info.chili.service.jrs.exception.ServiceException;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.OfficeRoles;
import info.yalamanchili.office.dao.profile.EmployeeDao;
import info.yalamanchili.office.dao.security.OfficeSecurityService;
import info.yalamanchili.office.entity.profile.Employee;
import info.yalamanchili.office.jms.MessagingService;
import info.yalamanchili.office.profile.EmployeeService;
import info.yalamanchili.office.profile.notification.ProfileNotificationService;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import info.yalamanchili.office.bpm.OfficeBPMIdentityService;
import info.yalamanchili.office.cache.OfficeCacheKeys;
import info.yalamanchili.office.dao.security.EmployeeLoginDto;
import info.yalamanchili.office.dto.profile.EmployeeCreateDto;
import info.yalamanchili.office.dto.security.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

@Path("secured/admin")
@Produces("application/json")
@Consumes("application/json")
@Component
@Transactional
@Scope("request")
public class AdminResource {

    //TODO make these be created on demand rather than at per request.
    @Autowired
    protected OfficeSecurityService securityService;
    @Autowired
    protected OfficeBPMIdentityService officeBPMIdentityService;
    @Autowired
    protected Mapper mapper;
    @Autowired
    protected MessagingService messagingService;
    @Autowired
    protected ProfileNotificationService profileNotificationService;
    @Autowired
    public EmployeeDao employeeDao;
    @PersistenceContext
    EntityManager em;

    @Path("/login")
    @PUT
    @Validate
    @Cacheable(value = OfficeCacheKeys.LOGIN, key = "#user.username")
    public EmployeeLoginDto login(CUser user) {
        return mapper.map(securityService.login(user), EmployeeLoginDto.class);
    }

    @Path("/ping")
    @GET
    public void ping() {
        
    }

    @Path("/changepassword/{empId}")
    @PUT
    @Validate
    public CUser changePassword(@PathParam("empId") Long empId, User user) {
        EmployeeService employeeService = (EmployeeService) SpringContext.getBean("employeeService");
        return employeeService.changePassword(empId, user);
    }

    @Path("/resetpassword/{empId}")
    @PUT
    @Validate
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_HR','ROLE_RELATIONSHIP')")
    public CUser resetPassword(@PathParam("empId") Long empId, User user) {
        EmployeeService employeeService = (EmployeeService) SpringContext.getBean("employeeService");
        return employeeService.resetPassword(empId, user);
    }

    @Path("/deactivateuser/{empId}")
    @PUT
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_HR_ADMINSTRATION','ROLE_RELATIONSHIP','ROLE_SYSTEM_AND_NETWORK_ADMIN')")
    @Caching(evict = {
        @CacheEvict(value = OfficeCacheKeys.EMPLOYEES, allEntries = true),
        @CacheEvict(value = OfficeCacheKeys.EMAILS, allEntries = true)
    })
    public void deactivateuser(@PathParam("empId") Long empId) {
        EmployeeService employeeService = (EmployeeService) SpringContext.getBean("employeeService");
        employeeService.deactivateUser(empId);
    }

    @Path("/createuser")
    @PUT
    @Produces("application/text")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_HR','ROLE_RELATIONSHIP','ROLE_TIME','ROLE_SYSTEM_AND_NETWORK_ADMIN')")
    @CacheEvict(value = "employees", allEntries = true)
    @Validate
    public String createUser(EmployeeCreateDto employee) {
        EmployeeService employeeService = (EmployeeService) SpringContext.getBean("employeeService");
        return employeeService.createUser(employee);
    }

    @GET
    @Path("/roles/{empId}/{start}/{limit}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public MultiSelectObj getUserRoles(@PathParam("empId") Long empId, @PathParam("start") Integer start, @PathParam("limit") Integer limit) {
        MultiSelectObj obj = new MultiSelectObj();
        Employee emp = em.find(Employee.class, empId);
        for (CRole role : CRoleDao.instance().query(start, limit)) {
            obj.addAvailable(role.getRoleId().toString(), role.getRolename());
        }
        for (CRole role : emp.getUser().getRoles()) {
            obj.addSelected(role.getRoleId().toString());
        }
        return obj;
    }

    @GET
    @Path("/role/add/{empId}/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Caching(evict = {
        @CacheEvict(value = OfficeCacheKeys.LOGIN, allEntries = true),
        @CacheEvict(value = OfficeCacheKeys.EMAILS, allEntries = true)
    })
    public void addUserRoles(@PathParam("empId") Long empId, @QueryParam("id") List<Long> ids) {
        Employee emp = em.find(Employee.class, empId);
        for (Long roleId : ids) {
            CRole role = CRoleDao.instance().findById(roleId);
            emp.getUser().addRole(role);
            OfficeBPMIdentityService.instance().addUserToGroup(emp.getUser().getUsername(), role.getRolename());
        }
    }

    @GET
    @Path("/role/remove/{empId}/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Caching(evict = {
        @CacheEvict(value = OfficeCacheKeys.LOGIN, allEntries = true),
        @CacheEvict(value = OfficeCacheKeys.EMAILS, allEntries = true)
    })
    public void removeUserRoles(@PathParam("empId") Long empId, @QueryParam("id") List<Long> ids) {
        EmployeeDao empDao = (EmployeeDao) SpringContext.getBean(EmployeeDao.class);
        Employee emp = empDao.findById(empId);
        for (Long roleId : ids) {
            CRole role = CRoleDao.instance().findById(roleId);
            if (role.getRolename().equals(OfficeRoles.OfficeRole.ROLE_USER.name()) || role.getRolename().equals(OfficeRoles.OfficeRole.ROLE_CORPORATE_EMPLOYEE.name())) {
                throw new ServiceException(ServiceException.StatusCode.INVALID_REQUEST, "SYSTEM", "invalid.action", "cannot remove role.");
            }
            if (ids.contains(roleId)) {
                emp.getUser().getRoles().remove(role);
                OfficeBPMIdentityService.instance().removeUserFromGroup(emp.getUser().getUsername(), role.getRolename());
            }
        }
    }

    @GET
    public Employee getCurrentUser() {
        return securityService.getCurrentUser();
    }
}
