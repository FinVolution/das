## 三、组管理 - DalGroupController


### 1、获取组的tree 1)管理员获得全部，2）其他用户根据userid获取
       url: `/group/tree`
       method:`GET`
###### 参数:
       root：true
###### reponse:
      {
          "code": 200,
          "msg": [
              {
                  "id": 1,
                  "group_name": "team003",
                  "group_comment": "",
                  "create_user_no": "1",
                  "app_id": 7000000169,
                  "text": null,
                  "icon": null,
                  "children": false,
                  "create_time": "2018-08-30 14:04:15",
                  "update_time": "2018-08-30 14:04:15"
              },
              {
                  "id": 2,
                  "group_name": "team003",
                  "group_comment": "",
                  "create_user_no": "1",
                  "app_id": 0,
                  "text": null,
                  "icon": null,
                  "children": false,
                  "create_time": "2018-08-30 14:45:14",
                  "update_time": "2018-08-30 14:45:14"
              },
              {
                  "id": 3,
                  "group_name": "team004",
                  "group_comment": "",
                  "create_user_no": "1",
                  "app_id": 1000000169,
                  "text": null,
                  "icon": null,
                  "children": false,
                  "create_time": "2018-08-30 15:34:49",
                  "update_time": "2018-08-30 15:34:49"
              }
          ]
      }
        
        
### 1、获取组翻页列表
       url: `/group/list`
       method:`GET`
###### 参数:
        无
###### reponse:
      [
          {
  
              "create_time":"2018-07-05T11:28:11+08:00",
              "create_user_no":"1",
              "group_comment":"dal",
              "group_name":"dal",
              "id":1
          },
          {
              "create_time":"2018-07-09T15:55:38+08:00",
              "create_user_no":"1",
              "group_comment":"组001",
              "group_name":"team001",
              "id":2
          },
          {
              "create_time":"2018-07-09T15:57:34+08:00",
              "create_user_no":"1",
              "group_comment":"新组002",
              "group_name":"team002",
              "id":3
          },
          {
              "create_time":"2018-07-18T14:55:02+08:00",
              "create_user_no":"1",
              "group_comment":"借贷账单",
              "group_name":"team003",
              "id":4
          }
      ]
      
### 2、新建组
       url: `/group/add`
       method:`POST`
###### 参数:
        {
            "group_name": "team003",
            "group_comment": "借贷账单",
            "app_id":7000000169
        }
###### reponse:
      {"code":"OK"}
 
### 3、更新组
       url: `/group/update`
       method:`PUT`
###### 参数:
        {
           groupId: 3
           groupName: das_test001
           groupComment: 测试组222
        }
###### reponse:
      {"code":"OK"} 

### 4、删除组
       url: `/rest/group/delete `
       method:`DELETE`
###### 参数:
        {
           id: 3
        }
###### reponse:
      {"code":"OK"}  
      
        
      
      