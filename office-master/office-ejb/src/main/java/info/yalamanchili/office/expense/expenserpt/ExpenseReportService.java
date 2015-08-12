/**
 * ;
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.expense.expenserpt;

import info.chili.commons.DateUtils;
import info.chili.commons.pdf.PDFUtils;
import info.chili.commons.pdf.PdfDocumentData;
import info.chili.jpa.QueryUtils;
import info.chili.security.Signature;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.bpm.OfficeBPMService;
import info.yalamanchili.office.bpm.OfficeBPMTaskService;
import info.yalamanchili.office.config.OfficeSecurityConfiguration;
import info.yalamanchili.office.dao.expense.expenserpt.ExpenseCategoryDao;
import info.yalamanchili.office.dao.expense.expenserpt.ExpenseReportsDao;
import info.yalamanchili.office.dao.ext.CommentDao;
import info.yalamanchili.office.dao.profile.EmployeeDao;
import info.yalamanchili.office.dao.security.OfficeSecurityService;
import info.yalamanchili.office.entity.expense.expenserpt.ExpenseCategory;
import info.yalamanchili.office.entity.expense.expenserpt.ExpenseFormType;
import info.yalamanchili.office.entity.expense.expenserpt.ExpenseItem;
import info.yalamanchili.office.entity.expense.expenserpt.ExpenseReceipt;
import info.yalamanchili.office.entity.expense.expenserpt.ExpenseReport;
import info.yalamanchili.office.entity.expense.expenserpt.ExpenseReportStatus;
import info.yalamanchili.office.entity.ext.Comment;
import info.yalamanchili.office.entity.profile.Employee;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author prasanthi.p
 */
@Component
@Scope("request")
public class ExpenseReportService {

    @Autowired
    protected ExpenseReportsDao expenseReportsDao;

