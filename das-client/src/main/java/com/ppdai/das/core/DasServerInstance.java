package com.ppdai.das.core;

import java.util.Objects;

public class DasServerInstance {
    private String address;
    private int port;

    public DasServerInstance(String address, int port) {
        super();
        this.address = address;
        this.port = port;
    }
    
    public String getAddress() {
        return address;
    }
    
    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "DasServerInstance{" +
                "address='" + address + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DasServerInstance that = (DasServerInstance) o;
        return port == that.port &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {

        return Objects.hash(address, port);
    }
}
