import {Control, Action} from 'ea-react-dm-v14'
import UserModel from '../../model/user/UserModel'

@Control(UserModel)
export default class UserControl extends Action {

    static loadUserMenus(param, _this, callback) {
        return ::this.ajaxGet('/api/menu', param, 'menus', _this, callback)
    }

    static loadUserList(param, _this, callback) {
        return ::this.ajaxPost('/user/list', param, 'list', _this, callback)
    }

    static addUser(user, _this, callback) {
        user.password = 111111
        return this.ajaxPost('/user/add', user, 'rs', _this, callback)
    }

    static deleteUser(user, _this, callback) {
        return this.ajaxDelete('/user/delete', user, 'rs', _this, callback)
    }

    static updateUser(user, _this, callback) {
        return this.ajaxPut('/user/update', user, 'rs', _this, callback)
    }

    static getWorkInfo(user, _this, callback) {
        return this.ajaxGet('/user/getWorkInfo', user, 'loginUser', _this, callback)
    }

    static updateUserMenus(valueLink, menus) {
        valueLink = valueLink.match(/\.(.+?)$/, valueLink)[1]
        return (dispatch) => {
            dispatch(this.save(valueLink, menus))
        }
    }
}
