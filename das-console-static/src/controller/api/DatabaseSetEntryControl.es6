import {Control, Action} from 'ea-react-dm-v14'
import {DatabaseSetEntryModel} from '../../model/Index'

@Control(DatabaseSetEntryModel)
export default class DatabaseSetEntryControl extends Action {

    static loadDbSetEntryList(param, _this, callback) {
        return ::this.ajaxPost('/groupdbSetEntry/list', param, 'list', _this, callback)
    }

    static addDbSetEntry(dbSetEntry, _this, callback) {
        return this.ajaxPost('/groupdbSetEntry/add', dbSetEntry, 'rs', _this, callback)
    }

    static addDbSetEntryList(list, _this, callback) {
        return this.ajaxPost('/groupdbSetEntry/adds', list, 'rs', _this, callback)
    }

    static deletedbSetEntry(dbSetEntry, _this, callback) {
        return this.ajaxDelete('/groupdbSetEntry/delete', dbSetEntry, 'rs', _this, callback)
    }

    static updateDbSetEntry(dbSetEntry, _this, callback) {
        return this.ajaxPut('/groupdbSetEntry/update', dbSetEntry, 'rs', _this, callback)
    }

}