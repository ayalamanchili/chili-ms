/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.bulkimport;

import info.chili.service.jrs.types.Entry;
import info.chili.spring.SpringContext;
import info.yalamanchili.office.bpm.time.BPMTimeService;
import info.yalamanchili.office.dao.bulkimport.BulkImportDao;
import info.yalamanchili.office.entity.bulkimport.BulkImport;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ayalamanchili
 */
@Component
@Scope("request")
public class BulkImportService {

    @Autowired
    protected BulkImportDao bulkImportDao;
    @PersistenceContext
    protected EntityManager em;

    public String saveBulkUpload(BulkImport entity) {
        BulkImport bi = (BulkImport) bulkImportDao.save(entity);
        BPMTimeService.instance().startBulkImportProcess(bi);
        return bi.getId().toString();
    }

    public List<Entry> getBulkImportAdapters() {
        List<Entry> res = new ArrayList<Entry>();
        Integer i = 0;
        for (String name : SpringContext.getApplicationContext().getBeanNamesForType(BulkImportProcess.class)) {
            Entry e = new Entry();
            e.setId(i.toString());
//            if (name.contains("BulkImportProcessBean")) {
//                name = name.replace("BulkImportProcessBean", "");
//            }
            e.setValue(name);
            res.add(e);
            i++;
        }
        return res;
    }
}
