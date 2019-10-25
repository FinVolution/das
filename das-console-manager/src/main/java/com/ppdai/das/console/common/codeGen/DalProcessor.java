package com.ppdai.das.console.common.codeGen;

import com.ppdai.das.console.common.codeGen.generator.java.context.JavaCodeGenContext;

public interface DalProcessor {
    void process(JavaCodeGenContext context) throws Exception;
}
