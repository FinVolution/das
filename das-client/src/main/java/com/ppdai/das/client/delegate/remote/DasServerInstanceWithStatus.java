package com.ppdai.das.client.delegate.remote;

import com.ppdai.das.core.configure.DasServerInstance;
import com.ppdai.das.service.DasServerStatus;

import java.util.Objects;


public class DasServerInstanceWithStatus extends DasServerInstance {

    private DasServerStatus status;
    private boolean checkType = false;

    public DasServerInstanceWithStatus(String address, int port) {
        super(address, port);
    }

    public DasServerInstanceWithStatus(String address, int port, DasServerStatus status) {
        super(address, port);
        this.status = status;
    }

    public DasServerStatus getStatus() {
        return status;
    }

    public DasServerInstanceWithStatus setCheckType() {
        this.checkType = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DasServerInstanceWithStatus that = (DasServerInstanceWithStatus) o;
        return checkType == that.checkType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), checkType);
    }

    static DasServerInstanceWithStatus asKey(String address, int port){
        return new DasServerInstanceWithStatus(address, port);
    }
}

