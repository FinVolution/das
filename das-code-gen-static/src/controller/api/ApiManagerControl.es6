import {Control, Action} from 'ea-react-dm-v14'
import ApiModel from '../../model/api/ApiModel'

@Control(ApiModel)
export default class ApiManagerControl extends Action {

    static loadApiList(param, _this, callback) {
        return this.ajaxPost('/apiConfig/list', param, 'list', _this, callback)
    }

    static getApiInfo(id, _this, callback) {
        return this.ajaxGet('/apiConfig/' + id, null, 'apiConfig', _this, callback)
    }

    static updateApiConfig(apiConfig, _this, callback) {
        return this.ajaxGet('/apiConfig/update', apiConfig, 'actionState', _this, callback)
    }

    static addApiConfig(apiConfig, _this, callback) {
        return this.ajaxPost('/apiConfig', apiConfig, 'actionState', _this, callback)
    }

    static deleteApiConfig(id, _this, callback) {
        return this.fetch('/apiConfig/' + id, 'DELETE', null, 'actionState', _this, callback)
    }

    static getPoints(id, _this, callback) {
        return this.ajaxGet('/api/getPoints?accountId=' + id, null, 'pointsView.points', _this, callback)
    }

    static getAccountList(_this, callback) {
        return this.ajaxGet('/api/getAccountList', null , 'accountList', _this, callback)
    }

    static transfer(data, _this, callback) {
        return this.ajaxPost('/api/transfer', data , 'reponse', _this, callback)
    }
}