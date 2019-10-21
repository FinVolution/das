import {Control, Action} from 'ea-react-dm-v14'
import {ServerGroupModel} from '../../model/Index'

@Control(ServerGroupModel)
export default class ServerGroupControl extends Action {

    static loadAllServerGroups(param, _this, callback) {
        return ::this.ajaxGet('/serverGroup/loadAllServerGroups', param, 'serverGroups', _this, callback)
    }

    static loadList(param, _this, callback, filer) {
        return ::this.ajaxPost('/serverGroup/list', param, 'list', _this, callback, filer)
    }

    static addServerGroup(serverGroup, _this, callback) {
        return this.ajaxPost('/serverGroup/add', serverGroup, 'rs', _this, callback)
    }

    static deleteServerGroup(serverGroup, _this, callback) {
        return this.ajaxDelete('/serverGroup/delete', serverGroup, 'rs', _this, callback)
    }

    static updateServerGroup(serverGroup, _this, callback) {
        return this.ajaxPut('/serverGroup/update', serverGroup, 'rs', _this, callback)
    }
}