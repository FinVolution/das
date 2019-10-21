import {Control, Action} from 'ea-react-dm-v14'
import {GroupModel} from '../../model/Index'

@Control(GroupModel)
export default class GroupControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost('/group/list', param, 'list', _this, callback)
    }

    static addGroup(group, _this, callback) {
        return this.ajaxPost('/group/add', group, 'rs', _this, callback)
    }

    static deleteGroup(group, _this, callback) {
        return this.ajaxDelete('/group/delete', group, 'rs', _this, callback)
    }

    static updateGroup(group, _this, callback) {
        return this.ajaxPut('/group/update', group, 'rs', _this, callback)
    }

    static loadTree(_this, callback) {
        return ::this.ajaxPost('/group/tree', null, 'tree', _this, callback)
    }
}