package com.ppdai.das.core.enums;

public enum IPDomainStatus {
    IP(0), Domain(1);

    private int intVal;

    IPDomainStatus(int intVal) {
        this.intVal = intVal;
    }

    public int getIntVal() {
        return intVal;
    }
}
