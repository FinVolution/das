import _ from 'underscore'
import $ from 'jquery'

let DasUtil = DasUtil || {}

DasUtil.getDbNameByType = (type) => {
    switch (type) {
        case 1:
            return 'MySql'
        case 2:
            return 'SqlServer'
    }
}

/**
 * 数据转换 list内指定字符串转 obj list
 * {acsed:{a=4;b=5;c=6}}  ‘acsed’  -> [{id:1,a:4},{id:1,b:5},{id:3,c:6}]
 */
DasUtil.transformStrategyStrToList = (list, key, type) => {
    const splitItem = (item, type, i = 0) => {
        const kv = item.split('=')
        let obj = {id: i + 1}
        const key = $.trim(String(kv[0]))
        let val = kv[1]
        val = _.isNumber(val) ? val : $.trim(String(val))
        obj.key = key
        if (type) {
            obj[type] = val
        } else {
            obj.value = val
        }
        return obj
    }
    try {
        if (list && !_.isEmpty(list)) {
            for (let j = 0; j < list.length; j++) {
                let data = list[j]
                let val = data[key]
                let rs = [], item, obj
                if (data && val) {
                    if (val.indexOf(';') > 0) {
                        let arr = val.split(';')
                        for (let i = 0; i < arr.length; i++) {
                            item = arr[i]
                            obj = splitItem(item, type, i)
                            rs.push(obj)
                        }
                    } else if (val.indexOf(';') == -1 && val.indexOf('=') > 0) {
                        const obj = splitItem(val, type)
                        rs.push(obj)
                    }
                }
                data.apiParams = rs
            }
        }
        return list
    } catch (e) {
        window.console.error('DasUtil.transformStrategy ---> ', list, key)
    }
}

/**
 *   obj list 转  list内指定字符串
 *  [{id:1,a:4},{id:1,b:5},{id:3,c:6}] ‘acsed’ -> {acsed:{a=4;b=5;c=6}}
 */
DasUtil.transformStrategyListToStr = (item, key1, key2) => {
    try {
        let list = item[key1]
        if (_.isArray(list) && !_.isEmpty(list)) {
            let rs = []
            list.forEach(e => {
                rs.push(e.key + '=' + e.value)
            })
            item[key2] = rs.join(';')
        }
    } catch (e) {
        window.console.error('DasUtil.transformStrategyListToStr ---> ', item, key1, key2)
    }
    return item
}


export default DasUtil