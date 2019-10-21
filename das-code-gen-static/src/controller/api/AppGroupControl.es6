import {Control, Action} from 'ea-react-dm-v14'
import {AppGroupModel} from '../../model/Index'

@Control(AppGroupModel)
export default class AppGroupControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost('/appGroup/list', param, 'list', _this, callback)
    }

    static addAppGroup(appgroup, _this, callback) {
        return this.ajaxPost('/appGroup/add', appgroup, 'rs', _this, callback)
    }

    static deleteAppGroup(appgroup, _this, callback) {
        return this.ajaxDelete('/appGroup/delete', appgroup, 'rs', _this, callback)
    }

    static updateAppGroup(appgroup, _this, callback) {
        return this.ajaxPut('/appGroup/update', appgroup, 'rs', _this, callback)
    }

    static loadServerList(appgroup, _this, callback) {
        return this.ajaxPut('/serverGroup/serversNoGroup', appgroup, 'rs', _this, callback)
    }

}