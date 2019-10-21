import {Control, Action} from 'ea-react-dm-v14'
import {TableEntityModel} from '../../model/Index'

@Control(TableEntityModel)
export default class TableEntityControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost('/tableEntity/list', param, 'list', _this, callback)
    }

    static addTableEntity(tableEntity, _this, callback) {
        return this.ajaxPost('/tableEntity/add', tableEntity, 'rs', _this, callback)
    }

    static addTablelist(tableEntryList, _this, callback) {
        return this.ajaxPost('/tableEntity/adds', tableEntryList, 'rs', _this, callback)
    }

    static deleteTableEntity(tableEntity, _this, callback) {
        return this.ajaxDelete('/tableEntity/delete', tableEntity, 'rs', _this, callback)
    }

    static updateTableEntity(tableEntity, _this, callback) {
        return this.ajaxPut('/tableEntity/update', tableEntity, 'rs', _this, callback)
    }

}