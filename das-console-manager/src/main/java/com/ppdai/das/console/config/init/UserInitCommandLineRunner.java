package com.ppdai.das.console.config.init;

import com.ppdai.das.console.api.AdditionalConfiguration;
import com.ppdai.das.console.api.DefaultConfiguration;
import com.ppdai.das.console.api.EncdecConfiguration;
import com.ppdai.das.console.common.utils.DasEnv;
import com.ppdai.das.console.common.utils.ResourceUtil;
import com.ppdai.das.console.constant.Consts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 初始化检测配置文件
 */
@Slf4j
@Component
@Order(value = 1)
public class UserInitCommandLineRunner implements ApplicationRunner {

    @Autowired
    private Consts consts;

    @Autowired
    public DefaultConfiguration defaultConfiguration;

    @Autowired
    public AdditionalConfiguration additionalConfiguration;

    @Autowired
    public EncdecConfiguration encdecConfiguration;

    private String pwd = "123";

    @Override
    public void run(ApplicationArguments args) {
        DasEnv.setDefaultConfiguration(defaultConfiguration);
        DasEnv.setAdditionalConfiguration(additionalConfiguration);
        DasEnv.setEncdecConfiguration(encdecConfiguration);
        //log.info("---==--- 服务启动后，当前环境 ---==---> " + consts.springActive);
        log.info("---==--- 服务启动后，当前路径 ---==---> " + ResourceUtil.getSingleInstance().getDbXmlPath());
        log.info("---==--- 服务启动后，文件路径 ---==---> " + consts.codeConsoleilePath);
        //log.info("---==--- 服务启动后，加解密组件 ---==---> 加密前:" + pwd + " 加密后: " + DasEnv.encdecConfiguration.encrypt(pwd));
    }
}
