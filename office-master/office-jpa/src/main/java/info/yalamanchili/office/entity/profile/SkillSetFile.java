/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.entity.profile;

import info.chili.jpa.AbstractEntity;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;

/**
 *
 * @author radhika.mukkala
 */
@Entity
@Audited
@XmlRootElement
@XmlType
public class SkillSetFile extends AbstractEntity {

    @Transient
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    protected String name;

    /**
     *
     */
    protected String fileURL;

    @ManyToOne
    @ForeignKey(name = "FK_SkillSet_ResumeUrl")
    protected SkillSet skillSet;

    @Enumerated(EnumType.STRING)
    protected SkillSetFileType skillSetFileType;

    public SkillSet getSkillSet() {
        return skillSet;
    }

    public void setSkillSet(SkillSet skillSet) {
        this.skillSet = skillSet;
    }

    public SkillSetFileType getSkillSetFileType() {
        return skillSetFileType;
    }

    public void setSkillSetFileType(SkillSetFileType skillSetFileType) {
        this.skillSetFileType = skillSetFileType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

    @XmlTransient
    public SkillSet getskillSet() {
        return skillSet;
    }

    public void setskillSet(SkillSet skillSet) {
        this.skillSet = skillSet;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SkillSetFile other = (SkillSetFile) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.fileURL, other.fileURL);
    }
}
