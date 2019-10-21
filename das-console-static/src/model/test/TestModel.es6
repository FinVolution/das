import {Model} from 'ea-react-dm-v14'
//import Immutable from 'immutable'

@Model
export default class TestModel {
    static dataTime = null
    static bookTypes = []
    static books = []
    static book = {}
    static updateIndex = -1

    static textArea = {
        value: '1902-212-1212232323'
    }
    static inputPuls = {
        value: 'inputPlus'
    }
    static radioPlus = {
        selectId: 1,
        citys: [
            {
                cityId: 1,
                city: '上海'
            },
            {
                cityId: 2,
                city: '北京'
            },
            {
                cityId: 3,
                city: '广东'
            }
        ]
    }
    static selectPlus = {
        selectId: 2,
        citys1: [
            {
                cityId: 1,
                city: '上海'
            },
            {
                cityId: 2,
                city: '北京'
            },
            {
                cityId: 3,
                city: '广东'
            }
        ],
        citys2: [
            {
                cityId: 4,
                city: '杭州'
            },
            {
                cityId: 5,
                city: '武汉'
            },
            {
                cityId: 6,
                city: '三亚'
            }
        ]
    }
    static calenderPanelPuls = {
        bin: '1902-11-11'
    }
    static stocks = {}
    /* static queryBookTypes(data, action){

     if(action.data){
     return data.merge(Immutable.fromJS(action.data) )
     }
     }
     static getBooks(data,action){
     return data.merge(Immutable.fromJS({books:action.data}) )
     }
     static updateBook(data,action){

     return data.setIn(['books',action.data.index],data.get('book') ).setIn(['updateIndex'],-1)
     }
     static delBook(data,action){

     return data.deleteIn(['books',action.data.index] )
     }*/
}