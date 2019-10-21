## 四、组员管理 - DalGroupMemberController


### 1、根据name模糊查询成员列表
       url: `/member/users`
       method:`GET`
###### 参数:
       name：admin
###### reponse:


### 2、根据组ID获取组员列表
       url: `/member/groupusers`
       method:`POST`
###### 参数:
       groupId：2
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
                        "role": 1,
                        "intAdduser": null,
                        "adduser": null,
                        "create_time": null,
                        "update_time": null,
                        "active": null,
                        "dalTeam": false
                    }
                ],
                "totalCount": 1,
                "page": 1,
                "pageSize": 10
            }
        }

### 3、获取全部用户列表
       url: `/user/all`
       method:`GET`
###### 参数:
       groupId：2
###### reponse:
        {
            "code": 200,
            "msg": [
                {
                    "user_id": 1,
                    "group_id": null,
                    "userNo": "1",
                    "userName": "admin",
                    "userEmail": "admim@12.com",
                    "role": null,
                    "opt_user": null,
                    "create_time": null
                }
            ]
        }
        
### 4、添加组员
       url: `/member/add`
       method:`POST`
###### 参数:
       {
       	"user_id": "2",
       	"group_id": "3",
       	"role": "2",
       	"opt_user": "2"
       }
    
###### reponse:
        {
            "code": 200,
            "msg": "success"
        }
        
### 5、删除组员
       url: `/member/delete`
       method:`DELETE`
###### 参数:
       {
       	"user_id": "2",
       	"group_id": "3"
       }
###### reponse:
        {
            "code": 200,
            "msg": "success"
        }
        
### 6、权限修改
       url: `/rest/member/update`
       method:`PUT`
###### 参数:
        {
            "user_id": "2",
            "group_id": "3",
            "role": "2",
            "opt_user": "2"
        }
###### reponse:
       {
           "code": 200,
           "msg": "success"
       }
        
### 7、批量添加组员
        