/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.dao.profile;

import info.chili.commons.EntityQueryUtils;
import info.chili.dao.CRUDDao;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.dao.security.OfficeSecurityService;
import info.yalamanchili.office.entity.profile.Certification;
import info.yalamanchili.office.entity.profile.Skill;
import info.yalamanchili.office.entity.profile.SkillSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.context.annotation.Scope;

import org.springframework.stereotype.Repository;

/**
 *
 * @author raghu
 */
@Repository
@Scope("prototype")
public class SkillDao extends CRUDDao<Skill> {

    public SkillDao() {
        super(Skill.class);
    }

    @PersistenceContext
    protected EntityManager em;

    public void addSkill(SkillSet skillSet, String name) {
        if (skillSet == null) {
            skillSet = OfficeSecurityService.instance().getCurrentUser().getSkillSet();
        }
        Skill skill = EntityQueryUtils.findEntity(getEntityManager(), Skill.class, "name", name.trim());
        if (skill != null) {
            skillSet.addSkill(skill);
        }
    }

    public void removeSkill(SkillSet skillSet, String name) {
        if (skillSet == null) {
            skillSet = OfficeSecurityService.instance().getCurrentUser().getSkillSet();
        }
        Skill skill = EntityQueryUtils.findEntity(getEntityManager(), Skill.class, "name", name.trim());
        if (skill != null) {
            skillSet.removeSkill(skill);
        }
    }

    public List<Skill> getSkills() {
        return SkillSetDao.instance().getCurrentUserSkillSet().getSkills();
    }

    public List<Skill> getSkills(Long skillSetId) {
        return SkillSetDao.instance().findById(skillSetId).getSkills();
    }

    public void addCertification(SkillSet skillSet, String name) {
        if (skillSet == null) {
            skillSet = OfficeSecurityService.instance().getCurrentUser().getSkillSet();
        }
        Certification cert = EntityQueryUtils.findEntity(getEntityManager(), Certification.class, "name", name.trim());
        if (cert != null) {
            skillSet.addCertification(cert);
        }
    }

    public void removeCertification(SkillSet skillSet, String name) {
        if (skillSet == null) {
            skillSet = OfficeSecurityService.instance().getCurrentUser().getSkillSet();
        }
        Certification cert = EntityQueryUtils.findEntity(getEntityManager(), Certification.class, "name", name.trim());
        if (cert != null) {
            skillSet.removeCertification(cert);
        }
    }

    public List<Certification> getCertifications() {
        return SkillSetDao.instance().getCurrentUserSkillSet().getCertifications();
    }

    public List<Certification> getCertifications(Long skillSetId) {
        return SkillSetDao.instance().findById(skillSetId).getCertifications();
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public static SkillDao instance() {
        return SpringContext.getBean(SkillDao.class);
    }

}
