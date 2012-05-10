package info.yalamanchili.office.jrs.profile;

import info.yalamanchili.office.dao.CRUDDao;
import info.yalamanchili.office.dao.profile.EmailDao;
import info.yalamanchili.office.entity.profile.Email;
import info.yalamanchili.office.jrs.CRUDResource;

import java.util.List;

import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/email")
@Component
public class EmailResource extends CRUDResource<Email> {

	@Autowired
	public EmailDao emailDao;

	@Override
	public CRUDDao getDao() {
		return emailDao;
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