    public ExpenseReportSaveDto save(ExpenseReportSaveDto dto) {
        Mapper mapper = (Mapper) SpringContext.getBean("mapper");
        ExpenseReport entity = mapper.map(dto, ExpenseReport.class);
        if (entity.getId() == null) {
            entity.setEmployee(OfficeSecurityService.instance().getCurrentUser());
        }
        entity.setStatus(ExpenseReportStatus.PENDING_MANAGER_APPROVAL);
        entity.setSubmittedDate(new Date());
        ExpenseCategoryDao expenseCategoryDao = ExpenseCategoryDao.instance();
        for (ExpenseItem item : entity.getExpenseItems()) {
            if (dto.getExpenseFormType().equals(ExpenseFormType.GENERAL_EXPENSE)) {
                item.setCategory((ExpenseCategory) QueryUtils.findEntity(expenseCategoryDao.getEntityManager(), ExpenseCategory.class, "name", "General"));
            } else {
                item.setCategory(expenseCategoryDao.findById(item.getCategory().getId()));
            }
            item.setExpenseReport(entity);
        }
        for (ExpenseReceipt receipt : entity.getExpenseReceipts()) {
            receipt.setExpenseReport(entity);
        }
        if (entity.getId() == null) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("entity", entity);
            vars.put("currentEmployee", OfficeSecurityService.instance().getCurrentUser());
            entity = expenseReportsDao.save(entity);
            vars.put("entityId", entity.getId());
            entity.setBpmProcessId(OfficeBPMService.instance().startProcess("expense_report_process", vars));
        } else {
            //TODO   update entity
        }
        return mapper.map(entity, ExpenseReportSaveDto.class);
    }

    public ExpenseReportSaveDto read(Long id) {
        Mapper mapper = (Mapper) SpringContext.getBean("mapper");
        ExpenseReport e = expenseReportsDao.findById(id);
        return mapper.map(expenseReportsDao.findById(id), ExpenseReportSaveDto.class);
    }

    public void delete(Long id) {
        ExpenseReport entity = expenseReportsDao.findById(id);
        //TODO use processid
        OfficeBPMTaskService.instance().deleteAllTasksForProcessId(entity.getBpmProcessId(), true);
        expenseReportsDao.delete(id);
    }

    public Response getReport(Long id) {
        ExpenseReport entity = expenseReportsDao.findById(id);
        EmployeeDao employeeDao = EmployeeDao.instance();
        OfficeSecurityConfiguration securityConfiguration = OfficeSecurityConfiguration.instance();
        PdfDocumentData data = new PdfDocumentData();
        data.setKeyStoreName(securityConfiguration.getKeyStoreName());
        if (entity.getExpenseFormType() != null && entity.getExpenseFormType().name().equals("GENERAL_EXPENSE")) {
            data.setTemplateUrl("/templates/pdf/expense-report-template.pdf");
        } else {
            data.setTemplateUrl("/templates/pdf/travel-expenses-form.pdf");
        }
        data.setKeyStoreName(securityConfiguration.getKeyStoreName());
        Employee preparedBy = entity.getEmployee();
        Signature preparedBysignature = new Signature(preparedBy.getEmployeeId(), preparedBy.getEmployeeId(), securityConfiguration.getKeyStorePassword(), true, "employeeSignature", DateUtils.dateToCalendar(entity.getSubmittedDate()), employeeDao.getPrimaryEmail(preparedBy), null);
        data.getData().put("submittedDate", new SimpleDateFormat("MM-dd-yyyy").format(entity.getSubmittedDate()));
        data.getSignatures().add(preparedBysignature);

        String prepareByStr = preparedBy.getLastName() + ", " + preparedBy.getFirstName();
        data.getData().put("name", prepareByStr);
        if (preparedBy.getCompany() == null || preparedBy.getCompany().getName().equals("System Soft Technologies LLC")) {
            data.getData().put("systemSoftTechnologies-LLC", "true");
        } else if (preparedBy.getCompany().getName().equals("System Soft Technologies INC")) {
            data.getData().put("systemSoftTechnologies-INC", "true");
        }
        data.getData().put("department", entity.getDepartment());
        data.getData().put("location", entity.getLocation());
        data.getData().put("projectName", entity.getProjectName());
        data.getData().put("projectNumber", entity.getProjectNumber());
        data.getData().put("startDate", new SimpleDateFormat("MM-dd-yyyy").format(entity.getStartDate()));
        data.getData().put("endDate", new SimpleDateFormat("MM-dd-yyyy").format(entity.getEndDate()));
        if (entity.getExpenseReimbursePaymentMode() != null) {
            switch (entity.getExpenseReimbursePaymentMode()) {
                case ACH:
                    data.getData().put("achType", "true");
                    break;
                case MAIL_CHECK:
                    data.getData().put("mailcheckType", "true");
                    break;
            }
        }

// Ecpense Item General 
        Integer i = 1;
        Integer p = 1;
        BigDecimal itemTotal = new BigDecimal(0);
        BigDecimal itemAmex = new BigDecimal(0);
        BigDecimal itemPersonal = new BigDecimal(0);
        BigDecimal amountDue = new BigDecimal(0);
        Integer a = 1;
        for (ExpenseItem item : entity.getExpenseItems()) {
            if ((entity.getExpenseFormType()) != null && entity.getExpenseFormType().name().equals("GENERAL_EXPENSE")) {
                data.getData().put("sl" + i, i.toString());
                data.getData().put("description" + i, item.getDescription());
                data.getData().put("purpose" + i, item.getPurpose());
                data.getData().put("remark" + i, item.getRemark());
                data.getData().put("category" + i, item.getCategory().getName());
                data.getData().put("itemStartDate" + i, new SimpleDateFormat("MM-dd-yyyy").format(item.getExpenseDate()));
                data.getData().put("amount" + i, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                itemTotal = itemTotal.add(item.getAmount());
                i++;
            } else {
                // Expanse Item Personal 
                if (item.getExpensePaymentMode() != null && item.getExpensePaymentMode().name().equals("PERSONAL_CARD")) {
                    if (item.getCategory() != null) {
                        if (item.getCategory().getName().equals("AirFare")) {
                            data.getData().put("Air Fare", "true");
                            data.getData().put("air-category" + p, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                        }
                        if (item.getCategory().getName().equals("Hotel")) {
                            data.getData().put("Hotel", "true");
                            data.getData().put("hotel-category" + p, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                        }
                        if (item.getCategory().getName().equals("Auto")) {
                            data.getData().put("Auto", "true");
                            data.getData().put("auto-category" + p, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                        }
                        if (item.getCategory().getName().equals("ClientEntertainment")) {
                            data.getData().put("ClientEntertainment", "true");
                            data.getData().put("client-category" + p, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                        }
                        if (item.getCategory().getName().equals("Miscellaneous")) {
                            data.getData().put("Miscellaneous", "true");
                            data.getData().put("mis-category" + p, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                        }
                        if (item.getCategory().getName().equals("Personal Auto")) {
                            data.getData().put("Personal Auto", "true");
                            data.getData().put("miles" + p, item.getExpenseMiles().setScale(2, BigDecimal.ROUND_UP).toString());
                            data.getData().put("miles-amount" + p, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                        }
                    }
                    data.getData().put("p-purpose" + p, item.getPurpose());
                    data.getData().put("p-itemStartDate" + p, new SimpleDateFormat("MM-dd-yyyy").format(item.getExpenseDate()));
                    data.getData().put("p-amount" + p, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                    itemPersonal = itemPersonal.add(item.getAmount());
                    p++;
                } else {
                    //Expanse Item Amex
                    if (item.getCategory().getName().equals("AirFare")) {
                        data.getData().put("Air Fare", "true");
                        data.getData().put("aair-category" + a, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                    }
                    if (item.getCategory().getName().equals("Hotel")) {
                        data.getData().put("Hotel", "true");
                        data.getData().put("ahotel-category" + a, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                    }
                    if (item.getCategory().getName().equals("Auto")) {
                        data.getData().put("Auto", "true");
                        data.getData().put("aauto-category" + a, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                    }
                    if (item.getCategory().getName().equals("ClientEntertainment")) {
                        data.getData().put("ClientEntertainment", "true");
                        data.getData().put("aclient-category" + a, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                    }
                    if (item.getCategory().getName().equals("Miscellaneous")) {
                        data.getData().put("Miscellaneous", "true");
                        data.getData().put("amis-category" + a, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                    }
                    data.getData().put("a-purpose" + a, item.getPurpose());
                    data.getData().put("a-itemStartDate" + a, new SimpleDateFormat("MM-dd-yyyy").format(item.getExpenseDate()));
                    data.getData().put("a-amount" + a, item.getAmount().setScale(2, BigDecimal.ROUND_UP).toString());
                    itemAmex = itemAmex.add(item.getAmount());
                    a++;
                }
            }
        }

        data.getData().put("p-itemTotal", itemPersonal.setScale(2, BigDecimal.ROUND_UP).toString());
        data.getData().put("a-itemTotal", itemAmex.setScale(2, BigDecimal.ROUND_UP).toString());
        data.getData().put("itemTotal", itemTotal.setScale(2, BigDecimal.ROUND_UP).toString());
        amountDue = itemPersonal.add(itemAmex);
        data.getData().put("amount-due", amountDue.setScale(2, BigDecimal.ROUND_UP).toString());
        //Comment
        List<Comment> cmnts = CommentDao.instance().findAll(entity.getId(), entity.getClass().getCanonicalName());
        String allComment = "";
        for (Comment comment : cmnts) {
            allComment = allComment + ". " + comment.getComment();
        }

        if (entity.getApprovedByCEO() != null) {
            Employee ceo = employeeDao.findEmployeWithEmpId(entity.getApprovedByCEO());
            if (ceo != null) {
                Signature approvedsignature = new Signature(ceo.getEmployeeId(), ceo.getEmployeeId(), securityConfiguration.getKeyStorePassword(), true, "ceoApprovalBy", DateUtils.dateToCalendar(entity.getApprovedByCEODate()), employeeDao.getPrimaryEmail(ceo), null);
                data.getSignatures().add(approvedsignature);
                data.getData().put("approvedByCEODate", new SimpleDateFormat("MM-dd-yyyy").format(entity.getApprovedByCEODate()));
            }
        }

        if ((entity.getExpenseFormType()) != null && entity.getExpenseFormType().name().equals("TRAVEL_EXPENSE")) {
            if (entity.getApprovedByAccountsDept() != null) {
                Employee ceo = employeeDao.findEmployeWithEmpId(entity.getApprovedByAccountsDept());
                if (ceo != null) {
                    Signature approvedsignature = new Signature(ceo.getEmployeeId(), ceo.getEmployeeId(), securityConfiguration.getKeyStorePassword(), true, "payrollApprovalBy", DateUtils.dateToCalendar(entity.getApprovedByAccountsDeptDate()), employeeDao.getPrimaryEmail(ceo), null);
                    data.getSignatures().add(approvedsignature);
                    data.getData().put("approvedByPayrollDate", new SimpleDateFormat("MM-dd-yyyy").format(entity.getApprovedByAccountsDeptDate()));
                }
            }
        }
        if ((entity.getExpenseFormType()) != null && entity.getExpenseFormType().name().equals("GENERAL_EXPENSE")) {
            if (entity.getApprovedByAccountsDept() != null) {
                Employee name = employeeDao.findEmployeWithEmpId(entity.getApprovedByAccountsDept());
                if (name != null) {
                    String approvedByPayroll = entity.getApprovedByAccountsDept();
                    data.getData().put("namePayroll", approvedByPayroll);
                    data.getData().put("PayrollDate", new SimpleDateFormat("MM-dd-yyyy").format(entity.getApprovedByAccountsDeptDate()));
                }
            }

        }
        data.getData().put("comment", allComment);
        byte[] pdf = PDFUtils.generatePdf(data);
        return Response.ok(pdf)
                .header("content-disposition", "filename = Expense-Report.pdf")
                .header("Content-Length", pdf)
                .build();
    }

    public static ExpenseReportService instance() {
        return SpringContext.getBean(ExpenseReportService.class);
    }
}
