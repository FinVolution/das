import MathUtil from './MathUtil'
import _ from 'underscore'
import $ from 'jquery'

let DataUtil = DataUtil || {}

/**
 * 检测各种具体是对象类型
 */
DataUtil.is = {types: ['Array', 'Boolean', 'Date', 'Number', 'Object', 'RegExp', 'String', 'Window', 'HTMLDocument']}
for (var i = 0; i < DataUtil.is.types.length - 1; i++) {
    var c = DataUtil.is.types[i]
    DataUtil.is[c] = (function (type) {
        return function (obj) {
            return Object.prototype.toString.call(obj) == '[object ' + type + ']'
        }
    })(c)
}

DataUtil.array = {
    push: function (arr, ele) {
        if (arr.indexOf(ele) == -1) {
            arr.push(ele)
        }
    },
    remove: function (arr, val) {
        const index = arr.indexOf(val)
        if (index > -1) {
            arr.splice(index, 1)
        }
    }
}

DataUtil.TypeUtils = {
    isInt: function (num) {
        var reg = /^[1-9]*[1-9][0-9]*$/
        return reg.test(num)
    },
    isFloat: function (num) {
        var reg = new RegExp('^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$') //正浮点数
        if (reg.test(num)) {
            return true
        }
        return false
    }
}

DataUtil.StringUtils = {
    isEmpty: function (str) {
        if (str === null || str === undefined || str == '') {
            return true
        }
        return false
    },
    trim: function (str) {
        if (str != null && typeof(str) != 'undefined' && str.length > 0) {
            return str.replace(/(^\s*)|(\s*$)/g, '')
        }
        return str
    },
    /**
     * 截取第一个点号之后的所有内容
     * 127.0.0.1 ==> 0.0.1
     */
    subStringByFirstPoint: function (str) {
        return str.match(MathUtil.REGS.subStringByFirstPoint, str)[1]
    },
    /**
     * 取字符串int部分
     */
    getInt: function (val) {
        if (val == '0' || val == 0) {
            return val
        }
        if (!DataUtil.TypeUtils.isInt(val)) {
            val = DataUtil.StringUtils.getInt(val.substring(0, val.length - 1))
        }
        return val
    },
    /**
     * 取字符串float部分
     */
    getFloat: function (val) {
        if (val == '0.' || val == 0) {
            return val
        }
        if (!DataUtil.TypeUtils.isFloat(val)) {
            val = DataUtil.StringUtils.getFloat(val.substring(0, val.length - 1))
        }
        return val
    },
    /**
     * 取字符串小写和下划线
     */
    getDbName: function (val) {
        if (val && !/^\w+$/.test(val)) {
            val = DataUtil.StringUtils.getDbName(val.substring(0, val.length - 1))
        }
        return val.toLowerCase()
    },
    /**
     * 取字符串英文写和下划线
     */
    getEnglishnderline: function (val) {
        if (val && !/^\w+$/.test(val)) {
            val = DataUtil.StringUtils.getDbName(val.substring(0, val.length - 1))
        }
        return val
    },
    /**
     * 取字符串英文和下划线,横线，加号及点。
     */
    getProjectName: function (val) {
        if (val && /[^a-z|A-Z|0-9|\-|_|\.]/g.test(val)) {
            val = DataUtil.StringUtils.getDbName(val.substring(0, val.length - 1))
        }
        return val
    },
    getClassName: function (val) {
        if (val && !/^[a-z0-9\.-]*$/g.test(val)) {
            val = DataUtil.StringUtils.getDbName(val.substring(0, val.length - 1))
        }
        return val
    },
    getLength: function (val, length) {
        if (val && val.length > length) {
            return val.substring(0, length)
        }
        return val
    },
    /**
     * 下划线转换驼峰
     */
    toHump: function (name) {
        return name.replace(/\_(\w)/g, function (all, letter) {
            return letter.toUpperCase()
        })
    },
    /**
     * 驼峰转换下划线
     */
    toLine: function (val) {
        return val.replace(/([A-Z])/g, '_$1').toLowerCase()
    },
    /**
     * 首字母大写
     */
    upperCase: function (val) {
        return val.substring(0, 1).toUpperCase() + val.substring(1)
    }

}

