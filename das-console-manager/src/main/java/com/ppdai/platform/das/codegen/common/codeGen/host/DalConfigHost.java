package com.ppdai.platform.das.codegen.common.codeGen.host;

import com.ppdai.platform.das.codegen.common.codeGen.utils.BeanGetter;
import com.ppdai.platform.das.codegen.dto.entry.das.DataBaseInfo;
import com.ppdai.platform.das.codegen.dto.entry.das.DatabaseSet;
import com.ppdai.platform.das.codegen.dto.entry.das.DatabaseSetEntry;
import com.ppdai.platform.das.codegen.enums.DataBaseEnum;

import java.sql.SQLException;
import java.util.*;

public class DalConfigHost {
    private String name;
    // <DatabaseSet ID, DatabaseSet>
    private Map<Long, DatabaseSet> databaseSet;
    // <DatabaseSet ID,<DatabaseSetEntry ID, DatabaseSetEntry>>
    private Map<Long, HashMap<Long, DatabaseSetEntry>> databaseSetEntries;
    // Key:DatabaseSet Entry ID
    private Map<Long, DatabaseSetEntry> databaseSetEntryMap;

    public DalConfigHost(String name) {
        this.name = name;
        this.databaseSet = new HashMap<>();
        this.databaseSetEntries = new HashMap<>();
        this.databaseSetEntryMap = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public Collection<DatabaseSet> getDatabaseSet() {
        return this.databaseSet.values();
    }

    public Collection<DatabaseSetEntry> getDatabaseSetEntry(int setId) {
        return this.databaseSetEntries.containsKey(setId) ? this.databaseSetEntries.get(setId).values() : null;
    }

    public Map<Long, DatabaseSetEntry> getDatabaseSetEntryMap() throws SQLException {
        Map<Long, DatabaseSetEntry> map = null;
        if (databaseSetEntryMap != null && databaseSetEntryMap.size() > 0) {
            map = new HashMap<>();
            List<Long> set = new ArrayList<>();
            for (Map.Entry<Long, DatabaseSetEntry> entry : databaseSetEntryMap.entrySet()) {
                set.add(entry.getValue().getDb_Id());
            }

            List<DataBaseInfo> dbs = BeanGetter.getDaoOfDalGroupDB().getAllDbsByIdss(set);

            if (dbs != null && dbs.size() > 0) {
                for (DataBaseInfo db : dbs) {
                    DatabaseSetEntry e = new DatabaseSetEntry();
                    e.setDb_Id(db.getId());
                    e.setConnectionString(db.getDbname()); //FIXME
                    e.setProviderName(DataBaseEnum.getDataBaseEnumByType(db.getDb_type()).getProvider()); //FIXME
                    e.setDbAddress(db.getDb_address());
                    e.setDbPort(db.getDb_port());
                    e.setUserName(db.getDb_user());
                    e.setPassword(db.getDb_password());
                    e.setDb_catalog(db.getDb_catalog()); //FIXME
                    //map.put(e.getConnectionString(), e); //FIXME
                    map.put(e.getDb_Id(), e);
                }
            }
        }

        return map;
    }

    public void addDatabaseSet(DatabaseSet set) {
        if (!this.databaseSet.containsKey(set.getId())) {
            this.databaseSet.put(set.getId(), set);
        }
    }

    public void addDatabaseSet(List<DatabaseSet> sets) {
        for (DatabaseSet databaseSet : sets) {
            this.addDatabaseSet(databaseSet);
        }
    }

    public void addDatabaseSetEntry(DatabaseSetEntry entry) {
        Long databaseSetId = entry.getDbset_id();
        Long databaseSetEntryId = entry.getId();

        if (!this.databaseSetEntries.containsKey(databaseSetId)) {
            this.databaseSetEntries.put(databaseSetId, new HashMap<Long, DatabaseSetEntry>());
        }
        if (!this.databaseSetEntries.get(databaseSetId).containsKey(databaseSetEntryId)) {
            this.databaseSetEntries.get(databaseSetId).put(databaseSetEntryId, entry);
        }
        if (!this.databaseSetEntryMap.containsKey(databaseSetEntryId)) {
            this.databaseSetEntryMap.put(databaseSetEntryId, entry);
        }
    }

    public void addDatabaseSetEntry(List<DatabaseSetEntry> entries) {
        for (DatabaseSetEntry databaseSetEntry : entries) {
            this.addDatabaseSetEntry(databaseSetEntry);
        }
    }
}
