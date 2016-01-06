/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.jrs.ext;

import com.google.common.base.Strings;
import info.chili.security.SecurityUtils;
import info.chili.service.jrs.types.Entry;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.dao.ext.CommentDao;
import info.yalamanchili.office.dao.profile.EmployeeDao;
import info.yalamanchili.office.dao.security.OfficeSecurityService;
import info.chili.email.Email;
import info.chili.jpa.AbstractEntity;
import info.yalamanchili.office.config.OfficeServiceConfiguration;
import info.yalamanchili.office.entity.ext.Comment;
import info.yalamanchili.office.entity.profile.Employee;
import info.yalamanchili.office.jms.MessagingService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ayalamanchili
 */
@Path("secured/comment")
@Component
@Transactional
@Scope("request")
//TODO create abstractREsource for ext
@Produces("application/json")
@Consumes("application/json")
public class CommentResource {

    @Autowired
    public CommentDao commentDao;

    @GET
    @Path("{targetClassName}/{id}/{start}/{limit}")
    public CommentTable getComments(@PathParam("targetClassName") String targetClassName, @PathParam("id") Long id, @PathParam("start") int start, @PathParam("limit") int limit) {
        CommentTable table = new CommentTable();
        List<Comment> comments = commentDao.findAll(id, targetClassName);
        table.setEntities(comments);
        table.setSize(Integer.valueOf(comments.size()).longValue());
        return table;

    }

    @PUT
    public void save(Comment comment) {
        Comment entity = commentDao.find(comment.getId());
        entity.setComment(comment.getComment());
        entity.setRating(comment.getRating());
        //TODO set notify emps
        commentDao.save(entity);
    }

    @PUT
    @Path("/delete/{id}")
    public void delete(@PathParam("id") Long id) {
        Comment comment = commentDao.find(id);
        if (comment.getId() != null) {
            commentDao.delete(id);
        }
    }

    @PUT
    @Path("{targetClassName}/{id}")
    public void save(@PathParam("targetClassName") String targetClassName, @PathParam("id") Long id, Comment comment) {
        List<Entry> notifyEmployees = comment.getNotifyEmployees();
        if (Strings.isNullOrEmpty(comment.getUpdatedBy())) {
            comment.setUpdatedBy(SecurityUtils.getCurrentUser());
        }
        if (comment.getUpdatedTS() == null) {
            comment.setUpdatedTS(new Date());
        }
        comment = commentDao.save(comment, id, targetClassName);
        try {
            sendCommentNotification(notifyEmployees, comment, (AbstractEntity) commentDao.getEntityManager().find(Class.forName(targetClassName), id));
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void sendCommentNotification(List<Entry> notifyEmployees, Comment comment, AbstractEntity entity) {
        if (notifyEmployees == null) {
            return;
        }
        Email email = new Email();
        for (Entry e : notifyEmployees) {
            Employee emp = EmployeeDao.instance().findEmployeWithEmpId(e.getId());
            if (emp != null) {
                email.addTo(EmployeeDao.instance().getPrimaryEmail(emp));
            }
        }
        Employee currentUser = OfficeSecurityService.instance().getCurrentUser();
        email.setSubject("Comment added by:" + currentUser.getFirstName() + "" + currentUser.getLastName() + " on " + entity.getClass().getSimpleName());
        StringBuilder body = new StringBuilder();
        body.append(currentUser.getFirstName()).append("").append(currentUser.getLastName()).append(" added the following comment: \n").append(comment.getComment());
        body.append("\n\nPrevious Comments : \n");
        //add previous comments
        for (Comment cmt : commentDao.findAll(entity.getId(), entity.getClass().getCanonicalName())) {
            body.append(cmt.getUpdatedBy()).append(" added the below comment @ ").append(new SimpleDateFormat("MM-dd-yyyy HH:MM").format(cmt.getUpdatedTS())).append(" \n");
            body.append(cmt.getComment()).append("\n").append(" \n");
        }
        //add entity info
        body.append("\nReference : ").append(entity.getClass().getSimpleName());
        if (entity instanceof AbstractEntity) {
            body.append(((AbstractEntity) (entity)).describe());
        }
        body.append("\n\n\t Please click on the below link to access: \n\t ").append(OfficeServiceConfiguration.instance().getPortalWebUrl());
        MessagingService messagingService = (MessagingService) SpringContext.getBean("messagingService");
        email.setBody(body.toString());
        email.setHtml(Boolean.TRUE);
        messagingService.sendEmail(email);
    }

    @XmlRootElement
    @XmlType
    public static class CommentTable implements java.io.Serializable {

        protected Long size;
        protected List<Comment> entities;

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        @XmlElement
        public List<Comment> getEntities() {
            return entities;
        }

        public void setEntities(List<Comment> entities) {
            this.entities = entities;
        }
    }
}
