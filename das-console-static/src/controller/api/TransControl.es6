import {Control, Action} from 'ea-react-dm-v14'
import {TransModel} from '../../model/Index'

@Control(TransModel)
export default class TransControl extends Action {

    static trans(param, _this, callback) {
        return ::this.ajaxPost('/trans/toDas', param, 'dasContent', _this, callback)
    }
}