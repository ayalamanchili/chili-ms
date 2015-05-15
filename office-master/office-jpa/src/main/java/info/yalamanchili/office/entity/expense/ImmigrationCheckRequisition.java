/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.entity.expense;

import info.chili.jpa.AbstractEntity;
import info.yalamanchili.office.entity.Company;
import info.yalamanchili.office.entity.profile.Employee;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Field;

/**
 *
 * @author Madhu.Badiginchala
 */
@Entity
@Audited
@XmlRootElement
@XmlType
public class ImmigrationCheckRequisition extends AbstractEntity {
    
    private static long serialVersionUID = 1L;
    /**
     *
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    @NotNull(message = "{requestedDate.not.empty.msg}")
    private Date requestedDate;
    /**
     *
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    @NotNull(message = "{neededBy.not.empty.msg}")
    private Date neededByDate;
    /**     
     *
     */
    @NotNull(message = "{amount.not.empty.msg}")
    private BigDecimal amount;
    /**
     *
     */
    @NotNull(message = "{mailingAddress.not.empty.msg}")
    private String mailingAddress;
    /**
     *
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "{ImmigrationCaseType.not.empty.msg}")
    private ImmigrationCaseType caseType;
    /**
     *
     */
    @Lob
    private String purpose;
    /**
     *
     */
    private String requesterName;
    /**
     *
     */
    private String hrName;
    /**
     *
     */
    private String approvedBy;
    /**
     *
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date approvedDate;
    /**
     *
     */
    private String accountedBy;
    /**
     *
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date checkIssuedDate;
    /**
     *
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date accountDeptReceivedDate;
    /**
     *
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    @ForeignKey(name = "FK_Company_ImmigrationCheckReqs")
    @NotNull(message = "{immigration.check.requisition.company.not.empty.msg}")
    private Company company;
    /**
     *
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    @ForeignKey(name = "FK_Employee_ImmigrationCheckReqs")
    @NotNull(message = "{immigration.check.requisition.employee.not.empty.msg}")
    private Employee employee;
    /**
     *
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<CheckRequisitionItem> items;
    /**
     *
     */
    @Enumerated(EnumType.STRING)
    @Field
    @NotNull(message = "{immigration.check.requisition.status.not.empty.msg}")
    private ImmigrationCheckRequisitionStatus status;
    /**
     * 
     */
    private String bpmProcessId;
    /**
    * GETTERS and SETTERS
    */
    public Date getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(Date requestedDate) {
        this.requestedDate = requestedDate;
    }

    public Date getNeededByDate() {
        return neededByDate;
    }

    public void setNeededByDate(Date neededByDate) {
        this.neededByDate = neededByDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public ImmigrationCaseType getCaseType() {
        return caseType;
    }

    public void setCaseType(ImmigrationCaseType caseType) {
        this.caseType = caseType;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getHrName() {
        return hrName;
    }

    public void setHrName(String hrName) {
        this.hrName = hrName;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getAccountedBy() {
        return accountedBy;
    }

    public void setAccountedBy(String accountedBy) {
        this.accountedBy = accountedBy;
    }

    public Date getCheckIssuedDate() {
        return checkIssuedDate;
    }

    public void setCheckIssuedDate(Date checkIssuedDate) {
        this.checkIssuedDate = checkIssuedDate;
    }

    public Date getAccountDeptReceivedDate() {
        return accountDeptReceivedDate;
    }

    public void setAccountDeptReceivedDate(Date accountDeptReceivedDate) {
        this.accountDeptReceivedDate = accountDeptReceivedDate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public List<CheckRequisitionItem> getItems() {
        return items;
    }

    public void setItems(List<CheckRequisitionItem> items) {
        this.items = items;
    }

    public ImmigrationCheckRequisitionStatus getStatus() {
        return status;
    }

    public void setStatus(ImmigrationCheckRequisitionStatus status) {
        this.status = status;
    }

    public String getBpmProcessId() {
        return bpmProcessId;
    }

    public void setBpmProcessId(String bpmProcessId) {
        this.bpmProcessId = bpmProcessId;
    }

    @Override
    public String toString() {
        return "ImmigrationCheckRequisition{" + "requestedDate=" + requestedDate + ", neededByDate=" + neededByDate + ", amount=" + amount + ", mailingAddress=" + mailingAddress + ", caseType=" + caseType + ", purpose=" + purpose + ", requesterName=" + requesterName + ", hrName=" + hrName + ", approvedBy=" + approvedBy + ", approvedDate=" + approvedDate + ", accountedBy=" + accountedBy + ", checkIssuedDate=" + checkIssuedDate + ", accountDeptReceivedDate=" + accountDeptReceivedDate + ", company=" + company + ", employee=" + employee + ", items=" + items + ", status=" + status + ", bpmProcessId=" + bpmProcessId + '}';
    }

}
