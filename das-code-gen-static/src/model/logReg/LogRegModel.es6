import {Model} from 'ea-react-dm-v14'

@Model('LogRegModel')
export default class LogRegModel {

    /*登录*/
    static loginfo = {userName: '', password: ''}

    /*注册*/
    static applyAccount = {displayName: '', email: '', password: ''}

    static regStatus = 0 //0:未完成 1：已完成 2：错误

    static reginfo = {
        userName: '',
        userRealName: '',
        userNo: '',
        userEmail: '',
        password: '',
        password2: ''
    }

    static applyAccountReponse = {}

    static rs = {}

}