import {fetch} from 'ea-react-dm-v14'
import FrwkUtil from '../../view/components/utils/util/FrwkUtil'
import LoadingBar from './LoadingBar'
import Immutable from 'immutable'

export const ajax = {
    fetch: function (connect, type, url, param, valueLink, _this, callBack) {
        let _data = {
            method: type,
            timeout: 60000
        }

        if (type.toLowerCase() != 'get') {
            _data = {
                body: JSON.stringify(param),
                method: type,
                timeout: 60000,
                header: {
                    'Content-Type': 'application/json'
                }
            }
        }
        return (dispatch) => {
            fetch(url + FrwkUtil.UrlUtils.initParams(param), _data).then((data) => {
                _this && callBack && callBack(_this, data)
                dispatch(connect.update(valueLink, data.msg))
            }, (error) => {
                _this && _this.showMsg && _this.showMsg('error', 'URL:' + url + ', 查询失败!!!')
                window.console.error('ajaxGet : ' + url + ' error!!', error)
            })
        }
    }
}

export default class BaseControl {
    static setValueByReducers(valueLink, val) {
        (!valueLink || !val) && window.console.error('Action valueLink or val is undifened', valueLink, val)
        const isEmptyObject = function (e) {
            var t
            for (t in e)
                return !1
            return !0
        }
        valueLink = valueLink.match(/\.(.+?)$/, valueLink)[1]
        if (isEmptyObject(val)) {
            return this.save(valueLink, Immutable.fromJS(val))
        }
        return this.save(valueLink, Immutable.fromJS(val))
    }

    @LoadingBar('mosk')
    static ajaxGetMosk(url, param, valueLink, _this, callBack) {
        return ajax.fetch(this, 'GET', url, param, valueLink, _this, callBack)
    }

    @LoadingBar('loading')
    static ajaxGet(url, param, valueLink, _this, callBack) {
        return ajax.fetch(this, 'GET', url, param, valueLink, _this, callBack)
    }

    @LoadingBar('mosk')
    static ajaxPostMosk(url, param, valueLink, _this, callBack) {
        return ajax.fetch(this, 'POST', url, param, valueLink, _this, callBack)
    }

    @LoadingBar('loading')
    static ajaxPost(url, param, valueLink, _this, callBack) {
        return ajax.fetch(this, 'POST', url, param, valueLink, _this, callBack)
    }

    @LoadingBar('loading')
    static ajaxDelete(url, param, valueLink, _this, callBack) {
        return ajax.fetch(this, 'DELETE', url, param, valueLink, _this, callBack)
    }
}
