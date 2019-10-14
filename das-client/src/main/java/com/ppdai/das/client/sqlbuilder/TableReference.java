package com.ppdai.das.client.sqlbuilder;

import com.ppdai.das.client.Segment;

public interface TableReference extends Segment {
    String getName();

    String getShardId();

    String getShardValue();
}
