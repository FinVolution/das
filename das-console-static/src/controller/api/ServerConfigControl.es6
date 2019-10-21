import {Control, Action} from 'ea-react-dm-v14'
import {ServerConfigModel} from '../../model/Index'

@Control(ServerConfigModel)
export default class ServerConfigControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost('/serverConfig/list', param, 'list', _this, callback)
    }

    static addServerConfig(serverConfig, _this, callback) {
        return this.ajaxPost('/serverConfig/add', serverConfig, 'rs', _this, callback)
    }

    static deleteServerConfig(serverConfig, _this, callback) {
        return this.ajaxDelete('/serverConfig/delete', serverConfig, 'rs', _this, callback)
    }

    static updateServerConfig(serverConfig, _this, callback) {
        return this.ajaxPut('/serverConfig/update', serverConfig, 'rs', _this, callback)
    }

}