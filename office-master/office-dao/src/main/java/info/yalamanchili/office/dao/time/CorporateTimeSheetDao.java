/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.dao.time;

import info.chili.commons.DateUtils;
import info.chili.dao.CRUDDao;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.entity.profile.Employee;
import info.yalamanchili.office.entity.time.CorporateTimeSheet;
import info.yalamanchili.office.entity.time.TimeSheetCategory;
import info.yalamanchili.office.entity.time.TimeSheetStatus;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

/**
 *
 * @author anuyalamanchili
 */
@Repository
@Scope("prototype")
public class CorporateTimeSheetDao extends CRUDDao<CorporateTimeSheet> {
    
    public CorporateTimeSheetDao() {
        super(CorporateTimeSheet.class);
    }
    
    public void saveTimeSheet(Employee emp, TimeSheetCategory category, BigDecimal hours, Date startDate, Date endDate) {
        if (findTimeSheet(emp, category, hours, startDate, endDate) == null) {
            CorporateTimeSheet ts = new CorporateTimeSheet();
            ts.setEmployee(emp);
            ts.setCategory(category);
            ts.setHours(hours);
            ts.setStatus(TimeSheetStatus.Approved);
            ts.setStartDate(startDate);
            ts.setEndDate(endDate);
            super.save(ts);
        }
    }
    
    public CorporateTimeSheet findTimeSheet(Employee emp, TimeSheetCategory category, BigDecimal hours, Date startDate, Date endDate) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("from ").append(CorporateTimeSheet.class.getCanonicalName()).append(" where");
        queryStr.append(" category=:categoryParam");
        queryStr.append(" and startDate=:startDateParam");
        queryStr.append(" and endDate=:endDateParam");
        queryStr.append(" and hours=:hoursParam");
        queryStr.append(" and employee=:empParam");
        TypedQuery<CorporateTimeSheet> query = getEntityManager().createQuery(queryStr.toString(), CorporateTimeSheet.class);
        query.setParameter("categoryParam", category);
        query.setParameter("startDateParam", startDate, TemporalType.DATE);
        query.setParameter("endDateParam", endDate, TemporalType.DATE);
        query.setParameter("hoursParam", hours);
        query.setParameter("empParam", emp);
        if (query.getResultList().size() > 0) {
            return query.getResultList().get(0);
        } else {
            return null;
        }
    }
    
    public Long getTimeSheetsSizeForEmployee(Employee employee) {
        Query query = getEntityManager().createQuery("select count(*) from " + CorporateTimeSheet.class.getCanonicalName() + " where employee=:employeeParam");
        query.setParameter("employeeParam", employee);
        return (Long) query.getSingleResult();
    }
    
    public List<CorporateTimeSheet> getTimeSheetsEmployee(Employee employee, int start, int limit) {
        Query query = getEntityManager().createQuery("from " + CorporateTimeSheet.class.getCanonicalName() + " where employee=:employeeParam");
        query.setParameter("employeeParam", employee);
        query.setFirstResult(start);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public BigDecimal getHoursInCurrentYear(Employee employee, TimeSheetCategory category) {
        TypedQuery<BigDecimal> query = getEntityManager().createQuery("select sum(hours) from " + CorporateTimeSheet.class.getCanonicalName() + " where employee=:employeeParam and category =:categoryParam and startDate >=:startDateParam and endDate <=:endDateParam", BigDecimal.class);
        query.setParameter("employeeParam", employee);
        query.setParameter("categoryParam", category);
        query.setParameter("startDateParam", DateUtils.getFirstDayOfCurrentYear(), TemporalType.DATE);
        query.setParameter("endDateParam", DateUtils.getLastDayCurrentOfYear(), TemporalType.DATE);
        if (query.getSingleResult() != null) {
            return query.getSingleResult();
        } else {
            return BigDecimal.ZERO;
        }
    }
    
    @PersistenceContext
    protected EntityManager em;
    
    @Override
    public EntityManager getEntityManager() {
        return em;
    }
    
    public static CorporateTimeSheetDao instance() {
        return SpringContext.getBean(CorporateTimeSheetDao.class);
    }
}
