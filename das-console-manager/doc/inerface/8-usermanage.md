## 八、用户管理 - UserController

### 1、获取user列表（刷新）
       url: `/user/list`
       method:`GET`
###### reponse:
       {
           "code": 200,
           "msg": {
               "list": [
                   {
                       "id": 1,
                       "userNo": "1",
                       "userName": "admin",
                       "userEmail": "admim@12.com",
                       "password": "e10adc3949ba59abbe56e057f20f883e",
                       "role": null,
                       "intAdduser": null,
                       "adduser": null,
                       "create_time": null,
                       "update_time": null,
                       "dalTeam": false
                   }
               ],
               "totalCount": 1,
               "page": 1,
               "pageSize": 10
           }
       }

### 2、根据域用户名获取用户信息
       url: `/user/getWorkInfo`
       method:`GET`
###### 参数:
       name：yurongxing
###### reponse:
       {
           "code": 200,
           "msg": {
               "id": null,
               "userNo": "010892",
               "userName": "wangliang",
               "userEmail": "wangliang@ppdai.com",
               "password": null,
               "role": null,
               "intAdduser": null,
               "adduser": null,
               "create_time": null,
               "update_time": null,
               "dalTeam": false
           }
       }
       
### 3、添加用户
       url: `/user/add`
       method:`POST`
###### 参数:
        {
            "userNo": "456",
            "userName": "wangwu",
            "userEmail": "wangwu@125.com",
            "password":"1234" 
        }
###### reponse:
       {"code":"OK"}
       
### 4、修改用户
       url: `/user/update`
       method:`POST`
###### 参数:
       {
       	"id":13,
           "userNo": "5678",
           "userName": "wangwu2",
           "userEmail": "wangwu3@125.com",
           "password":"111111" 
       }
###### reponse:
       {"code":"OK"}
            
### 5、删除用户
       url: `/user/delete`
       method:`DELETE`
###### 参数:
        {
            "id":13
        }
###### reponse:
       {"code":"OK"}
       
### 6、权限判断
       url: `/user/isDefaultSuperUser`
       method:`GET`
###### 参数:
      
###### reponse:
       true
            