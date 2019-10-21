import {Control, Action} from 'ea-react-dm-v14'
import {DataBaseSetSyncModel} from '../../model/Index'
import {DasUtil} from '../../view/components/utils/util/Index'

@Control(DataBaseSetSyncModel)
export default class DataBaseSetSyncControl extends Action {

    static loadList(param, _this, callback, loadListFiler) {
        return ::this.ajaxPost(window.DASENV.dasSyncTarget + '/groupdbset/list', param, 'list', _this, callback, loadListFiler)
    }

    static loadTree(_this, callback) {
        return ::this.ajaxPost(window.DASENV.dasSyncTarget + '/group/tree', null, 'tree', _this, callback)
    }

    static loadDBList(groupId, _this, callback) {
        return ::this.ajaxGet(window.DASENV.dasSyncTarget + '/db/dbs', {groupId: groupId}, 'dblist', _this, callback)
    }

    static addDbSet(dbSet, _this, callback) {
        DasUtil.transformStrategyListToStr(dbSet, 'apiParams', 'strategySource')
        return this.ajaxPost('/groupdbset/syncdb', dbSet, 'rs', _this, callback)
    }


}