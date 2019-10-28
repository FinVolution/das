package com.ppdai.das.console.config;

import com.ppdai.das.console.api.*;
import com.ppdai.das.console.api.impl.*;
import com.ppdai.das.console.api.impl.SecurityManager;
import com.ppdai.das.console.openapi.ConfigProvider;
import com.ppdai.das.console.openapi.LoginProvider;
import com.ppdai.das.console.openapi.impl.ConfigProviderDefaultImpl;
import com.ppdai.das.console.openapi.impl.LoginProviderDefaultImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provide default API beans, if no specific beans are found in Spring context.
 * Users could implement following beans to fulfill their requirement.
 */
@Configuration
public class APIBeanConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AdditionalConfiguration additionalConfiguration() {
        return new AdditionalManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigLoader configLoader() {
        return new FileConfigLoader();
    }

    @Bean
    @ConditionalOnMissingBean
    public DataBaseConfiguration dataBaseConfiguration() {
        return new DataBaseManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public DataSearchConfiguration dataSearchConfiguration() {
        return new DataSearchManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public DbSetConfiguration dbSetConfiguration() {
        return new DbSetManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultConfiguration defaultConfiguration() {
        return new DefaultManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public EncdecConfiguration encdecConfiguration() {
        return new EncdecManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public MenuItemConfiguration menuItemConfiguration() {
        return new MenuItemManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProjectConfiguration projectConfiguration() {
        return new ProjectManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityConfiguration securityConfiguration() {
        return new SecurityManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public SyncConfiguration syncConfiguration() {
        return new SyncManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserConfiguration userConfiguration() {
        return new UserMananger();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigProvider configProvider() {
        return new ConfigProviderDefaultImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public LoginProvider loginProvider() {
        return new LoginProviderDefaultImpl();
    }
}
