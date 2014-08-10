/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.jrs.employee;

import info.chili.dao.CRUDDao;
import info.yalamanchili.office.dao.employee.PerformanceEvaluationDao;
import info.yalamanchili.office.dao.profile.EmployeeDao;
import info.yalamanchili.office.dao.security.OfficeSecurityService;
import info.yalamanchili.office.dto.employee.PerformanceEvaluationSaveDto;
import info.yalamanchili.office.dto.employee.QuestionComment;
import info.yalamanchili.office.employee.PerformanceEvaluationService;
import info.yalamanchili.office.entity.employee.PerformanceEvaluation;
import info.yalamanchili.office.entity.ext.QuestionCategory;
import info.yalamanchili.office.entity.ext.QuestionContext;
import info.yalamanchili.office.entity.profile.Employee;
import info.yalamanchili.office.jrs.CRUDResource;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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
@Path("secured/performance-evaluation")
@Component
@Transactional
@Scope("request")
public class PerformanceEvaluationResource extends CRUDResource<PerformanceEvaluation> {

    @Autowired
    public PerformanceEvaluationDao performanceEvaluationDao;

    @Override
    public CRUDDao getDao() {
        return performanceEvaluationDao;
    }

    @PUT
    @Path("/save")
    public void savePerformanceEvaluation(PerformanceEvaluationSaveDto dto) {
        PerformanceEvaluationService.instance().savePerformanceEvaluation(dto);
    }

    @GET
    @Path("/comments/{id}")
    public List<QuestionComment> getQuestionComments(@PathParam("id") Long id, @QueryParam("category") QuestionCategory category, @QueryParam("context") QuestionContext context) {
        return PerformanceEvaluationService.instance().getQuestionComments(id, category, context);
    }

    @GET
    @Path("/{start}/{limit}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_HR')")
    public PerformanceEvaluationTable table(@QueryParam("employeeId") Long employeeId, @PathParam("start") int start, @PathParam("limit") int limit) {
        Employee emp = null;
        if (employeeId != null) {
            emp = EmployeeDao.instance().findById(employeeId);

        } else {
            emp = OfficeSecurityService.instance().getCurrentUser();
        }
        PerformanceEvaluationTable tableObj = new PerformanceEvaluationTable();
        tableObj.setEntities(performanceEvaluationDao.getPerformanceEvaluationsForEmp(emp));
        tableObj.setSize(getDao().size());
        return tableObj;
    }

    @XmlRootElement
    @XmlType
    public static class PerformanceEvaluationTable implements java.io.Serializable {

        protected Long size;
        protected List<PerformanceEvaluation> entities;

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        @XmlElement
        public List<PerformanceEvaluation> getEntities() {
            return entities;
        }

        public void setEntities(List<PerformanceEvaluation> entities) {
            this.entities = entities;
        }
    }
}
