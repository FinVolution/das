package com.ppdai.das.console.dao.base;

import com.ppdai.das.client.DasClient;
import com.ppdai.das.client.Parameter;
import com.ppdai.das.client.SqlBuilder;
import com.ppdai.das.console.api.ConfigLoader;
import com.ppdai.das.console.api.impl.FileConfigLoader;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class BaseDao {

    @Setter
    public DasClient dasClient;

    @Autowired
    private ConfigLoader configLoader;

    public BaseDao(){
        this.configLoader = new FileConfigLoader();
    }

    public DasClient getDasClient() {
        if (dasClient == null) {
            this.dasClient = DasClientGetter.getClient(configLoader.isLoaderFile());
        }
        return this.dasClient;
    }

    public Long getCount(String sql, Parameter... parameters) throws SQLException {
        SqlBuilder builder = new SqlBuilder().appendTemplate(sql, parameters).intoObject();
        return (Long) getDasClient().queryObject(builder);
    }

    public int updataBysql(String sql, Parameter... parameters) throws SQLException {
        SqlBuilder builder = new SqlBuilder().appendTemplate(sql, parameters).intoObject();
        return getDasClient().update(builder);
    }

    public <T> List<T> queryBySql(String sql, Class<T> c, Parameter... parameters) throws SQLException {
        SqlBuilder builder = new SqlBuilder()
                .appendTemplate(sql, parameters)
                .into(c);
        return getDasClient().query(builder);
    }

}