DataUtil.ObjUtils = {
    merge: function (arr) {
        let rs = {}
        for (var i in arr) {
            rs = _.extend(rs, arr[i].param)
        }
        return rs
    },

    stroes: function (arr) {
        let rs = {}
        for (var i in arr) {
            rs = _.extend(rs, arr[i].stroes)
        }
        return rs
    },

    /**
     *
     * */
    sort: (arr) => {
        const compare = () => {
            return (a, b) => {
                if (a.toString().toLowerCase() > b.toString().toLowerCase()) {
                    return 1
                }
                return -1
            }
        }
        return arr.sort(compare())
    },

    sortAscByKey: (attr, key = 'key') => {
        const compare = function (property) {
            return function (a, b) {
                const value1 = a[property]
                const value2 = b[property]
                if (value1.toString().toLowerCase() > value2.toString().toLowerCase()) {
                    return 1
                }
                return -1
            }
        }
        return attr.sort(compare(key))
    },

    sortDesByKey: (attr, key = 'key') => {
        const compare = function (property) {
            return function (a, b) {
                const value1 = a[property]
                const value2 = b[property]
                if (value1.toString().toLowerCase() < value2.toString().toLowerCase()) {
                    return 1
                }
                return -1
            }
        }
        return attr.sort(compare(key))
    },

    extendObjToArr: (arr, obj) => {
        null != arr && arr.length > 0 && arr.forEach((item) => {
            _.extend(item, obj)
        })
    },

    /**
     * 合并数组并过滤重复
     * [{id:1, name:'tom'}, {id:2, name:'jerry'}] , [{id:2, name:'jerry'}, {id:3, name:'tom'}]
     *              => [{id:1, name:'tom'}, {id:2, name:'jerry'}, {id:3, name:'jon'}]
     */
    extendArr: (arr1, arr2) => {
        let list
        if (_.isArray(arr1) && _.isArray(arr2)) {
            /* list = _.filter(arr1, item => {
                 return !DataUtil.ObjUtils.includes(arr2, item)
             })*/
            arr1.forEach(item => {
                arr2.push(item)
            })
            list = _.uniq(arr2)
            list = DataUtil.ObjUtils.sortAscByKey(list)
        }
        return list
    },

    /**
     * 从obj中取出keys，返回新的obj
     * @param obj
     * @param keys
     */
    some: (obj, keys) => {
        if (null == obj || null == keys || keys.length == 0) {
            return obj
        }
        let rs = {}
        keys.forEach((key) => {
            rs[key] = obj[key]
        })
        return rs
    },

    isEqual: function (object, other) {
        if ((!object && other) || (object && !other)) {
            window.console.warn('DataUtil.ObjUtils.isEqual: object or other is null', object, other)
            return false
        }
        return _.isEqual(object, other)
    },
    /**
     * {a:1,b:2}, 1 => a
     * [{id:1,name:'tom'},{id:2,name:'jerry'}] , {name:'tom'}}=> {id:1,name:'tom'}
     */
    findWhere: function (list, properties) {
        (!list || _.isEmpty(list) || !properties) && window.console.warn('DataUtil.ObjUtils.findWhereIdByVal: list or obj is empty', list, properties)
        if (_.isArray(list)) {
            return _.findWhere(list, properties)
        }
        return properties
    },
    /**
     *  过滤筛选含有指定字段的集合
     * [{id:1,name:'tom'},{id:2,name:'jerry'}] , {name:'om'}}=> {id:1,name:'tom'}
     */
    filterWhere: function (list, item, key) {
        (!list || _.isEmpty(list) || !item) && window.console.warn('DataUtil.ObjUtils.filterWhere: list or obj is empty', list, item)
        if (_.isArray(list)) {
            const rs = []
            list.forEach(i => {
                if (i[key].includes(item[key])) {
                    rs.push(i)
                }
            })
            return rs
        }
        return item
    },
    /**
     * 根据val取第一个匹配的key,兼容[],{}
     * {a:1,b:2}, 1 => a
     * [{id:1,name:'tom'},{id:2,name:'jerry'}] , {name:'tom'}}, 'id'=> 1
     * @param list
     * @param val
     */
    findWhereIdByVal: function (list, val, id) {
        (!list || _.isEmpty(list)) && window.console.error('DataUtil.ObjUtils.findWhereIdByVal: tradeList is empty', list, val, id)
        !val && window.console.error('DataUtil.ObjUtils.findWhereIdByVal: val is null', list, val, id)
        !id && window.console.error('DataUtil.ObjUtils.findWhereIdByVal: id is null', list, val, id)
        if (_.isArray(list)) {
            const item = _.findWhere(list, val)
            if (item) {
                return String(item[id])
            }
            return item
        } else if (_.isObject(list)) {
            for (let key in list) {
                if (list[key] == val) {
                    return String(key)
                }
            }
        }
    },

    /**
     * 根据val取第一个匹配的key,兼容[],{}
     * {a:1,b:2} => 1
     * [{id:1,name:'tom'},{id:2,name:'jerry'}] => tom
     * @param list
     * @param val
     */
    findWhereValById: function (list, id, name) {
        (!list || _.isEmpty(list)) && window.console.error('DataUtil.ObjUtils.findWhereIdByVal: tradeList is empty', list, id, name)
        !id && window.console.error('DataUtil.ObjUtils.findWhereIdByVal: val is null', list, id, name)
        !name && window.console.error('DataUtil.ObjUtils.findWhereIdByVal: id is null', list, id, name)
        try {
            if (_.isArray(list)) {
                const item = _.findWhere(list, id)
                if (item) {
                    return item[name]
                }
                return null
            } else if (_.isObject(list)) {
                for (let key in list) {
                    if (key == id) {
                        return String(list[key])
                    }
                }
            }
        } catch (e) {
            window.console.error(list, id, name, e)
            return null
        }
    },

    /**
     * 清空obj，excet除外['ad,as,asa,']
     * @param json
     * @param except
     */
    cleanJson: function (json, except) {
        let rs = {}
        let item
        for (const key in json) {
            if (except && except.length > 0 && except.includes(key)) {
                rs[key] = json[key]
                continue
            }
            let iscontinue = false
            for (let i = 0; i < except.length; i++) {
                const item = except[i]
                if (DataUtil.is.Object(item) && item.k == key) {
                    rs[key] = item.v
                    iscontinue = true
                    continue
                }
            }
            if (iscontinue) {
                continue
            }
            item = json[key]
            if (DataUtil.is.String(item)) {
                rs[key] = ''
            } else if (DataUtil.is.Number(item)) {
                rs[key] = 0
            } else if (DataUtil.is.Array(item)) {
                rs[key] = []
            } else if (DataUtil.is.Object(item)) {
                rs[key] = {}
            }
        }
        return rs
    },
    /**
     * 得到key的数组[{id:1, name:tom}, {id:2, name:jerry}] 或 {id:2, name:jerry} -> [id, name]
     * @param list
     */
    getKeys: function (obj) {
        if (obj && _.isArray(obj) && obj.length > 0) {
            return Object.keys(obj[0])
        }
        else if (obj && !_.isEmpty(obj) && !_.isArray(obj) && _.isObject(obj)) {
            return Object.keys(obj)
        }
        return []
    },
    /**
     * 清空obj，excet除外['ad,as,asa,']
     * @param json
     * @param except
     */
    clearJson: function (json, except) {
        let rs = {}
        for (const key in json) {
            if (except && except.length > 0 && except.includes(key)) {
                rs[key] = json[key]
            }
        }
        return rs
    },
    /**
     * 指定字段执为空
     * @param item
     * @param arr
     */
    toNnull: function (item, arr) {
        let rs = {}
        for (const key in item) {
            if (!arr.includes(key)) {
                rs[key] = item[key]
            }
        }
        return rs
    },
    /**
     * 格式转换
     * {1: 'MySql', 2: 'SQLServer'} ==> [{id: 1, name: 'mySql'}, {id: 2, name: 'sqlServer'}]
     * @param _this
     */
    objToArr: function (obj, keya = 'id', keyb = 'name') {
        const arr = []
        _.each(obj, function (v, k) {
            const item = {}
            item[keya] = k
            item[keyb] = v
            arr.push(item)
        })
        return arr
    },
    /**
     * 初始化 数组或对象
     * [{name:'tom', id:1}, {name:'jerry', id:2}] ==> {'1':'tom','2':'jerry'}
     * {1:'tom',2:'jerry'} ==> {'1':'tom','2':'jerry'}
     * @param _this
     */
    transform: function (list, param) {
        let objs = {}
        try {
            if (DataUtil.is.Array(list)) {
                for (let i in list) {
                    const item = list[i]
                    const id = String(item[param.id])
                    if (item[param.id] == undefined || item[param.name] == undefined) {
                        window.console.error('FrwkUtil.transform: param.id or param.name is undefined')
                    }
                    objs[id] = String(item[param.name])
                }
            } else if (DataUtil.is.Object(list)) {
                _.each(list, function (name, id) {
                    objs[String(id)] = String(name)
                })
            }
            if ($.isEmptyObject(objs)) {
                window.console.error(list, 'FrwkUtil.transform: objs is null!!!')
            }
        } catch (e) {
            window.console.error('FrwkUtil.transform', e, list, param)
        }
        return objs
    },
    /**
     * 去重 alist 从 blist 里去重
     * [{id:1,name:tom}, {id:2,name:jerrt}] [{id:1,name:tom}, {id:2,name:yhon}] =>  [{id:1,name:tom}]
     */
    uniq: (alist, blist, key = 'id') => {
        const arr = _.map(blist, item => item[key])
        const list = []
        alist.forEach(item => {
            if (!DataUtil.ObjUtils.includes(arr, item)) {
                list.push(item)
            }
        })
        return list
    },

    /**
     * unique 数组去重
     */
    unique: arr => {
        return _.uniq(arr)
    },

    /**
     * 获取重复内容 [1,2,3,1,2] = [1,2,3]
     */
    getRepeat: arr => {
        const tmp = []
        arr.forEach(function (item) {
            (arr.indexOf(item) !== arr.lastIndexOf(item) && tmp.indexOf(item) === -1) && tmp.push(item)
        })
        return tmp
    },

    /***
     /***
     * [{id:1,name:tom},{id:2,name:jerry}], name-> [tom, jerry]
     */
    filter: (list, key) => {
        return _.map(list, item => item[key])
    },
    /***
     * 过滤
     * [{id:1,name:tom},{id:2,name:jerry}, {id:3,name:ho}], {name:o} -> [{id:1,name:tom}, {id:3,name:ho}]
     */
    filterTransform: (list, item, key = 'id') => {
        return _.filter(list, obj => {
            if (obj[key].includes(item[key])) {
                return obj
            }
        })
    },
    /***
     * 过滤
     * [{id:1,name:tom},{id:2,name:jerry}, {id:3,name:ho}], [1,2] -> [{id:1,name:tom}, {id:2,name:jerry}]
     */
    filterListByKeys: (list, arr, key = 'id') => {
        return _.filter(list, item => {
            return arr.includes(item[key])
        })
    },

    /***
     * 过滤
     * {id:1,name:tom,age:20}, [name, age] -> {name:tom,age:20}
     */
    filterObjByKeys: (obj, keys) => {
        const rs = {}
        keys.forEach(key => {
            rs[key] = obj[key]
        })
        return rs
    },

    /***
     * 过滤
     * {1: "project001", 3: "project002", 4: "project004", 5: "project005"}, [1,3]-> ["project001", "project002"]
     */
    filterObjToArr: (list, arr) => {
        let rs = []
        if (!arr || arr.length == 0) {
            return rs
        }
        _.each(list, (element, key) => {
            if (DataUtil.is.String(arr[0]) && arr.includes(String(key))) {
                rs.push(element)
            } else if (DataUtil.is.Number(arr[0]) && arr.includes(parseInt(key))) {
                rs.push(element)
            }
        })
        return rs
    },
    /***
     * 过滤
     * [{id:1,name:"project001"},{id:2,name:"project002"}, {id:3,name:"project003"}], [1,2] -> ["project001", "project002"]
     */
    filterObjListToArr: (list, arr, key = 'name') => {
        let rs = []
        if (_.isEmpty(arr)) {
            return rs
        }
        _.each(list, item => {
            if (DataUtil.is.String(arr[0]) && arr.includes(String(item.id))) {
                rs.push(item[key])
            } else if (DataUtil.is.Number(arr[0]) && arr.includes(parseInt(item.id))) {
                rs.push(item[key])
            }
        })
        return rs
    },
    /***
     * 过滤
     * [{id: 1, name: 'mySql'}, {id: 2, name: 'sqlServer'}], ['mySql'], 'name', 'id' -> [1]
     */
    filterArrToItems: (arr, items, keya = 'name', keyb = 'id') => {
        const rs = []
        arr.forEach(i => {
            if (items.includes(i[keya])) {
                rs.push(i[keyb])
            }
        })
        return rs
    },
    /***
     * list 是否包含item
     * [{id:1,name:tom},{id:2,name:jerry}, {id:3,name:ho}], {id:2,name:jerry} -> true
     */
    includes: (list, item, key = '') => {
        if (!key) {
            for (let i = 0; i < list.length; i++) {
                if (_.isEqual(list[i], item)) {
                    return true
                }
            }
        } else {
            for (let i = 0; i < list.length; i++) {
                if (_.isEqual(list[i][key], item[key])) {
                    return true
                }
            }
        }
        return false
    },
    /***
     * JSON 格式转换
     */
    transformJson: (objs, json) => {
        let rs = []
        if (DataUtil.is.Array(objs)) {
            for (let i = 0; i < objs.length; i++) {
                let item = objs[i], arr = {}
                $.each(json, function (i, v) {
                    arr[i] = item[v]
                })
                rs.push(arr)
            }
            return rs
        } else if (DataUtil.is.Object(objs)) {
            let arr = {}
            $.each(json, function (i, v) {
                arr[i] = objs[v]
            })
            return arr
        }
    },
    /**
     * 获取所有重复对象
     * [{name: '笔记本', value: 2},{name: '记事本', value: 3},{name: '书本', value: 4}], 'name' => [{name: '记事本', value: 3},{name: '书本', value: 4}
     */
    getAllRepeats: (arr, key = 'id') => {
        let list = []
        const map = new Map()
        arr.forEach(v => {
            if (map.get(v[key]) && list.every(vD => vD[key] != v[key])) {
                list.push(v)
            } else {
                map.set(v[key], v)
            }
        })
        return list
    },
    /**
     * 获取对象的key数组
     */
    getAtrNumber: obj => {
        const keys = []
        for (let key in obj) {
            keys.push(key)
        }
        return keys
    },
    /**
     * 去空格
     */
    trim: obj => {
        if (obj) {
            _.each(obj, (v, k) => {
                if (DataUtil.is.String(v)) {
                    obj[k] = $.trim(v)
                }
            })
        }
    }
}

