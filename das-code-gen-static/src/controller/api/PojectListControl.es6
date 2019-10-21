import {Control, Action} from 'ea-react-dm-v14'
import {ProjectListModel} from '../../model/Index'

@Control(ProjectListModel)
export default class PojectListControl extends Action {

    static loadList(param, _this, callback) {
        return ::this.ajaxPost('/project/list', param, 'list', _this, callback)
    }

}