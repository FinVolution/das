package com.ppdai.das.client.delegate.remote;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomRule implements Rule {

    private ServerSelector serverSelector;

    private final Random rand = new Random();

    private int retryTimes = 3;

    private long waitTime = 10L;

    @Override
    public void setServerSelector(ServerSelector serverSelector) {
        this.serverSelector = serverSelector;
    }

    @Override
    public DasServerInstanceWithStatus chooseServer() {
        int time = 0;
        while(time++ < retryTimes) {
            List<DasServerInstanceWithStatus> allList = serverSelector.getAllServers();
            List<DasServerInstanceWithStatus> candidates = allList.stream().filter( s -> ServerSelector.isServerAvailable(s) ).collect(Collectors.toList());
            if (candidates.isEmpty()) {
                continue;
            } else {
                int index = rand.nextInt(candidates.size());
                return candidates.get(index);
            }
        }
        return null;
    }
}
