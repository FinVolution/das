package com.ppdai.das.client.delegate.remote;

/**
 * It can be implemented as Random, RoundRobin, etc
 */
public interface Rule {

   void setServerSelector(ServerSelector serverSelector);

    DasServerInstanceWithStatus chooseServer();
}