DataUtil.validate = {
    zero: function (str) {
        if (DataUtil.StringUtils.trim(str + '') == '0') {
            return false
        }
        return true
    },
    empty: function (str) {
        if (DataUtil.StringUtils.trim(str + '') == '') {
            return false
        }
        return true
    },
    required: function (str) {
        if (str == null || str == undefined || DataUtil.StringUtils.trim(str + '') == '') {
            return false
        }
        return true
    },
    boolean: function (str) {
        return DataUtil.is.Boolean(str)
    },
    /**
     * 身份证校验
     * @param str
     * @returns {boolean}
     */
    lgalIdCard: function (str) {
        if (str == undefined) {
            return false
        }
        var idCardReg_15 = /^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$/
        //^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$
        var idCardReg_18 = /^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/

        return idCardReg_15.test($.trim(str.toLowerCase())) || idCardReg_18.test($.trim(str.toLowerCase()))
    },
    email: function (str) {
        var reg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/
        return reg.test(str)
    },
    /**
     * 手机号
     * @param str 仅校验11位
     * @returns {boolean}
     */
    mobile: function (str) {
        str = str + ''
        if (str && str.length == 11) {
            return true
        }
        return false
        /*var reg = /^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1}))+\d{8})$/;
         return reg.test(str);*/
    }/*,
     maxLength: function (str) {
     str = trim(str + '');
     if(str && str.length )
     return reg.test(str);
     }*/
}

