/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.dao.hr;

import info.chili.dao.CRUDDao;
import info.yalamanchili.office.entity.hr.Prospect;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author radhika.mukkala
 */
@Repository
@Scope("prototype")
public class ProspectDao extends CRUDDao<Prospect> {

    @PersistenceContext
    protected EntityManager em;

    @Transactional(readOnly = true)
    @Override
    public List<Prospect> query(int start, int limit) {
        TypedQuery<Prospect> findAllQuery = getEntityManager().createQuery("from " + Prospect.class.getCanonicalName() + " order by startDate DESC ", Prospect.class);
        findAllQuery.setFirstResult(start);
        findAllQuery.setMaxResults(limit);
        return findAllQuery.getResultList();
    }

    public ProspectDao() {
        super(Prospect.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

}
