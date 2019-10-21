import {Model} from 'ea-react-dm-v14'

@Model('InitModel')
export default class InitModel {
    static rs

    static tree = []

    static list = []

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id',
        data: {}
    }

    static states = {
        editeVisible: false,
        checkVisible: false
    }

    static item = {
        db_type: 1,
        db_address: '',
        db_port: '',
        db_user: '',
        db_password: ''
    }

    static admin = {
        password:'',
        password1:''
    }
}