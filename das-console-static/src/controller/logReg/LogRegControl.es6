import {Control, Action} from 'ea-react-dm-v14'
import {LogRegModel} from '../../model/Index'

@Control(LogRegModel)
export default class LogRegControl extends Action {

    static login(param, _this, callback) {
        return this.ajaxPost('/logReg/login', param, 'rs', _this, callback)
    }

    static emailLogin(param, _this, callback) {
        return this.ajaxPost('/api/emailLogin', param, 'rs', _this, callback)
    }

    static register(data, _this, callback) {
        return this.ajaxPost('/logReg/register', data, 'rs', _this, callback)
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
}