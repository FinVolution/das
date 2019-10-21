import {Control, Action} from 'ea-react-dm-v14'
import {MemberModel} from '../../model/Index'

@Control(MemberModel)
export default class MemberControl extends Action {

    static loadMemberList(group, _this, callback) {
        return this.ajaxPost('/member/list', group, 'list', _this, callback)
    }

    static addMember(user, _this, callback) {
        user['opt_user'] = user.role
        return this.ajaxPost('/member/add', user, 'rs', _this, callback)
    }

    static deleteMember(member, _this, callback) {
        return this.ajaxDelete('/member/delete', member, 'rs', _this, callback)
    }

    static updateMember(member, _this, callback) {
        return this.ajaxPut('/member/update', member, 'rs', _this, callback)
    }

    static loadTree(_this, callback) {
        return ::this.ajaxPost('/group/tree', null, 'tree', _this, callback)
    }
}