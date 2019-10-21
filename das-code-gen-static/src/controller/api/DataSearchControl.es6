import {Control, Action} from 'ea-react-dm-v14'
import DataSearchModel from '../../model/das/DataSearchModel'

@Control(DataSearchModel)
export default class DataSearchControl extends Action {

    static loadList(param, _this, callback) {
        return this.ajaxPost('/dataSearch/log/list', param, 'list', _this, callback)
    }
}