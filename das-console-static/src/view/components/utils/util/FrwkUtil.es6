import React from 'react'
import DataUtil from './DataUtil'
import {fetch} from 'ea-react-dm-v14'
import _ from 'underscore'
//import $ from 'jquery'

let FrwkUtil = FrwkUtil || {}

FrwkUtil.store = {
    getValueByReducers: function (props, valueLink) {
        (!props || !valueLink) && window.console.error('FrwkUtil.store.getValueByReducers : props or valueLink is undefined', props, valueLink)
        let keys = valueLink.split('.')
        const modelName = keys.shift()
        const model = props[modelName.toLowerCase()]
        if (!model || !keys) {
            window.console.warn('FrwkUtil.store.getValueByReducers model or keys miss:', props, valueLink)
            return ''
        }
        let val = '', rs = []
        try {
            if (keys.length > 1) {
                for (var i in keys) {
                    if (i == 0) {
                        rs = model.get(keys[i])
                    } else if (i > 0) {
                        rs = rs.get(keys[i])
                    }
                }
                val = rs
            } else {
                val = model.get(keys[0])
            }
        } catch (e) {
            window.console && window.console.warn('FrwkUtil.store.getValueByReducers', keys.join(), rs, e)
        }
        return val
    }
}

FrwkUtil.UrlUtils = {
    /**
     * 获取get请求所有参数
     * 例http://a.html?b=1&c=2
     * @returns {b:1,c:2}
     */
    getUrls: function () {
        var aQuery = window.location.href.split('?')
        var aGET = {}
        if (aQuery.length > 1) {
            var aBuf = aQuery[1].split('&')
            for (var i = 0, iLoop = aBuf.length; i < iLoop; i++) {
                var aTmp = aBuf[i].split('=')
                aGET[aTmp[0]] = aTmp[1]
            }
        }
        return aGET
    },
    /**
     * 组合请求参数
     * @param {b:1,c:2}
     * @returns ?b=1&c=2
     */
    initParams: function (data) {
        if (!data || _.isEmpty(data)) {
            return ''
        }
        var arr = []
        for (var item in data) {
            arr.push('&' + item + '=')
            arr.push(data[item])
        }
        if (arr.length == 0) {
            return ''
        }
        var str = arr.join('')
        return '?' + str.substring(1, str.length)
    }
}

FrwkUtil.ComponentUtils = {
    /**
     * 初始化 defaultId
     * @param _this
     * TODO 根据 defaultName 取 defaultId
     */
    getDefaultId: function (_this) {
        let {defaultId, valueLink} = _this.props
        if (defaultId || _.isNumber(defaultId) || DataUtil.validate.boolean(defaultId)) {
            FrwkUtil.store.getValueByReducers(_this.props, valueLink) != defaultId && _this.props.setValueByReducers(valueLink, defaultId)
            return defaultId
        } else {
            defaultId = String(FrwkUtil.store.getValueByReducers(_this.props, _this.props.valueLink))
        }
        if (!defaultId) {
            window.console.error(_this.props.valueLink, 'defaultId is null')
        }
        return defaultId
    }
}

FrwkUtil.fetch = {
    fetchGet: (url, param, _this, callBack) => {
        let _data = {
            method: 'GET',
            mode: 'cors',
            timeout: 60000
        }
        let args = ''
        const params = FrwkUtil.UrlUtils.initParams(param)
        if (url.includes('?')) {
            const arr = url.split('?')
            if (!_.isEmpty(arr)) {
                url = arr[0]
                if (arr.length > 2) {
                    arr.forEach((e, i) => {
                        if (!params && i === 1) {
                            args += '?' + arr[i]
                        } else if (i > 1) {
                            args += '&' + arr[i]
                        }
                    })
                    url += params + args
                } else if (arr.length === 2) {
                    if (!params) {
                        args = '?' + arr[1]
                    } else {
                        args = '&' + arr[1]
                    }

                }
            }
        }
        url += params + args
        fetch(url, _data).then(data => {
                _this && callBack && callBack(data, _this)
            }, (error) => {
                window.console.error('fetchGet error : ' + url, error)
            }
        )
    },
    fetchPost: (url, param, _this, callBack, data_, errCallBack) => {
        let _data = {
            body: JSON.stringify(param),
            method: 'POST',
            timeout: 60000,
            header: {
                'Content-Type': 'application/json'
            }
        }
        if (data_) {
            _data = _.extend(_data, data_)
        }
        fetch(url, _data).then(data => {
                _this && callBack && callBack(data, _this)
            }, (error) => {
                _this && errCallBack && errCallBack(error, _this)
                window.console.error('fetchPost error : ' + url, error)
            }
        )
    }
}

FrwkUtil.load = {
    getList: (url, param, _this, key) => {
        FrwkUtil.fetch.fetchGet(url, param, _this, (data, _this) => {
                if (data.code === 200) {
                    if (!DataUtil.ObjUtils.isEqual(_this.state[key], data.msg)) {
                        _this.state[key] = data.msg
                        _this.setState(_this.state)
                    }
                } else if (data.code === 500) {
                    _this.showErrorsNotification(data.msg)
                }
            }
        )
    }
}

FrwkUtil.createContent = data => {
    if(!DataUtil.is.String(data)){
        return data
    }
    const content = []
    if (data.includes(';')) {
        const arr = data.split(';')
        arr.forEach(item => {
            content.push(<p>{item}</p>)
        })
        return <div>
            {content}
        </div>
    }
    if (data.includes(',')) {
        const arr = data.split(',')
        let names = [], n = 0
        arr.forEach((item, i) => {
            n = i + 1
            names.push(item)
            if (n % 4 === 0) {
                content.push(<p>{names.join(',')}</p>)
                names = []
            }
        })
        if (names.length > 0) {
            content.push(<p>{names.join(',')}</p>)
        }
    } else {
        return <div>
            {data}
        </div>
    }
    return <div>
        {content}
    </div>
}

export default FrwkUtil