DataUtil.Date = {
    formatTime: function (time) {
        time = String(time)
        return time.substr(0, 4) + '-' + time.substr(4, 2) + '-' + time.substr(6, 2)
    }
}


/**
 * 取的缓存中的数据
 */
DataUtil.getLocalStorageData = function (storageCode, defaultData) {
    if (localStorage) {
        var result = {}
        var data = localStorage.getItem(storageCode)
        if (data) {
            result = JSON.parse(data)
        } else {
            if (defaultData) {
                return defaultData
            }
        }
        if (result) {
            return result
        }
    }
    return null
}

/**
 * 重新设置缓存中的数据
 * @param storageData
 */
DataUtil.setLocalStorageData = function (storageCode, storageData) {
    if (localStorage) {
        localStorage.removeItem(storageCode)
        localStorage.setItem(storageCode, JSON.stringify(storageData))
    }
}

DataUtil.updateTable = function (table, item, key = 'id') {
    let rs = []
    for (var i in table) {
        if (table[i][key] == item[key]) {
            rs.push(item)
        } else {
            rs.push(table[i])
        }
    }
    return rs
}

DataUtil.deteleTable = function (table, item, key = 'id') {
    let rs = []
    for (var i in table) {
        if (table[i][key] != item[key]) {
            rs.push(table[i])
        }
    }
    return rs
}

export default DataUtil
