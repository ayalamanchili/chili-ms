/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.jrs.time;

import info.chili.dao.CRUDDao;
import info.yalamanchili.office.dao.profile.EmployeeDao;
import info.yalamanchili.office.dao.time.CorporateTimeSheetDao;
import info.yalamanchili.office.dto.time.CorporateTimeSummary;
import info.yalamanchili.office.entity.profile.Employee;
import info.yalamanchili.office.entity.time.CorporateTimeSheet;
import info.yalamanchili.office.jrs.CRUDResource;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author prasanthi.p
 */
@Path("secured/corporate-timesheet")
@Component
@Transactional
@Scope("request")
public class CorporateTimeSheetResource extends CRUDResource<CorporateTimeSheet> {

    @GET
    @Path("/summary")
    public CorporateTimeSummary getCorporateTimeSummary() {
        //TODO implement
        return new CorporateTimeSummary();
    }

    @GET
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TIME','ROLE_HR')")
    @Path("/summary/{empId}")
    public CorporateTimeSummary getCorporateTimeSummary(@PathParam("empId") Long empId) {
        //TODO implement
        return new CorporateTimeSummary();
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TIME','ROLE_HR')")
    public CorporateTimeSheet save(CorporateTimeSheet entity) {
        Employee emp = EmployeeDao.instance().findById(entity.getEmployee().getId());
        entity.setEmployee(emp);
        return super.save(entity);
    }

    @Autowired
    public CorporateTimeSheetDao corporateTimeSheetDao;

    @Override
    public CRUDDao getDao() {
        return corporateTimeSheetDao;
    }

    @GET
    @Path("/employee/{empId}/{start}/{limit}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TIME','ROLE_HR')")
    public CorporateTimeSheetResource.CorporateTimeSheetTable getCorporateTimeSheet(@PathParam("empId") Long empId, @PathParam("start") int start, @PathParam("limit") int limit, @QueryParam("incluedeInactive") boolean includeInactive) {
        CorporateTimeSheetResource.CorporateTimeSheetTable tableObj = new CorporateTimeSheetResource.CorporateTimeSheetTable();
        Employee emp = EmployeeDao.instance().findById(empId);
        tableObj.setEntities(corporateTimeSheetDao.getTimeSheetsEmployee(emp, start, limit));
        tableObj.setSize(corporateTimeSheetDao.getTimeSheetsSizeForEmployee(emp));
        return tableObj;
    }

    @XmlRootElement
    @XmlType
    public static class CorporateTimeSheetTable {

        protected Long size;
        protected List<CorporateTimeSheet> entities;

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        @XmlElement
        public List<CorporateTimeSheet> getEntities() {
            return entities;
        }

        public void setEntities(List<CorporateTimeSheet> entities) {
            this.entities = entities;
        }
    }
}
