import {Control, Action} from 'ea-react-dm-v14'
import {PublicStrategyModel} from '../../model/Index'
import {DasUtil} from '../../view/components/utils/util/Index'

@Control(PublicStrategyModel)
export default class PublicStrategyControl extends Action {

    static loadList(param, _this, callback, loadListFiler) {
        return ::this.ajaxPost('/publicStrategy/list', param, 'list', _this, callback, loadListFiler)
    }

    static addPublicStrategy(publicStrategy, _this, callback) {
        DasUtil.transformStrategyListToStr(publicStrategy, 'apiParams', 'strategyParams')
        return this.ajaxPost('/publicStrategy/add', publicStrategy, 'rs', _this, callback)
    }

    static deletePublicStrategy(publicStrategy, _this, callback) {
        return this.ajaxDelete('/publicStrategy/delete', publicStrategy, 'rs', _this, callback)
    }

    static updatePublicStrategy(publicStrategy, _this, callback) {
        DasUtil.transformStrategyListToStr(publicStrategy, 'apiParams', 'strategyParams')
        return this.ajaxPut('/publicStrategy/update', publicStrategy, 'rs', _this, callback)
    }
}