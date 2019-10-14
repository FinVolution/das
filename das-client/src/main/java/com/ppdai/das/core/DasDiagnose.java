package com.ppdai.das.core;

import java.util.*;

/**
 * store the diagnose information which is usually used for developer's inspection to sql execution
 *
 * @author huangyinhuang
 * @date 8/1/2018
 */
public class DasDiagnose {

    private String name;
    private Integer spaceLevel;
    private DasDiagnose parentDiagnose = null;
    private List<DasDiagnose> childDiagnoses = new LinkedList<>();
    private Map<String, Object> diagnoseInfoMap = new LinkedHashMap<>();

    public DasDiagnose(String name, Integer spaceLevel) {
        if (spaceLevel < 0) {
            spaceLevel = 0;
        }

        this.name = name;
        this.spaceLevel = spaceLevel;
    }

    public String getName() {
        return name;
    }

    public DasDiagnose getParentDiagnose() {
        return parentDiagnose;
    }

    public void setParentDiagnose(DasDiagnose parentDiagnose) {
        this.parentDiagnose = parentDiagnose;
    }

    public DasDiagnose spawn(String name) {
        DasDiagnose subDiagnose = new DasDiagnose(name, spaceLevel + 1);
        this.childDiagnoses.add(subDiagnose);
        subDiagnose.setParentDiagnose(this);
        return subDiagnose;
    }

    public synchronized void append(String key, Object info) {
        this.diagnoseInfoMap.put(key, info);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String spacePrefix = spaceLevel == 0 ? "" : String.format("%" + spaceLevel * 4 + "s", "");

        for (Map.Entry<String, Object> entry : diagnoseInfoMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value != null) {
                builder.append(String.format(spacePrefix + "%s = %s \r\n", key, value.toString()));
            } else {
                builder.append(String.format(spacePrefix + "%s = \r\n", key));
            }
        }

        for (DasDiagnose subDiagnose : childDiagnoses) {
            builder.append(subDiagnose.toString());
        }

        return builder.toString();
    }

    public Integer getSpaceLevel() {
        return spaceLevel;
    }

    public List<DasDiagnose> getChildDiagnoses() {
        return childDiagnoses;
    }

    public Map<String, Object> getDiagnoseInfoMap() {
        return diagnoseInfoMap;
    }
}
