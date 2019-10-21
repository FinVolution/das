import {Control, Action} from 'ea-react-dm-v14'
import {SystemModel} from '../../model/Index'

@Control(SystemModel)
export default class SystemControl extends Action {

    static loadApiList(param, _this, callback) {
        return ::this.ajaxPost('/system/list', param, 'list', _this, callback)
    }

    static updateApiConfig(apiConfig, _this, callback) {
        return this.ajaxGet('/apiConfig/update', apiConfig, 'actionState', _this, callback)
    }

    static addApiConfig(apiConfig, _this, callback) {
        return this.ajaxGet('/apiConfig/asss', apiConfig, 'actionState', _this, callback)
    }

    static deleteApiConfig(id, _this, callback) {
        return this.ajaxGet('/apiConfig/' + id + '/delete', null, 'actionState', _this, callback)
    }
}