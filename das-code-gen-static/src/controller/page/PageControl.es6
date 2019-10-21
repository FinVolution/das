import {Control,Action} from 'ea-react-dm-v14'
import PageModel from '../../model/page/PageModel'

@Control(PageModel)
export default class PageControl extends Action {

    static loadMRealTime(param, _this, callback) {
        return ::this.ajaxGet('/m/rv', param, 'realTime', _this, callback)
    }

    static updateChartsdata(valueLink, chartsdata) {
        valueLink = valueLink.match(/\.(.+?)$/, valueLink)[1]
        return (dispatch) => {
            dispatch(this.save(valueLink, chartsdata))
        }
    }
}