/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.jrs.tae;

import info.yalamanchili.office.dao.CRUDDao;
import info.yalamanchili.office.dao.tae.SOWDao;
import info.yalamanchili.office.entity.time.TimeSheet;
import info.yalamanchili.office.entity.client.StatementOfWork;
import info.yalamanchili.office.entity.time.TimeSheet;
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
/**
 *
 * @author bala
 */
@Path("sow")
@Component
@Transactional
@Scope("request")
public class SOWResource extends CRUDResource<StatementOfWork> {

    @Autowired
    public SOWDao sowDao;

    @Override
    public CRUDDao getDao() {
        return sowDao;
    }

    @XmlRootElement
    @XmlType
    public static class SOWTable {

        protected Long size;
        protected List<StatementOfWork> entities;

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        @XmlElement
        public List<StatementOfWork> getEntities() {
            return entities;
        }

        public void setEntities(List<StatementOfWork> entities) {
            this.entities = entities;
        }
    }
}