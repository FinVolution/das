package com.ppdai.das.client;

import com.ppdai.das.client.sqlbuilder.BuilderContext;

public interface Segment {
    String build(BuilderContext context);
}