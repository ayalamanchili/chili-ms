/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.entity.expense;

import info.chili.jpa.AbstractEntity;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author Prasanthi.p
 */
@Entity
@Audited
@XmlRootElement
@XmlType
public class ExpenseItem extends AbstractEntity {

    protected static long serialVersionUID = 1L;
    /**
     *
     */
    @org.hibernate.annotations.Index(name = "EXP_CATG")
    @Enumerated(EnumType.STRING)
    @OneToOne
    protected ExpenseCategory category;
    /**
     *
     */
    @Lob
    @NotEmpty
    protected String description;
    /**
     *
     */
    @NotNull
    protected BigDecimal amount;
    /**
     *
     */
    @Lob
    protected String purpose;
    /**
     *
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    @NotNull
    protected Date itemStartDate;
    /**
     *
     */
    @Temporal(javax.persistence.TemporalType.DATE)
    @NotNull
    protected Date itemEndDate;
    /**
     *
     */
    @Lob
    protected String remarks;
    /**
     *
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    @ForeignKey(name = "FK_EXP_RPT_EXP_ITEMS")
    protected ExpenseReport expenseReport;

    /**
     * @return the category
     */
    public ExpenseCategory getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the purpose
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * @param purpose the purpose to set
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * @return the itemStartDate
     */
    public Date getItemStartDate() {
        return itemStartDate;
    }

    /**
     * @param itemStartDate the itemStartDate to set
     */
    public void setItemStartDate(Date itemStartDate) {
        this.itemStartDate = itemStartDate;
    }

    /**
     * @return the itemEndDate
     */
    public Date getItemEndDate() {
        return itemEndDate;
    }

    /**
     * @param itemEndDate the itemEndDate to set
     */
    public void setItemEndDate(Date itemEndDate) {
        this.itemEndDate = itemEndDate;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the expenseReport
     */
    public ExpenseReport getExpenseReport() {
        return expenseReport;
    }

    /**
     * @param expenseReport the expenseReport to set
     */
    public void setExpenseReport(ExpenseReport expenseReport) {
        this.expenseReport = expenseReport;
    }
}
