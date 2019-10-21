import {Control, Action} from 'ea-react-dm-v14'
import {ServerModel} from '../../model/Index'

@Control(ServerModel)
export default class ServerControl extends Action {

    static loadList(param, _this, callback, flier) {
        return ::this.ajaxPost('/server/list', param, 'list', _this, callback, flier)
    }

    static addServer(server, _this, callback) {
        return this.ajaxPost('/server/add', server, 'rs', _this, callback)
    }

    static deleteServer(server, _this, callback) {
        return this.ajaxDelete('/server/delete', server, 'rs', _this, callback)
    }

    static updateServer(server, _this, callback) {
        return this.ajaxPut('/server/update', server, 'rs', _this, callback)
    }

}