import {Control, Action} from 'ea-react-dm-v14'
import {GroupModel} from '../../model/Index'

@Control(GroupModel)
export default class DbInfoControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost('/group/list', param, 'list', _this, callback)
    }

    static loadApiList(param, _this, callback) {
        return ::this.ajaxPost('/api/getBlockChainList', param, 'list', _this, callback)
    }

    static getBlockDetail(param, _this, callback) {
        return this.ajaxGet('/api/getBlockDetail', param, 'blockDetail', _this, callback)
    }

    static updateApiConfig(apiConfig, _this, callback) {
        return this.ajaxGet('/apiConfig/update', apiConfig, 'actionState', _this, callback)
        //return ::this.ajaxPost('/stkciholder/search', param, 'tradeList', _this, callback)
    }

    static addApiConfig(apiConfig, _this, callback) {
        return this.ajaxGet('/apiConfig/dv', apiConfig, 'actionState', _this, callback)
    }

    static deleteApiConfig(id, _this, callback) {
        return this.ajaxGet('/apiConfig/' + id + '/delete', null, 'actionState', _this, callback)
    }
}