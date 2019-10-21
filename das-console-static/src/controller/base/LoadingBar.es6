import {MaskBar, LoadingBar} from 'gfs-loadingbar'
import RTools from 'gfs-react-tools'

export default function LoadingBarAop(type, params) {
    return function (target, name, descriptor) {
        type = type.toLowerCase()
        const loadingBar = new LoadingBar()
        const text = (params && params.text) || '加载中...'
        let f = (function (currentMethod) {
            let c = currentMethod
            switch (type) {
                case 'loading':
                    return function () {
                        RTools.addLoadingBar(loadingBar)
                        return c.apply(this, arguments)
                    }
                case 'mosk':
                    return function () {
                        RTools.addLoadingBar(new MaskBar({text: text}))
                        return c.apply(this, arguments)
                    }
                default:return function () {
                    return c.apply(this, arguments)
                }
            }
        })(descriptor.value)
        descriptor.value = f
        return descriptor
    }
}