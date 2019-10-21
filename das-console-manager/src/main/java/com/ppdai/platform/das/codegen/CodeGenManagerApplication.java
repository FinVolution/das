package com.ppdai.platform.das.codegen;

import com.ppdai.das.core.configure.DalConfigureFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.nio.charset.Charset;

/**
 * @author wangliang
 * @date 2018/9/20
 */
@EnableWebMvc
@SpringBootApplication
@ServletComponentScan
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class CodeGenManagerApplication extends SpringBootServletInitializer {

    @Bean
    public HttpMessageConverter<String> responseBodyConverter() {
        StringHttpMessageConverter converter = new StringHttpMessageConverter(
                Charset.forName("UTF-8"));
        return converter;
    }

    /**
     * 如果要发布到自己的Tomcat中的时候，需要继承SpringBootServletInitializer类，并且增加如下的configure方法。
     * 如果不发布到自己的Tomcat中的时候，就无需上述的步骤
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CodeGenManagerApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(CodeGenManagerApplication.class, args);
    }
}