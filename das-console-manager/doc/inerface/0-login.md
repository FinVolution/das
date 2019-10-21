## 一、登录注册

### 1、登录
        url: `/user/signin`
        method:`POST`
###### 参数:
        {
        	"userNo":"123",
        	"password":"1000002235"
        }
###### reponse:
        {
            "code": 500,
            "msg": "用户名或密码不正确"
        }
      
## 二、DB初始化      
### 1、DB检测
        url: `/setupDb/setupDbCheck`
        method:`GET`
###### 参数:
        无
###### reponse:
        {
            "code": 500,
            "msg": "配置信息读取失败！"
        }

### 2、链接测试
        url: `/setupDb/connectionTest`
        method:`POST`
###### 参数:
        {
            "dbtype": "MySQL",
            "dbaddress": "127.0.0.1",
            "dbport": "3306",
            "dbuser": "root",
            "dbpassword": "root"
        }
###### reponse:
        {
            "code": 200,
            "msg": "[\"information_schema\",\"performance_schema\",\"n_data\",\"videodb\",\"sakila\",\"dal_shard_1\",\"sys\",\"n_data_1\",\"webmagic\",\"ppdai_rt_crawler\",\"world\",\"code_gen\",\"dal_shard_0\",\"s_trade\",\"mysql\",\"eth_data\",\"indoorlocation\"]"
        }

### 3、判断是否有表
        url: `setupDb/tableConsistentCheck`
        method:`POST`
###### 参数:
        {
            "dbaddress": "127.0.0.1",
            "dbport": "3306",
            "dbuser": "root",
            "dbpassword": "root",
            "dbcatalog": "code_gen"
        }
###### reponse:
        {
            "code": 200,
            "msg": null
        }

### 4、初始化系统DB
        url: `/setupDb/initializeDb`
        method:`POST`
###### 参数:
        {
            "dbaddress": "127.0.0.1",
            "dbport": "3306",
            "dbuser": "root",
            "dbpassword": "root",
            "dbcatalog": "eth_data",
            "groupName": "1",
            "groupAppid": "7000000169",
            "groupComment": "comasas",
            "adminNo": "1",
            "adminName": "jerry",
            "adminEmail": "jerry@123.com",
            "adminPass": "123456"
        }
###### reponse:
        {
            "code": 500,
            "msg": "Column name is already used by other field"
        }
