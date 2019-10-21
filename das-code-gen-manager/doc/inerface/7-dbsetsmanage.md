## 七、逻辑数据库管理 GroupDbSetController, GroupDbSetEntryController

### 1、根据groupId查询逻辑数据库列表 dbset
       url: `/groupdbset//{groupId}/list`
       method:`GET`
###### 参数:
        groupId: 1
###### reponse:
       {
           "code": 200,
           "msg": [
               {
                   "id": 3,
                   "name": "dal-3",
                   "provider": "mySqlProvider",
                   "shardingStrategy": null,
                   "groupId": 1,
                   "update_user_no": null,
                   "create_time": null,
                   "update_time": "2018-09-04 16:59:35"
               },
               {
                   "id": 4,
                   "name": "dbname",
                   "provider": "mySqlProvider",
                   "shardingStrategy": null,
                   "groupId": 1,
                   "update_user_no": null,
                   "create_time": null,
                   "update_time": "2018-09-05 14:24:32"
               }
           ]
       }

        
### 2、新建逻辑数据库 dbset
       url: `/groupdbset/add`
       method:`POST`
###### 参数:
        {
            "name": "db_logic_004",
            "db_type": 1,
            "shardingStrategy": "class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=CountryID;mod=2",
            "groupId":1
        }
###### reponse:
       {"code":"OK"}

### 3、更新逻辑数据库 dbset
       url: `/rest/groupdbset/update`
       method:`PUT`
###### 参数:
        {
            "id":16,
            "name": "212121asasas",
            "db_type": 1,
            "shardingStrategy": "class=com.ctrip.platform.dal.dao.strategy.ShardColModShardStrategy;columns=CountryID;mod=2",
            "groupId":1
        }
###### reponse:
       {"code":"OK"}

### 4、删除逻辑数据库 dbset
       url: `/rest/groupdbset/delete`
       method:`DELETE`
###### 参数:
        {
            "id":16,
            "groupId":1
        }
###### reponse:
       {"code":"OK"}
       
## GroupDbSetEntryController

### 1、根据逻辑数据库查询DbsetEntry 分页
       url: `/groupdbSetEntry//list`
       method:`POST`
###### 参数:
        dbsetId: 27
###### reponse:
            {
                "code":200,
                "msg":{
                    "list":[
                        {
                            "id":1,
                            "name":"sasas",
                            "databaseType":"1",
                            "sharding":"sasa",
                            "db_Id":1,
                            "databaseSet_Id":13,
                            "update_user_no":"as",
                            "create_time":null,
                            "update_time":"2018-10-21 19:04:47",
                            "providerName":null,
                            "userName":null,
                            "password":null,
                            "dbAddress":null,
                            "dbPort":null,
                            "dbCatalog":null,
                            "groupId":null
                        }
                    ],
                    "totalCount":1,
                    "page":1,
                    "pageSize":10
                }
            }

### 2、新建逻辑数据库 DbsetEntry
       url: `/groupdbSetEntry/add`
       method:`post`
###### 参数:
       {
       	"name": "dal-1",
       	"databaseType": "1",
       	"sharding": "21212",
       	"db_Id": 1,
       	"dbset_id": 14,
       	"groupId": 1
       }
###### reponse:
       {"code":"OK"}

### 3、修改逻辑数据库 DbsetEntry
       url: `/groupdbset/update`
       method:`PUT`
###### 参数:
        id: 35
        name: mysql_shard_data
        databaseType: Slave
        sharding: 1
        connectionString: mysql_shard_data
        dbsetId: 32
        groupId: 1
###### reponse:
       {"code":"OK"}
       
### 4、删除逻辑数据库 DbsetEntry
       url: `/groupdbset/deletedbsetEntry`
       method:`DELETE`
###### 参数:
        groupId: 1
        dbsetEntryId: 35
        dbsetId: 32
###### reponse:
       {"code":"OK"}