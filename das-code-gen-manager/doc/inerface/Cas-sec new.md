## Cas-sec

#### 1. 加解密接口

**POST**

http://cassec.ppdapi.com/tool/encript

**HTTP请求头：**

| 字段         | 类型   | 描述                           |
| ------------ | ------ | ------------------------------ |
| Content-Type | String | application/json;charset=UTF-8 |
| Accept       | String | application/json;charset=UTF-8 |

**请求参数：**

| 字段        | 类型   | 描述                         |
| ----------- | ------ | ---------------------------- |
| appId       | String | 应用ID，每个应用具备唯一性ID |
| plainString | String | 需要加密的数据               |

**请求参数示例：**

```json
{
	"appId":"1000002604",
	"plainString":"canglaoshi"
}
```

**成功响应字段：**

| 字段            | 类型   | 描述                                       |
| --------------- | ------ | ------------------------------------------ |
| responseCode    | String | 返回状态码，responseCode="000000",表示成功 |
| responseMessage | String | 解密成功                                   |
| responseContent | String | 返回加密之后的数据                         |

```json
{
    "responseCode": "000000",
    "responseMessage": "操作成功",
    "responseContent": "iJ1cEdmSaLlEZAV+X6Skag=="
}
```
**失败响应字段：**

| 字段            | 类型   | 描述                                        |
| --------------- | ------ | ------------------------------------------- |
| responseCode    | String | 返回状态码，responseCode=!"000000",表示失败 |
| responseMessage | String | 返回具体失败的原因                          |
| responseContent | String | 返回加密之后的数据                          |
```json
{
    "responseCode": "000010",
    "responseMessage": "appId不能为空",
    "responseContent": "xxxxx"
}    
```
#### 2. 解密接口

**POST**

http://cassec.ppdapi.com/tool/decrypt

**HTTP请求头：**

| 字段         | 类型   | 描述                           |
| ------------ | ------ | ------------------------------ |
| Content-Type | String | application/json;charset=UTF-8 |
| Accept       | String | application/json;charset=UTF-8 |

**请求参数：**

| 字段            | 类型   | 描述                         |
| --------------- | ------ | ---------------------------- |
| appId           | String | 应用ID，每个应用具备唯一性ID |
| encryptedString | String | 需要解密的数据               |

**请求参数示例：**

```json
{
    "appId": "1000002604",
    "encryptedString":"iJ1cEdmSaLlEZAV+X6Skag=="
}
```

**成功响应字段：**

| 字段            | 类型   | 描述                                       |
| --------------- | ------ | ------------------------------------------ |
| responseCode    | String | 返回状态码，responseCode="000000",表示成功 |
| responseMessage | String | 解密成功                                   |
| responseContent | String | 返回解密之后的数据                         |

```json
{
    "responseCode": "000000",
    "responseMessage": "操作成功",
    "responseContent": "canglaoshi"
}
```
**失败响应字段：**

| 字段            | 类型   | 描述                                        |
| --------------- | ------ | ------------------------------------------- |
| responseCode    | String | 返回状态码，responseCode=!"000000",表示失败 |
| responseMessage | String | 解密失败                                    |
| responseContent | String | 返回解密失败的具体原因                      |
```json
{
    "responseCode": "000010",
    "responseMessage": "appId不能为空",
    "responseContent": "xxxxx"
}    
```

#### 3. Database instance添加更新接口
**POST**

http://cassec.ppdapi.com/instance/set

**HTTP请求头：**

| 字段         | 类型   | 描述                           |
| ------------ | ------ | ------------------------------ |
| Content-Type | String | application/json;charset=UTF-8 |
| Accept       | String | application/json;charset=UTF-8 |

**请求参数：**	

| 字段            | 类型   | 描述                         |
| --------------- | ------ | ---------------------------- |
| dasAuthKey | String | das进行cas-sec操作需要传入参数AuthKey,由Das和CasSec协商 |
| instanceId | String | Das业务提供的InstanceId,关联到具体的实例 |
| instanceData | String | 数据库连接字符串               |

**请求参数示例：**

```json
{
    "secInfoDto": {
        "dasAuthKey": "vem3Z1AOKFxbwriqGVMT7fyEQJ4dCspk"
    },
    "data": {
        "instanceId": "1000000000001",
    	"instanceData": "jdbc:mysql://fat-2.mysql.ppdaidb.com:3406/ppdai_cas?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull"
    }
}
```

**成功响应字段：**

| 字段            | 类型   | 描述                                       |
| --------------- | ------ | ------------------------------------------ |
| responseCode    | String | 返回状态码，responseCode="000000",表示成功 |
| responseMessage | String | null                                   |
| responseContent | String | 添加数据库Instance成功                         |

```json
{
    "responseCode": "000000",
    "responseMessage": null,
    "responseContent": "DB Instance添加或更新成功！"
}
```
**失败响应字段：**

