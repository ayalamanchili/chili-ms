/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.dao.ext;

import com.google.common.base.Strings;
import info.chili.dao.AbstractHandleEntityDao;
import info.chili.jpa.AbstractEntity;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.dao.security.OfficeSecurityService;
import info.yalamanchili.office.entity.ext.Comment;
import info.yalamanchili.office.entity.profile.Employee;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ayalamanchili
 */
@Repository
@Scope("prototype")
public class CommentDao extends AbstractHandleEntityDao<Comment> {

    @PersistenceContext
    protected EntityManager em;

    public Comment addComment(String comment, AbstractEntity target) {
        if (Strings.isNullOrEmpty(comment)) {
            return null;
        } else {
            Comment cmnt = new Comment();
            cmnt.setComment(comment);
            return save(cmnt, target);
        }
    }

    public Comment addComment(String comment, Double rating, AbstractEntity target) {
        if (Strings.isNullOrEmpty(comment)) {
            return null;
        } else {
            Comment cmnt = new Comment();
            cmnt.setRating(rating);
            cmnt.setComment(comment);
            return save(cmnt, target);
        }
    }

    public Comment addComment(String comment, Double rating, AbstractEntity source, AbstractEntity target) {
        Comment cmnt = new Comment();
        cmnt.setRating(rating);
        cmnt.setComment(comment);
        return save(cmnt, source, target);
    }

    @Override
    public Comment save(Comment source, AbstractEntity target) {
        Employee emp = OfficeSecurityService.instance().getCurrentUser();
        String updatedBy = null;
        if (emp != null) {
            updatedBy = emp.getFirstName() + " " + emp.getLastName();
        } else {
            updatedBy = "system";
        }
        source.setUpdatedBy(updatedBy);
        source.setUpdatedTS(new Date());
        return super.save(source, target);
    }

    //Temp method for bis migration
    public Comment save(Comment source, AbstractEntity target,String updatedBy) {
        Employee emp = OfficeSecurityService.instance().getCurrentUser();
        source.setUpdatedBy(updatedBy);
        source.setUpdatedTS(new Date());
        return super.save(source, target);
    }

    public CommentDao() {
        super(Comment.class);
    }

    public static CommentDao instance() {
        return SpringContext.getBean(CommentDao.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }
}
