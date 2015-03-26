/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.bulkimport;

import info.chili.service.jrs.exception.ServiceException;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.dao.bulkimport.BulkImportDao;
import info.yalamanchili.office.entity.bulkimport.BulkImport;
import info.yalamanchili.office.entity.bulkimport.BulkImportMessage;
import info.yalamanchili.office.entity.bulkimport.BulkImportMessageType;
import info.yalamanchili.office.entity.bulkimport.BulkImportStatus;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 *
 * @author ayalamanchili
 */
@Component
public class BulkImportRevert implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        BulkImport bulkImport = (BulkImport) execution.getVariable("bulkImport");
        BulkImportProcess adapter = (BulkImportProcess) SpringContext.getBean(bulkImport.getAdapter());
        try {
            bulkImport = adapter.revert(bulkImport);
        } catch (Exception e) {
            BulkImportMessage msg = new BulkImportMessage();
            msg.setCode("ERROR");
            msg.setDescription(e.getLocalizedMessage());
            msg.setMessageType(BulkImportMessageType.ERROR);
            bulkImport.addMessage(msg);
            bulkImport.setStatus(BulkImportStatus.ERROR);
            BulkImportDao.instance().save(bulkImport);
            BulkImportDao.instance().getEntityManager().flush();
            throw new ServiceException(ServiceException.StatusCode.INTERNAL_SYSTEM_ERROR, "SYSTEM", "bulkimport.revert.error", e.getLocalizedMessage());
        }
        bulkImport.setStatus(BulkImportStatus.REJECTED);
        execution.setVariable("bulkImport", bulkImport);
    }
}
