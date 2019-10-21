import {Control, Action} from 'ea-react-dm-v14'
import {DatabaseModel} from '../../model/Index'

@Control(DatabaseModel)
export default class DataBaseControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost('/db/page/list', param, 'list', _this, callback)
    }

    static deleteDatabase(db, _this, callback) {
        return this.ajaxDelete('/db/delete', db, 'rs', _this, callback)
    }

    static updateDatabase(db, _this, callback) {
        return this.ajaxPut('/db/update', db, 'rs', _this, callback)
    }

    static sync(param, _this, callback) {
        param = {id: param.id}
        return ::this.ajaxGet('/db/sync', param, 'rs', _this, callback)
    }

    static loadGroupTree(_this, callback) {
        return ::this.ajaxPost('/group/tree', null, 'tree', _this, callback)
    }

    static connectionTest(param, _this, callback) {
        return this.ajaxPost('/setupDb/connectionTest', param, 'db_catalogs', _this, callback)
    }

    static connectionTestNew(param, _this, callback) {
        return this.ajaxPost('/db/connectionTest', param, 'db_catalog_trees', _this, callback)
    }

    static addDbs(param, _this, callback) {
        return this.ajaxPost('/db/addDbs', param, 'rs', _this, callback)
    }

}