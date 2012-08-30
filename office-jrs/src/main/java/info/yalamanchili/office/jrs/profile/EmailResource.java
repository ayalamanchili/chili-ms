package info.yalamanchili.office.jrs.profile;

import info.yalamanchili.office.dao.CRUDDao;
import info.yalamanchili.office.dao.profile.EmailDao;
import info.yalamanchili.office.dao.profile.EmployeeDao;
import info.yalamanchili.office.dto.profile.Employee;
import info.yalamanchili.office.entity.profile.Email;
import info.yalamanchili.office.entity.profile.EmailType;
import info.yalamanchili.office.jrs.CRUDResource;

import java.util.List;
import javax.ws.rs.PUT;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Path("email")
@Component
@Transactional
@Scope("request")
public class EmailResource extends CRUDResource<Email> {

	@Autowired
	public EmailDao emailDao;

        @Autowired
       public EmployeeDao employeeDao;
        
	@Override
	public CRUDDao getDao() {
		return emailDao;
	}

    @PUT
    @Path("/email/{empId}")
    public void addEmail(@PathParam("empId") Long empId, Email email) {
        Employee emp = (Employee) employeeDao.findById(empId);

        if (email.getEmailType() != null) {
            EmailType emailType = getDao().getEntityManager().find(EmailType.class, email.getEmailType().getId());
            email.setEmailType(emailType);
        }
        email = employeeDao.UpdatePrimaryEmail(emp, email);
        emp.addEmail(email);
    }
        
	@XmlRootElement
	@XmlType
	public static class EmailTable {
		protected Long size;
		protected List<Email> entities;

		public Long getSize() {
			return size;
		}

		public void setSize(Long size) {
			this.size = size;
		}

		@XmlElement
		public List<Email> getEntities() {
			return entities;
		}

		public void setEntities(List<Email> entities) {
			this.entities = entities;
		}

	}
}
