package com.ppdai.platform.das.codegen.common.codeGen.generator.processor.prepareData;

import com.ppdai.platform.das.codegen.common.codeGen.generator.java.context.DbSetGenContext;
import com.ppdai.platform.das.codegen.dao.DataBaseSetEntryDao;
import com.ppdai.platform.das.codegen.dao.DatabaseSetDao;
import com.ppdai.platform.das.codegen.dto.view.DatabaseSetEntryView;
import com.ppdai.platform.das.codegen.dto.view.DatabaseSetView;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class DbSetDataProcessor {

    public void process(DbSetGenContext ctx) throws Exception {
        Long projectId = ctx.getProjectId();
        DataBaseSetEntryDao dataBaseSetEntryDao = new DataBaseSetEntryDao();
        List<DatabaseSetView> dbsets = new DatabaseSetDao().getAllDatabaseSetByProjectId(projectId);

        for (DatabaseSetView dbset : dbsets) {
            List<DatabaseSetEntryView> entries = dataBaseSetEntryDao.getDataBaseSetEntryListByDbSetId(dbset.getId());
            if (CollectionUtils.isEmpty(entries)) {
                continue;
            }
            dbset.setDatabaseSetEntryList(entries);
        }
        ctx.setDatabaseSets(dbsets);
    }

}
