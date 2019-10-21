import {Control, Action} from 'ea-react-dm-v14'
import {CodeModel} from '../../model/Index'

@Control(CodeModel)
export default class CodeControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost('/groupdbset/list', param, 'list', _this, callback)
    }

    static addDbSet(dbSet, _this, callback) {
        return this.ajaxPost('/groupdbset/add', dbSet, 'rs', _this, callback)
    }

    static deleteDbSet(dbSet, _this, callback) {
        return this.ajaxDelete('/groupdbset/delete', dbSet, 'rs', _this, callback)
    }

    static updateDbSet(dbSet, _this, callback) {
        return this.ajaxPut('/groupdbset/update', dbSet, 'rs', _this, callback)
    }

    static loadTree(_this, callback) {
        return ::this.ajaxPost('/group/tree', null, 'tree', _this, callback)
    }
}