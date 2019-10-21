## 二、项目一览 - DalGroupProjectResource

### 1、获取项目树
       url: `rest/projectview`
       method:`GET`
###### 参数:
       root：true
###### reponse:
        [
            {
                "app_id":1000000169,
                "children":true,
                "create_time":"2018-08-06T15:25:46+08:00",
                "create_user_no":"1",
                "group_comment":"巴安安",
                "group_name":"1",
                "icon":"glyphicon glyphicon-folder-open",
                "id":1,
                "text":"1"
            },
            {
                "app_id":1000002234,
                "children":true,
                "create_time":"2018-08-08T15:10:55+08:00",
                "create_user_no":"1",
                "group_comment":"demo",
                "group_name":"das_team",
                "icon":"glyphicon glyphicon-folder-open",
                "id":2,
                "text":"das_team"
            }
        ]
        
### 2、获取子目录
       url: `rest/projectview/groupprojects`
       method:`GET`
###### 参数:
       groupId=1
###### reponse:
        [
            {
                "children":false,
                "dal_config_name":"test-hyh",
                "dal_group_id":2,
                "icon":"glyphicon glyphicon-tasks",
                "id":10001,
                "name":"test-hyh",
                "namespace":"test-hyh",
                "str_update_time":"2018-08-08 15:16:01",
                "text":"test-hyh",
                "update_time":"2018-08-08T15:16:01+08:00",
                "update_user_no":"huangyinhuang(008543)"
            }
        ]
        
### 3、根据项目id取task列表
       url: `rest/task`
       method:`GET`
###### 参数:
       project_id：10000
###### reponse:
            {
                "autoTasks":[
            
                ],
                "sqlTasks":[
                    {
                        "allInOneName":"ppdai_das_mysql_1",
                        "approveMsg":"",
                        "approved":2,
                        "class_name":"PersonSelectInfo",
                        "comment":"自定义查询",
                        "crud_type":"select",
                        "databaseSetName":"MySqlSimple",
                        "generated":true,
                        "hints":"",
                        "id":2,
                        "method_name":"PersonSelectInfo",
                        "pagination":false,
                        "parameters":"",
                        "pojoType":"EntityType",
                        "pojo_name":"PersonSelectInfo",
                        "project_id":10001,
                        "scalarType":"List",
                        "sql_content":"select 
                                        t.Name, 
                                        t.CountryID, 
                                        t.DataChange_LastTime, 
                                        t.ProvinceID 
                                        from person t; ",
                        "sql_style":"java",
                        "str_approved":"通过",
                        "str_update_time":"2018-08-08 17:48:16",
                        "update_time":"2018-08-08T17:48:16+08:00",
                        "update_user_no":"wangliang(010892)",
                        "version":2
                    }
                ],
                "tableViewSpTasks":[
                    {
                        "allInOneName":"ppdai_das_mysql_1",
                        "api_list":"selectAllCreateMethodAPIChk,selectAllRetrieveMethodAPIChk,selectAllUpdateMethodAPIChk,selectAllDeleteMethodAPIChk",
                        "approveMsg":"",
                        "approved":2,
                        "comment":"",
                        "cud_by_sp":false,
                        "databaseSetName":"MySqlSimple",
                        "generated":true,
                        "id":1,
                        "pagination":true,
                        "prefix":"",
                        "project_id":10001,
                        "sp_names":"",
                        "sql_style":"java",
                        "str_approved":"通过",
                        "str_update_time":"2018-08-08 15:47:33",
                        "suffix":"",
                        "table_names":"person,person_0,person_1,person_2,person_3",
                        "update_time":"2018-08-08T15:47:33+08:00",
                        "update_user_no":"huangyinhuang(008543)",
                        "version":4,
                        "view_names":""
                    }
                ]
            }