/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.profile.invite;

import info.chili.spring.SpringContext;
import info.yalamanchili.office.dao.invite.InviteCodeDao;
import info.yalamanchili.office.email.MailUtils;
import info.chili.email.Email;
import info.yalamanchili.office.dao.invite.InviteTypeDao;
import info.yalamanchili.office.entity.profile.invite.InvitationType;
import info.yalamanchili.office.entity.profile.invite.InviteType;
import info.yalamanchili.office.entity.profile.invite.InviteCode;
import info.yalamanchili.office.jms.MessagingService;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Madhu.Badiginchala
 */
@Component
@Scope("prototype")
@Transactional
public class InviteCodeGeneratorService {

    @Autowired
    protected InviteCodeDao inviteCodeDao;

    @Autowired
    protected MailUtils mailUtils;

    public InviteCode generate(InvitationType type, String email, Date vaidDate, Date expiryDate, boolean sendEmail) {
        InviteCode code = new InviteCode();
        code.setInviteType(InviteTypeDao.instance().find(type));
        code.setValidFromDate(vaidDate);
        code.setExpiryDate(expiryDate);
        code.setEmail(email);
        return generate(code, sendEmail);
    }

    public InviteCode generate(InviteCode entity, boolean sendEmail) {
        entity.setInvitationCode(uuidGen());
        inviteCodeDao.save(entity);
        if (sendEmail) {
            sendInviteCodeEmail(entity);
        }
        return entity;
    }

    public void sendInviteCodeEmail(InviteCode entity) {
        Email email = new Email();
        email.addTo(entity.getEmail());
        StringBuilder subject = new StringBuilder();
        subject.append("System Soft Invitation");
        email.setSubject(subject.toString());
        Map<String, Object> emailCtx = new HashMap<>();
        emailCtx.put("invitationCode", entity.getInvitationCode());
        email.setTemplateName("send_onboarding_invitation_eamil_template.html");
        String messageText = "http://localhost:9090/office-web/?inviteCode=" + entity.getInvitationCode();
        email.setContext(emailCtx);
        email.setBody(messageText);
        MessagingService.instance().sendEmail(email);
    }

    private String uuidGen() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

    public static InviteCodeGeneratorService instance() {
        return SpringContext.getBean(InviteCodeGeneratorService.class);
    }

}
