package com.ppdai.das.client.sqlbuilder;

import java.util.List;

import com.ppdai.das.client.ParameterDefinition;

public interface ParameterDefinitionProvider {
    /**
     * @return the parameter definition with direction, name and type
     */
    List<ParameterDefinition> buildDefinitions();
}
