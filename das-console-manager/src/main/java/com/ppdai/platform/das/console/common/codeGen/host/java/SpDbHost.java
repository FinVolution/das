package com.ppdai.platform.das.console.common.codeGen.host.java;

import com.ppdai.platform.das.console.common.codeGen.enums.DatabaseCategory;
import com.ppdai.platform.das.console.common.codeGen.utils.BeanGetter;
import com.ppdai.platform.das.console.common.codeGen.utils.DatabaseSetUtils;
import com.ppdai.platform.das.console.common.codeGen.utils.DbUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class SpDbHost {
    private DatabaseCategory databaseCategory;
    private String packageName;
    private Long dbSetId;
    private String dbSetName;
    // <SpHost spName, SpHost>
    private HashMap<String, SpHost> spHosts;

    public SpDbHost(Long dbSetId, String packageName) throws Exception {
        this.dbSetId = dbSetId;
        this.packageName = packageName;
        this.databaseCategory = DatabaseCategory.SqlServer;
        this.dbSetName = BeanGetter.getDaoOfDatabaseSet().getDatabaseSetById(dbSetId).getName();
        String dbType = DbUtils.getDbType(DatabaseSetUtils.getAlldbsIdByDbSetId(dbSetId));
        if (null != dbType && !dbType.equalsIgnoreCase("Microsoft SQL Server")) {
            this.databaseCategory = DatabaseCategory.MySql;
        }
        this.spHosts = new HashMap<>();
    }


    public DatabaseCategory getDatabaseCategory() {
        return this.databaseCategory;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public Long getDbSetId() {
        return dbSetId;
    }

    public String getDbSetName() {
        return dbSetName;
    }

    public Collection<SpHost> getSpHosts() {
        return this.spHosts.values();
    }

    public Set<String> getDaoImports() {
        Set<String> imports = new TreeSet<>();
        imports.add("com.ctrip.platform.dal.dao.*");
        imports.add("com.ctrip.platform.dal.dao.helper.*");
        imports.add(java.sql.SQLException.class.getName());
        imports.add(java.sql.Types.class.getName());
        imports.add(java.util.Map.class.getName());

        return imports;
    }

}
