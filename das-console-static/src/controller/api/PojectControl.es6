import {Control, Action} from 'ea-react-dm-v14'
import {ProjectModel} from '../../model/Index'

@Control(ProjectModel)
export default class PojectControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost('/project/list', param, 'list', _this, callback)
    }

    static addProject(project, _this, callback) {
        return this.ajaxPost('/project/add', project, 'rs', _this, callback)
    }

    static deleteProject(project, _this, callback) {
        return this.ajaxDelete('/project/delete', project, 'rs', _this, callback)
    }

    static updateProject(project, _this, callback) {
        return this.ajaxPut('/project/update', project, 'rs', _this, callback)
    }

    static loadTree(_this, callback) {
        return ::this.ajaxPost('/group/tree', null, 'tree', _this, callback)
    }
}