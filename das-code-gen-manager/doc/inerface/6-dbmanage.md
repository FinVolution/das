
## 六、数据库管理 DalGroupDbController


### 1、根据groupid获取数据库列表
       url: `/groupdb/dblist`
       method:`GET`
###### 参数:
        groupId：1
###### reponse:
       {
           "code": 200,
           "msg": [
               {
                   "id": 1,
                   "dbname": "dal-4",
                   "comment": null,
                   "dal_group_id": 1,
                   "db_address": "127.0.0.11111",
                   "db_port": "3306",
                   "db_user": "root",
                   "db_password": "W1f6usYRkdhuhA+2MfuNhA==",
                   "db_catalog": "dal_shard_1",
                   "db_type": 1,
                   "create_time": null,
                   "update_time": null,
                   "addToGroup": false,
                   "genDefault": false
               }
           ]
       }

### 2、添加数物理据库信息到组
       url: `/groupdb/add`
       method:`POST`
###### 参数:
        {
        	"id": 1,
        	"comment": "数控1111啊啊大大",
        	"dal_group_id": 1
        }
###### reponse:
        {"code":"OK"}

### 3、修改组的数物理据库信息 - 限制只能改备注
       url: `/rest/groupdb/update`
       method:`PUT`
###### 参数:
        {
        	"id": 1,
        	"comment": "asasasasasas",
        	"dal_group_id": 1
        }
###### reponse:
        {
           "code": 200,
           "msg": "success"
       }
        
### 4、删除组对应的数据库
       url: `/groupdb/delete`
       method:`DELETE`
###### 参数:
        {
        	"id": 1,
        	"dal_group_id": 1
        }
###### reponse:
       {
           "code": 200,
           "msg": "success"
       }
       
### 5、转移数据库
       url: `/groupdb/transfer`
       method:`PUT`
###### 参数:
        {
        	"id": 1,
        	"dal_group_id": 2
        }
###### reponse:
       {
           "code": 200,
           "msg": "success"
       }