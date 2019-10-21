import {Control, Action} from 'ea-react-dm-v14'
import {DataBaseGroupModel} from '../../model/Index'

@Control(DataBaseGroupModel)
export default class DataBaseGroupControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost('/db/list', param, 'list', _this, callback)
    }

    static addGroupDb(groupDb, _this, callback) {
        return this.ajaxPost('/groupdb/add', groupDb, 'rs', _this, callback)
    }

    static deleteGroupDb(groupDb, _this, callback) {
        return this.ajaxDelete('/groupdb/delete', groupDb, 'rs', _this, callback)
    }

    static updateGroupDb(groupDb, _this, callback) {
        return this.ajaxPut('/groupdb/update', groupDb, 'rs', _this, callback)
    }

    static loadTree(_this, callback) {
        return ::this.ajaxPost('/group/tree', null, 'tree', _this, callback)
    }
}