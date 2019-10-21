import {Control,fetch,Action} from 'ea-react-dm-v14'
import TestModel from '../../model/test/TestModel'
//import BaseControl from '../base/BaseControl'

@Control(TestModel)
export default class TestControl extends Action{

    static queryBookTypes(){

        return (dispatch)=>{
            fetch('/test').then((data)=>{
                dispatch(this.update(data.data) )
            })
        }
    }

    static getBooks(index,bookList){
        //根据此类生成的update方法
        return this.update('books',bookList[index] )
    }
    static updateBook(index,value){
        return (dispatch)=>{
            //根据此类生成的update方法
            dispatch(this.update(`books.${index}`,value))
            //根据此类生成的update方法
            dispatch(this.update('updateIndex',-1) )
        }
    }

    static delBook(index){
        //根据此类生成的del方法
        return this.del(`books.${index}`)
    }

    static updateBookModel(key,value){
        return this.update(key,value)
    }

    static getBooks1(index,bookList){
        //根据此类生成的update方法
        return this.update('books',bookList[index] )
    }
}