| 字段            | 类型   | 描述                                        |
| --------------- | ------ | ------------------------------------------- |
| responseCode    | String | 返回状态码，responseCode=!"000000",表示失败 |
| responseMessage | String | dasAuthKey不正确                            |
| responseContent | String |                                             |
```json
{
    "responseCode": "000010",
    "responseMessage": "dasAuthKey不正确",
    "responseContent": "xxxxx"
}    
```
####  4. Database instance删除接口


**POST**

http://cassec.ppdapi.com/instance/delete

**HTTP请求头：**

| 字段         | 类型   | 描述                           |
| ------------ | ------ | ------------------------------ |
| Content-Type | String | application/json;charset=UTF-8 |
| Accept       | String | application/json;charset=UTF-8 |

**请求参数：**	

| 字段            | 类型   | 描述                         |
| --------------- | ------ | ---------------------------- |
| dasAuthKey | String | das进行cas-sec操作需要传入参数AuthKey,由Das和CasSec协商 |
| instanceId | String | Das业务提供的InstanceId,关联到具体的实例 |

**请求参数示例：**

```json
{
    "secInfoDto": {
        "dasAuthKey": "vem3Z1AOKFxbwriqGVMT7fyEQJ4dCspk"
    },
    "data": {
        "instanceId": "1000000000001"
    }
}
```

**成功响应字段：**

| 字段            | 类型   | 描述                                       |
| --------------- | ------ | ------------------------------------------ |
| responseCode    | String | 返回状态码，responseCode="000000",表示成功 |
| responseMessage | String | null                                       |
| responseContent | String | 删除数据库Instance成功                     |

```json
{
    "responseCode": "000000",
    "responseMessage": "操作成功",
    "responseContent": "删除数据库Instance成功"
}    
```
**失败响应字段：**

| 字段            | 类型   | 描述                                        |
| --------------- | ------ | ------------------------------------------- |
| responseCode    | String | 返回状态码，responseCode=!"000000",表示失败 |
| responseMessage | String | dasAuthKey不正确                            |
| responseContent | String |                                             |
```json
{
    "responseCode": "000010",
    "responseMessage": "dasAuthKey不正确",
    "responseContent": "xxxxx"
}    
```

####  5. cas-sec 获取业务系统Token
**POST**

**HTTP请求头：**

| 字段         | 类型   | 描述                           |
| ------------ | ------ | ------------------------------ |
| Content-Type | String | application/json;charset=UTF-8 |
| Accept       | String | application/json;charset=UTF-8 |

**请求参数：**

| 字段            | 类型   | 描述                         |
| --------------- | ------ | ---------------------------- |
| appId | String | 应用ID，每个应用具备唯一性ID|

```json
{
    "appId": "1000002604"
}
```

http://cassec.ppdapi.com/getToken

**成功响应字段：**

| 字段            | 类型   | 描述                                       |
| --------------- | ------ | ------------------------------------------ |
| responseCode    | String | 返回状态码，responseCode="000000",表示成功 |
| responseMessage | String | 操作成功                                   |
| responseContent | String | token                                      |

```json
{
    "responseCode": "000000",
    "responseMessage": "操作成功",
    "responseContent": "B435A499751A423EA28B9232C5E36488"
}
```
**失败响应字段：**

| 字段            | 类型   | 描述                                        |
| --------------- | ------ | ------------------------------------------- |
| responseCode    | String | 返回状态码，responseCode=!"000000",表示失败 |
| responseMessage | String | 获取token失败                            |
| responseContent | String |                                             |
```json
{
    "responseCode": "000010",
    "responseMessage": "获取token失败",
    "responseContent": "xxxxx"
}    
```

#### 6. cas-sec 注册更新应用Token（失效）

注：由于每个应用在每个环境只能有且只有一个token，保持不变，故该部分不提供更新或删除接口。

**POST**

http://cassec.ppdapi.com/das/bindAppToken

**HTTP请求头：**

| 字段         | 类型   | 描述                           |
| ------------ | ------ | ------------------------------ |
| Content-Type | String | application/json;charset=UTF-8 |
| Accept       | String | application/json;charset=UTF-8 |

**请求参数：**

| 字段            | 类型   | 描述                         |
| --------------- | ------ | ---------------------------- |
| dasAuthKey | String | das进行cas-sec操作需要传入参数AuthKey,由Das和CasSec协商 |
| appId | String | 应用ID，每个应用具备唯一性ID|
| token | String | 应用token|


**请求参数示例：**

```json
{
    "info": {
        "dasAuthKey": "vem3Z1AOKFxbwriqGVMT7fyEQJ4dCspk"
    },
    "data": {
        "appId": "1000000000001",
    	"token": "XXXX"
    }
}
```

**成功响应字段：**

