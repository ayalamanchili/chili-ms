/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.Time;

import info.chili.reporting.ReportGenerator;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.Time.track.EmployeeTimeDataBulkImportProcessBean;
import info.yalamanchili.office.config.OfficeServiceConfiguration;
import info.yalamanchili.office.dao.profile.EmployeeDao;
import info.yalamanchili.office.dao.time.TimeRecordDao;
import info.yalamanchili.office.dto.time.AvantelTimeSummaryDto;
import info.yalamanchili.office.entity.profile.Branch;
import info.yalamanchili.office.entity.profile.Employee;
import info.yalamanchili.office.jms.MessagingService;
import info.yalamanchili.office.model.time.TimeRecord;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author prasanthi.p
 */
@Component
@Scope("prototype")
public class TimeRecordService {

    @Autowired
    protected TimeRecordDao timeRecordDao;
    @Autowired
    protected Mapper mapper;

    /**
     * this will generate a summary report of timerecords based on start and end
     * dates
     *
     * @param dto
     */
    public void getAllEmployeesSummaryReport(String email, TimeRecordDao.TimeRecordSearchDto dto) {
        List<AvantelTimeSummaryDto> res = new ArrayList();

        //
        //for loop of all india team employees (corporate employee whose branch is india)
        // find all time records get hours data and add and populate dto.
        for (Employee emp : EmployeeDao.instance().getEmployeesByType("Corporate Employee")) {
            AvantelTimeSummaryDto summaryRec = new AvantelTimeSummaryDto();
            if (Branch.Hyderabad.equals(emp.getBranch())) {

                BigDecimal cubicalHours = BigDecimal.ZERO;
                for (TimeRecord timeRecord : timeRecordDao.findAll(emp.getEmployeeId(), dto.getStartDate(), dto.getEndDate())) {
                    cubicalHours.add(timeRecord.getTags().get(EmployeeTimeDataBulkImportProcessBean.CUBICAL));
                }

                BigDecimal recptionHours = BigDecimal.ZERO;
                for (TimeRecord timeRecord : timeRecordDao.findAll(emp.getEmployeeId(), dto.getStartDate(), dto.getEndDate())) {
                    recptionHours.add(timeRecord.getTags().get(EmployeeTimeDataBulkImportProcessBean.RECEPTION));
                }
                BigDecimal secondndFloorHours = BigDecimal.ZERO;
                for (TimeRecord timeRecord : timeRecordDao.findAll(emp.getEmployeeId(), dto.getStartDate(), dto.getEndDate())) {
                    secondndFloorHours.add(timeRecord.getTags().get(EmployeeTimeDataBulkImportProcessBean.SECOND_FLOOR));
                }
                BigDecimal timeInHours = BigDecimal.ZERO;
                for (TimeRecord timeRecord : timeRecordDao.findAll(emp.getEmployeeId(), dto.getStartDate(), dto.getEndDate())) {
                    timeInHours.add(timeRecord.getTags().get(EmployeeTimeDataBulkImportProcessBean.TIME_IN));
                }
                summaryRec.setCubicalHours(cubicalHours);
                summaryRec.setReceptionHours(recptionHours);
                summaryRec.setSecondndFloorHours(secondndFloorHours);
                summaryRec.setEmployee(emp.getFirstName() + " " + emp.getLastName());
                summaryRec.setStartDate(dto.getStartDate());
                summaryRec.setEndDate(dto.getEndDate());
            }
            res.add(summaryRec);
        }
        MessagingService.instance().emailReport(ReportGenerator.generateExcelReport(res, "Attandance-Summary", OfficeServiceConfiguration.instance().getContentManagementLocationRoot()), email);
    }

    public static TimeRecordService instance() {
        return SpringContext.getBean(TimeRecordService.class);
    }
}
