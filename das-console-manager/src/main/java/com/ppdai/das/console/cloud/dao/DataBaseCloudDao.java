package com.ppdai.das.console.cloud.dao;

import com.ppdai.das.console.cloud.dto.view.DataBaseView;
import com.ppdai.das.console.dao.base.BaseDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class DataBaseCloudDao extends BaseDao {

    public List<DataBaseView> getAllDbByAppId(String appid) throws SQLException {
        String sql = "select t3.group_name, t1.db_name, t1.comment,  t1.db_address, t1.db_port, t1.db_user, t1.db_catalog, if(t1.db_type = 1, 'mySql', 'SqlServer') db_type, t1.insert_time  from alldbs t1\n" +
                "inner join dal_group t3 on t3.id = t1.dal_group_id " +
                "inner join (\n" +
                "select distinct a1.db_Id from databasesetentry a1\n" +
                "inner join databaseset a2 on a1.dbset_id = a2.id\n" +
                "inner join project_dbset_relation a3 on a3.dbset_id = a2.id\n" +
                "inner join project a4 on a4.id = project_id\n" +
                "where a4.app_id = '" + appid +
                "') t2 on t1.id= t2.db_Id";
        return this.queryBySql(sql, DataBaseView.class);
    }

}
