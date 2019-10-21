import {Control, Action} from 'ea-react-dm-v14'
import {ProjectSyncModel} from '../../model/Index'

@Control(ProjectSyncModel)
export default class PojectSyncControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost(window.DASENV.dasSyncTarget + '/project/list', param, 'list', _this, callback)
    }

    static loadTree(_this, callback) {
        return ::this.ajaxPost(window.DASENV.dasSyncTarget + '/group/tree', null, 'tree', _this, callback)
    }

    static addProject(project, _this, callback) {
        return this.ajaxPost('/project/syncdb', project, 'rs', _this, callback)
    }
}