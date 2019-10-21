import {Control, Action} from 'ea-react-dm-v14'
import {DataBaseSyncModel} from '../../model/Index'

@Control(DataBaseSyncModel)
export default class DataBaseSyncControl extends Action {

    static loadTree(_this, callback) {
        return ::this.ajaxPost(window.DASENV.dasSyncTarget + '/group/tree', null, 'tree', _this, callback)
    }

    static loadList(param, _this, callback) {
        return ::this.ajaxPost(window.DASENV.dasSyncTarget + '/db/list', param, 'list', _this, callback)
    }

    static addGroupDb(groupDb, _this, callback) {
        return this.ajaxPost('/db/syncdb', groupDb, 'rs', _this, callback)
    }
}