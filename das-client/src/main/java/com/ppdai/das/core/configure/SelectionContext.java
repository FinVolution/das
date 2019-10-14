package com.ppdai.das.core.configure;

import java.util.List;

import com.ppdai.das.core.DalHintEnum;
import com.ppdai.das.client.Hints;
import com.ppdai.das.core.client.DalHA;

public class SelectionContext {
    private String appId;
    private String logicDbName;
    private Hints hints;
    private DalHA ha;
    private String designatedDatabase;
    private String shard;
    private boolean masterOnly;
    private boolean select;
    private List<DataBase> masters;
    private List<DataBase> slaves;
    
    public SelectionContext(String appId, String logicDbName, Hints hints, String shard,
            boolean isMaster,
            boolean isSelect) {
        this.appId = appId;
        this.logicDbName = logicDbName;
        this.hints = hints;
        this.shard = shard;
        if(hints != null) {
            this.ha = hints.getHA();
            this.designatedDatabase = hints.getString(DalHintEnum.designatedDatabase);
        }
        
        this.masterOnly = isMaster;
        this.select = isSelect;
    }

    public String getAppId() {
        return appId;
    }
    
    public String getLogicDbName() {
        return logicDbName;
    }

    public String getDesignatedDatabase() {
        return designatedDatabase;
    }

    public DalHA getHa() {
        return ha;
    }
    public List<DataBase> getMasters() {
        return masters;
    }
    
    public List<DataBase> getSlaves() {
        return slaves;
    }
    
    public Hints getHints() {
        return hints;
    }
    
    public String getShard() {
        return shard;
    }

    public boolean isMasterOnly() {
        return masterOnly;
    }

    public boolean isSelect() {
        return select;
    }

    public void setMasters(List<DataBase> masters) {
        this.masters = masters;
    }

    public void setSlaves(List<DataBase> slaves) {
        this.slaves = slaves;
    }
}
