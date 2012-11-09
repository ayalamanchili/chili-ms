/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.dao.message;

import info.chili.beans.BeanMapper;
import info.chili.jpa.AbstractEntity;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.dao.CRUDDao;
import info.yalamanchili.office.dao.security.SecurityService;
import info.yalamanchili.office.entity.message.Message;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author Prashanthi
 */
@Component
public class MessageDao extends CRUDDao<Message> {

    @PersistenceContext
    protected EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public MessageDao() {
        super(Message.class);
    }

    public static MessageDao instance() {
        return SpringContext.getBean(MessageDao.class);
    }
}
