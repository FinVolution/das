package com.ppdai.platform.das.console.common.codeGen;

import com.ppdai.platform.das.console.common.codeGen.generator.java.context.JavaCodeGenContext;

public interface DalProcessor {
    void process(JavaCodeGenContext context) throws Exception;
}
