import {Control, Action} from 'ea-react-dm-v14'
import {InitModel} from '../../model/Index'

@Control(InitModel)
export default class DataBaseControl extends Action {

    static addInit(param, _this, callback) {
        return this.ajaxPost('/config/add', param, 'rs', _this, callback)
    }

    static connectionTest(param, _this, callback) {
        return this.ajaxPost('/setupDb/connectionTest', param, 'db_catalogs', _this, callback)
    }

    static initAdminInfo(param, _this, callback) {
        return this.ajaxPost('/logReg/initAdminInfo', param, 'rs', _this, callback)
    }

}