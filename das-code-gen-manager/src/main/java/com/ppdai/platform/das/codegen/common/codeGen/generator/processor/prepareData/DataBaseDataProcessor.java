package com.ppdai.platform.das.codegen.common.codeGen.generator.processor.prepareData;

import com.ppdai.platform.das.codegen.common.codeGen.generator.java.context.DataBaseGenContext;
import com.ppdai.platform.das.codegen.dao.DataBaseDao;
import com.ppdai.platform.das.codegen.dto.entry.das.DataBaseInfo;

import java.util.List;

public class DataBaseDataProcessor {

    public void process(DataBaseGenContext ctx) throws Exception {
        Long projectId = ctx.getProjectId();
        List<DataBaseInfo> dbs = new DataBaseDao().getAllDbByProjectId(projectId);
        ctx.setDbs(dbs);
    }

}
