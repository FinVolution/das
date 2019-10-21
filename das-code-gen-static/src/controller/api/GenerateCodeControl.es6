import {Control, Action} from 'ea-react-dm-v14'
import {GenerateCodeModel} from '../../model/Index'

@Control(GenerateCodeModel)
export default class GenerateCodeControl extends Action {

    static generate(param, _this, callback) {
        return ::this.ajaxPost('/code/generate', param, 'rs', _this, callback)
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