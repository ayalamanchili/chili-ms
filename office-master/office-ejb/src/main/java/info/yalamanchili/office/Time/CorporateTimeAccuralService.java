/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.Time;

import info.chili.audit.AuditService;
import info.chili.commons.DateUtils;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.OfficeRoles;
import info.yalamanchili.office.dao.security.OfficeSecurityService;
import info.yalamanchili.office.dao.time.CorporateTimeSheetDao;
import info.yalamanchili.office.entity.profile.Employee;
import info.yalamanchili.office.entity.time.CorporateTimeSheet;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author anuyalamanchili
 */
@Component
@Transactional
public class CorporateTimeAccuralService {

    //TODO support for proration hours
    //TODO avoid duplicate creation
    public void accureMonthlyTime() {
        CorporateTimeSheetDao dao = CorporateTimeSheetDao.instance();
        Date today = new Date();
        for (Employee emp : OfficeSecurityService.instance().getUsersWithRoles(0, 5000, OfficeRoles.OfficeRole.ROLE_CORPORATE_EMPLOYEE.name())) {
            if (emp.getStartDate() == null) {
                continue;
            }
            Date startDate = emp.getStartDate();
            int numberOfMonthsCompleted = DateUtils.differenceInMonths(startDate, new Date());
            CorporateTimeSheet ptoAccruedTS = dao.getPTOAccruedTimeSheet(emp);
            if (ptoAccruedTS != null) {
                BigDecimal beforeHours = ptoAccruedTS.getHours();
                if (today.before(DateUtils.getNextDay(startDate, 30))) {
                    //New employee less than a month
                    Long daysWorkedInMonth = DateUtils.differenceInDays(startDate, today);
                    ptoAccruedTS.setHours(DateUtils.getProratedHours(TimeAccuralConstants.lessThanOneYearHoursAccural, new BigDecimal("30"), new BigDecimal(daysWorkedInMonth)));
                } else if (today.before(DateUtils.getNextYear(startDate, 1))) {
                    if (ptoAccruedTS.getHours().add(TimeAccuralConstants.lessThanOneYearHoursAccural).compareTo(TimeAccuralConstants.lessThanOneYearHoursAccuralMax) >= 0) {
                        ptoAccruedTS.setHours(TimeAccuralConstants.lessThanOneYearHoursAccuralMax);
                    } else {
                        ptoAccruedTS.setHours(ptoAccruedTS.getHours().add(TimeAccuralConstants.lessThanOneYearHoursAccural));
                    }
                } else if (today.after(DateUtils.getNextYear(startDate, 1)) && today.before(DateUtils.getNextYear(startDate, 5))) {
                    if (ptoAccruedTS.getHours().add(TimeAccuralConstants.twoToFourYearsHoursAccural).compareTo(TimeAccuralConstants.twoToFourYearsHoursAccuralMax) >= 0) {
                        ptoAccruedTS.setHours(TimeAccuralConstants.twoToFourYearsHoursAccuralMax);
                    } else {
                        ptoAccruedTS.setHours(ptoAccruedTS.getHours().add(TimeAccuralConstants.twoToFourYearsHoursAccural));
                    }
                } else if (today.after(DateUtils.getNextYear(startDate, 5)) && today.before(DateUtils.getNextYear(startDate, 10))) {
                    if (ptoAccruedTS.getHours().add(TimeAccuralConstants.fiveToTenYearsHoursAccural).compareTo(TimeAccuralConstants.fiveToTenYearsHoursAccuralMax) >= 0) {
                        ptoAccruedTS.setHours(TimeAccuralConstants.fiveToTenYearsHoursAccuralMax);
                    } else {
                        ptoAccruedTS.setHours(ptoAccruedTS.getHours().add(TimeAccuralConstants.fiveToTenYearsHoursAccural));
                    }
                } else if (today.after(DateUtils.getNextYear(startDate, 10))) {
                    if (ptoAccruedTS.getHours().add(TimeAccuralConstants.moreThanTenYearsHoursAccural).compareTo(TimeAccuralConstants.moreThanTenYearsHoursAccuralMax) >= 0) {
                        ptoAccruedTS.setHours(TimeAccuralConstants.moreThanTenYearsHoursAccuralMax);
                    } else {
                        ptoAccruedTS.setHours(ptoAccruedTS.getHours().add(TimeAccuralConstants.moreThanTenYearsHoursAccural));
                    }
                }
                dao.getEntityManager().merge(ptoAccruedTS);
                dao.addTimeSheetUpdateComment("System Montly Update: ", beforeHours, ptoAccruedTS);
            }
        }
    }

    public void revertRecentPTOAccruedChanges() {
        CorporateTimeSheetDao dao = CorporateTimeSheetDao.instance();
        for (Employee emp : OfficeSecurityService.instance().getUsersWithRoles(0, 5000, OfficeRoles.OfficeRole.ROLE_CORPORATE_EMPLOYEE.name())) {
            if (emp.getStartDate() == null) {
                continue;
            }
            CorporateTimeSheet ptoAccruedTS = dao.getPTOAccruedTimeSheet(emp);
            if (ptoAccruedTS.getId() != null) {
                CorporateTimeSheet previousVersion = (CorporateTimeSheet) AuditService.instance().mostRecentVersion(CorporateTimeSheet.class, ptoAccruedTS.getId());
                ptoAccruedTS.setHours(previousVersion.getHours());
                dao.getEntityManager().merge(ptoAccruedTS);
                dao.addTimeSheetUpdateComment("System Reverting recent Change: ", previousVersion.getHours(), ptoAccruedTS);
            }
        }

    }
//TODO temp method needed only once 

    public void convertCarryForwardToPTO() throws ParseException {
        CorporateTimeSheetDao dao = CorporateTimeSheetDao.instance();
        Date date = new SimpleDateFormat("yyyy", Locale.ENGLISH).parse("2014");
        for (Employee emp : OfficeSecurityService.instance().getUsersWithRoles(0, 5000, OfficeRoles.OfficeRole.ROLE_CORPORATE_EMPLOYEE.name())) {
            if (emp.getStartDate() == null) {
                continue;
            }
            BigDecimal balance = CorporateTimeService.instance().getYearlyVacationBalance(emp, date);
            CorporateTimeSheet ptoAccruedTS = dao.getPTOAccruedTimeSheet(emp);
            if (balance.compareTo(new BigDecimal("40.00")) >= 0) {
                ptoAccruedTS.setHours(new BigDecimal("40.00"));
            } else {
                ptoAccruedTS.setHours(balance);
            }
            dao.getEntityManager().merge(ptoAccruedTS);
        }
    }

    public static CorporateTimeAccuralService instance() {
        return SpringContext.getBean(CorporateTimeAccuralService.class);
    }
}
