## 五、数据库一览 DatabaseController
       
### 1、新建物理数据库
       url: `/db/add`
       method:`POST`
###### 参数:
        {
        	"db_type": 1,
        	"dbname": "dal-3",
        	"db_address": "127.0.0.1",
        	"db_port": "3306",
        	"db_user": "root",
        	"db_password": "root",
        	"db_catalog": "dal_shard_0",
        	"dal_group_id": 1,
        	"addToGroup": true,
        	"isGenDefault": true
        }
###### reponse:
       {
           "code": 200,
           "msg": "success"
       }
       
### 2、更新物理数据库
       url: `/db/update`
       method:`PUT`
###### 参数:
        id: 19
        dbtype: MySQL
        allinonename: mysql_shard_data
        dbaddress: 10.114.27.179
        dbport: 3306
        dbuser: root
        dbpassword: 123456
        dbcatalog: ambari
###### reponse:
       {
           "code": 200,
           "msg": "success"
       }  
       
### 3、删除物理数据库
       url: `/db/delete`
       method:`DELETE`
###### 参数:
        {
            "id":1
        }
###### reponse:
       {
           "code": 200,
           "msg": "success"
       }