import {Control, Action} from 'ea-react-dm-v14'
import {SelectEntityModel} from '../../model/Index'

@Control(SelectEntityModel)
export default class SelectEntityControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost('/selectEntity/list', param, 'list', _this, callback)
    }

    static addSelectEntity(selectEntity, _this, callback) {
        return this.ajaxPost('/selectEntity/add', selectEntity, 'rs', _this, callback)
    }

    static deleteSelectEntity(selectEntity, _this, callback) {
        return this.ajaxDelete('/selectEntity/delete', selectEntity, 'rs', _this, callback)
    }

    static updateSelectEntity(selectEntity, _this, callback) {
        return this.ajaxPut('/selectEntity/update', selectEntity, 'rs', _this, callback)
    }

}