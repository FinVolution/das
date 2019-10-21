import {Control, Action} from 'ea-react-dm-v14'
import {DatabaseSetModel} from '../../model/Index'
import {DasUtil} from '../../view/components/utils/util/Index'

@Control(DatabaseSetModel)
export default class DatabaseSetControl extends Action {

    static loadList(param, _this, callback, loadListFiler) {
        return ::this.ajaxPost('/groupdbset/list', param, 'list', _this, callback, loadListFiler)
    }

    static addDbSet(dbSet, _this, callback) {
        DasUtil.transformStrategyListToStr(dbSet, 'apiParams', 'strategySource')
        return this.ajaxPost('/groupdbset/add', dbSet, 'rs', _this, callback)
    }

    static deleteDbSet(dbSet, _this, callback) {
        return this.ajaxDelete('/groupdbset/delete', dbSet, 'rs', _this, callback)
    }

    static updateDbSet(dbSet, _this, callback) {
        DasUtil.transformStrategyListToStr(dbSet, 'apiParams', 'strategySource')
        return this.ajaxPut('/groupdbset/update', dbSet, 'rs', _this, callback)
    }

    static loadTree(_this, callback) {
        return ::this.ajaxPost('/group/tree', null, 'tree', _this, callback)
    }

    static loadDBList(groupId, _this, callback) {
        return ::this.ajaxGet('/db/dbs', {groupId: groupId}, 'dblist', _this, callback)
    }
}