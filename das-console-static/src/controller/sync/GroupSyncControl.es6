import {Control, Action} from 'ea-react-dm-v14'
import {GroupSyncModel} from '../../model/Index'
import {sysnc} from '../../model/base/BaseModel'

@Control(GroupSyncModel)
export default class GroupSyncControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost(window.DASENV.dasSyncTarget + '/group/list?' + sysnc.token, param, 'list', _this, callback)
    }

    static addGroup(group, _this, callback) {
        return this.ajaxPost('/group/add', group, 'rs', _this, callback)
    }
}