| 字段            | 类型   | 描述                                       |
| --------------- | ------ | ------------------------------------------ |
| responseCode    | String | 返回状态码，responseCode="000000",表示成功 |
| responseMessage | String | null                                       |
| responseContent | String | apptoken                                   |

```json
{
    "responseCode": "000000",
    "responseMessage": "操作成功",
    "responseContent": "xxxx"
}    
```
**失败响应字段：**

| 字段            | 类型   | 描述                                        |
| --------------- | ------ | ------------------------------------------- |
| responseCode    | String | 返回状态码，responseCode=!"000000",表示失败 |
| responseMessage | String | token不存在                                 |
| responseContent | String |                                             |
```json
{
    "responseCode": "000010",
    "responseMessage": "token不存在",
    "responseContent": "null"
}    
```


#### 7. 添加或更新AppId和DB Instance ID关联

<font color="#DC143C">**注：POST的字段需要确认**</font>

**POST**

http://cassec.ppdapi.com/das/bindDbInstance

**HTTP请求头：**

| 字段         | 类型   | 描述                           |
| ------------ | ------ | ------------------------------ |
| Content-Type | String | application/json;charset=UTF-8 |
| Accept       | String | application/json;charset=UTF-8 |

**请求参数：**

| 字段            | 类型   | 描述                         |
| --------------- | ------ | ---------------------------- |
| appId | String | 应用ID，每个应用具备唯一性ID|
| instanceIds | String | DB instance Id |



**请求参数示例：**

```json
{
    "secInfoDto": {
        "dasAuthKey": "vem3Z1AOKFxbwriqGVMT7fyEQJ4dCspk"
    },
    "data": {
        "appId": "1000000000001",
    	"instanceIds": ["XXXX","xxx","xxx"]
    }
}
```

**成功响应字段：**

| 字段            | 类型   | 描述                                       |
| --------------- | ------ | ------------------------------------------ |
| responseCode    | String | 返回状态码，responseCode="000000",表示成功 |
| responseMessage | String | null                                   |
| responseContent | String | token                         |

```json
{
    "responseCode": "000000",
    "responseMessage": "null",
    "responseContent": "AppId和InstanceId新增或更新成功"
}    
```
**失败响应字段：**

| 字段            | 类型   | 描述                                        |
| --------------- | ------ | ------------------------------------------- |
| responseCode    | String | 返回状态码，responseCode=!"000000",表示失败 |
| responseMessage | String |                                 |
| responseContent | String |  instanceId 不存在                                            |
```json
{
    "responseCode": "000010",
    "responseMessage": "null",
    "responseContent": "Instance Id不存在"
}    
```

**注：如果需要解绑appID和instanceId,那么直接使用这个接口更新即可。**

#### 8. 获取物理库详情

<font color="#DC143C">**注：POST的字段需要确认**</font>

**POST**

http://cassec.ppdapi.com/das/getInstanceDetail

**HTTP请求头：**

| 字段         | 类型   | 描述                           |
| ------------ | ------ | ------------------------------ |
| Content-Type | String | application/json;charset=UTF-8 |
| Accept       | String | application/json;charset=UTF-8 |

**请求参数：**

| 字段            | 类型   | 描述                         |
| --------------- | ------ | ---------------------------- |
| appId | String | 应用ID，每个应用具备唯一性ID|
| token | String |应用唯一性Token|
| instanceIds | String | DB instance Id |

**请求参数示例：**

```json
{
    "appId": "XXXX",
    "token": "xxxxx",
    "instanceIds": ["XXXX","xxx","xxx"]
}
```

**成功响应字段：**

| 字段            | 类型   | 描述                                       |
| --------------- | ------ | ------------------------------------------ |
| responseCode    | String | 返回状态码，responseCode="000000",表示成功 |
| responseMessage | String | null                                       |
| responseContent | Object | instancenUrl                               |

```json
{
    "responseCode": "000000",
    "responseMessage": null,
    "responseContent": {
        "instanceDetailList": [
            {
                "instanceId": "1000000000001",
                "instanceData": "jdbc:mysql://fatss-222323 sql.ppdaidb.com:3406/ppdai_cas?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull"
            },
            {
                "instanceId": "1000000000001",
                "instanceData": "jdbc:mysql://fatss-222323 sql.ppdaidb.com:3406/ppdai_cas?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull"
            }
        ]
    }
}   
```
**失败响应字段：**

| 字段            | 类型   | 描述                                        |
| --------------- | ------ | ------------------------------------------- |
| responseCode    | String | 返回状态码，responseCode=!"000000",表示失败 |
| responseMessage | String |                                 |
| responseContent | String |  instanceId 不存在                                            |
```json
{
    "responseCode": "000010",
    "responseMessage": "null",
    "responseContent": "token不存在或其他错误"
}    
```


