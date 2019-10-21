## 一、代码生成器 ProjectResource


### 1、获取用户的所有组 - 重复
     url: `/user/userGroups`
     method:`GET`  
###### 参数:
        {
            root=true
        }
###### reponse:
    [{"app_id":1000000169,"children":true,"create_time":"2018-08-06T15:25:46+08:00","create_user_no":"1","group_comment":"巴安安","group_name":"1","icon":"glyphicon glyphicon-folder-open","id":1,"text":"1"},{"app_id":1000002234,"children":true,"create_time":"2018-08-08T15:10:55+08:00","create_user_no":"1","group_comment":"demo","group_name":"das_team","icon":"glyphicon glyphicon-folder-open","id":2,"text":"das_team"},{"app_id":7000000169,"children":true,"create_time":"2018-08-20T11:26:04+08:00","create_user_no":"1","group_comment":"new_team","group_name":"new_team","icon":"glyphicon glyphicon-folder-open","id":3,"text":"new_team"}]

### 2、根据组ID获取组对应的项目列表
     url: `/rest/project/groupprojects`
     method:`GET`  
###### 参数:
        {
            groupId：2
        }
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
  
### 3、根据组id取组信息
     url: `/rest/group/onegroup`
     method:`POST`  
###### 参数:
        {
              id:2
        }
###### reponse:
        {
            "app_id":1000002234,
            "children":false,
            "create_time":"2018-08-08T15:10:55+08:00",
            "create_user_no":"1",
            "group_comment":"demo",
            "group_name":"das_team",
            "id":2
        }
        
### 4、根据项目id取项目对应的task信息列表
     url: `/rest/task`
     method:`POST`  
###### 参数:
        {
            project_id:10000
        }
###### reponse:    
        {
            "autoTasks":[
        
            ],
            "sqlTasks":[
        
            ],
            "tableViewSpTasks":[
                {
                    "allInOneName":"ppdai_das_mysql_1",
                    "api_list":"selectAllCreateMethodAPIChk,selectAllRetrieveMethodAPIChk,selectAllUpdateMethodAPIChk,selectAllDeleteMethodAPIChk",
                    "approveMsg":"",
                    "approved":2,
                    "comment":"cs",
                    "cud_by_sp":false,
                    "databaseSetName":"MySqlSimple",
                    "generated":true,
                    "id":4,
                    "pagination":true,
                    "prefix":"",
                    "project_id":10001,
                    "sp_names":"",
                    "sql_style":"java",
                    "str_approved":"通过",
                    "str_update_time":"2018-08-15 11:14:44",
                    "suffix":"",
                    "table_names":"alldbs,dal_group,person_2",
                    "update_time":"2018-08-15T11:14:44+08:00",
                    "update_user_no":"wangliang(010892)",
                    "version":4,
                    "view_names":""
                }
            ]
        } 

   
### 2-1、添加DAO - 权限检验
       url: `/rest/project/projectPermisionCheck`
       method:`POST`
###### reponse:
      {"code":"OK"}
      
### 2-2、添加DAO - 根据项目id取-准备弃用
     url: `/rest/task/getLanguageType?project_id=10000`
     method:`POST`  
###### 参数:
        {
              project_id:项目id
        }
###### reponse:
    {"code":"OK","infoSaec":"java"}
    
### 2-3、添加DAO - 根据组id取组对应的逻辑数据库
     url: `/rest/groupdbset/getDbset`
     method:`get`
###### 参数:
        {
              daoFlag:true
              groupId：2
        }
###### reponse:
     [
         {
             "groupId":2,
             "id":6,
             "name":"MySqlSimple",
             "provider":"mySqlProvider",
             "shardingStrategy":"",
             "str_update_time":"2018-08-08 15:23:01",
             "update_time":"2018-08-08T15:23:01+08:00",
             "update_user_no":"huangyinhuang(008543)"
         },
         {
             "groupId":2,
             "id":7,
             "name":"MySqlSimpleDbShard",
             "provider":"mySqlProvider",
             "shardingStrategy":"class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=CountryID;mod=2;",
             "str_update_time":"2018-08-08 15:40:24",
             "update_time":"2018-08-08T15:40:24+08:00",
             "update_user_no":"huangyinhuang(008543)"
         }
     ]
    
    
### 2-4、生成向导 - 根据逻辑数据库名获取物理库表名列表 
       url: `rest/db/table_sps`
       method:`get`
###### 参数:
        {
              db_name:MySqlSimpleDbTableShard
        }
###### reponse:
      {"code":"OK","infoSaec":"{\"dbType\":\"MySQL\",\"tables\":[\"person\",\"person_0\",\"person_1\",\"person_2\",\"person_3\"],\"sps\":[],\"views\":[]}"}
    
    
### 2-5、生成代码 - 表实体提交表单
       url: `/rest/task/table`
       method:`POST`
###### 参数:
        {
              project_id: 10001
              db_name: MySqlSimple
              sql_style: java
              comment: 描述
              action: insert
              table_names: alldbs
              view_names: 
            
        }
###### reponse:
       {"code":"OK"}
       
### 3-1、生成代码 - 自定义查询 （检查类名是否可用）
       url: `rest/task/checkDaoNameConflict`
       method:`POST`
###### 参数:
        {
              prefix: 
              suffix: 
              dao_id: -1
              project_id: 10000
              db_set_name: dal-1
              daoName: GetCityID
              is_update: 0
        }
###### reponse:
      {"code":"OK"}
    
### 3-2、生成代码 - 自定义查询 （sql执行计划）
       url: `rest/task/sql/sqlValidate`
       method:`POST`
###### 参数:
        {
              db_name: dal-1
              crud_type: select
              sql_content: select 1
              params: 
              pagination: false
              mockValues: 
        }
###### reponse:
      {"code":"OK"}
      
### 3-3、生成代码 - 生成自定义查询 （点击提交）
       url: `rest/task/sql`
       method:`POST`
###### 参数:
        {
              project_id: 10008
              db_name: dalpppppppppppp
              sql_style: java
              comment: 
              action: insert
              class_name: Person
              pojo_name: Person
              method_name: Person
              scalarType: List
              pagination: false
              crud_type: select
              sql_content: select * from person
              params: 
              hints: 
        }
###### reponse:
      {"code":"OK"}
      

### 3-1、生成代码 - 自定义查询 （查询实体类名和dao类名）- 准备弃用
       url: `rest/task/sql_class`
       method:`POST`
###### 参数:
        {
              project_id:10000
              db_name:dal_1
        }
###### reponse:
      {"classes":["PersonPojoSelect","PersonPojo","GetCityID"],"pojos":["PersonPojoSelect","FreePersonPo","GetCityInfo"]}
    

### 生成代码
       url: `rest/project/generate`
       method:`POST`
###### 参数:
        {
              project_id: 10001
              regenerate: false
              language: cs
              newPojo: true
              random: 1539333995224
        }
###### reponse:




### 同步数据
       url: `/rest/project/sysDataApollo`
       method:`POST`
###### 参数:
        {
              project_id: 10001
        }
###### reponse:
      {"code":"OK","infoSaec":"java"}
    
    
      
  






            


       
       


      
    
