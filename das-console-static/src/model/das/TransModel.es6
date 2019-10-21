import {Model} from 'ea-react-dm-v14'

@Model('TransModel')
export default class TransModel {
    static rs

    static list = []

    static suggestion = {}

    static searchInfo = {
        totalCount: 0,
        page: 1,
        pageSize: 10,
        sort: 'id',
        data: {//DatabaseSet
            id: null
        }
    }

    static states = {
        editeVisible: false,
        checkVisible: false
    }

    static xmlContent = ''
    static dasContent = ''
}