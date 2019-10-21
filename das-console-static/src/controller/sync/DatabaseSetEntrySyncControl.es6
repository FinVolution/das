import {Control, Action} from 'ea-react-dm-v14'
import {DatabaseSetEntrySyncModel} from '../../model/Index'

@Control(DatabaseSetEntrySyncModel)
export default class DatabaseSetEntrySyncControl extends Action {

    static loadDbSetEntryList(param, _this, callback) {
        return ::this.ajaxPost(window.DASENV.dasSyncTarget +'/groupdbSetEntry/list', param, 'list', _this, callback)
    }

    static addDbSetEntry(dbSetEntry, _this, callback) {
        return this.ajaxPost('/groupdbSetEntry/syncdb', dbSetEntry, 'rs', _this, callback)
    }

}