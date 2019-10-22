package com.ppdai.platform.das.console.common.codeGen.generator.processor.prepareData;

import com.ppdai.platform.das.console.common.codeGen.generator.java.context.DataBaseGenContext;
import com.ppdai.platform.das.console.dao.DataBaseDao;
import com.ppdai.platform.das.console.dto.entry.das.DataBaseInfo;

import java.util.List;

public class DataBaseDataProcessor {

    public void process(DataBaseGenContext ctx) throws Exception {
        Long projectId = ctx.getProjectId();
        List<DataBaseInfo> dbs = new DataBaseDao().getAllDbByProjectId(projectId);
        ctx.setDbs(dbs);
    }

}
