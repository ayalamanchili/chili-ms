/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.entity.employee;

import info.chili.commons.DateUtils;
import info.chili.jpa.AbstractEntity;
import info.yalamanchili.office.entity.ext.Question;
import info.yalamanchili.office.entity.profile.Employee;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;

/**
 *
 * @author anuyalamanchili
 */
@Indexed
@XmlRootElement
@Entity
@Audited
public class PerformanceEvaluation extends AbstractEntity {

    private static final long serialVersionUID = 1L;
    /**
     * evaluation Date
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date evaluationDate;
    /**
     * start Date
     */
    @NotNull(message = "{evaluationPeriodStartDate.not.empty.msg}")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    protected Date evaluationPeriodStartDate;
    /**
     * end Date
     */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @NotNull(message = "{evaluationPeriodEndDate.not.empty.msg}")
    protected Date evaluationPeriodEndDate;
    /**
     * Evaluation Frequency
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{performanceevaluation.type.not.empty.msg}")
    protected EvaluationFrequencyType type;
    /**
     *
     */
//    @NotNull(message = "{rating.not.empty.msg}")
    protected Double rating;
    /**
     *
     */
    @Lob
    protected String keyAccomplishments;
    /**
     *
     */
    @Lob
    protected String areasNeedImprovement;
    /**
     *
     */
    @Lob
    protected String ManagersComments;
    /**
     *
     */
    @Lob
    protected String employeeComments;
    /**
     *
     */
    @Lob
    protected String ceoComments;
    /**
     * Employee
     */
    @ManyToOne
    @ForeignKey(name = "FK_EMP_PERF_EVALS")
    protected Employee employee;

    /**
     * questions
     */
    @ManyToMany
    protected Set<Question> questions;

    public PerformanceEvaluation() {
    }

    public Date getEvaluationDate() {
        return evaluationDate;
    }

    public void setEvaluationDate(Date evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public Date getEvaluationPeriodStartDate() {
        return evaluationPeriodStartDate;
    }

    public void setEvaluationPeriodStartDate(Date evaluationPeriodStartDate) {
        this.evaluationPeriodStartDate = evaluationPeriodStartDate;
    }

    public Date getEvaluationPeriodEndDate() {
        return evaluationPeriodEndDate;
    }

    public void setEvaluationPeriodEndDate(Date evaluationPeriodEndDate) {
        this.evaluationPeriodEndDate = evaluationPeriodEndDate;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public EvaluationFrequencyType getType() {
        return type;
    }

    public void setType(EvaluationFrequencyType type) {
        this.type = type;
    }

    public String getKeyAccomplishments() {
        return keyAccomplishments;
    }

    public void setKeyAccomplishments(String keyAccomplishments) {
        this.keyAccomplishments = keyAccomplishments;
    }

    public String getAreasNeedImprovement() {
        return areasNeedImprovement;
    }

    public void setAreasNeedImprovement(String areasNeedImprovement) {
        this.areasNeedImprovement = areasNeedImprovement;
    }

    public String getManagersComments() {
        return ManagersComments;
    }

    public void setManagersComments(String ManagersComments) {
        this.ManagersComments = ManagersComments;
    }

    public String getEmployeeComments() {
        return employeeComments;
    }

    public void setEmployeeComments(String employeeComments) {
        this.employeeComments = employeeComments;
    }

    public String getCeoComments() {
        return ceoComments;
    }

    public void setCeoComments(String ceoComments) {
        this.ceoComments = ceoComments;
    }

    @XmlTransient
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    @XmlTransient
    public Set<Question> getQuestions() {
        if (this.questions == null) {
            this.questions = new HashSet<Question>();
        }
        return questions;
    }

    public void addQuestion(Question entity) {
        if (entity == null) {
            return;
        }
        getQuestions().add(entity);
    }

    protected String evaluationFYYear;

    public void setEvaluationFYYear(String evaluationFYYear) {
        this.evaluationFYYear = evaluationFYYear;
    }

    @Transient
    public String getEvaluationFYYear() {
        return DateUtils.getYearFromDate(getEvaluationPeriodStartDate()).toString();
    }
}
